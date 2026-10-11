@echo off
title Grand Horizon Hotel - Web Application
echo =======================================================
echo    GRAND HORIZON HOTEL MANAGEMENT SYSTEM (WEB APP)
echo =======================================================
echo.

if not exist bin mkdir bin

echo [1/2] Compiling Java source files...
javac -cp "lib/*;src" -d bin src/*.java
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation failed! Please check your JDK installation.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching Web Server on http://localhost:8080...
echo.
echo Press Ctrl+C in this terminal to stop the server.
echo.

start "" "http://localhost:8080/index.html"
java -cp "lib/*;bin;src" Main

pause
