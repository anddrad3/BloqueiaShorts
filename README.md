<p align="center">
  <img src="docs/tela-principal.png" alt="Tela principal do BloqueiaShorts" width="300">
</p>

<h1 align="center">BloqueiaShorts</h1>

<p align="center">
  <b>Menos rolagem, mais vida.</b><br>
  App Android gratuito e de código aberto que bloqueia vídeos curtos:
  YouTube Shorts, Instagram Reels, TikTok, Kwai e os mesmos vídeos abertos no navegador.
</p>

<p align="center">
  <a href="https://github.com/anddrad3/BloqueiaShorts/releases/latest"><b>⬇️ Baixar a versão mais recente</b></a>
  &nbsp;·&nbsp;
  <a href="#guia-completo-de-instalação-passo-a-passo"><b>📖 Guia de instalação</b></a>
</p>

---

## Sumário

- [O que o app faz](#o-que-o-app-faz)
- [Guia completo de instalação (passo a passo)](#guia-completo-de-instalação-passo-a-passo)
  - [Antes de começar](#antes-de-começar)
  - [Etapa 1 — Ativar o modo desenvolvedor](#etapa-1--ativar-o-modo-desenvolvedor)
  - [Etapa 2 — Ligar as opções de depuração](#etapa-2--ligar-as-opções-de-depuração)
  - [Etapa 3 — Baixar e extrair o instalador](#etapa-3--baixar-e-extrair-o-instalador)
  - [Etapa 4 — Rodar o instalador no computador](#etapa-4--rodar-o-instalador-no-computador)
  - [Etapa 5 — Conectar o celular](#etapa-5--conectar-o-celular)
  - [Etapa 6 — Ativar o BloqueiaShorts](#etapa-6--ativar-o-bloqueiashorts)
  - [Etapa 7 — Ajustes do celular (não pule!)](#etapa-7--ajustes-do-celular-não-pule)
  - [Etapa 8 — Testar](#etapa-8--testar)
  - [Etapa 9 — Senha e modo invisível (opcional)](#etapa-9--senha-e-modo-invisível-opcional)
  - [Etapa 10 — Desligar a depuração (opcional)](#etapa-10--desligar-a-depuração-opcional)
- [Instalar sem computador](#instalar-sem-computador)
- [Funcionalidades](#funcionalidades)
- [Problemas e soluções](#problemas-e-soluções)
- [Perguntas frequentes](#perguntas-frequentes)
- [Privacidade](#privacidade)
- [Como funciona por dentro](#como-funciona-por-dentro)
- [Compilar a partir do código](#compilar-a-partir-do-código)
- [Contribuir](#contribuir)
- [Licença](#licença)

---

## O que o app faz

O BloqueiaShorts usa o serviço de **Acessibilidade** do Android para saber qual tela está aberta.
Quando aparece um vídeo curto, ele age sozinho:

| Onde | O que acontece |
|---|---|
| **YouTube Shorts** (app do YouTube) | Aperta "voltar" assim que o player de Shorts abre |
| **Instagram Reels** | Aperta "voltar" ao abrir o player de Reels |
| **TikTok, TikTok Lite, Kwai, Douyin, Kuaishou** | Vai direto para a tela inicial — o app inteiro é bloqueado |
| **No navegador** (Chrome, Firefox, Samsung Internet, Edge, Brave, Opera, DuckDuckGo, navegador da Mi) | Aperta "voltar" ao abrir links de `youtube.com/shorts`, `instagram.com/reels`, `tiktok.com` e `kwai.com` |

Vídeos normais do YouTube **não** são bloqueados — só o formato curto.

> **Testado em:** Redmi Note 11S, Android 13 (MIUI 14) — YouTube Shorts no app e no Chrome.
> Os demais apps e navegadores foram configurados, mas ainda não testados em aparelho.
> Encontrou algo que não bloqueia? [Abra uma issue](https://github.com/anddrad3/BloqueiaShorts/issues).

---

## Guia completo de instalação (passo a passo)

### Por que instalar pelo computador?

A partir do **Android 13**, apps instalados por fora de uma loja (navegador, WhatsApp, Telegram…) ficam com a
Acessibilidade bloqueada pelo aviso **"Configuração restrita"**. Em alguns celulares — principalmente
**Xiaomi, Redmi e Poco** — **não existe** nenhuma opção para liberar.

Instalando **pelo computador**, esse aviso **não aparece**, em nenhum celular. O instalador faz tudo sozinho:
você só precisa preparar o celular uma vez. Leva uns **10 minutos**.

### Antes de começar

Você vai precisar de:

- ✅ Um celular **Android 7.0 ou mais novo**.
- ✅ Um computador **Windows, Mac ou Linux** com internet.
- ✅ **Uma** destas formas de ligar o celular ao computador:
  - **Wi-Fi** (recomendado): celular e computador **na mesma rede Wi-Fi**. Precisa de **Android 11 ou mais novo**.
  - **Cabo USB**: precisa ser um cabo que **transfere dados** (muitos cabos só carregam).
- ✅ **Xiaomi / Redmi / Poco:** estar **logado na conta Mi** e ter um **chip (SIM)** no celular.

> 💡 Descobrir a versão do Android: **Configurações → Sobre o telefone → Versão do Android**.

---

### Etapa 1 — Ativar o modo desenvolvedor

O "modo desenvolvedor" libera um menu escondido chamado **Opções do desenvolvedor**. É seguro e pode ser desligado depois.

1. Abra o app **Configurações** do celular.
2. Entre em **Sobre o telefone** (em alguns aparelhos fica no fim da lista).
3. Toque **7 vezes seguidas** no item indicado na tabela abaixo para a sua marca.
4. O celular pode pedir o **PIN/senha/padrão de desbloqueio** — digite.
5. Aparece a mensagem **"Você agora é um desenvolvedor!"** (ou "O modo de desenvolvedor foi ativado").

| Marca | Onde tocar 7 vezes | Onde ficam as **Opções do desenvolvedor** depois |
|---|---|---|
| **Xiaomi / Redmi / Poco** | Sobre o telefone → **"Versão do MIUI"** ou **"Versão do SO HyperOS"** | Configurações → **Configurações adicionais** → Opções do desenvolvedor |
| **Samsung** | Sobre o telefone → **Informações do software** → **"Número de compilação"** | Configurações → **Opções do desenvolvedor** (no fim da lista) |
| **Motorola** | Sobre o telefone → **"Número da versão"** | Configurações → **Sistema** → Opções do desenvolvedor |
| **Google Pixel** | Sobre o telefone → **"Número da versão"** | Configurações → **Sistema** → Opções do desenvolvedor |
| **Outras marcas** | Sobre o telefone → **"Número da versão"** / **"Número de compilação"** (às vezes dentro de "Informações do software") | Configurações → **Sistema** ou **Configurações adicionais** → Opções do desenvolvedor |

> 💡 Não achou? Use a **busca** no topo das Configurações e procure por **"número da versão"**, **"número de compilação"** ou **"opções do desenvolvedor"**.

---

### Etapa 2 — Ligar as opções de depuração

Abra as **Opções do desenvolvedor** (veja a última coluna da tabela acima) e ligue:

**Se for usar Wi-Fi:**
1. Role até **"Depuração por Wi-Fi"** (às vezes "Depuração sem fio").
2. Ligue a chave. Se perguntar **"Permitir depuração por Wi-Fi nesta rede?"**, marque **"Sempre permitir nesta rede"** e toque em **Permitir**.

**Se for usar cabo:**
1. Ligue **"Depuração USB"** e confirme em **OK**.

**Só para Xiaomi / Redmi / Poco — obrigatório nos dois casos:**
1. Ligue **"Instalar via USB"**.
   - A Xiaomi pode pedir para **entrar na conta Mi** e mostrar **avisos com contagem regressiva** — espere e toque em **Aceitar**.
   - Se aparecer *"Insira um cartão SIM"* ou falha de rede: coloque o chip e **ligue os dados móveis** por um instante (a Xiaomi confere online). Depois pode desligar.
2. Se existir, ligue também **"Depuração USB (Configurações de segurança)"**.

> ⚠️ Sem **"Instalar via USB"**, a Xiaomi recusa a instalação com o erro `INSTALL_FAILED_USER_RESTRICTED`.

---

### Etapa 3 — Baixar e extrair o instalador

1. No **computador**, abra a página de [**Releases**](https://github.com/anddrad3/BloqueiaShorts/releases/latest).
2. Em **Assets**, clique em **`BloqueiaShorts-Instalador-PC.zip`** para baixar.
3. **Extraia** o ZIP (não rode de dentro do ZIP):
   - **Windows:** clique com o botão direito no arquivo → **Extrair tudo…** → **Extrair**.
   - **Mac:** dois cliques no arquivo.
   - **Linux:** `unzip BloqueiaShorts-Instalador-PC.zip`
4. Abra a pasta extraída. Dentro dela devem estar:
   `Instalar-Windows.bat`, `instalar-mac-linux.sh`, `BloqueiaShorts.apk` e `LEIA-ME.txt`.

---

### Etapa 4 — Rodar o instalador no computador

**Windows**
1. Dê **dois cliques** em **`Instalar-Windows.bat`**.
2. Se aparecer a tela azul **"O Windows protegeu o computador"**: clique em **Mais informações** → **Executar assim mesmo**.
   (Isso aparece com qualquer arquivo baixado da internet que não seja assinado por uma empresa.)
3. Abre uma janela preta (Prompt de Comando). Na primeira vez, ele **baixa a ferramenta oficial do Google (adb)**, ~7 MB.

**Mac / Linux**
1. Abra o **Terminal**.
2. Digite `cd ` (com espaço), **arraste a pasta extraída** para dentro do Terminal e aperte **Enter**.
3. Digite `bash instalar-mac-linux.sh` e aperte **Enter**.

O instalador então pergunta:

```
Como o celular vai se conectar?
  1 = Cabo USB
  2 = Wi-Fi
```

Digite **`2`** (Wi-Fi) ou **`1`** (cabo) e aperte **Enter**.

---

### Etapa 5 — Conectar o celular

#### Opção Wi-Fi (digitou `2`)

Na primeira vez, o celular precisa ser **pareado** com o computador com um código.

1. No celular, abra **Opções do desenvolvedor**.
2. Toque no **nome** **"Depuração por Wi-Fi"** — **não só na chave**. Abre uma tela com:
   - o nome do aparelho e **"Endereço IP e porta"** (ex.: `192.168.1.66:41555`) — esse é o endereço de **conexão**;
   - a opção **"Parear dispositivo com código de pareamento"**.
3. Toque em **"Parear dispositivo com código de pareamento"**. Abre uma janelinha com:
   - **"Código de pareamento Wi-Fi"** — 6 números (ex.: `128177`);
   - **"Endereço IP e porta"** — outro endereço, só para o pareamento.
4. **Deixe essa janelinha aberta** e, no computador, digite **apenas os 6 números** e aperte **Enter**.
5. O instalador encontra o celular **sozinho** na rede e mostra `Successfully paired`.

> ⚠️ **Não confunda os dois endereços.** Se o instalador pedir um endereço, ele diz qual:
> o **da janelinha** (pareamento) ou o **da tela "Depuração por Wi-Fi"** (conexão).
> Digitar o endereço errado dá `protocol fault`.

Nas próximas vezes, se o celular continuar pareado, o instalador conecta direto, sem código.

#### Opção cabo USB (digitou `1`)

1. Conecte o cabo no celular e no computador.
2. Desbloqueie o celular. Aparece **"Permitir depuração USB?"** → marque **"Sempre permitir deste computador"** → **OK**.
3. Se não aparecer: puxe a barra de notificações, toque em **"Carregando via USB"** e escolha **"Transferência de arquivos"**.

#### A instalação

Com o celular conectado, o instalador mostra `Celular conectado. Instalando...`.

- **Xiaomi: olhe para o celular agora!** Pode aparecer um aviso perguntando se quer instalar — toque em **Instalar** rápido (ele some em poucos segundos).
- Se já existir uma versão antiga com "Configuração restrita" ou com outra assinatura, o instalador **remove e instala de novo** sozinho.
- No fim aparece **`PRONTO!`** e o BloqueiaShorts **abre sozinho no celular**.

---

### Etapa 6 — Ativar o BloqueiaShorts

1. Na tela do BloqueiaShorts aparece **"Quase lá"** em vermelho. Toque em **Abrir Acessibilidade**.
2. Procure a seção **"Apps baixados"** (ou "Serviços instalados" / "Apps instalados").
3. Toque em **BloqueiaShorts**.
4. Ligue a chave **"Usar BloqueiaShorts"**.
5. Aparece um aviso dizendo que o app poderá **ver e controlar a tela**. Toque em **Permitir**.
   (É o que permite apertar "voltar" por você. Nada é salvo nem enviado — veja [Privacidade](#privacidade).)
6. Volte para o BloqueiaShorts: agora deve aparecer **"Proteção ativa"** em verde. ✅

Instalou pelo computador e mesmo assim apareceu **"Configuração restrita"**? Rode o instalador de novo — ele remove a versão travada.

---

### Etapa 7 — Ajustes do celular (não pule!)

Muitos celulares desligam apps em segundo plano para economizar bateria. No BloqueiaShorts, o cartão
**"Ajustes do celular"** mostra tudo o que precisa e abre a tela certa ao tocar.

Cada item mostra ✅ (ok), ⚠️ (precisa ajustar) ou ⓘ (o app não consegue conferir — toque e veja você mesmo).

| Item | O que fazer na tela que abrir | Por quê |
|---|---|---|
| **Acessibilidade** | Deixe o BloqueiaShorts ligado | Sem isso nada funciona |
| **Bateria sem restrições** | Escolha **"Sem restrições"** (Xiaomi) ou **"Não otimizar"** / **"Irrestrito"** | Impede o sistema de desligar o bloqueio |
| **Inicialização automática** *(Xiaomi)* | Ligue a chave do **BloqueiaShorts** | O bloqueio volta sozinho depois de reiniciar |
| **Janelas pop-up em segundo plano** *(Xiaomi)* | Em **Outras permissões**, permita **"Exibir janelas pop-up enquanto executado em segundo plano"** | Necessário para o código secreto do discador abrir o app |

> Samsung: em *Configurações → Bateria → Limites de uso em segundo plano*, confira se o BloqueiaShorts **não** está em "Apps em suspensão".

---

### Etapa 8 — Testar

1. Abra o **YouTube** e toque em qualquer **Short** (ou na aba Shorts).
2. Em menos de 1 segundo o app deve **voltar sozinho**. ✅
3. Opcional: no Chrome, abra `youtube.com/shorts` — deve voltar também.

---

### Etapa 9 — Senha e modo invisível (opcional)

**Senha** — impede que alguém (ou você mesmo, num impulso) desligue o bloqueio:
1. No app: **Segurança → Definir senha**. Só números, no mínimo 4. Digite duas vezes.
2. **Anote a senha em um lugar seguro.** Ela também é o código para reabrir o app escondido.

**Teste o código ANTES de esconder o ícone:**
1. Saia do BloqueiaShorts e abra o app **Telefone** (discador).
2. Digite `*#*#` + sua senha + `#*#*` — por exemplo, senha `2580` → `*#*#2580#*#*`. Não precisa apertar ligar.
3. O código some do discador e o **BloqueiaShorts abre**. ✅
   Se o código sumir mas o app **não** abrir: falta a permissão **"Janelas pop-up em segundo plano"** ([Etapa 7](#etapa-7--ajustes-do-celular-não-pule)).

**Esconder o ícone** (só depois que o teste acima funcionou):
1. No app: **Modo invisível → Esconder ícone** → digite a senha → leia o aviso → **Esconder agora**.
2. Para reabrir: digite o código no discador. Dica: **"Copiar código de reabertura"** copia o código pronto para colar.
3. Para mostrar o ícone de novo: abra pelo código → **Esconder ícone** (desligar).

> 🛟 **Rede de segurança:** se você digitar o código certo e o celular impedir o app de abrir, **o ícone volta sozinho**
> para a tela inicial.

---

### Etapa 10 — Desligar a depuração (opcional)

A depuração só era necessária para instalar. Por segurança, você pode desligar em **Opções do desenvolvedor**:
**Depuração por Wi-Fi**, **Depuração USB** e **Instalar via USB**. O BloqueiaShorts continua funcionando.
(Para atualizar o app no futuro, ligue de novo e rode o instalador da versão nova.)

---

## Instalar sem computador

Também dá para instalar o **`BloqueiaShorts.apk`** direto no celular, mas no **Android 13+** vai aparecer
**"Configuração restrita"** ao ligar a Acessibilidade.

1. Baixe o `BloqueiaShorts.apk` no celular e abra → **Instalar**. Se pedir, permita **"Instalar apps desconhecidos"**.
   Se o **Play Protect** avisar: **Mais detalhes → Instalar mesmo assim**.
2. Abra o app → **Abrir Acessibilidade** → BloqueiaShorts → tente ligar (o aviso aparece — tudo bem).
3. Libere conforme a marca (o app mostra este passo a passo em **"Apareceu 'Configuração restrita'?"**):

**Samsung, Motorola, Pixel e a maioria:** segure o ícone → **Informações do app** → **⋮** (canto superior direito) →
**"Permitir configurações restritas"** → confirme → volte e ligue a Acessibilidade.

**Xiaomi — tente:** segure o ícone → **Informações do app** → **⋮** → **"Informações do app"** → **⋮** de novo →
**"Permitir configurações restritas"**.

**Xiaomi sem essa opção:** use o [instalador pelo computador](#guia-completo-de-instalação-passo-a-passo) ou o caminho **Shizuku + SAI**:

<details>
<summary><b>Passo a passo: Shizuku + SAI</b></summary>

1. Instale pela Play Store: **Shizuku** e **SAI (Split APKs Installer)**.
2. Faça a [Etapa 1](#etapa-1--ativar-o-modo-desenvolvedor) e ligue **"Depuração por Wi-Fi"** e (Xiaomi) **"Instalar via USB"**.
3. Abra o **Shizuku** → *"Iniciar via depuração sem fio"* → **Parear**. Ele cria uma **notificação** pedindo um código.
4. Vá em **Opções do desenvolvedor → Depuração por Wi-Fi → Parear dispositivo com código de pareamento**.
5. Sem fechar essa tela, puxe as notificações e **digite o código na notificação do Shizuku**.
6. Volte ao Shizuku → **Iniciar**. Deve aparecer *"Shizuku está em execução"*.
7. Abra o **SAI** → ⚙️ **Configurações → Instalador** → **Shizuku** → permita.
8. Desinstale o BloqueiaShorts, se estiver instalado.
9. No SAI: **Instalar APKs → Seletor de arquivos interno** → escolha o `BloqueiaShorts.apk`.
10. Siga a partir da [Etapa 6](#etapa-6--ativar-o-bloqueiashorts).

</details>

---

## Funcionalidades

### 🔒 Senha
Opcional, só números (mínimo 4). Com senha, é preciso digitá-la para **pausar/ativar o bloqueio**,
**esconder o ícone**, **trocar** ou **remover a senha** e abrir telas de ajuste. Depois de 5 senhas erradas, o app espera 1 minuto.
A senha fica salva apenas como *hash* (SHA-256 com *salt*) no próprio celular.

### 🛡️ Proteção contra desligar
Com senha e bloqueio ativo, o app **sai das telas do sistema que servem para desligá-lo**: a tela de informações do
BloqueiaShorts (Desinstalar, Forçar parada, Limpar dados), a chave dele na Acessibilidade e caixas como
"Interromper o BloqueiaShorts?". Listas de apps e "Outras permissões" continuam acessíveis.

**Precisa mexer nessas telas?** Toque em **Pausar bloqueio** e digite a senha — a proteção desliga junto.

### 👻 Modo invisível
Esconde o ícone. Reabra digitando **`*#*#SUASENHA#*#*`** no app Telefone. Detalhes na [Etapa 9](#etapa-9--senha-e-modo-invisível-opcional).

### ⚙️ Ajustes do celular
Mostra o estado da Acessibilidade, bateria, inicialização automática e janelas pop-up, com atalhos para cada tela.

### 🎨 Visual
Material 3 com modo claro e escuro automáticos, animações e uma tela **"Como o app funciona"** (botão ⓘ).

---

## Problemas e soluções

| Problema | Solução |
|---|---|
| `INSTALL_FAILED_USER_RESTRICTED` | Xiaomi: ligue **"Instalar via USB"** ([Etapa 2](#etapa-2--ligar-as-opções-de-depuração)) e aceite o aviso no celular durante a instalação. |
| `protocol fault` / `Pareamento falhou` | Você digitou o endereço de conexão no lugar do de pareamento, ou a janelinha fechou. Abra a janelinha de novo e rode o instalador outra vez. |
| O instalador não acha o celular | Mesma rede Wi-Fi? "Depuração por Wi-Fi" ligada? Tente o cabo. |
| Cabo não é reconhecido | O cabo pode ser só de carga. Use o Wi-Fi ou outro cabo. |
| "O Windows protegeu o computador" | **Mais informações → Executar assim mesmo**. |
| "Configuração restrita" | Instale pelo computador ou veja [Instalar sem computador](#instalar-sem-computador). |
| Parou de bloquear | Abra o app: se estiver "Quase lá", ative a Acessibilidade de novo. Confira a [Etapa 7](#etapa-7--ajustes-do-celular-não-pule). |
| O código some do discador mas o app não abre | Xiaomi: permita **"Janelas pop-up em segundo plano"**. Na versão atual o ícone volta sozinho nesse caso. |
| Escondi o ícone e não consigo abrir o app | Veja [Esqueci a senha / fiquei sem acesso](#perguntas-frequentes). |

---

## Perguntas frequentes

<details>
<summary><b>Se eu reiniciar o celular ou a bateria acabar, continua funcionando?</b></summary>

Sim. O serviço de Acessibilidade volta sozinho quando o celular liga, e a senha e as configurações ficam salvas.
Para isso, faça a [Etapa 7](#etapa-7--ajustes-do-celular-não-pule) (inicialização automática e bateria sem restrições).
</details>

<details>
<summary><b>Esqueci a senha / fiquei sem acesso ao app. E agora?</b></summary>

**Modo de segurança** (sem computador) — nele o BloqueiaShorts não roda:
1. Segure o botão de desligar → **segure o dedo em "Desligar"** até aparecer **"Reiniciar no modo de segurança"** → OK.
2. Vá em **Configurações → Apps → BloqueiaShorts** e:
   - **Desinstale** (para recomeçar do zero), ou
   - Xiaomi: em **Outras permissões**, permita **"Janelas pop-up em segundo plano"** para o código do discador voltar a abrir o app.
3. Reinicie normalmente.

**Pelo computador:** com a depuração ligada, rode `adb uninstall com.bloqueiashorts` e instale de novo.
</details>

<details>
<summary><b>Por que o app precisa de Acessibilidade?</b></summary>

É a única forma de um app saber que um vídeo curto abriu dentro de outro app e apertar "voltar" por você.
O BloqueiaShorts só observa os apps da lista de bloqueio, as telas do sistema protegidas pela senha e o
discador (apenas para reconhecer o código `*#*#...#*#*`). Nada é salvo nem enviado.
</details>

<details>
<summary><b>O app pede alguma permissão?</b></summary>

Nenhuma permissão de internet, contatos, localização, câmera ou armazenamento.
Ele **nem tem acesso à internet** — é impossível enviar dados para fora.
</details>

<details>
<summary><b>O "modo desenvolvedor" deixa meu celular inseguro?</b></summary>

Ter o menu ativado não muda nada. As opções de **depuração** permitem que um computador pareado controle o celular,
por isso recomendamos desligá-las depois de instalar ([Etapa 10](#etapa-10--desligar-a-depuração-opcional)).
</details>

<details>
<summary><b>Dá para bloquear só alguns apps, ou liberar Shorts por um tempo?</b></summary>

Ainda não. Por enquanto existe o botão **Pausar bloqueio** (com senha). Sugestões nas
[issues](https://github.com/anddrad3/BloqueiaShorts/issues).
</details>

<details>
<summary><b>Funciona no Android 15 ou 16?</b></summary>

Sim. No Android 15+ a "Configuração restrita" ficou mais rígida, então prefira a instalação pelo computador.
</details>

<details>
<summary><b>Por que não está na Play Store?</b></summary>

Publicar na Play Store custa US$ 25 e o Google é muito rigoroso com apps que usam Acessibilidade.
Por enquanto a distribuição é gratuita, aqui pelo GitHub.
</details>

---

## Privacidade

- ❌ Sem internet (o app nem declara a permissão).
- ❌ Sem anúncios, rastreadores ou análise de uso.
- ❌ Nenhum dado salvo além das suas configurações (bloqueio ligado/desligado e o *hash* da senha).
- ✅ Código 100% aberto — você pode conferir tudo neste repositório.

---

## Como funciona por dentro

| Arquivo | Função |
|---|---|
| [`Rules.kt`](app/src/main/java/com/bloqueiashorts/Rules.kt) | **Tudo o que é bloqueado**: IDs das telas de vídeo curto, apps bloqueados inteiros, navegadores, padrões de endereço e textos das telas protegidas. |
| [`ShortsBlockerService.kt`](app/src/main/java/com/bloqueiashorts/ShortsBlockerService.kt) | O serviço de Acessibilidade: detecta o vídeo curto e age; protege as configurações; lê o código do discador. |
| [`MainActivity.kt`](app/src/main/java/com/bloqueiashorts/MainActivity.kt) | A tela do app (status, ajustes do celular, senha, modo invisível, ajudas). |
| [`DeviceSetup.kt`](app/src/main/java/com/bloqueiashorts/DeviceSetup.kt) | Estado e atalhos de bateria, inicialização automática e pop-up em segundo plano. |
| [`Prefs.kt`](app/src/main/java/com/bloqueiashorts/Prefs.kt) | Configurações salvas e a senha (SHA-256 com *salt*). |
| [`IconHider.kt`](app/src/main/java/com/bloqueiashorts/IconHider.kt) | Esconde/mostra o ícone ligando e desligando um `activity-alias`. |
| [`accessibility_service_config.xml`](app/src/main/res/xml/accessibility_service_config.xml) | Lista de apps que o serviço observa. |
| [`distribuicao/`](distribuicao) | Instaladores pelo computador (`.bat` e `.sh`). |

**Detecção:**
- **Player dentro de apps (YouTube, Instagram):** procura views com IDs específicos (ex.: `reel_recycler`)
  que ocupem pelo menos 60% da altura da tela — miniaturas de Shorts no feed são ignoradas.
- **Navegadores:** lê o texto da barra de endereço (pelo ID conhecido ou procurando no topo da tela).
- **Robustez:** se o "voltar" não fechar o vídeo, tenta de novo a cada ~0,9 s; após 3 tentativas, vai para a tela inicial.
  Uma verificação periódica pega vídeos que ficam tocando sem gerar eventos.

**Instalador pelo computador:** baixa o `platform-tools` oficial do Google, descobre o celular por mDNS
(`adb mdns services`), pareia com o código, escolhe um único aparelho (`ANDROID_SERIAL`), remove versões travadas
ou com outra assinatura e instala com `adb install`.

---

## Compilar a partir do código

Requisitos: **JDK 17** e **Android SDK** (plataforma 35) — o jeito mais fácil é instalar o [Android Studio](https://developer.android.com/studio).

```bash
git clone https://github.com/anddrad3/BloqueiaShorts.git
cd BloqueiaShorts
./gradlew assembleRelease        # Windows: gradlew assembleRelease
```

O APK sai em `app/build/outputs/apk/release/app-release.apk`.

**Assinatura:** o build procura a chave oficial em `~/.bloqueiashorts/keystore.properties`
(`storeFile`, `storePassword`, `keyAlias`, `keyPassword`). Sem esse arquivo, o release é assinado com a chave
de depuração — funciona, mas não atualiza por cima da versão oficial.

**Pacote de distribuição:** copie o APK como `BloqueiaShorts.apk` para junto dos arquivos de
[`distribuicao/`](distribuicao) e compacte tudo em `BloqueiaShorts-Instalador-PC.zip`.

> Pastas com acentos (ex.: "Área de Trabalho") funcionam: o `gradle.properties` inclui `android.overridePathCheck=true`.

---

## Contribuir

Contribuições são muito bem-vindas!

- **O YouTube/Instagram mudou e parou de bloquear?** Descubra os novos IDs das views
  (Android Studio → *Layout Inspector*, ou `adb shell uiautomator dump`) e atualize [`Rules.kt`](app/src/main/java/com/bloqueiashorts/Rules.kt).
- **Testou em outro celular/app?** Conte nas [issues](https://github.com/anddrad3/BloqueiaShorts/issues) o que funcionou.
- **Ideias:** Facebook Reels, Pinterest, Snapchat Spotlight, horários de bloqueio, estatísticas.

Ao adicionar um app novo em `Rules.kt`, inclua o pacote também em `accessibility_service_config.xml`.

---

## Licença

Distribuído sob a **GNU General Public License v3.0** — veja [`LICENSE`](LICENSE).
Você pode usar, estudar, modificar e redistribuir, desde que versões modificadas também sejam abertas sob a GPL-3.0.

*YouTube, Instagram, TikTok e Kwai são marcas de seus respectivos donos. Este projeto não tem relação com essas empresas.*
