# GUI User Guide

## Overview

The Cisco to SONiC Configuration Converter now includes a modern graphical user interface (GUI) that makes configuration conversion easier and more intuitive.

## 🚀 Launching the GUI

### Method 1: Using Launcher Scripts

**On macOS/Linux:**
```bash
chmod +x launch-gui.sh
./launch-gui.sh
```

**On Windows:**
```cmd
launch-gui.bat
```

### Method 2: Using Java Command

```bash
java src/com/cisco/sonic/converter/gui/ConverterGUI.java
```

### Method 3: From Main Application

```bash
java src/com/cisco/sonic/converter/CiscoToSonicConverter.java --gui
```

---

## 🖥️ GUI Interface Overview

The GUI is divided into several sections:

### 1. **Header Section**
- **Title**: Displays application name and target platform
- **Load File Button**: Opens file browser to load Cisco configuration
- **Clear All Button**: Clears all input and output

### 2. **Input Panel (Left Side)**
- **Cisco Configuration Input**: Large text area for pasting or viewing Cisco config
- **Hint Text**: Reminds you to paste configuration or load from file
- **Scrollable**: Handles large configuration files

### 3. **Output Panel (Right Side)**
- **SONiC Configuration Output**: Displays generated JSON configuration
- **Read-Only**: Prevents accidental editing of output
- **Save Button**: Saves the output to a file
- **Scrollable**: Easy navigation of JSON output

### 4. **Conversion Log (Bottom)**
- **Real-time Updates**: Shows conversion progress
- **Warnings**: Displays any warnings during conversion
- **Unsupported Features**: Lists features that couldn't be converted
- **Success Messages**: Confirms successful conversion

### 5. **Status Bar**
- **Status Label**: Shows current operation status
- **Progress Bar**: Displays during conversion
- **Convert Button**: Large green button to start conversion

---

## 📋 Step-by-Step Usage

### Converting a Configuration File

1. **Launch the GUI** using one of the methods above

2. **Load Your Cisco Configuration**:
   - Click "Load Cisco Config File" button
   - Browse to your configuration file
   - Select the file (*.txt or *.cfg)
   - Click "Open"

3. **Review the Input**:
   - The configuration appears in the left panel
   - Verify it loaded correctly

4. **Convert**:
   - Click the green "Convert to SONiC" button
   - Watch the conversion log for progress
   - Wait for completion message

5. **Review the Output**:
   - Check the right panel for JSON output
   - Review any warnings in the log
   - Note any unsupported features

6. **Save the Output**:
   - Click "Save SONiC Config" button
   - Choose save location
   - Enter filename (default: config_db.json)
   - Click "Save"

### Pasting Configuration Text

1. **Launch the GUI**

2. **Paste Configuration**:
   - Click in the left text area
   - Paste your Cisco configuration (Ctrl+V or Cmd+V)

3. **Convert and Save** (same as steps 4-6 above)

---

## 🎨 GUI Features

### ✅ User-Friendly Features

- **Drag and Drop**: (Future enhancement)
- **Syntax Highlighting**: Clear, monospaced font for readability
- **Auto-Scroll**: Log automatically scrolls to show latest messages
- **File Filters**: Only shows relevant file types (.txt, .cfg, .json)
- **Default Filenames**: Suggests appropriate filenames
- **Confirmation Dialogs**: Prevents accidental data loss
- **Error Messages**: Clear, helpful error dialogs
- **Progress Indication**: Visual feedback during conversion

### 🎯 Keyboard Shortcuts

- **Ctrl+O** (Cmd+O on Mac): Open file (future enhancement)
- **Ctrl+S** (Cmd+S on Mac): Save output (future enhancement)
- **Ctrl+N** (Cmd+N on Mac): Clear all (future enhancement)

---

## 📊 Understanding the Conversion Log

### Log Message Types

**Information Messages:**
```
Starting conversion...
Parsing Cisco configuration...
Found 12 interfaces
Found 5 VLANs
```

**Warning Messages:**
```
⚠️  Warnings:
  - Unsupported interface type: Loopback0
  - Feature not supported: port-channel
```

**Error Messages:**
```
❌ Unsupported Features:
  - OSPF advanced configuration
  - QoS policies
```

**Success Messages:**
```
✅ Conversion completed successfully!
```

---

## 🔧 Troubleshooting

