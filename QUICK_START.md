# Quick Start Guide

Get started with the Cisco to SONiC Configuration Converter in minutes!

## 🚀 5-Minute Quick Start

### Step 1: Verify Java Installation

```bash
java -version
```

You should see Java 21 or higher. If not, install Java from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/).

### Step 2: Open the Project

1. Open IntelliJ IDEA
2. Select "Open" and navigate to the `CISCO-SONiC` directory
3. Wait for the project to index

### Step 3: Run Your First Conversion

#### Option A: GUI Mode (Recommended) 🖥️

1. Open a terminal in the project directory
2. Launch the GUI:

**On macOS/Linux:**
```bash
./launch-gui.sh
```

**On Windows:**
```cmd
launch-gui.bat
```

3. In the GUI:
   - Click "Load Cisco Config File"
   - Select `examples/sample_cisco_config.txt`
   - Click the green "Convert to SONiC" button
   - Review the output in the right panel
   - Click "Save SONiC Config" to save

**That's it!** You've completed your first conversion with a visual interface.

#### Option B: Command-Line Mode (Fastest) 💻

1. Open a terminal in the project directory
2. Run the converter with the sample file:

```bash
java src/com/cisco/sonic/converter/CiscoToSonicConverter.java examples/sample_cisco_config.txt output.json
```

3. Check the generated `output.json` file

#### Option C: Interactive Mode ⌨️

1. Run without arguments:

```bash
java src/com/cisco/sonic/converter/CiscoToSonicConverter.java
```

2. Choose option 1 (Load from file) or 2 (Paste configuration)
3. Follow the prompts

### Step 4: Review the Output

Open the generated `config_db.json` file. You should see a properly formatted SONiC configuration:

```json
{
  "DEVICE_METADATA": {
    "localhost": {
      "hostname": "CISCO-SWITCH-01",
      ...
    }
  },
  "PORT": { ... },
  "VLAN": { ... },
  ...
}
```

## 📋 Common Use Cases

### Use Case 1: Convert a Single Configuration File

```bash
java src/com/cisco/sonic/converter/CiscoToSonicConverter.java my-cisco-config.txt sonic-config.json
```

### Use Case 2: Batch Conversion (Multiple Files)

Create a simple shell script:

```bash
#!/bin/bash
for file in cisco-configs/*.txt; do
    output="sonic-configs/$(basename "$file" .txt).json"
    java src/com/cisco/sonic/converter/CiscoToSonicConverter.java "$file" "$output"
done
```

### Use Case 3: Copy-Paste Configuration

1. Copy your Cisco configuration to clipboard
2. Run: `java src/com/cisco/sonic/converter/CiscoToSonicConverter.java`
3. Choose option 2
4. Paste your configuration
5. Type `END` on a new line
6. Specify output filename

## 🔍 What Gets Converted?

### ✅ Automatically Converted

- **Hostname**: `hostname SWITCH-01` → Device metadata
- **Interfaces**: `interface GigabitEthernet0/1` → `Ethernet1`
- **IP Addresses**: `ip address 10.0.1.1 255.255.255.0` → `10.0.1.1/24`
- **VLANs**: `vlan 10` → VLAN configuration
- **Switchports**: Access and trunk modes
- **Static Routes**: `ip route` commands
- **Interface Status**: `shutdown` / `no shutdown`

### ⚠️ Requires Review

- Interface name mappings (verify physical port assignments)
- Routing protocol configurations (basic conversion only)
- ACLs (may need manual adjustment)

### ❌ Not Converted (Manual Configuration Required)

- Port-channels/LAG
- Spanning Tree Protocol
- QoS policies
- DHCP configuration
- NAT rules

## 📝 Example Workflow

### 1. Prepare Your Cisco Configuration

Extract the running configuration from your Cisco device:

```
# On Cisco device
show running-config > cisco-config.txt
```

### 2. Run the Converter

```bash
java src/com/cisco/sonic/converter/CiscoToSonicConverter.java cisco-config.txt sonic-config.json
```

### 3. Review the Output

Check for warnings and unsupported features:

```
Conversion Summary:
  - Hostname: CISCO-SWITCH-01
  - Interfaces: 12
  - VLANs: 5
  - Static Routes: 3

Warnings:
  - Unsupported interface type: Loopback0
  
Configuration written to: sonic-config.json
```

### 4. Validate the Configuration

Review the generated JSON file:

```bash
# Pretty-print the JSON
python -m json.tool sonic-config.json

# Or use jq
jq . sonic-config.json
```

### 5. Deploy to SONiC

Copy the configuration to your SONiC device:

```bash
# Copy to SONiC device
scp sonic-config.json admin@sonic-switch:/tmp/

# On SONiC device
sudo cp /tmp/sonic-config.json /etc/sonic/config_db.json
sudo config reload -y
```

## 🛠️ Troubleshooting

### Problem: "Command not found"

**Solution**: Make sure you're in the project directory and Java is installed.

### Problem: "File not found"

**Solution**: Check the file path. Use absolute paths if relative paths don't work:

```bash
java src/com/cisco/sonic/converter/CiscoToSonicConverter.java /full/path/to/config.txt
```

### Problem: "Invalid configuration"

**Solution**: Ensure your Cisco configuration is valid. The tool expects standard IOS/IOS-XE syntax.

### Problem: "Empty output file"

**Solution**: Check if your Cisco configuration contains supported features. Review the warnings in the console output.

## 📚 Next Steps

1. **Read the Full Documentation**: Check [README.md](README.md) for detailed information
2. **Review Configuration Mapping**: See [CONFIGURATION_MAPPING.md](CONFIGURATION_MAPPING.md) for syntax details
3. **Test in Lab**: Always test converted configurations in a lab environment first
4. **Customize**: Modify the converter code to handle your specific requirements

## 💡 Tips

- **Start Small**: Convert a simple configuration first to understand the tool
- **Compare Outputs**: Keep the original Cisco config alongside the SONiC config for reference
- **Incremental Migration**: Convert and test one feature at a time for complex setups
- **Backup Everything**: Always backup configurations before making changes
- **Use Version Control**: Track your configurations in Git

## 🆘 Getting Help

- Check the [Configuration Mapping Guide](CONFIGURATION_MAPPING.md)
- Review the [sample configuration](examples/sample_cisco_config.txt)
- Look at the generated warnings and errors for clues
- Consult the [SONiC documentation](https://github.com/sonic-net/SONiC/wiki)

---

**Ready to convert?** Start with the sample configuration and work your way up to your production configs!

