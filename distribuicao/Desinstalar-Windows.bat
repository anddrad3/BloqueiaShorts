@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"
title Desinstalador de emergencia - BloqueiaShorts

echo.
echo  ==========================================
echo     Desinstalador de emergencia do BloqueiaShorts
echo  ==========================================
echo.
echo  Remove o BloqueiaShorts do celular pelo computador - funciona
echo  mesmo com senha, icone escondido ou protecao ativa.
echo  Use se esqueceu a senha ou ficou sem acesso ao app.
echo.


rem ---------------------------------------------------------------
rem  1. Ferramenta oficial do Google - adb
rem ---------------------------------------------------------------
set "ADB=%~dp0platform-tools\adb.exe"
if exist "%ADB%" goto temadb
echo  [1/4] Baixando a ferramenta oficial do Google - adb, cerca de 7 MB...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri 'https://dl.google.com/android/repository/platform-tools-latest-windows.zip' -OutFile 'platform-tools.zip'; Expand-Archive -Force 'platform-tools.zip' '.'; Remove-Item 'platform-tools.zip'"
if not exist "%ADB%" (
    echo  [ERRO] Nao foi possivel baixar o adb. Verifique a internet e tente de novo.
    goto fim
)
:temadb
echo  [1/4] Ferramenta adb pronta.
"%ADB%" start-server >nul 2>&1
echo.

rem ---------------------------------------------------------------
rem  2. Preparar o celular
rem ---------------------------------------------------------------
echo  [2/4] Prepare o celular:
echo.
echo    a. Configuracoes ^> Sobre o telefone ^> toque 7 vezes em
echo       "Numero da versao" - na Xiaomi: "Versao MIUI" ou "HyperOS".
echo    b. Abra as "Opcoes do desenvolvedor" - na Xiaomi ficam em
echo       Configuracoes adicionais.
echo.
echo  Como o celular vai se conectar?
echo    1 = Cabo USB - ligue "Depuracao USB". O cabo precisa transferir dados.
echo    2 = Wi-Fi    - ligue "Depuracao por Wi-Fi". Mesma rede, Android 11+.
echo.
set "MODO="
set /p MODO=  Digite 1 ou 2 e aperte Enter:
if "%MODO%"=="2" goto wifi

:usb
echo.
echo  Conecte o cabo. No celular, aceite "Permitir depuracao USB?".
echo  Aguardando o celular...
"%ADB%" wait-for-usb-device
goto conectado

:wifi
echo.
echo  Procurando o celular na rede...
call :conectar_mdns 6
call :tem_celular
if not errorlevel 1 goto conectado

echo.
echo  O celular precisa ser pareado com este computador - so na primeira vez.
echo.
echo  No celular: Opcoes do desenvolvedor ^> toque no NOME "Depuracao por Wi-Fi"
echo  ^> "Parear dispositivo com codigo de pareamento".
echo  Vai abrir uma janelinha com um CODIGO de 6 numeros. Deixe ela ABERTA.
echo.

:pedir_codigo
set "CODIGO="
set /p CODIGO=  Digite so o CODIGO de 6 numeros:
echo %CODIGO%| findstr /r /x "[0-9][0-9][0-9][0-9][0-9][0-9]" >nul
if errorlevel 1 (
    echo  Isso nao e um codigo de 6 numeros. Digite apenas os 6 numeros da janelinha.
    goto pedir_codigo
)

rem  Descobre sozinho o endereco de pareamento - o da janelinha - esperando ate 30 s.
echo  Procurando a janelinha de pareamento na rede...
set "PAREAR="
for /l %%i in (1,1,30) do if not defined PAREAR (
    for /f "usebackq tokens=3" %%a in (`call "%ADB%" mdns services ^| findstr /c:"_adb-tls-pairing"`) do set "PAREAR=%%a"
    if not defined PAREAR timeout /t 1 /nobreak >nul
)
if defined PAREAR goto parear

