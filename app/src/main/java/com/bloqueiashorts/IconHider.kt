package com.bloqueiashorts

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * Esconde/mostra o ícone do app ligando e desligando o "LauncherAlias" do manifesto.
 * A tela principal continua existindo; só o atalho da tela inicial some.
 */
object IconHider {

    private fun alias(c: Context) = ComponentName(c, "${c.packageName}.LauncherAlias")

    fun isHidden(c: Context) =
        c.packageManager.getComponentEnabledSetting(alias(c)) ==
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED

    fun setHidden(c: Context, hidden: Boolean) {
        c.packageManager.setComponentEnabledSetting(
            alias(c),
            if (hidden) PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            else PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
            PackageManager.DONT_KILL_APP
        )
    }
}
