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

        /** O mesmo código do discador chega em vários eventos; conta uma vez só neste intervalo. */
        private const val DIALER_DEDUP_MS = 5000L
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
    private var lastDialerCodeAt = 0L

    private fun matchSecretCode(text: String): String? =
        Rules.SECRET_CODE.find(text.filterNot { it.isWhitespace() })?.groupValues?.get(1)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        if (pkg in Rules.DIALERS) {
            checkDialer(event) // funciona mesmo com o bloqueio pausado
            return
        }
        check()
    }

    /**
     * Mostra o cartão de desbloqueio quando *#*#SENHA#*#* é digitado no discador.
     *
     * O discador do Google APAGA o campo assim que reconhece um código *#*#...#*#*, muitas vezes
     * antes de dá para ler a tela. Por isso o código é procurado primeiro no próprio evento de
     * digitação: o texto novo e o texto ANTERIOR (o evento de "apagar" ainda traz o código inteiro).
     */
    private fun checkDialer(event: AccessibilityEvent) {
        if (!Prefs.hasPin(this)) return

        var code: String? = null
        var node: AccessibilityNodeInfo? = null
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val candidates = event.text.map { it.toString() } + listOfNotNull(event.beforeText?.toString())
            code = candidates.firstNotNullOfOrNull { matchSecretCode(it) }
        }
        if (code == null) {
            val found = rootInActiveWindow?.let { findSecretCode(it) }
            code = found?.second
            node = found?.first
        }
        if (code == null) {
            // Campo sem código: libera para conferir o próximo, exceto logo após um código
            // (o evento de "apagar" chega depois do evento com o código completo).
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED &&
                SystemClock.elapsedRealtime() - lastDialerCodeAt > DIALER_DEDUP_MS
            ) lastDialerCode = null
            return
        }
        if (code == lastDialerCode && SystemClock.elapsedRealtime() - lastDialerCodeAt < DIALER_DEDUP_MS) return
        lastDialerCode = code
        lastDialerCodeAt = SystemClock.elapsedRealtime()

        if (!Prefs.checkPin(this, code)) {
            Log.d(TAG, "Código secreto digitado, mas não é a senha")
            return
        }
        Log.i(TAG, "Código secreto correto — mostrando o cartão de desbloqueio")

        // Apaga o código do campo (se o discador ainda não apagou), para a senha não ficar visível.
        node?.performAction(
            AccessibilityNodeInfo.ACTION_SET_TEXT,
            android.os.Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "")
            }
        )
        Session.unlock()

        // 1) O jeito que funciona em QUALQUER celular: o ícone reaparece na tela inicial na hora
        //    (não abre tela nenhuma, então nenhum fabricante bloqueia). Ele volta a se esconder
        //    quando a pessoa sair do app, ou em 3 minutos se ela não abrir.
        revealIconTemporarily()

        // 2) Atalho, onde o celular permitir: um cartão por cima do discador com "Abrir".
        showUnlockOverlay()
    }

    private val rehideIcon = Runnable {
        if (Prefs.isIconTempRevealed(this) && Session.lastResumedAt < revealedAt) {
            IconHider.setHidden(this, true)
            Prefs.setIconTempRevealed(this, false)
            Log.i(TAG, "Ícone escondido de novo (não foi aberto a tempo)")
        }
    }
    private var revealedAt = 0L

    private fun revealIconTemporarily() {
        val wasHidden = IconHider.isHidden(this)
        if (wasHidden) {
            IconHider.setHidden(this, false)
            Prefs.setIconTempRevealed(this, true)
            revealedAt = SystemClock.elapsedRealtime()
            handler.removeCallbacks(rehideIcon)
            handler.postDelayed(rehideIcon, 3 * 60_000L)
        }
        Log.i(TAG, "Código correto — ícone visível")
        android.widget.Toast.makeText(
            this,
            if (wasHidden) "BloqueiaShorts desbloqueado ✅ O ícone apareceu na tela inicial: toque nele para abrir."
            else "BloqueiaShorts desbloqueado ✅ Toque no ícone do app para abrir.",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }

    // ------------------------------------------------------------------ cartão sobre o discador

    private var overlay: android.view.View? = null
    private val dismissOverlay = Runnable { removeOverlay() }

    private fun showUnlockOverlay() {
        removeOverlay()
        val wm = getSystemService(android.view.WindowManager::class.java)
        val themed = android.view.ContextThemeWrapper(this, R.style.Theme_BloqueiaShorts)
        val view = android.view.LayoutInflater.from(themed).inflate(R.layout.overlay_unlock, null)

        view.findViewById<android.view.View>(R.id.btnOverlayOpen).setOnClickListener {
            removeOverlay()
            openMainScreen()
        }
        view.findViewById<android.view.View>(R.id.btnOverlayClose).setOnClickListener { removeOverlay() }

        val params = android.view.WindowManager.LayoutParams(
            android.view.WindowManager.LayoutParams.MATCH_PARENT,
            android.view.WindowManager.LayoutParams.MATCH_PARENT,
            android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        try {
            wm.addView(view, params)
            overlay = view
            // pequena animação de entrada
            view.findViewById<android.view.View>(R.id.overlayCard).apply {
                alpha = 0f; scaleX = 0.9f; scaleY = 0.9f
                animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(260)
                    .setInterpolator(android.view.animation.OvershootInterpolator(1.6f)).start()
            }
            handler.postDelayed(dismissOverlay, 60_000)
        } catch (e: Exception) {
            // Sem problema: o ícone já está visível na tela inicial.
            Log.w(TAG, "Não foi possível mostrar o cartão", e)
        }
    }

    private fun removeOverlay() {
        handler.removeCallbacks(dismissOverlay)
        overlay?.let {
            try {
                getSystemService(android.view.WindowManager::class.java).removeView(it)
            } catch (e: Exception) {
                // já removido
            }
        }
        overlay = null
    }

    /** Tenta abrir a tela principal. Se o celular impedir, o ícone já está na tela inicial. */
    private fun openMainScreen() {
        try {
            startActivity(
                android.content.Intent(this, MainActivity::class.java).addFlags(
                    android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
            )
        } catch (e: Exception) {
            Log.w(TAG, "Não foi possível abrir a tela", e)
        }
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
        removeOverlay()
        super.onDestroy()
    }
}
