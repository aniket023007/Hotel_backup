@echo off
title Grand Horizon Hotel - Desktop GUI
echo =======================================================
echo    GRAND HORIZON HOTEL MANAGEMENT SYSTEM (SWING GUI)
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

echo [2/2] Launching Swing Desktop Interface...
java -cp "lib/*;bin;src" Main --swing

pause
