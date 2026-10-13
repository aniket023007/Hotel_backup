@echo off
title Grand Horizon Hotel - OOP Validation Tests
echo =======================================================
echo    GRAND HORIZON HOTEL - OOP TEST VALIDATION SUITE
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

echo [2/2] Running OOP Test Suite...
echo.
java -cp "backend/lib/*;lib/*;backend/bin;bin" Main --test

pause
