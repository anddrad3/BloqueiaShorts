package com.bloqueiashorts

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.view.animation.PathInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    companion object {
        private val EMPHASIZED = PathInterpolator(0.2f, 0f, 0f, 1f)
        private val BLOCKED_ITEMS = listOf("YouTube Shorts")
    }

    private enum class State { SERVICE_OFF, ACTIVE, PAUSED }

    private lateinit var content: ViewGroup
    private lateinit var statusCircle: View
    private lateinit var pulseRing: View
    private lateinit var statusIcon: ImageView
    private lateinit var statusTitle: TextView
    private lateinit var statusSubtitle: TextView
    private lateinit var btnMain: MaterialButton

    private var lastState: State? = null
    private var circleColor = 0
    private var pulseAnimator: ValueAnimator? = null

    // ------------------------------------------------------------------ ciclo de vida

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content = findViewById(R.id.content)
        statusCircle = findViewById(R.id.statusCircle)
        pulseRing = findViewById(R.id.pulseRing)
        statusIcon = findViewById(R.id.statusIcon)
        statusTitle = findViewById(R.id.statusTitle)
        statusSubtitle = findViewById(R.id.statusSubtitle)
        btnMain = findViewById(R.id.btnMain)

        btnMain.setOnClickListener { haptic(it); onMainButton() }
        findViewById<View>(R.id.rowPin).setOnClickListener { haptic(it); onPinRow() }
        findViewById<View>(R.id.rowRemovePin).setOnClickListener { haptic(it); onRemovePinRow() }
        findViewById<View>(R.id.rowHide).setOnClickListener { haptic(it); showHideIconSheet() }
        findViewById<View>(R.id.rowReopen).setOnClickListener { haptic(it); showReopenInfo() }
        findViewById<View>(R.id.rowSetupA11y).setOnClickListener {
            haptic(it)
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        findViewById<View>(R.id.rowSetupBattery).setOnClickListener {
            haptic(it)
            DeviceSetup.openBatterySettings(this)
        }
        findViewById<View>(R.id.rowSetupAutostart).setOnClickListener {
            haptic(it)
            DeviceSetup.openAutostartSettings(this)
        }
        findViewById<View>(R.id.btnHelp).setOnClickListener { haptic(it); showHelpSheet() }
        findViewById<View>(R.id.btnRestricted).setOnClickListener { haptic(it); showRestrictedSheet() }
        findViewById<View>(R.id.btnBank).setOnClickListener { haptic(it); onBankButton() }
        findViewById<View>(R.id.rowHelp).setOnClickListener { haptic(it); showHelpSheet() }

        val chips = findViewById<ChipGroup>(R.id.chips)
        BLOCKED_ITEMS.forEach { label ->
            chips.addView(Chip(this).apply {
                text = label
                isClickable = false
                chipBackgroundColor = ColorStateList.valueOf(color(R.color.surface_variant))
                chipStrokeWidth = 0f
                setTextColor(color(R.color.on_surface))
                setEnsureMinTouchTargetSize(false)
            })
        }

        refresh(animate = false)
        if (savedInstanceState == null) playEntrance()

        // Na primeira vez, explica como o app funciona.
        if (savedInstanceState == null && !Prefs.wasHelpSeen(this)) {
            content.postDelayed({ if (!isFinishing) showHelpSheet() }, 650)
        }
    }

    override fun onResume() {
        super.onResume()
        refresh(animate = true)
    }

    override fun onStop() {
        super.onStop()
        Session.lock() // saiu da tela: volta a pedir senha
        pulseAnimator?.cancel()
    }

    // ------------------------------------------------------------------ estado da tela

    private fun currentState() = when {
        !isServiceEnabled() -> State.SERVICE_OFF
        Prefs.isBlockingOn(this) -> State.ACTIVE
        else -> State.PAUSED
    }

    private fun refresh(animate: Boolean) {
        val state = currentState()
        val changed = state != lastState
        if (animate && changed) {
            TransitionManager.beginDelayedTransition(content, AutoTransition().setDuration(280).setInterpolator(EMPHASIZED))
        }

        val (fg, bg, icon) = when (state) {
            State.ACTIVE -> Triple(R.color.state_on, R.color.state_on_container, R.drawable.ic_shield_check)
            State.PAUSED -> Triple(R.color.state_paused, R.color.state_paused_container, R.drawable.ic_pause)
            State.SERVICE_OFF -> Triple(R.color.state_off, R.color.state_off_container, R.drawable.ic_warning)
        }
        statusIcon.setImageResource(icon)
        statusIcon.imageTintList = ColorStateList.valueOf(color(fg))
        pulseRing.backgroundTintList = ColorStateList.valueOf(color(fg))
        animateCircleColor(color(bg), animate && changed)

        when (state) {
            State.ACTIVE -> {
                statusTitle.text = "Proteção ativa"
                statusSubtitle.text = "Os Shorts do YouTube estão bloqueados."
                btnMain.text = "Pausar bloqueio"
                btnMain.setIconResource(R.drawable.ic_pause)
            }
            State.PAUSED -> {
                statusTitle.text = "Bloqueio pausado"
                statusSubtitle.text = "Os Shorts do YouTube estão liberados."
                btnMain.text = "Ativar bloqueio"
                btnMain.setIconResource(R.drawable.ic_shield_check)
            }
            State.SERVICE_OFF -> if (Prefs.isBankMode(this)) {
                statusTitle.text = "Desligado para o banco"
                statusSubtitle.text = "Terminou de usar o app do banco? Toque abaixo e ligue o BloqueiaShorts de novo em Acessibilidade → Apps baixados."
                btnMain.text = "Religar a proteção"
                btnMain.setIconResource(R.drawable.ic_shield_check)
            } else {
                statusTitle.text = "Quase lá"
                statusSubtitle.text = "Ative o BloqueiaShorts em Acessibilidade → Apps baixados."
                btnMain.text = "Abrir Acessibilidade"
                btnMain.setIconResource(R.drawable.ic_accessibility)
            }
        }
        // Serviço ligado de novo: sai do modo banco.
        if (state != State.SERVICE_OFF && Prefs.isBankMode(this)) Prefs.setBankMode(this, false)

        findViewById<View>(R.id.btnRestricted).visibility =
            if (state == State.SERVICE_OFF && !Prefs.isBankMode(this) && android.os.Build.VERSION.SDK_INT >= 33)
                View.VISIBLE else View.GONE
        findViewById<View>(R.id.btnBank).visibility =
            if (state != State.SERVICE_OFF) View.VISIBLE else View.GONE

        if (animate && changed && lastState != null) popIcon()
        if (state == State.ACTIVE) startPulse() else stopPulse()
        lastState = state

        // Segurança
        val hasPin = Prefs.hasPin(this)
        findViewById<TextView>(R.id.rowPinTitle).text = if (hasPin) "Alterar senha" else "Definir senha"
        findViewById<TextView>(R.id.rowPinSub).text =
            if (hasPin) "Pedida para pausar o bloqueio ou desligar para o banco"
            else "Sem senha, qualquer pessoa pode pausar o bloqueio"
        val pinOnly = if (hasPin) View.VISIBLE else View.GONE
        listOf(R.id.rowRemovePin, R.id.dividerRemove)
            .forEach { findViewById<View>(it).visibility = pinOnly }

        refreshSetup()
    }

    // ------------------------------------------------------------------ ações

    private fun onMainButton() {
        when (currentState()) {
            State.SERVICE_OFF -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            State.ACTIVE -> withPin("Pausar bloqueio") {
                Prefs.setBlockingOn(this, false)
                refresh(animate = true)
                snack("Bloqueio pausado")
            }
            State.PAUSED -> withPin("Ativar bloqueio") {
                Prefs.setBlockingOn(this, true)
                refresh(animate = true)
                snack("Proteção ativada")
            }
        }
    }

    /**
     * Alguns apps de banco recusam qualquer serviço de Acessibilidade que não veio da Play Store,
     * mesmo um tão restrito quanto o do BloqueiaShorts 2.0. Este botão desliga o serviço
     * (recurso oficial do Android) para a pessoa usar o banco, e depois ela religa.
     */
    private fun onBankButton() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Usar app de banco")
            .setIcon(R.drawable.ic_lock)
            .setMessage(
                "O BloqueiaShorts só observa o YouTube, mas alguns bancos recusam qualquer app de fora " +
                    "da Play Store com a Acessibilidade ligada — é uma proteção contra golpes.\n\n" +
                    "Só use este botão se o seu banco reclamar. Ele desliga o BloqueiaShorts e os Shorts " +
                    "ficam liberados até você religar.\n\n" +
                    "Quando terminar de usar o banco, abra o BloqueiaShorts e toque em “Religar a proteção”."
            )
            .setPositiveButton("Desligar") { _, _ ->
                withPin("Desligar para usar o banco") {
                    Prefs.setBankMode(this, true)
                    if (ShortsBlockerService.turnOff()) {
                        content.postDelayed({ refresh(animate = true) }, 400)
                        snack("BloqueiaShorts desligado. Pode abrir o app do banco.")
                    } else {
                        // Serviço não estava rodando: abre a Acessibilidade para desligar manualmente.
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun onPinRow() {
        if (Prefs.hasPin(this)) withPin("Senha atual") { askNewPin() } else askNewPin()
    }

    private fun onRemovePinRow() = withPin("Remover senha") {
        Prefs.removePin(this)
        refresh(animate = true)
        snack("Senha removida")
    }

    // ------------------------------------------------------------------ modais

    private fun showHelpSheet() {
        val (sheet, view) = openSheet(R.layout.sheet_how_it_works)
        view.findViewById<MaterialButton>(R.id.btnSheetPrimary).setOnClickListener {
            haptic(it)
            sheet.dismiss()
        }
        sheet.setOnDismissListener { Prefs.setHelpSeen(this) }
        sheet.show()
    }

    private fun refreshSetup() {
        val xiaomi = DeviceSetup.isXiaomi()
        setSetupRow(
            R.id.iconSetupA11y, R.id.subSetupA11y, isServiceEnabled(),
            "Ativa", "Desativada · toque para ativar"
        )
        setSetupRow(
            R.id.iconSetupBattery, R.id.subSetupBattery, DeviceSetup.isBatteryUnrestricted(this),
            "Ok · o sistema não vai desligar o app",
            "Toque e escolha “Sem restrições”"
        )
        val xiaomiRows = if (xiaomi) View.VISIBLE else View.GONE
        listOf(R.id.rowSetupAutostart, R.id.dividerSetupAutostart)
            .forEach { findViewById<View>(it).visibility = xiaomiRows }
        if (xiaomi) {
            setSetupRow(
                R.id.iconSetupAutostart, R.id.subSetupAutostart, DeviceSetup.isAutostartAllowed(this),
                "Ligada · o app volta sozinho ao reiniciar",
                "Toque e ligue o BloqueiaShorts"
            )
        }
    }

    /** ok = true (verde), false (amarelo, precisa ajustar) ou null (não dá para saber: confira). */
    private fun setSetupRow(iconId: Int, subId: Int, ok: Boolean?, okText: String, todoText: String) {
        val icon = findViewById<ImageView>(iconId)
        val (img, fg, bg) = when (ok) {
            true -> Triple(R.drawable.ic_shield_check, R.color.state_on, R.color.state_on_container)
            false -> Triple(R.drawable.ic_warning, R.color.state_paused, R.color.state_paused_container)
            null -> Triple(R.drawable.ic_info, R.color.accent, R.color.accent_container)
        }
        icon.setImageResource(img)
        icon.imageTintList = ColorStateList.valueOf(color(fg))
        icon.backgroundTintList = ColorStateList.valueOf(color(bg))
        findViewById<TextView>(subId).text = when (ok) {
            true -> okText
            false -> todoText
            null -> "Toque para conferir · $todoText"
        }
    }

    /** Passo a passo para liberar a "Configuração restrita" do Android 13+. */
    private fun showRestrictedSheet() {
        val (sheet, view) = openSheet(R.layout.sheet_restricted)
        view.findViewById<MaterialButton>(R.id.btnSheetPrimary).setOnClickListener {
            haptic(it)
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:$packageName"))
            )
        }
        view.findViewById<MaterialButton>(R.id.btnSheetSecondary).setOnClickListener { sheet.dismiss() }
        sheet.show()
    }

    /** Cria um modal de baixo para cima, já expandido, com entrada em cascata dos elementos. */
    private fun openSheet(layoutRes: Int): Pair<BottomSheetDialog, View> {
        val sheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(layoutRes, null)
        sheet.setContentView(view)
        sheet.behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
        sheet.behavior.skipCollapsed = true
        sheet.setOnShowListener {
            val group = view.findViewById<ViewGroup>(R.id.sheetContent)
            for (i in 0 until group.childCount) {
                val child = group.getChildAt(i)
                child.alpha = 0f
                child.translationY = dp(18f)
                child.animate().alpha(1f).translationY(0f)
                    .setStartDelay(40L + i * 35L).setDuration(380).setInterpolator(EMPHASIZED).start()
            }
            view.findViewById<View>(R.id.sheetIcon).apply {
                scaleX = 0.5f; scaleY = 0.5f
                animate().scaleX(1f).scaleY(1f).setStartDelay(120).setDuration(520)
                    .setInterpolator(OvershootInterpolator(2.2f)).start()
            }
        }
        return sheet to view
    }

    /**
     * Passo a passo para esconder o ícone pelo próprio celular (Samsung, Xiaomi, Oppo...).
     * O app não esconde o próprio ícone: isso exigia o código pelo discador, que lia o que
     * a pessoa digitava — exatamente o que os apps de banco desconfiam.
     */
    private fun showHideIconSheet() {
        val (sheet, view) = openSheet(R.layout.sheet_hide_icon)
        view.findViewById<MaterialButton>(R.id.btnSheetPrimary).setOnClickListener {
            haptic(it)
            sheet.dismiss()
        }
        sheet.show()
    }

    /** Como abrir o app com o ícone escondido: botão "Configurações" do serviço em Acessibilidade. */
    private fun showReopenInfo() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Abrir com o ícone escondido")
            .setIcon(R.drawable.ic_accessibility)
            .setMessage(
                "Em qualquer celular, mesmo com o ícone escondido:\n\n" +
                    "Configurações → Acessibilidade → Apps baixados (ou Serviços instalados) → " +
                    "BloqueiaShorts → Configurações.\n\n" +
                    "Esse botão “Configurações” abre esta tela direto."
            )
            .setPositiveButton("Abrir Acessibilidade") { _, _ ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            .setNegativeButton("Fechar", null)
            .show()
    }

    // ------------------------------------------------------------------ senha

    /** Executa [action] direto se não há senha (ou se já foi digitada há pouco); senão, pede a senha. */
    private fun withPin(title: String, action: () -> Unit) {
        if (!Prefs.hasPin(this) || Session.isUnlocked()) {
            action()
            return
        }
        askPin(title) { action() }
    }

    /** Sempre pede a senha e, se estiver certa, entrega o número digitado em [onOk]. */
    private fun askPin(title: String, onOk: (String) -> Unit) {
        val wait = Prefs.lockoutSecondsLeft(this)
        if (wait > 0) {
            snack("Muitas tentativas erradas. Aguarde $wait s.")
            return
        }

        val view = layoutInflater.inflate(R.layout.dialog_pin, null)
        val layout = view.findViewById<TextInputLayout>(R.id.pinLayout)
        val input = view.findViewById<TextInputEditText>(R.id.pinInput)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setIcon(R.drawable.ic_lock)
            .setView(view)
            .setPositiveButton("Confirmar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        fun submit() {
            val pin = input.text.toString()
            if (Prefs.checkPin(this, pin)) {
                Session.unlock()
                dialog.dismiss()
                onOk(pin)
            } else {
                val left = Prefs.lockoutSecondsLeft(this)
                layout.error = if (left > 0) "Senha errada. Aguarde $left s." else "Senha errada"
                input.text = null
                shake(layout)
                if (left > 0) dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            }
        }

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { submit() }
            input.requestFocus()
        }
        input.setOnEditorActionListener { _, _, _ -> submit(); true }
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog.show()
    }

    private fun askNewPin() {
        val view = layoutInflater.inflate(R.layout.dialog_pin, null)
        val layout = view.findViewById<TextInputLayout>(R.id.pinLayout)
        val input = view.findViewById<TextInputEditText>(R.id.pinInput)
        val confirmLayout = view.findViewById<TextInputLayout>(R.id.pinConfirmLayout)
        val confirm = view.findViewById<TextInputEditText>(R.id.pinConfirmInput)
        layout.hint = "Nova senha (mín. ${Prefs.MIN_PIN_LENGTH} números)"
        confirmLayout.visibility = View.VISIBLE

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(if (Prefs.hasPin(this)) "Nova senha" else "Criar senha")
            .setIcon(R.drawable.ic_key)
            .setMessage("Ela será pedida para pausar o bloqueio, desligar para o banco ou remover a senha.")
            .setView(view)
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        fun submit() {
            val pin = input.text.toString()
            layout.error = null
            confirmLayout.error = null
            when {
                pin.length < Prefs.MIN_PIN_LENGTH -> {
                    layout.error = "Use pelo menos ${Prefs.MIN_PIN_LENGTH} números"; shake(layout)
                }
                confirm.text.toString() != pin -> {
                    confirmLayout.error = "As senhas não conferem"; shake(confirmLayout)
                }
                else -> {
                    Prefs.setPin(this, pin)
                    Session.unlock()
                    dialog.dismiss()
                    refresh(animate = true)
                    snack("Senha salva")
                }
            }
        }

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { submit() }
            input.requestFocus()
        }
        confirm.setOnEditorActionListener { _, _, _ -> submit(); true }
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog.show()
    }

    // ------------------------------------------------------------------ animações

    /** Cartões entram em cascata, subindo e aparecendo. */
    private fun playEntrance() {
        for (i in 0 until content.childCount) {
            val child = content.getChildAt(i)
            child.alpha = 0f
            child.translationY = dp(28f)
            child.animate().alpha(1f).translationY(0f)
                .setStartDelay(60L + i * 70L).setDuration(520).setInterpolator(EMPHASIZED).start()
        }
        findViewById<View>(R.id.logo).apply {
            rotation = -18f; scaleX = 0.6f; scaleY = 0.6f
            animate().rotation(0f).scaleX(1f).scaleY(1f).setStartDelay(80).setDuration(700)
                .setInterpolator(OvershootInterpolator(2f)).start()
        }
    }

    private fun animateCircleColor(target: Int, animate: Boolean) {
        if (!animate || circleColor == 0) {
            circleColor = target
            statusCircle.backgroundTintList = ColorStateList.valueOf(target)
            return
        }
        ValueAnimator.ofObject(ArgbEvaluator(), circleColor, target).apply {
            duration = 420
            interpolator = EMPHASIZED
            addUpdateListener {
                statusCircle.backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
            }
            start()
        }
        circleColor = target
    }

    private fun popIcon() {
        statusIcon.scaleX = 0.3f
        statusIcon.scaleY = 0.3f
        statusIcon.rotation = -40f
        statusIcon.animate().scaleX(1f).scaleY(1f).rotation(0f)
            .setDuration(560).setInterpolator(OvershootInterpolator(2.6f)).start()
        statusCircle.animate().scaleX(1.08f).scaleY(1.08f).setDuration(160).withEndAction {
            statusCircle.animate().scaleX(1f).scaleY(1f).setDuration(360)
                .setInterpolator(OvershootInterpolator(3f)).start()
        }.start()
    }

    /** Anel que "respira" em volta do escudo enquanto a proteção está ativa. */
    private fun startPulse() {
        if (pulseAnimator?.isRunning == true) return
        pulseRing.visibility = View.VISIBLE
        pulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2200
            repeatCount = ValueAnimator.INFINITE
            interpolator = PathInterpolator(0.3f, 0f, 0.2f, 1f)
            addUpdateListener {
                val t = it.animatedValue as Float
                val s = 1f + 0.26f * t
                pulseRing.scaleX = s
                pulseRing.scaleY = s
                pulseRing.alpha = 0.35f * (1f - t)
            }
            start()
        }
    }

    private fun stopPulse() {
        pulseAnimator?.cancel()
        pulseAnimator = null
        pulseRing.visibility = View.INVISIBLE
    }

    private fun shake(v: View) {
        ObjectAnimator.ofFloat(v, View.TRANSLATION_X, 0f, 22f, -22f, 15f, -15f, 7f, -7f, 0f)
            .setDuration(420).start()
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    // ------------------------------------------------------------------ utilidades

    private fun haptic(v: View) = v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)

    private fun snack(msg: String) {
        Snackbar.make(content, msg, Snackbar.LENGTH_LONG).apply {
            view.translationY = dp(-8f)
            show()
        }
    }

    private fun color(id: Int) = ContextCompat.getColor(this, id)

    private fun dp(v: Float) = v * resources.displayMetrics.density

    /** Verifica se o AccessibilityService está ligado nas configurações do sistema. */
    private fun isServiceEnabled(): Boolean {
        val expected = ComponentName(this, ShortsBlockerService::class.java)
        val enabled = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(':').any { ComponentName.unflattenFromString(it) == expected }
    }
}
