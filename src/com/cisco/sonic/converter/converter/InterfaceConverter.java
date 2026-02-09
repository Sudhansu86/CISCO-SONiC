package com.cisco.sonic.converter.converter;

import com.cisco.sonic.converter.model.*;
import com.cisco.sonic.converter.util.NetworkUtils;
import java.util.*;

/**
 * Converts Cisco interface configurations to SONiC format
 */
public class InterfaceConverter {
    
    /**
     * Convert all interfaces from Cisco to SONiC
     */
    public void convert(CiscoConfig ciscoConfig, SonicConfig sonicConfig) {
        // First pass: Create Port-Channel interfaces
        Map<Integer, InterfaceConfig> portChannels = new LinkedHashMap<>();
        for (Map.Entry<String, InterfaceConfig> entry : ciscoConfig.getInterfaces().entrySet()) {
            String ciscoInterfaceName = entry.getKey();
            InterfaceConfig interfaceConfig = entry.getValue();

            // Collect Port-Channel interfaces
            if (ciscoInterfaceName.startsWith("Port-channel")) {
                String number = ciscoInterfaceName.substring(12).trim();
                try {
                    int portChannelId = Integer.parseInt(number);
                    portChannels.put(portChannelId, interfaceConfig);
                } catch (NumberFormatException e) {
                    // Ignore invalid Port-channel number
                }
            }
        }

        // Second pass: Convert all interfaces
        for (Map.Entry<String, InterfaceConfig> entry : ciscoConfig.getInterfaces().entrySet()) {
            String ciscoInterfaceName = entry.getKey();
            InterfaceConfig interfaceConfig = entry.getValue();

            // Convert interface name to SONiC format
            String sonicInterfaceName = convertInterfaceName(ciscoInterfaceName);

            if (sonicInterfaceName == null) {
                sonicConfig.addWarning("Unsupported interface type: " + ciscoInterfaceName);
                continue;
            }

            // Handle Port-Channel member interfaces
            if (interfaceConfig.getChannelGroup() != null) {
                convertPortChannelMember(interfaceConfig, sonicInterfaceName, sonicConfig);
            }

            // Convert Port-Channel interface itself
            if (ciscoInterfaceName.startsWith("Port-channel")) {
                convertPortChannelInterface(interfaceConfig, sonicInterfaceName, sonicConfig);
            }

            // Convert based on interface mode
            if ("routed".equals(interfaceConfig.getMode()) || interfaceConfig.getIpAddress() != null) {
                convertRoutedInterface(interfaceConfig, sonicInterfaceName, sonicConfig);
            } else if ("access".equals(interfaceConfig.getMode())) {
                convertAccessInterface(interfaceConfig, sonicInterfaceName, sonicConfig);
            } else if ("trunk".equals(interfaceConfig.getMode())) {
                convertTrunkInterface(interfaceConfig, sonicInterfaceName, sonicConfig);
            }

            // Add port configuration
            convertPortConfig(interfaceConfig, sonicInterfaceName, sonicConfig);
        }
    }
    
    /**
     * Convert Cisco interface name to SONiC format
     */
    private String convertInterfaceName(String ciscoName) {
        ciscoName = ciscoName.trim();
        
        // GigabitEthernet -> Ethernet
        if (ciscoName.startsWith("GigabitEthernet")) {
            String number = ciscoName.substring(15);
            return "Ethernet" + convertInterfaceNumber(number);
        }
        
        // TenGigabitEthernet -> Ethernet
        if (ciscoName.startsWith("TenGigabitEthernet")) {
            String number = ciscoName.substring(18);
            return "Ethernet" + convertInterfaceNumber(number);
        }
        
        // FastEthernet -> Ethernet
        if (ciscoName.startsWith("FastEthernet")) {
            String number = ciscoName.substring(12);
            return "Ethernet" + convertInterfaceNumber(number);
        }
        
        // Port-channel -> PortChannel
        if (ciscoName.startsWith("Port-channel")) {
            String number = ciscoName.substring(12);
            return "PortChannel" + number.trim();
        }
        
        // Vlan interface
        if (ciscoName.startsWith("Vlan")) {
            return ciscoName;
        }
        
        // Loopback
        if (ciscoName.startsWith("Loopback")) {
            return ciscoName;
        }
        
        return null;
    }
    
