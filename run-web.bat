@echo off
title Grand Horizon Hotel - Web Application
echo =======================================================
echo    GRAND HORIZON HOTEL MANAGEMENT SYSTEM (WEB APP)
echo =======================================================
echo.

if not exist backend\bin mkdir backend\bin

echo [1/2] Compiling Java source files...
javac -cp "backend/lib/*;lib/*;backend/src;src" -d backend/bin backend/src/*.java
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

start "" "http://localhost:8080/login.html"
java -cp "backend/lib/*;lib/*;backend/bin;bin" Main

pause
