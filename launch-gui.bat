@echo off
REM Cisco to SONiC Configuration Converter - GUI Launcher (Windows)
REM This script launches the graphical user interface

echo ================================================================
echo    Cisco to SONiC Configuration Converter - GUI
echo    Target Platform: Broadcom Enterprise SONiC
echo ================================================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 21 or higher
    pause
    exit /b 1
)

echo Launching GUI...
echo.

REM Launch the GUI
java src\com\cisco\sonic\converter\gui\ConverterGUI.java

REM Check exit status
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error: Failed to launch GUI
    echo Make sure all Java files are in the correct location
    pause
    exit /b 1
)

