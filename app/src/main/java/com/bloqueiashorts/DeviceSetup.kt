package com.bloqueiashorts

import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

/**
 * Ajustes do sistema que o app precisa para funcionar bem: bateria sem restrições e,
 * na Xiaomi, inicialização automática.
 * Os estados da Xiaomi são lidos por um método não oficial; quando não dá para ler, retorna null.
 */
object DeviceSetup {

    /** Códigos internos da MIUI/HyperOS. */
    private const val MIUI_OP_AUTOSTART = 10008

    fun isXiaomi() = Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true) ||
        Build.BRAND.lowercase() in setOf("xiaomi", "redmi", "poco")

    fun isBatteryUnrestricted(c: Context) =
        c.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(c.packageName)

    fun isAutostartAllowed(c: Context) = miuiOpAllowed(c, MIUI_OP_AUTOSTART)

    private fun miuiOpAllowed(c: Context, op: Int): Boolean? = try {
        val ops = c.getSystemService(AppOpsManager::class.java)
        val method = AppOpsManager::class.java.getMethod(
            "checkOpNoThrow", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java
        )
        (method.invoke(ops, op, c.applicationInfo.uid, c.packageName) as Int) == AppOpsManager.MODE_ALLOWED
    } catch (e: Throwable) {
        null
    }

    // ---- telas de configuração ----

    fun openBatterySettings(c: Context) = startFirst(
        c,
        Intent().setComponent(
            ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity")
        ).putExtra("package_name", c.packageName)
            .putExtra("package_label", c.getString(R.string.app_name)),
        Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS),
        appDetails(c)
    )

    fun openAutostartSettings(c: Context) = startFirst(
        c,
        Intent().setComponent(
            ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
        ),
        appDetails(c)
    )

    private fun appDetails(c: Context) =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${c.packageName}"))

    /** Abre a primeira tela que existir neste celular. */
    private fun startFirst(c: Context, vararg intents: Intent): Boolean {
        for (intent in intents) {
            try {
                c.startActivity(intent)
                return true
            } catch (e: Exception) {
                // tenta a próxima
            }
        }
        return false
    }
}
