@echo off
title Grand Horizon Hotel - OOP Validation Tests
echo =======================================================
echo    GRAND HORIZON HOTEL - OOP TEST VALIDATION SUITE
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

echo [2/2] Running OOP Test Suite...
echo.
java -cp "lib/*;bin;src" Main --test

pause