rem  Nao achou: pede o endereco, recusando o endereco de conexao por engano.
set "CONEXAO="
for /f "usebackq tokens=3" %%a in (`call "%ADB%" mdns services ^| findstr /c:"_adb-tls-connect"`) do set "CONEXAO=%%a"
echo.
echo  Nao encontrei a janelinha automaticamente.
echo  Olhe NA JANELINHA do codigo: ela mostra "Endereco IP e porta".
:pedir_endereco
set /p PAREAR=  Digite esse endereco, ex 192.168.0.10:37123:
if defined CONEXAO if "%PAREAR%"=="%CONEXAO%" (
    echo  Esse e o endereco de CONEXAO, da tela anterior. Use o que aparece NA JANELINHA do codigo.
    set "PAREAR="
    goto pedir_endereco
)

:parear
echo  Pareando com %PAREAR%...
"%ADB%" pair %PAREAR% %CODIGO%
if errorlevel 1 (
    echo  [ERRO] Pareamento falhou. A janelinha fechou ou o codigo mudou.
    echo  Abra a janelinha de novo e rode o instalador outra vez.
    goto fim
)

echo.
echo  Pareado. Conectando...
call :conectar_mdns 15
call :tem_celular
if not errorlevel 1 goto conectado

echo.
echo  Digite o "Endereco IP e porta" da tela "Depuracao por Wi-Fi"
echo  - fica embaixo do nome do aparelho, NAO e o da janelinha.
set "CONECTAR="
set /p CONECTAR=  Ex 192.168.0.10:41555:
"%ADB%" connect %CONECTAR%
call :tem_celular
if errorlevel 1 (
    echo  [ERRO] Nao foi possivel conectar ao celular.
    goto fim
)

:conectado
set "ANDROID_SERIAL="
for /f "usebackq skip=1 tokens=1,2" %%a in (`call "%ADB%" devices`) do (
    if "%%b"=="device" if not defined ANDROID_SERIAL set "ANDROID_SERIAL=%%a"
)
echo.
echo  [3/4] Celular conectado.
"%ADB%" shell pm list packages com.bloqueiashorts | findstr /x /c:"package:com.bloqueiashorts" >nul
if errorlevel 1 (
    echo  O BloqueiaShorts nao esta instalado neste celular. Nada a fazer.
    goto fim
)
echo.
set "CONFIRMA="
set /p CONFIRMA=  Desinstalar o BloqueiaShorts agora? Digite S e aperte Enter: 
if /i not "%CONFIRMA%"=="S" (
    echo  Cancelado. Nada foi alterado.
    goto fim
)
echo  [4/4] Desinstalando...
"%ADB%" uninstall com.bloqueiashorts
if errorlevel 1 (
    echo  [ERRO] Nao foi possivel desinstalar.
    goto fim
)
echo.
echo  ==========================================
echo   PRONTO! O BloqueiaShorts foi removido.
echo   Para instalar de novo, use o Instalar-Windows.bat.
echo  ==========================================
goto fim

rem ---------------------------------------------------------------
rem  Funcoes
rem ---------------------------------------------------------------
:conectar_mdns
rem  Tenta, por ate %1 segundos, conectar no endereco que o celular anuncia na rede.
for /l %%i in (1,1,%1) do (
    "%ADB%" devices | findstr /r /c:"device$" >nul && exit /b 0
    for /f "usebackq tokens=3" %%a in (`call "%ADB%" mdns services ^| findstr /c:"_adb-tls-connect"`) do (
        "%ADB%" connect %%a >nul 2>&1
    )
    timeout /t 1 /nobreak >nul
)
exit /b 0

:tem_celular
rem  errorlevel 0 = ha um celular conectado e autorizado.
"%ADB%" devices | findstr /r /c:"device$" >nul
exit /b %errorlevel%

:fim
echo.
pause
endlocal
