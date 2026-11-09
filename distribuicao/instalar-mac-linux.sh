#!/usr/bin/env bash
# Instalador do BloqueiaShorts para Mac e Linux.
# Instala o app pelo computador (adb), assim o Android não mostra
# o aviso "Configuração restrita" na Acessibilidade.
set -u
cd "$(dirname "$0")"

echo
echo "=========================================="
echo "       Instalador do BloqueiaShorts"
echo "=========================================="
echo

if [ ! -f BloqueiaShorts.apk ]; then
  echo "[ERRO] O arquivo BloqueiaShorts.apk não está nesta pasta. Extraia o ZIP inteiro."
  exit 1
fi

# 1. adb oficial do Google
ADB="./platform-tools/adb"
if [ ! -x "$ADB" ]; then
  case "$(uname -s)" in
    Darwin) OS=darwin ;;
    *) OS=linux ;;
  esac
  echo "[1/4] Baixando a ferramenta oficial do Google (adb)..."
  curl -fsSL -o platform-tools.zip "https://dl.google.com/android/repository/platform-tools-latest-$OS.zip" \
    && unzip -q -o platform-tools.zip && rm -f platform-tools.zip
  if [ ! -x "$ADB" ]; then
    echo "[ERRO] Não foi possível baixar o adb. Verifique a internet."
    exit 1
  fi
fi
echo "[1/4] Ferramenta adb pronta."
"$ADB" start-server >/dev/null 2>&1

# 2. Preparar o celular
cat <<'TXT'

[2/4] Prepare o celular:
  a. Configurações > Sobre o telefone > toque 7 vezes em "Número da versão"
     (Xiaomi: "Versão MIUI/HyperOS").
  b. Abra as "Opções do desenvolvedor" (Xiaomi: em Configurações adicionais).
  c. Ligue "Depuração USB". Na Xiaomi, ligue também "Instalar via USB".

Como o celular vai se conectar?
  1 = Cabo USB (o cabo precisa transferir dados)
  2 = Wi-Fi (mesma rede, Android 11 ou mais novo)
TXT
read -r -p "Digite 1 ou 2: " MODO

conectado() { "$ADB" devices | grep -q "device$"; }

mdns() { "$ADB" mdns services 2>/dev/null | grep "$1" | awk '{print $3}' | head -1; }
conectar_mdns() { local a; a=$(mdns _adb-tls-connect); [ -n "$a" ] && "$ADB" connect "$a" >/dev/null 2>&1; }

if [ "$MODO" = "2" ]; then
  echo
  echo "Procurando o celular na rede..."
  conectar_mdns
fi

if [ "$MODO" = "2" ] && ! conectado; then
  echo
  echo "No celular: Opções do desenvolvedor > toque no nome 'Depuração por Wi-Fi'"
  echo "> Parear dispositivo com código de pareamento. Deixe a janelinha ABERTA."
  read -r -p "Código de 6 dígitos: " CODIGO
  PAREAR=""
  for _ in $(seq 1 10); do PAREAR=$(mdns _adb-tls-pairing); [ -n "$PAREAR" ] && break; sleep 1; done
  if [ -z "$PAREAR" ]; then
    read -r -p "Endereço IP e porta que aparece NA JANELINHA do código (ex 192.168.0.10:37123): " PAREAR
  fi
  "$ADB" pair "$PAREAR" "$CODIGO" || { echo "[ERRO] Pareamento falhou. Abra a janelinha de novo e repita."; exit 1; }
  echo "Pareado. Conectando..."
  sleep 3
  conectar_mdns
  if ! conectado; then
    read -r -p "Endereço IP e porta da tela 'Depuração por Wi-Fi', NÃO o da janelinha (ex 192.168.0.10:41555): " CONECTAR
    "$ADB" connect "$CONECTAR"
    conectado || { echo "[ERRO] Não foi possível conectar."; exit 1; }
  fi
elif [ "$MODO" != "2" ]; then
  echo
  echo "Conecte o cabo e aceite 'Permitir depuração USB?' no celular. Aguardando..."
  "$ADB" wait-for-usb-device
fi

# 3. Instalar
# O mesmo celular pode aparecer duas vezes (IP e nome na rede): usa só o primeiro.
ANDROID_SERIAL=$("$ADB" devices | awk 'NR>1 && $2=="device" {print $1; exit}')
export ANDROID_SERIAL
echo
echo "[3/4] Celular conectado. Instalando o BloqueiaShorts..."
cat <<'TXT'

##########################################################
##   OLHE PARA O CELULAR AGORA!                         ##
##   Vai aparecer um aviso com o botão "INSTALAR".       ##
##   TOQUE EM "INSTALAR" no celular — o aviso some       ##
##   sozinho em poucos segundos.                        ##
##   Se não tocar, a instalação é cancelada.            ##
##########################################################

TXT
sleep 4
if "$ADB" shell cmd appops get com.bloqueiashorts ACCESS_RESTRICTED_SETTINGS 2>/dev/null | grep -q ignore; then
  echo "Removendo a versão antiga que estava com 'Configuração restrita'..."
  "$ADB" uninstall com.bloqueiashorts >/dev/null 2>&1
fi
SAIDA=$("$ADB" install -r BloqueiaShorts.apk 2>&1); OK=$?; echo "$SAIDA"
if [ $OK -ne 0 ] && echo "$SAIDA" | grep -q INSTALL_FAILED_UPDATE_INCOMPATIBLE; then
  echo "Removendo uma versão antiga com assinatura diferente..."
  "$ADB" uninstall com.bloqueiashorts >/dev/null 2>&1
  SAIDA=$("$ADB" install -r BloqueiaShorts.apk 2>&1); OK=$?; echo "$SAIDA"
fi
if [ $OK -ne 0 ]; then
  if echo "$SAIDA" | grep -q INSTALL_FAILED_USER_RESTRICTED; then
    echo "O celular RECUSOU a instalação. Xiaomi: ligue 'Instalar via USB' (conta Mi + chip) e aceite o aviso no celular."
  fi
  echo
  echo "[ERRO] A instalação falhou."
  echo " - Xiaomi: ligue 'Instalar via USB' e aceite o aviso no celular."
  echo " - Se apareceu um aviso no celular, toque em 'Instalar'."
  exit 1
fi

# 4. Abrir
echo
echo "[4/4] Abrindo o app no celular..."
"$ADB" shell am start -n com.bloqueiashorts/.LauncherAlias >/dev/null 2>&1

cat <<'TXT'

==========================================
 PRONTO! Agora, no celular:
 "Abrir Acessibilidade" > "Apps baixados" > BloqueiaShorts > ligue a chave.
==========================================
TXT
