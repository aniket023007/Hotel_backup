@echo off
title Grand Horizon Hotel - Desktop GUI
echo =======================================================
echo    GRAND HORIZON HOTEL MANAGEMENT SYSTEM (SWING GUI)
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

echo [2/2] Launching Swing Desktop Interface...
java -cp "backend/lib/*;lib/*;backend/bin;bin" Main --swing

pause
