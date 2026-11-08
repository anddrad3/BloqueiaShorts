package com.bloqueiashorts

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Bloqueia vídeos curtos em vários apps e sites (veja [Rules]):
 *  - player de vídeo curto dentro de apps (YouTube Shorts, Instagram Reels) -> "voltar"
 *  - apps que são só vídeo curto (TikTok, Kwai...) -> tela inicial
 *  - sites de vídeo curto abertos no navegador -> "voltar"
 *  - com senha definida: telas do sistema que permitiriam desativar/desinstalar o app -> "voltar"
 * Se a tela continuar lá depois da ação, tenta de novo.
 */
class ShortsBlockerService : AccessibilityService() {

    companion object {
        private const val TAG = "BloqueiaShorts"

        /** Intervalo entre uma ação e a próxima tentativa, se a tela continuar aberta. */
        private const val RETRY_MS = 900L

        /** Máximo de "voltar" seguidos sem a tela fechar; depois disso, vai para a tela inicial. */
        private const val MAX_BACK_ATTEMPTS = 3

        /** Fração mínima da altura da tela para um player contar como tela cheia. */
        private const val MIN_SCREEN_FRACTION = 0.6f

        /** Faixa do topo da tela onde fica a barra de endereço dos navegadores. */
        private const val URL_BAR_TOP_FRACTION = 0.25f

        /** Limite de views visitadas ao procurar o endereço sem ID conhecido. */
        private const val MAX_NODES_SCANNED = 400
    }

    private enum class Action { BACK, HOME }

    private data class Detection(val reason: String, val action: Action)

    private val handler = Handler(Looper.getMainLooper())
    private val recheck = Runnable { check() }

    private var lastActionAt = 0L

    /** Quantas ações seguidas foram feitas sem a tela bloqueada sumir. */
    private var attempts = 0

