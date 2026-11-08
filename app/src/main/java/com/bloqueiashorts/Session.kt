package com.bloqueiashorts

import android.os.SystemClock

/**
 * Desbloqueio temporário, só em memória: depois de acertar a senha (na tela ou pelo
 * código do discador), as próximas ações não pedem a senha de novo por um tempo.
 * Some quando o processo morre ou quando a tela do app sai de vista.
 */
object Session {
    private const val DEFAULT_MS = 2 * 60_000L

    private var unlockedUntil = 0L

    fun unlock(ms: Long = DEFAULT_MS) {
        unlockedUntil = SystemClock.elapsedRealtime() + ms
    }

    fun lock() {
        unlockedUntil = 0L
    }

    fun isUnlocked() = SystemClock.elapsedRealtime() < unlockedUntil
}
