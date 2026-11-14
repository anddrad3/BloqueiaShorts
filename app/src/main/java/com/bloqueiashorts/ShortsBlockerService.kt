package com.bloqueiashorts

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

/**
 * Sai dos YouTube Shorts: quando o player de Shorts ocupa a tela, aperta "voltar".
 *
 * O serviço só recebe eventos do app do YouTube (veja accessibility_service_config.xml).
 * Ele não lê o que é digitado, não faz gestos, não desenha por cima de outros apps
 * e não vigia as Configurações. Se "voltar" não fechar os Shorts depois de algumas
 * tentativas, vai para a tela inicial.
 */
class ShortsBlockerService : AccessibilityService() {

    companion object {
        private const val TAG = "BloqueiaShorts"

        /** Instância em execução (para a tela do app poder desligar o serviço). */
        @Volatile
        var running: ShortsBlockerService? = null
            private set

        /**
         * Desliga o próprio serviço de Acessibilidade (recurso oficial do Android).
         * Usado pelo botão "Desligar para usar app de banco". Retorna false se não estava rodando.
         */
        fun turnOff(): Boolean {
            val service = running ?: return false
            service.disableSelf()
            return true
        }

        /** Intervalo entre uma ação e a próxima tentativa, se os Shorts continuarem abertos. */
        private const val RETRY_MS = 900L

        /** Máximo de "voltar" seguidos sem os Shorts fecharem; depois disso, tela inicial. */
        private const val MAX_BACK_ATTEMPTS = 3

        /** Fração mínima da altura da tela para o player contar como tela cheia (e não miniatura). */
        private const val MIN_SCREEN_FRACTION = 0.6f

        /** Não repete o aviso "Shorts bloqueado" mais de uma vez neste intervalo. */
        private const val TOAST_INTERVAL_MS = 8000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val recheck = Runnable { check() }

    private var lastActionAt = 0L
    private var lastToastAt = 0L

    /** Quantas ações seguidas foram feitas sem os Shorts fecharem. */
    private var attempts = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.packageName?.toString() != Rules.YOUTUBE) return
        check()
    }

    private fun check() {
        handler.removeCallbacks(recheck)
        if (!Prefs.isBlockingOn(this)) return

        val root = rootInActiveWindow ?: return
        if (root.packageName?.toString() != Rules.YOUTUBE) {
            attempts = 0
            return
        }

        if (!isShortsOnScreen(root)) {
            if (attempts > 0) Log.d(TAG, "Shorts fechou")
            attempts = 0
            return
        }

        // Continua conferindo mesmo sem novos eventos (um vídeo tocando quase não gera eventos).
        handler.postDelayed(recheck, RETRY_MS)

        val now = SystemClock.elapsedRealtime()
        if (now - lastActionAt < RETRY_MS) return

        val goHome = attempts >= MAX_BACK_ATTEMPTS
        attempts++
        lastActionAt = now
        Log.i(TAG, "Shorts bloqueado -> ${if (goHome) "início" else "voltar"} (tentativa $attempts)")
        performGlobalAction(if (goHome) GLOBAL_ACTION_HOME else GLOBAL_ACTION_BACK)

        if (now - lastToastAt > TOAST_INTERVAL_MS) {
            lastToastAt = now
            Toast.makeText(this, "Shorts bloqueado pelo BloqueiaShorts", Toast.LENGTH_SHORT).show()
        }
    }

    /** Procura o player de Shorts ocupando quase a tela toda. */
    private fun isShortsOnScreen(root: AccessibilityNodeInfo): Boolean {
        val screenHeight = resources.displayMetrics.heightPixels
        val bounds = Rect()
        for (id in Rules.SHORTS_PLAYER_IDS) {
            for (node in root.findAccessibilityNodeInfosByViewId("${Rules.YOUTUBE}:id/$id")) {
                if (!node.isVisibleToUser) continue
                node.getBoundsInScreen(bounds)
                if (bounds.height() >= screenHeight * MIN_SCREEN_FRACTION) return true
            }
        }
        return false
    }

    override fun onInterrupt() {
        handler.removeCallbacks(recheck)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        running = this
    }

    override fun onDestroy() {
        running = null
        handler.removeCallbacks(recheck)
        super.onDestroy()
    }
}