    /** Último código secreto conferido, para não contar o mesmo erro várias vezes. */
    private var lastDialerCode: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        if (pkg in Rules.DIALERS) {
            checkDialer() // funciona mesmo com o bloqueio pausado
            return
        }
        check()
    }

    /** Abre o app quando o campo do discador mostra *#*#SENHA#*#* com a senha certa. */
    private fun checkDialer() {
        if (!Prefs.hasPin(this)) return
        val root = rootInActiveWindow ?: return
        val (node, code) = findSecretCode(root) ?: run { lastDialerCode = null; return }
        if (code == lastDialerCode) return
        lastDialerCode = code

        if (!Prefs.checkPin(this, code)) {
            Log.d(TAG, "Código secreto digitado, mas não é a senha")
            return
        }
        Log.i(TAG, "Código secreto correto — abrindo o app")

        // Apaga o código do campo, para a senha não ficar visível no discador.
        node.performAction(
            AccessibilityNodeInfo.ACTION_SET_TEXT,
            android.os.Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "")
            }
        )
        lastDialerCode = null
        Session.unlock()
        val requestedAt = SystemClock.elapsedRealtime()
        try {
            startActivity(
                android.content.Intent(this, MainActivity::class.java).addFlags(
                    android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
            )
        } catch (e: Exception) {
            Log.w(TAG, "Não foi possível abrir a tela", e)
        }

        // Plano B: se o sistema (ex.: Xiaomi) impediu a tela de abrir, devolve o ícone à tela inicial.
        handler.postDelayed({
            if (Session.lastResumedAt < requestedAt && IconHider.isHidden(this)) {
                IconHider.setHidden(this, false)
                Log.i(TAG, "Tela bloqueada pelo sistema — ícone restaurado")
                android.widget.Toast.makeText(
                    this,
                    "O celular impediu abrir o BloqueiaShorts. O ícone voltou para a tela inicial.",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }, 2500)
    }

    /** Procura na tela do discador um texto no formato do código secreto. */
    private fun findSecretCode(root: AccessibilityNodeInfo): Pair<AccessibilityNodeInfo, String>? {
        val queue = ArrayDeque<AccessibilityNodeInfo>().apply { add(root) }
        var visited = 0
        while (queue.isNotEmpty() && visited < MAX_NODES_SCANNED) {
            val node = queue.removeFirst()
            visited++
            val text = node.text?.toString()?.filterNot { it.isWhitespace() }
            if (!text.isNullOrEmpty()) {
                Rules.SECRET_CODE.find(text)?.let { return node to it.groupValues[1] }
            }
            for (i in 0 until node.childCount) node.getChild(i)?.let { queue.add(it) }
        }
        return null
    }

    private fun check() {
        handler.removeCallbacks(recheck)
        if (!Prefs.isBlockingOn(this)) return

        val root = rootInActiveWindow ?: return
        val pkg = root.packageName?.toString() ?: return

        val detection = detect(pkg, root)
        if (detection == null) {
            if (attempts > 0) Log.d(TAG, "Tela bloqueada fechou")
            attempts = 0
            return
        }

        // Continua conferindo mesmo sem novos eventos (um vídeo tocando quase não gera eventos).
        handler.postDelayed(recheck, RETRY_MS)

        val now = SystemClock.elapsedRealtime()
        if (now - lastActionAt < RETRY_MS) return

        // Se "voltar" não resolveu depois de algumas tentativas, vai para a tela inicial.
        val action = if (attempts >= MAX_BACK_ATTEMPTS) Action.HOME else detection.action

        attempts++
        lastActionAt = now
        Log.i(TAG, "Bloqueado: ${detection.reason} [$pkg] -> $action (tentativa $attempts)")
        performGlobalAction(
            if (action == Action.HOME) GLOBAL_ACTION_HOME else GLOBAL_ACTION_BACK
        )
    }

    private fun detect(pkg: String, root: AccessibilityNodeInfo): Detection? {
        if (pkg in Rules.WHOLE_APPS) {
            return Detection("app de vídeo curto", Action.HOME)
        }

        Rules.IN_APP_PLAYERS[pkg]?.let { ids ->
            return findFullScreenView(pkg, ids, root)?.let { Detection(it, Action.BACK) }
        }

        if (pkg in Rules.BROWSERS) {
            return findShortVideoUrl(pkg, root)?.let { Detection("site $it", Action.BACK) }
        }

        if (pkg in Rules.PROTECTED_SYSTEM_APPS && Prefs.isTamperProtectionActive(this) &&
            !Session.systemScreensAllowed()
        ) {
            if (isDangerousScreenForThisApp(root)) {
                return Detection("configuração protegida por senha", Action.BACK)
            }
        }
        return null
    }

    /**
     * Só considera perigosa a tela que é DO BloqueiaShorts e tem um jeito de desligá-lo:
     * o nome do app como título (no topo) + um botão/ação como "Desinstalar", "Forçar parada"
     * ou a chave "Usar BloqueiaShorts". Listas de apps (onde o nome aparece no meio de outros)
     * continuam acessíveis.
     */
    private fun isDangerousScreenForThisApp(root: AccessibilityNodeInfo): Boolean {
        val appName = getString(R.string.app_name)
        val topLimit = resources.displayMetrics.heightPixels * 0.35f
        val bounds = Rect()

        var titleOnTop = false
        var hasDangerAction = false
        val queue = ArrayDeque<AccessibilityNodeInfo>().apply { add(root) }
        var visited = 0
        while (queue.isNotEmpty() && visited < MAX_NODES_SCANNED) {
            val node = queue.removeFirst()
            visited++
            val text = (node.text ?: node.contentDescription)?.toString()?.trim()
            if (!text.isNullOrEmpty() && node.isVisibleToUser) {
                if (text.equals(appName, ignoreCase = true)) {
                    node.getBoundsInScreen(bounds)
                    if (bounds.top < topLimit) titleOnTop = true
                }
                val lower = text.lowercase()
                val danger = Rules.DANGER_ACTIONS.any { lower.contains(it) }
                // Caixas como "Interromper o BloqueiaShorts?" ou "Desinstalar BloqueiaShorts?"
                if (danger && lower.contains(appName.lowercase()) &&
                    !lower.contains("instalador")
                ) return true
                if (danger) hasDangerAction = true
                if (titleOnTop && hasDangerAction) return true
            }
            for (i in 0 until node.childCount) node.getChild(i)?.let { queue.add(it) }
        }
        return titleOnTop && hasDangerAction
    }

    /** Procura uma das views do player ocupando quase a tela toda. */
    private fun findFullScreenView(pkg: String, ids: List<String>, root: AccessibilityNodeInfo): String? {
        val screenHeight = resources.displayMetrics.heightPixels
        val bounds = Rect()
        for (id in ids) {
            for (node in root.findAccessibilityNodeInfosByViewId("$pkg:id/$id")) {
                if (!node.isVisibleToUser) continue
                node.getBoundsInScreen(bounds)
                if (bounds.height() >= screenHeight * MIN_SCREEN_FRACTION) return "player $id"
            }
        }
        return null
    }

    /** Lê o endereço da página no navegador e devolve o trecho de vídeo curto, se houver. */
    private fun findShortVideoUrl(pkg: String, root: AccessibilityNodeInfo): String? {
        val urlBarId = Rules.BROWSERS[pkg]
        if (urlBarId != null) {
            val nodes = root.findAccessibilityNodeInfosByViewId("$pkg:id/$urlBarId")
            if (nodes.isNotEmpty()) {
                for (node in nodes) {
                    // Ignora enquanto a pessoa está digitando na barra.
                    if (node.isFocused) continue
                    matchUrl(node.text)?.let { return it }
                }
                return null
            }
            // ID mudou numa atualização do navegador: cai na busca pelo texto.
        }
        return scanTopOfScreenForUrl(root)
    }

    /** Busca, nas views do topo da tela, um texto que pareça endereço de vídeo curto. */
    private fun scanTopOfScreenForUrl(root: AccessibilityNodeInfo): String? {
        val limit = resources.displayMetrics.heightPixels * URL_BAR_TOP_FRACTION
        val bounds = Rect()
        val queue = ArrayDeque<AccessibilityNodeInfo>().apply { add(root) }
        var visited = 0

        while (queue.isNotEmpty() && visited < MAX_NODES_SCANNED) {
            val node = queue.removeFirst()
            visited++
            node.getBoundsInScreen(bounds)
            if (bounds.top > limit) continue

            if (!node.isFocused) {
                matchUrl(node.text)?.let { return it }
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return null
    }

    private fun matchUrl(text: CharSequence?): String? {
        if (text.isNullOrEmpty() || text.contains(' ')) return null
        return Rules.SHORT_VIDEO_URL.find(text)?.value
    }

    override fun onInterrupt() {
        handler.removeCallbacks(recheck)
    }

    override fun onDestroy() {
        handler.removeCallbacks(recheck)
        super.onDestroy()
    }
}
