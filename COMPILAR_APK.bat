@echo off
echo Compilando TaskFlow APK...
echo.

where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Java nao encontrado!
    echo Baixe o JDK em: https://adoptium.net
    pause
    exit /b 1
)

where gradle >nul 2>&1
if %errorlevel% neq 0 (
    echo Gradle nao encontrado. Instalando via wrapper...
    call gradle wrapper --gradle-version 8.2 >nul 2>&1
)

echo Rodando build...
call gradlew.bat assembleDebug

if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ================================================
    echo  APK gerado com sucesso!
    echo  Local: app\build\outputs\apk\debug\app-debug.apk
    echo ================================================
    start "" "app\build\outputs\apk\debug"
) else (
    echo.
    echo ERRO: APK nao gerado. Verifique o log acima.
)
pause
