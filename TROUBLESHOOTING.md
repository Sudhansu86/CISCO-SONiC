# Troubleshooting Guide

Common issues and solutions when using the Cisco to SONiC Configuration Converter.

## Table of Contents
1. [Installation Issues](#installation-issues)
2. [Conversion Issues](#conversion-issues)
3. [Output Issues](#output-issues)
4. [Deployment Issues](#deployment-issues)
5. [Common Error Messages](#common-error-messages)

---

## Installation Issues

### Java Version Error

**Problem**: `Error: A JNI error has occurred` or `Unsupported class file major version`

**Solution**: 
```bash
# Check your Java version
java -version

# You need Java 21 or higher
# Download from: https://www.oracle.com/java/technologies/downloads/
```

### Class Not Found Error

**Problem**: `Error: Could not find or load main class`

**Solution**:
```bash
# Make sure you're in the project root directory
cd /path/to/CISCO-SONiC

# Use the full class path
java src/com/cisco/sonic/converter/CiscoToSonicConverter.java
```

---

## Conversion Issues

### Empty Output File

**Problem**: Generated `config_db.json` is empty or contains only metadata

**Possible Causes**:
1. Input file contains no supported features
2. Configuration syntax is not recognized
3. File encoding issues

**Solutions**:
```bash
# Check if input file is readable
cat your-config.txt

# Verify file encoding (should be UTF-8 or ASCII)
file your-config.txt

# Look for warnings in console output
java src/com/cisco/sonic/converter/CiscoToSonicConverter.java your-config.txt output.json 2>&1 | grep -i warning
```

### Interface Names Not Converting

**Problem**: Interfaces like `GigabitEthernet0/0/1` not recognized

**Solution**: The converter supports common interface formats:
- `GigabitEthernet0/1` ✅
- `GigabitEthernet0/0/1` ⚠️ (may need adjustment)
- `Gi0/1` ❌ (abbreviations not supported)

**Workaround**: Expand abbreviations in your Cisco config before conversion:
```bash
# Replace abbreviated interface names
sed 's/Gi\([0-9]\)/GigabitEthernet\1/g' input.txt > expanded.txt
```

### VLAN Configuration Missing

**Problem**: VLANs defined but not appearing in output

**Check**:
1. VLAN ID is in valid range (1-4094)
2. VLAN configuration block is properly formatted
3. No syntax errors in VLAN section

**Example of correct format**:
```
vlan 10
 name MANAGEMENT
!
```

### IP Address Conversion Issues

**Problem**: IP addresses not converting correctly

**Common Issues**:
- Missing subnet mask
- Invalid IP address format
- Secondary IP addresses (not supported)

**Solution**: Ensure format is:
```
interface GigabitEthernet0/1
 ip address 10.0.1.1 255.255.255.0
```

Not:
```
interface GigabitEthernet0/1
 ip address 10.0.1.1  # Missing subnet mask
```

---

## Output Issues

### Invalid JSON Format

**Problem**: Generated JSON file is malformed

**Diagnosis**:
```bash
# Validate JSON syntax
python -m json.tool output.json

# Or use jq
jq . output.json
```

**Solution**: This usually indicates a bug. Check:
1. Special characters in descriptions or names
2. Very long configuration files
3. Unusual configuration syntax

### Missing Configuration Sections

**Problem**: Some configuration sections are missing from output

**Check the console output** for warnings:
```
Warnings:
  - Unsupported interface type: Loopback0
  - Feature not supported: port-channel
```

**Solution**: These features need manual configuration in SONiC.

---

## Deployment Issues

### SONiC Won't Load Configuration

**Problem**: `config load` fails on SONiC device

**Diagnosis**:
```bash
# On SONiC device, validate the config
sonic-cfggen -j /tmp/config_db.json --print-data

# Check for errors
sudo config load /tmp/config_db.json -y
```

**Common Issues**:
1. **Invalid interface names**: Interface names must match physical ports
2. **VLAN members before VLAN creation**: Ensure VLANs are defined
3. **Invalid IP addresses**: Check CIDR notation

**Solution**: Review and adjust interface mappings:
```json
{
  "PORT": {
    "Ethernet0": { ... },    # Verify this matches your hardware
    "Ethernet1": { ... }
  }
}
```

### Interfaces Not Coming Up

**Problem**: Interfaces show as "down" after loading config

**Check**:
```bash
# On SONiC device
show interfaces status

# Check admin status
show interfaces description
```

**Solution**: Verify admin_status in config:
```json
{
  "PORT": {
    "Ethernet0": {
      "admin_status": "up"    # Should be "up", not "down"
    }
  }
}
```

### VLANs Not Working

**Problem**: VLAN interfaces not passing traffic

**Diagnosis**:
```bash
# On SONiC device
show vlan brief
show vlan config
```

**Common Issues**:
1. VLAN members not properly configured
2. Tagging mode incorrect (tagged vs untagged)
3. VLAN interface IP not configured

**Solution**: Verify VLAN member configuration:
```json
{
  "VLAN_MEMBER": {
    "Vlan10|Ethernet0": {
      "tagging_mode": "untagged"    # or "tagged" for trunk
    }
  }
}
```

---

## Common Error Messages

### "No hostname configured"

**Severity**: Warning

**Impact**: Device will use default hostname

**Solution**: Add hostname to Cisco config:
```
hostname MY-SWITCH
```

### "Unsupported interface type"

**Severity**: Warning

**Impact**: Interface will not be converted

**Solution**: 
- Check if interface type is supported (see FEATURE_SUPPORT.md)
- Manually configure unsupported interfaces in SONiC

### "Invalid VLAN ID"

**Severity**: Error

**Impact**: VLAN will not be created

**Solution**: Use VLAN IDs between 1 and 4094:
```
vlan 10      # Valid
vlan 5000    # Invalid (too high)
```

### "Interface has IP address but no subnet mask"

**Severity**: Error

**Impact**: Interface IP will not be configured

**Solution**: Add subnet mask:
```
interface GigabitEthernet0/1
 ip address 10.0.1.1 255.255.255.0
```

### "VLAN member references undefined VLAN"

**Severity**: Error

**Impact**: VLAN member will not be created

**Solution**: Ensure VLAN is defined before assigning members:
```
vlan 10
 name SERVERS
!
interface GigabitEthernet0/1
 switchport access vlan 10
```

---

## Debug Mode

### Enable Verbose Output

To get more detailed information during conversion:

1. **Check raw parsed data**: Review the console output for parsing details
2. **Examine intermediate objects**: Add debug prints in the code
3. **Validate input**: Ensure Cisco config is syntactically correct

### Manual Verification Steps

1. **Count features in input**:
```bash
grep -c "^interface" cisco-config.txt
grep -c "^vlan" cisco-config.txt
grep -c "^ip route" cisco-config.txt
```

2. **Count features in output**:
```bash
jq '.PORT | length' output.json
jq '.VLAN | length' output.json
jq '.STATIC_ROUTE | length' output.json
```

3. **Compare counts**: Numbers should match (accounting for unsupported features)

---

## Getting Help

If you're still experiencing issues:

1. **Check Documentation**:
   - [README.md](README.md) - General information
   - [CONFIGURATION_MAPPING.md](CONFIGURATION_MAPPING.md) - Syntax mapping
   - [FEATURE_SUPPORT.md](FEATURE_SUPPORT.md) - Supported features

2. **Review Examples**:
   - [examples/sample_cisco_config.txt](examples/sample_cisco_config.txt)

3. **Test with Sample Config**:
   ```bash
   java src/com/cisco/sonic/converter/CiscoToSonicConverter.java examples/sample_cisco_config.txt test.json
   ```

4. **Simplify Your Config**:
   - Start with a minimal configuration
   - Add features incrementally
   - Identify which feature causes issues

---

## Best Practices to Avoid Issues

1. ✅ **Validate Cisco config** before conversion
2. ✅ **Start with simple configs** to test the tool
3. ✅ **Review warnings** in console output
4. ✅ **Test in lab** before production deployment
5. ✅ **Keep backups** of original configurations
6. ✅ **Document manual changes** needed after conversion
7. ✅ **Verify interface mappings** match your hardware

---

**Last Updated**: 2026-01-09

