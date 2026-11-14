package com.bloqueiashorts

/**
 * O que o BloqueiaShorts bloqueia: só o player de Shorts dentro do app do YouTube.
 *
 * Desde a versão 2.0 o app observa UM app só, de propósito. Apps de banco desconfiam de
 * serviços de Acessibilidade que vigiam o celular inteiro (discador, Configurações, o que é
 * digitado) — é o mesmo comportamento dos golpes de "mão fantasma". Por isso nada além do
 * YouTube entra aqui. O pacote também precisa estar em res/xml/accessibility_service_config.xml.
 */
object Rules {

    const val YOUTUBE = "com.google.android.youtube"

    /** IDs das views do player de Shorts (confirmados num Redmi Note 11S, nov/2026). */
    val SHORTS_PLAYER_IDS: List<String> = listOf(
        "reel_recycler",
        "reel_player_page_container",
        "reel_watch_player"
    )
}