### GUI Won't Launch

**Problem**: Double-clicking launcher does nothing

**Solution**:
1. Open terminal/command prompt
2. Run launcher script manually
3. Check error messages
4. Verify Java is installed: `java -version`

### File Won't Load

**Problem**: "Error loading file" message

**Solution**:
1. Check file permissions
2. Verify file is readable
3. Ensure file is text format (not binary)
4. Try copying content and pasting instead

### Conversion Fails

**Problem**: Error during conversion

**Solution**:
1. Check the conversion log for specific errors
2. Verify Cisco configuration syntax
3. Try with a smaller configuration first
4. Review unsupported features list

### Output Won't Save

**Problem**: "Error saving file" message

**Solution**:
1. Check write permissions on target directory
2. Ensure filename is valid
3. Close file if it's open in another program
4. Try saving to a different location

---

## 💡 Tips and Best Practices

### Before Converting

1. **Backup Original**: Keep a copy of your Cisco configuration
2. **Clean Up Config**: Remove unnecessary comments or old configurations
3. **Test with Sample**: Try the sample configuration first

### During Conversion

1. **Watch the Log**: Monitor for warnings and errors
2. **Don't Close**: Wait for "Conversion completed" message
3. **Review Warnings**: Note any unsupported features

### After Conversion

1. **Validate JSON**: Check that output is valid JSON
2. **Review Mappings**: Verify interface names and VLANs
3. **Test in Lab**: Deploy to test environment first
4. **Document Changes**: Note any manual adjustments needed

---

## 🎓 Example Workflow

### Scenario: Converting a Switch Configuration

1. **Prepare**:
   ```
   - Extract running-config from Cisco switch
   - Save as cisco-switch-01.txt
   ```

2. **Launch GUI**:
   ```bash
   ./launch-gui.sh
   ```

3. **Load File**:
   - Click "Load Cisco Config File"
   - Select cisco-switch-01.txt
   - Verify content in left panel

4. **Convert**:
   - Click "Convert to SONiC"
   - Watch log messages:
     ```
     Starting conversion...
     Parsing Cisco configuration...
     Found 24 interfaces
     Found 10 VLANs
     Found 3 static routes
     Converting to SONiC format...
     ✅ Conversion completed successfully!
     ```

5. **Review**:
   - Check JSON output in right panel
   - Note any warnings
   - Verify interface mappings

6. **Save**:
   - Click "Save SONiC Config"
   - Save as: sonic-switch-01.json
   - Confirm save location

7. **Deploy**:
   - Copy to SONiC device
   - Test in lab environment
   - Validate functionality

---

## 🚀 Advanced Features

### Batch Processing (Future)

The GUI will support:
- Multiple file conversion
- Batch processing queue
- Export conversion reports

### Configuration Comparison (Future)

Planned features:
- Side-by-side comparison
- Diff highlighting
- Validation against SONiC schema

### Templates (Future)

Coming soon:
- Save conversion templates
- Reuse common mappings
- Custom interface mappings

---

## 📸 Screenshots

### Main Window
```
┌─────────────────────────────────────────────────────────────┐
│ Cisco to SONiC Configuration Converter                      │
│ Target Platform: Broadcom Enterprise SONiC                  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐  ┌──────────────────┐               │
│  │ Cisco Config     │  │ SONiC Config     │               │
│  │ (Input)          │  │ (Output)         │               │
│  │                  │  │                  │               │
│  │                  │  │                  │               │
│  │                  │  │                  │               │
│  └──────────────────┘  └──────────────────┘               │
│                                                              │
│  ┌──────────────────────────────────────────┐              │
│  │ Conversion Log                            │              │
│  │ Starting conversion...                    │              │
│  │ ✅ Conversion completed successfully!     │              │
│  └──────────────────────────────────────────┘              │
│                                                              │
│  Status: Ready              [Convert to SONiC]              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🆘 Getting Help

- **Documentation**: See README.md for general information
- **Mapping Guide**: Check CONFIGURATION_MAPPING.md for syntax details
- **Troubleshooting**: Review TROUBLESHOOTING.md for common issues
- **Quick Start**: See QUICK_START.md for basic usage

---

**GUI Version**: 1.0.0  
**Last Updated**: 2026-01-09  
**Platform**: Cross-platform (Windows, macOS, Linux)

