package com.bloqueiashorts

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

/** Configurações salvas no aparelho: bloqueio ligado/desligado e senha. */
object Prefs {

    private const val FILE = "config"
    private const val KEY_BLOCKING = "blocking_on"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_PIN_SALT = "pin_salt"
    private const val KEY_FAILS = "pin_fails"
    private const val KEY_LOCKOUT_UNTIL = "pin_lockout_until"

    const val MIN_PIN_LENGTH = 4
    private const val MAX_FAILS = 5
    private const val LOCKOUT_MS = 60_000L

    private fun sp(c: Context) = c.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    // ---- Bloqueio ligado/desligado ----

    fun isBlockingOn(c: Context) = sp(c).getBoolean(KEY_BLOCKING, true)

    fun setBlockingOn(c: Context, on: Boolean) {
        sp(c).edit().putBoolean(KEY_BLOCKING, on).apply()
    }

    // ---- Senha ----

    fun hasPin(c: Context) = sp(c).contains(KEY_PIN_HASH)

    fun setPin(c: Context, pin: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        sp(c).edit()
            .putString(KEY_PIN_SALT, saltB64)
            .putString(KEY_PIN_HASH, hash(pin, saltB64))
            .apply()
    }

    fun removePin(c: Context) {
        sp(c).edit().remove(KEY_PIN_HASH).remove(KEY_PIN_SALT).apply()
    }

    /** Segundos que ainda faltam de espera após muitas senhas erradas (0 = pode tentar). */
    fun lockoutSecondsLeft(c: Context): Long {
        val left = sp(c).getLong(KEY_LOCKOUT_UNTIL, 0L) - System.currentTimeMillis()
        return if (left > 0) (left + 999) / 1000 else 0
    }

    /** Confere a senha. Depois de 5 erros seguidos, bloqueia novas tentativas por 1 minuto. */
    fun checkPin(c: Context, pin: String): Boolean {
        if (lockoutSecondsLeft(c) > 0) return false
        val prefs = sp(c)
        val salt = prefs.getString(KEY_PIN_SALT, null) ?: return false
        val ok = MessageDigest.isEqual(
            hash(pin, salt).toByteArray(),
            prefs.getString(KEY_PIN_HASH, "")!!.toByteArray()
        )
        if (ok) {
            prefs.edit().putInt(KEY_FAILS, 0).apply()
        } else {
            val fails = prefs.getInt(KEY_FAILS, 0) + 1
            val edit = prefs.edit().putInt(KEY_FAILS, fails)
            if (fails >= MAX_FAILS) {
                edit.putInt(KEY_FAILS, 0)
                    .putLong(KEY_LOCKOUT_UNTIL, System.currentTimeMillis() + LOCKOUT_MS)
            }
            edit.apply()
        }
        return ok
    }

    private fun hash(pin: String, saltB64: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(Base64.decode(saltB64, Base64.NO_WRAP))
        val bytes = digest.digest(pin.toByteArray())
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    // ---- Serviço desligado para usar app de banco ----

    fun isBankMode(c: Context) = sp(c).getBoolean("bank_mode", false)

    fun setBankMode(c: Context, value: Boolean) {
        sp(c).edit().putBoolean("bank_mode", value).apply()
    }

    // ---- Tela de ajuda ----

    fun wasHelpSeen(c: Context) = sp(c).getBoolean("help_seen", false)

    fun setHelpSeen(c: Context) {
        sp(c).edit().putBoolean("help_seen", true).apply()
    }
}
