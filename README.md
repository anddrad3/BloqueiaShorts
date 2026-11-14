<p align="center">
  <img src="docs/tela-principal.png" alt="Tela principal do BloqueiaShorts" width="300">
</p>

<h1 align="center">BloqueiaShorts</h1>

<p align="center">
  <b>Menos rolagem, mais vida.</b><br>
  App Android gratuito e de código aberto que bloqueia os <b>Shorts do YouTube</b>,
  sem modo desenvolvedor, sem computador e sem atrapalhar os apps de banco.
</p>

<p align="center">
  <a href="https://github.com/anddrad3/BloqueiaShorts/releases/latest"><b>⬇️ Baixar a versão mais recente</b></a>
  &nbsp;·&nbsp;
  <a href="#instalação"><b>📖 Instalação</b></a>
</p>

---

> [!WARNING]
> **Usou a versão 1.x? Leia isto primeiro.**
> As versões 1.0 a 1.2 eram instaladas com o **modo desenvolvedor e a depuração USB/Wi-Fi ligados** e
> vigiavam o discador e as Configurações. Com isso, **vários apps de banco pararam de abrir**.
> Veja [como deixar o celular normal de novo](#meu-banco-parou-de-abrir-depois-da-versão-1x) e
> [por que o app mudou](#por-que-o-app-mudou-na-versão-20).

## Sumário

- [O que o app faz](#o-que-o-app-faz)
- [Por que o app mudou na versão 2.0](#por-que-o-app-mudou-na-versão-20)
- [Instalação](#instalação)
- [Esconder o ícone (pelo próprio celular)](#esconder-o-ícone-pelo-próprio-celular)
- [Funcionalidades](#funcionalidades)
- [Apps de banco](#apps-de-banco)
- [Problemas e soluções](#problemas-e-soluções)
- [Privacidade](#privacidade)
- [Como funciona por dentro](#como-funciona-por-dentro)
- [Compilar a partir do código](#compilar-a-partir-do-código)
- [Contribuir](#contribuir)
- [Licença](#licença)

---

## O que o app faz

Quando você abre um Short no app do YouTube, o BloqueiaShorts aperta **"voltar"** na hora e mostra
"Shorts bloqueado". O resto do YouTube continua normal: vídeos comuns, busca, inscrições, playlists.

- ✅ Bloqueia o player de Shorts no app do YouTube.
- ✅ Senha opcional para ninguém pausar o bloqueio.
- ✅ Funciona com o ícone escondido pelo próprio celular.
- ❌ Não observa nenhum outro app: Instagram, TikTok, navegador, banco, WhatsApp... o app nem enxerga.
- ❌ Não lê o que você digita, não coleta nada e não usa internet.

---

## Por que o app mudou na versão 2.0

A versão 1.x funcionava, mas **quebrava os apps de banco**, e de um jeito difícil de entender para quem
instalou. Foram três motivos:

| O que a 1.x fazia | Por que o banco recusava | O que a 2.0 faz |
|---|---|---|
| Pedia **modo desenvolvedor + depuração USB/Wi-Fi** para instalar pelo computador | Muitos bancos se recusam a abrir com as Opções do desenvolvedor ligadas | **Instala como qualquer APK.** Para liberar a Acessibilidade, usa o menu "Permitir configurações restritas" do próprio Android |
| Observava **discador, Configurações, instaladores, Instagram, TikTok, Kwai e 11 navegadores**, e lia **o que era digitado** no discador | É o mesmo comportamento dos golpes de "mão fantasma", que leem a tela e impedem a desinstalação | **Observa só o app do YouTube.** Não lê texto digitado, não faz gestos, não desenha por cima de outros apps |
| Escondia o próprio ícone e **impedia a desinstalação** quando havia senha | "Não deixa desinstalar" é outra característica típica de vírus | **O ícone é escondido pelo próprio celular**, e o app pode ser desinstalado normalmente |

A conclusão é simples: **um app que lê a tela não pode também se comportar como vírus.** A 2.0 pede o
mínimo que dá para pedir. A troca é que fica um pouco mais fácil burlar o bloqueio (desligando em
Acessibilidade ou desinstalando), mas o seu celular continua funcionando.

**O que saiu:** Instagram Reels, TikTok, Kwai, bloqueio no navegador, o código secreto `*#*#senha#*#*`,
o modo invisível dentro do app, a proteção contra desligar e os instaladores pelo computador
(`Instalar-Windows.bat` / `.sh`).

---

## Instalação

Não precisa de computador nem de modo desenvolvedor.

1. **Baixe o APK** em [Releases](https://github.com/anddrad3/BloqueiaShorts/releases/latest)
   (`BloqueiaShorts.apk`) direto no celular.
2. **Instale.** O Android vai pedir para permitir instalar apps do navegador ou do gerenciador de arquivos: permita.
3. **Abra o BloqueiaShorts** e toque em **"Abrir Acessibilidade"** → **Apps baixados** (ou *Serviços instalados*)
   → **BloqueiaShorts** → ligue a chave.
4. **Apareceu "Configuração restrita"?** (Android 13 ou mais novo.) É normal para app de fora da loja:
   - Volte ao BloqueiaShorts → **"Apareceu Configuração restrita?"** → **Abrir informações do app**.
   - Toque nos **⋮** (canto superior direito) → **"Permitir configurações restritas"**.
   - **Xiaomi/Redmi/Poco:** nos ⋮, toque antes em **"Informações do app"** (abre a tela original do Android)
     e, nela, nos ⋮ de novo → **"Permitir configurações restritas"**.
   - Volte em Acessibilidade e ligue a chave.
5. **Ajustes do celular:** na tela do app, deixe a **bateria "Sem restrições"** e, na Xiaomi, ligue a
   **"Inicialização automática"**. Sem isso, o sistema pode desligar o bloqueio sozinho.
6. **Teste:** abra o YouTube e toque num Short. Ele deve fechar na hora.

> Atualizando da versão 1.x? **Desinstale a versão antiga primeiro**, siga a
> [limpeza do celular](#meu-banco-parou-de-abrir-depois-da-versão-1x) e depois instale a 2.0.

---

## Esconder o ícone (pelo próprio celular)

O BloqueiaShorts não esconde mais o próprio ícone. Quem esconde é o celular, que já tem essa opção, e o
bloqueio continua funcionando normalmente. Os nomes dos menus mudam um pouco de versão para versão.

| Celular | Como esconder |
|---|---|
| **Samsung (One UI)** | Abra a gaveta de apps → **⋮** → **Configurações** → **"Ocultar apps nas telas Início e Aplicativos"** → marque o BloqueiaShorts → *Concluído*. |
| **Xiaomi / Redmi / Poco (MIUI, HyperOS)** | **Configurações → Apps → Bloqueio de apps** (em algumas versões: app *Segurança* → *Bloqueio de apps*) → ⚙ ou aba **"Ocultar apps"** → ligue o BloqueiaShorts. Para ver os apps ocultos: na tela inicial, afaste dois dedos (gesto de zoom) e digite a senha. |
| **Oppo / Realme / OnePlus (ColorOS)** | **Configurações → Privacidade → Ocultar apps** → ligue o BloqueiaShorts. O sistema pede um código de acesso, que se digita no app Telefone. |
| **Motorola, Pixel e outros** | Muitos não têm a opção no launcher padrão. Use um launcher da Play Store que oculta apps (Nova Launcher, Niagara, Smart Launcher) ou coloque o app numa pasta com outro nome. |

**Para abrir o app com o ícone escondido, em qualquer celular:**
**Configurações → Acessibilidade → Apps baixados → BloqueiaShorts → Configurações.**
Esse botão "Configurações" é um recurso oficial do Android e abre o app direto.

> ⚠️ **Não coloque o app no "Espaço privado" (Android 15) nem na "Pasta Segura" (Samsung).**
> Lá dentro os apps param de rodar quando o espaço é trancado, e o bloqueio para junto.

---

## Funcionalidades

- **🛡️ Bloqueio de Shorts:** detecta o player de Shorts em tela cheia (as miniaturas no feed são ignoradas)
  e volta. Se "voltar" não resolver em 3 tentativas, vai para a tela inicial.
- **⏸️ Pausar:** libera os Shorts sem desligar o app.
- **🔒 Senha (opcional):** pedida para pausar ou ativar o bloqueio, desligar para o banco e remover a senha.
  Depois de 5 erros seguidos, espera 1 minuto.
- **🏦 Desligar para usar app de banco:** só se o seu banco reclamar (veja abaixo).
- **⚙️ Ajustes do celular:** mostra se a bateria e a inicialização automática (Xiaomi) estão certas e leva até a tela.

---

## Apps de banco

A 2.0 foi feita para os bancos não confundirem o app com golpe: ela não precisa de modo desenvolvedor,
observa só o YouTube e não lê o que é digitado. **Mesmo assim, não dá para garantir todos os bancos.**
Alguns recusam *qualquer* serviço de Acessibilidade que não veio da Play Store.

Se o seu banco recusar: abra o BloqueiaShorts → **"Desligar para usar app de banco"**. Depois de usar o banco,
toque em **"Religar a proteção"**.

### Meu banco parou de abrir depois da versão 1.x

1. **Configurações → Acessibilidade → Apps baixados → BloqueiaShorts → desligar.**
2. **Desinstale o BloqueiaShorts 1.x.** Com o ícone escondido: Configurações → Apps → BloqueiaShorts → Desinstalar.
   Se a proteção não deixar, desligue antes a Acessibilidade (passo 1) ou reinicie em **modo de segurança**.
3. **Opções do desenvolvedor → "Revogar autorizações de depuração USB"** e depois **desligue a chave geral no topo**
   da tela. Isso desliga a depuração USB e Wi-Fi. Na Xiaomi, desligue também "Instalar via USB" antes.
4. **Reinicie o celular.**
5. Se o banco continuar recusando: **Configurações → Apps → [banco] → Armazenamento → Limpar dados**
   (ou reinstale o app do banco pela Play Store) e **cadastre o aparelho de novo** no app do banco.
6. Se aparecer "dispositivo bloqueado" ou "conta em análise", **ligue para o banco**. Nesse caso o bloqueio
   está no servidor deles, e só eles podem tirar. Explique que era um app de acessibilidade que você já removeu.

---

## Problemas e soluções

| Problema | Solução |
|---|---|
| **Parou de bloquear depois de um tempo** | Bateria "Sem restrições" e, na Xiaomi, "Inicialização automática". Se continuar, desligue e ligue o BloqueiaShorts em Acessibilidade. |
| **Não aparece a opção "Permitir configurações restritas"** | Tente ligar o app em Acessibilidade **uma vez antes** (a opção só aparece depois do aviso). Na Xiaomi, use o caminho pela tela original "Informações do app". |
| **O YouTube atualizou e os Shorts não fecham mais** | O YouTube mudou o nome interno do player. Abra uma [issue](https://github.com/anddrad3/BloqueiaShorts/issues): a correção é trocar os IDs em [`Rules.kt`](app/src/main/java/com/bloqueiashorts/Rules.kt). |
| **Shorts no navegador (Chrome) não são bloqueados** | É de propósito: observar o navegador significaria ler todos os sites que você visita. |
| **Esqueci a senha** | Configurações → Acessibilidade → desligue o BloqueiaShorts, ou desinstale e instale de novo. |
| **Não acho o app depois de esconder** | Configurações → Acessibilidade → Apps baixados → BloqueiaShorts → **Configurações**. |

---

## Privacidade

- ❌ Sem internet (o app nem declara a permissão).
- ❌ Sem anúncios, rastreadores ou análise de uso.
- ❌ Não observa nenhum app além do YouTube, e do YouTube só procura o player de Shorts.
- ❌ Nenhum dado salvo além das suas configurações (bloqueio ligado/desligado e o *hash* da senha).
- ✅ Código 100% aberto: dá para conferir tudo neste repositório.

---

## Como funciona por dentro

| Arquivo | Função |
|---|---|
| [`Rules.kt`](app/src/main/java/com/bloqueiashorts/Rules.kt) | Pacote do YouTube e IDs das views do player de Shorts. |
| [`ShortsBlockerService.kt`](app/src/main/java/com/bloqueiashorts/ShortsBlockerService.kt) | O serviço de Acessibilidade: detecta o player de Shorts e aperta "voltar". |
| [`accessibility_service_config.xml`](app/src/main/res/xml/accessibility_service_config.xml) | O que o serviço pode ver: **só** `com.google.android.youtube`, só eventos de mudança de tela. O `settingsActivity` cria o botão "Configurações" que abre o app escondido. |
| [`MainActivity.kt`](app/src/main/java/com/bloqueiashorts/MainActivity.kt) | A tela do app (status, ajustes, senha, ajudas). |
| [`DeviceSetup.kt`](app/src/main/java/com/bloqueiashorts/DeviceSetup.kt) | Estado e atalhos de bateria e inicialização automática (Xiaomi). |
| [`Prefs.kt`](app/src/main/java/com/bloqueiashorts/Prefs.kt) | Configurações salvas e a senha (SHA-256 com *salt*). |

**Detecção:** procura views com IDs do player de Shorts (ex.: `reel_recycler`) que ocupem pelo menos 60% da
altura da tela. Se o "voltar" não fechar, tenta de novo a cada ~0,9 s; após 3 tentativas, vai para a tela inicial.
Uma verificação periódica pega vídeos que ficam tocando sem gerar eventos.

**Princípio de projeto:** o serviço pede o mínimo. Antes de adicionar qualquer app em `packageNames`,
qualquer tipo de evento ou qualquer capacidade (gestos, filtro de teclas, sobreposição), lembre que é
justamente isso que faz os bancos recusarem o app.

---

## Compilar a partir do código

Requisitos: **JDK 17** e **Android SDK** (plataforma 35). O jeito mais fácil é instalar o [Android Studio](https://developer.android.com/studio).

```bash
git clone https://github.com/anddrad3/BloqueiaShorts.git
cd BloqueiaShorts
./gradlew assembleRelease        # Windows: gradlew assembleRelease
```

O APK sai em `app/build/outputs/apk/release/app-release.apk`.

**Assinatura:** o build procura a chave oficial em `~/.bloqueiashorts/keystore.properties`
(`storeFile`, `storePassword`, `keyAlias`, `keyPassword`). Sem esse arquivo, o release é assinado com a chave
de depuração: funciona, mas não atualiza por cima da versão oficial.

> Pastas com acentos (ex.: "Área de Trabalho") funcionam: o `gradle.properties` inclui `android.overridePathCheck=true`.

---

## Contribuir

- **O YouTube mudou e parou de bloquear?** Descubra os novos IDs das views (Android Studio → *Layout Inspector*)
  e atualize [`Rules.kt`](app/src/main/java/com/bloqueiashorts/Rules.kt).
- **Testou com o seu banco?** Conte nas [issues](https://github.com/anddrad3/BloqueiaShorts/issues) se ele
  aceitou ou recusou a 2.0. Isso ajuda outras pessoas.
- **Próximo passo:** publicar na Play Store. Apps vindos da loja são mais aceitos pelos bancos.

---

## Licença

Distribuído sob a **GNU General Public License v3.0**. Veja [`LICENSE`](LICENSE).
Você pode usar, estudar, modificar e redistribuir, desde que versões modificadas também sejam abertas sob a GPL-3.0.

*YouTube é marca do Google. Este projeto não tem relação com o Google.*
