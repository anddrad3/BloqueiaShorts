package com.bloqueiashorts

/**
 * Tudo que o BloqueiaShorts bloqueia fica aqui.
 * Ao adicionar um pacote novo, inclua-o também em res/xml/accessibility_service_config.xml
 * (atributo android:packageNames), senão o serviço não recebe eventos dele.
 */
object Rules {

    /**
     * Apps com vídeo curto DENTRO deles: bloqueia só o player, pelos IDs das views.
     * O player precisa ocupar quase a tela toda, para não confundir com miniaturas no feed.
     */
    val IN_APP_PLAYERS: Map<String, List<String>> = mapOf(
        // YouTube Shorts (IDs confirmados num Redmi Note 11S, nov/2026)
        "com.google.android.youtube" to listOf(
            "reel_recycler",
            "reel_player_page_container",
            "reel_watch_player"
        ),
        // Instagram Reels (IDs não confirmados em aparelho — ajustar se não funcionar)
        "com.instagram.android" to listOf(
            "clips_viewer_view_pager",
            "clips_viewer_container"
        )
    )

    /** Apps que SÃO vídeo curto: abriu, vai para a tela inicial. */
    val WHOLE_APPS: Set<String> = setOf(
        "com.zhiliaoapp.musically",      // TikTok
        "com.ss.android.ugc.trill",      // TikTok (algumas regiões)
        "com.zhiliaoapp.musically.go",   // TikTok Lite
        "com.ss.android.ugc.aweme",      // Douyin
        "com.kwai.video",                // Kwai
        "com.smile.gifmaker"             // Kuaishou
    )

    /** Navegadores: lê o endereço da página e volta se for vídeo curto. */
    val BROWSERS: Map<String, String?> = mapOf(
        // pacote -> ID da barra de endereço (null = procura pelo texto no topo da tela)
        "com.android.chrome" to "url_bar",
        "com.chrome.beta" to "url_bar",
        "com.brave.browser" to "url_bar",
        "com.microsoft.emmx" to "url_bar",
        "com.kiwibrowser.browser" to "url_bar",
        "org.mozilla.firefox" to "mozac_browser_toolbar_url_view",
        "com.sec.android.app.sbrowser" to "location_bar_edit_text",
        "com.mi.globalbrowser" to null,
        "com.opera.browser" to null,
        "com.opera.mini.native" to null,
        "com.duckduckgo.mobile.android" to null
    )

    /** Endereços de vídeo curto (sem "https://", porque a barra costuma esconder isso). */
    val SHORT_VIDEO_URL = Regex(
        """(youtube\.com/shorts|youtu\.be/shorts|instagram\.com/reels?(/|$)|""" +
            """facebook\.com/reels?(/|$)|fb\.watch|tiktok\.com|kwai\.com|kwai-video\.com)""",
        RegexOption.IGNORE_CASE
    )

    /** Discadores onde o código secreto *#*#SENHA#*#* abre o app. */
    val DIALERS: Set<String> = setOf(
        "com.google.android.dialer",     // Telefone do Google (usado no seu Redmi)
        "com.android.contacts",          // discador da Xiaomi
        "com.android.dialer",            // discador padrão do Android
        "com.samsung.android.dialer"     // Samsung
    )

    /** Formato do código secreto digitado no discador. */
    val SECRET_CODE = Regex("""^\*#\*#(\d{4,12})#\*#\*$""")

    /**
     * Textos de ações que desligam ou removem o app (em minúsculas, PT e EN).
     * A proteção só age quando um deles aparece numa tela que tem "BloqueiaShorts" como título.
     */
    val DANGER_ACTIONS: List<String> = listOf(
        "desinstalar", "uninstall",
        "forçar parada", "forçar interrupção", "force stop",
        "limpar dados", "limpar armazenamento", "clear data", "clear storage",
        "usar bloqueiashorts", "use bloqueiashorts",
        "desativar", "disable", "interromper", "stop"
    )

    /**
     * Telas do sistema onde dá para desativar ou desinstalar o BloqueiaShorts.
     * Com senha definida, o serviço sai dessas telas sempre que elas mostram o nome do app.
     */
    val PROTECTED_SYSTEM_APPS: Set<String> = setOf(
        "com.android.settings",
        "com.miui.securitycenter",
        "com.android.packageinstaller",
        "com.google.android.packageinstaller",
        "com.miui.packageinstaller"
    )
}