    /**
     * Convert interface number format (e.g., "0/1" to "1")
     */
    private String convertInterfaceNumber(String number) {
        number = number.trim();
        // Simple conversion: take the last number after slash
        if (number.contains("/")) {
            String[] parts = number.split("/");
            return parts[parts.length - 1];
        }
        return number;
    }
    
    /**
     * Convert routed interface (Layer 3)
     */
    private void convertRoutedInterface(InterfaceConfig interfaceConfig, String sonicName, SonicConfig sonicConfig) {
        if (interfaceConfig.getIpAddress() != null && interfaceConfig.getSubnetMask() != null) {
            Map<String, Object> interfaceData = new LinkedHashMap<>();
            
            // Convert to CIDR notation
            String cidr = NetworkUtils.toCidr(interfaceConfig.getIpAddress(), interfaceConfig.getSubnetMask());
            interfaceData.put("NULL", "NULL");
            
            sonicConfig.addInterface(sonicName + "|" + cidr, interfaceData);
        }
    }
    
    /**
     * Convert access interface (Layer 2)
     */
    private void convertAccessInterface(InterfaceConfig interfaceConfig, String sonicName, SonicConfig sonicConfig) {
        if (interfaceConfig.getAccessVlan() != null) {
            String vlanName = "Vlan" + interfaceConfig.getAccessVlan();
            String memberKey = vlanName + "|" + sonicName;
            
            Map<String, Object> memberData = new LinkedHashMap<>();
            memberData.put("tagging_mode", "untagged");
            
            sonicConfig.addVlanMember(memberKey, memberData);
        }
    }
    
    /**
     * Convert trunk interface (Layer 2)
     */
    private void convertTrunkInterface(InterfaceConfig interfaceConfig, String sonicName, SonicConfig sonicConfig) {
        for (Integer vlanId : interfaceConfig.getAllowedVlans()) {
            String vlanName = "Vlan" + vlanId;
            String memberKey = vlanName + "|" + sonicName;
            
            Map<String, Object> memberData = new LinkedHashMap<>();
            memberData.put("tagging_mode", "tagged");
            
            sonicConfig.addVlanMember(memberKey, memberData);
        }
    }
    
    /**
     * Convert port-level configuration
     */
    private void convertPortConfig(InterfaceConfig interfaceConfig, String sonicName, SonicConfig sonicConfig) {
        Map<String, Object> portData = new LinkedHashMap<>();
        
        // Admin status
        portData.put("admin_status", interfaceConfig.isEnabled() ? "up" : "down");
        
        // MTU
        if (interfaceConfig.getMtu() != 1500) {
            portData.put("mtu", String.valueOf(interfaceConfig.getMtu()));
        }
        
        // Speed
        if (interfaceConfig.getSpeed() != null) {
            portData.put("speed", String.valueOf(interfaceConfig.getSpeed()));
        }
        
        // Description
        if (interfaceConfig.getDescription() != null) {
            portData.put("description", interfaceConfig.getDescription());
        }
        
        sonicConfig.addPort(sonicName, portData);
    }

    /**
     * Convert Port-Channel interface configuration
     */
    private void convertPortChannelInterface(InterfaceConfig interfaceConfig, String sonicName, SonicConfig sonicConfig) {
        Map<String, Object> portChannelData = new LinkedHashMap<>();

        // Admin status
        portChannelData.put("admin_status", interfaceConfig.isEnabled() ? "up" : "down");

        // MTU
        if (interfaceConfig.getMtu() != 1500) {
            portChannelData.put("mtu", String.valueOf(interfaceConfig.getMtu()));
        }

        // Min links (default to 1)
        portChannelData.put("min_links", "1");

        sonicConfig.addPortChannel(sonicName, portChannelData);
    }

    /**
     * Convert Port-Channel member interface
     */
    private void convertPortChannelMember(InterfaceConfig interfaceConfig, String sonicName, SonicConfig sonicConfig) {
        Integer channelGroup = interfaceConfig.getChannelGroup();
        if (channelGroup == null) {
            return;
        }

        String portChannelName = "PortChannel" + channelGroup;
        String memberKey = portChannelName + "|" + sonicName;

        Map<String, Object> memberData = new LinkedHashMap<>();
        // Empty map - SONiC doesn't require additional member configuration

        sonicConfig.addPortChannelMember(memberKey, memberData);
    }
}

