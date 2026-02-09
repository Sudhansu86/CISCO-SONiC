#!/bin/bash

# Cisco to SONiC Configuration Converter - GUI Launcher
# This script launches the graphical user interface

echo "╔════════════════════════════════════════════════════════════╗"
echo "║   Cisco to SONiC Configuration Converter - GUI            ║"
echo "║   Target Platform: Broadcom Enterprise SONiC              ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 21 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "Warning: Java 21 or higher is recommended"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
fi

echo "Launching GUI..."
echo ""

# Launch the GUI
java src/com/cisco/sonic/converter/gui/ConverterGUI.java

# Check exit status
if [ $? -ne 0 ]; then
    echo ""
    echo "Error: Failed to launch GUI"
    echo "Make sure all Java files are in the correct location"
    exit 1
fi

