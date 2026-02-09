# Cisco to SONiC Configuration Mapping Guide

## Overview
This document provides a comprehensive mapping between Cisco IOS/IOS-XE configuration syntax and Broadcom Enterprise SONiC configuration format.

## Target Platform
- **Primary Target**: Broadcom Enterprise SONiC
- **Configuration Format**: JSON (config_db.json)

---

## 1. Device Metadata

### Cisco
```
hostname SWITCH-01
```

### SONiC (config_db.json)
```json
{
  "DEVICE_METADATA": {
    "localhost": {
      "hostname": "SWITCH-01",
      "platform": "x86_64-broadcom_enterprise_sonic",
      "type": "LeafRouter"
    }
  }
}
```

---

## 2. Physical Interfaces

### Cisco - Layer 3 Interface
```
interface GigabitEthernet0/1
 description Uplink to Core
 ip address 10.0.1.1 255.255.255.0
 mtu 9000
 no shutdown
```

### SONiC Equivalent
```json
{
  "PORT": {
    "Ethernet1": {
      "admin_status": "up",
      "mtu": "9000",
      "description": "Uplink to Core"
    }
  },
  "INTERFACE": {
    "Ethernet1|10.0.1.1/24": {
      "NULL": "NULL"
    }
  }
}
```

### Interface Name Mapping
| Cisco | SONiC |
|-------|-------|
| GigabitEthernet0/1 | Ethernet1 |
| TenGigabitEthernet0/1 | Ethernet1 |
| FastEthernet0/1 | Ethernet1 |
| Port-channel1 | PortChannel1 |

---

## 3. VLANs

### Cisco
```
vlan 10
 name MANAGEMENT

interface Vlan10
 description Management VLAN
 ip address 192.168.10.1 255.255.255.0
 no shutdown
```

### SONiC Equivalent
```json
{
  "VLAN": {
    "Vlan10": {
      "vlanid": "10",
      "description": "MANAGEMENT"
    }
  },
  "VLAN_INTERFACE": {
    "Vlan10|192.168.10.1/24": {
      "NULL": "NULL"
    }
  }
}
```

---

## 4. Switchport Configuration

### Cisco - Access Port
```
interface GigabitEthernet0/2
 switchport mode access
 switchport access vlan 20
 no shutdown
```

### SONiC Equivalent
```json
{
  "PORT": {
    "Ethernet2": {
      "admin_status": "up"
    }
  },
  "VLAN_MEMBER": {
    "Vlan20|Ethernet2": {
      "tagging_mode": "untagged"
    }
  }
}
```

### Cisco - Trunk Port
```
interface GigabitEthernet0/3
 switchport mode trunk
 switchport trunk allowed vlan 10,20,30
 no shutdown
```

### SONiC Equivalent
```json
{
  "PORT": {
    "Ethernet3": {
      "admin_status": "up"
    }
  },
  "VLAN_MEMBER": {
    "Vlan10|Ethernet3": {
      "tagging_mode": "tagged"
    },
    "Vlan20|Ethernet3": {
      "tagging_mode": "tagged"
    },
    "Vlan30|Ethernet3": {
      "tagging_mode": "tagged"
    }
  }
}
```

---

## 5. Static Routes

### Cisco
```
ip route 0.0.0.0 0.0.0.0 10.0.1.1
ip route 172.16.0.0 255.255.0.0 10.0.2.1
```

### SONiC Equivalent
```json
{
  "STATIC_ROUTE": {
    "0.0.0.0/0|0.0.0.0/0|10.0.1.1": {
      "nexthop": "10.0.1.1",
      "ifname": ""
    },
    "0.0.0.0/0|172.16.0.0/16|10.0.2.1": {
      "nexthop": "10.0.2.1",
      "ifname": ""
    }
  }
}
```

---

## 6. Supported Features

### Currently Supported
- ✅ Hostname configuration
- ✅ Physical interface configuration (Layer 2 and Layer 3)
- ✅ VLAN creation and naming
- ✅ VLAN interfaces (SVIs) with IP addressing
- ✅ Switchport access mode
- ✅ Switchport trunk mode
- ✅ Static routing
- ✅ Interface descriptions
- ✅ MTU configuration
- ✅ Admin status (shutdown/no shutdown)

### Partially Supported
- ⚠️ OSPF (basic configuration only)
- ⚠️ BGP (basic configuration only)
- ⚠️ Access Control Lists (basic conversion)

### Not Yet Supported
- ❌ Port-channel/LAG configuration
- ❌ Spanning Tree Protocol (STP)
- ❌ Quality of Service (QoS)
- ❌ DHCP server/relay
- ❌ NAT configuration
- ❌ VRF configuration
- ❌ Multicast routing
- ❌ SNMP configuration

---

## 7. Common Conversion Notes

### IP Address Format
- **Cisco**: Uses dotted decimal subnet mask (e.g., 255.255.255.0)
- **SONiC**: Uses CIDR notation (e.g., /24)
- **Conversion**: Automatic conversion performed by the tool

### Interface Numbering
- Cisco interface numbers (e.g., 0/1, 1/0/1) are simplified to sequential numbers in SONiC
- The tool attempts to preserve logical ordering but may require manual adjustment

### Default Values
- SONiC requires explicit configuration for many features that have defaults in Cisco
- The tool applies sensible defaults where appropriate

---

## 8. Best Practices

1. **Review Output**: Always review the generated SONiC configuration before deployment
2. **Test in Lab**: Test converted configurations in a lab environment first
3. **Backup**: Keep original Cisco configurations as reference
4. **Incremental Migration**: Convert and test one feature at a time for complex configurations
5. **Validation**: Use SONiC's built-in validation tools after loading the configuration

---

## 9. Troubleshooting

### Common Issues

**Issue**: Interface names don't match physical ports
- **Solution**: Manually map interface names to physical ports based on your hardware

**Issue**: VLAN members not appearing correctly
- **Solution**: Verify VLAN is created before adding members

**Issue**: Static routes not working
- **Solution**: Ensure next-hop IP addresses are reachable and interfaces are up

---

## 10. Additional Resources

- [SONiC Official Documentation](https://github.com/sonic-net/SONiC/wiki)
- [Broadcom Enterprise SONiC Documentation](https://sonic.broadcom.com)
- [SONiC Configuration Examples](https://github.com/sonic-net/SONiC/wiki/Configuration)

