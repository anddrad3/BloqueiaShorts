#!/usr/bin/env bash
# Desinstalador de emergência do BloqueiaShorts (Mac e Linux).
# Remove o app pelo computador, mesmo com senha ou ícone escondido.
set -u
cd "$(dirname "$0")"

echo
echo "=========================================="
echo "  Desinstalador de emergência do BloqueiaShorts"
echo "=========================================="
echo


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
  c. Ligue "Depuração USB" (cabo) ou "Depuração por Wi-Fi".

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

ANDROID_SERIAL=$("$ADB" devices | awk 'NR>1 && $2=="device" {print $1; exit}')
export ANDROID_SERIAL
echo
echo "[3/4] Celular conectado."
if ! "$ADB" shell pm list packages com.bloqueiashorts | grep -qx 'package:com.bloqueiashorts'; then
  echo "O BloqueiaShorts não está instalado neste celular. Nada a fazer."
  exit 0
fi
read -r -p "Desinstalar o BloqueiaShorts agora? Digite S e aperte Enter: " CONFIRMA
if [ "$CONFIRMA" != "S" ] && [ "$CONFIRMA" != "s" ]; then
  echo "Cancelado. Nada foi alterado."
  exit 0
fi
echo "[4/4] Desinstalando..."
"$ADB" uninstall com.bloqueiashorts || { echo "[ERRO] Não foi possível desinstalar."; exit 1; }
echo
echo "PRONTO! O BloqueiaShorts foi removido. Para instalar de novo, use o instalar-mac-linux.sh."
