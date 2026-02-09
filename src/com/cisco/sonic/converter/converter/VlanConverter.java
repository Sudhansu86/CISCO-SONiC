package com.cisco.sonic.converter.converter;

import com.cisco.sonic.converter.model.*;
import com.cisco.sonic.converter.util.NetworkUtils;
import java.util.*;

/**
 * Converts Cisco VLAN configurations to SONiC format
 */
public class VlanConverter {
    
    /**
     * Convert all VLANs from Cisco to SONiC
     */
    public void convert(CiscoConfig ciscoConfig, SonicConfig sonicConfig) {
        for (Map.Entry<Integer, VlanConfig> entry : ciscoConfig.getVlans().entrySet()) {
            Integer vlanId = entry.getKey();
            VlanConfig vlanConfig = entry.getValue();
            
            String vlanName = "Vlan" + vlanId;
            
            // Add VLAN definition
            Map<String, Object> vlanData = new LinkedHashMap<>();
            vlanData.put("vlanid", String.valueOf(vlanId));
            
            if (vlanConfig.getName() != null) {
                vlanData.put("description", vlanConfig.getName());
            }
            
            sonicConfig.addVlan(vlanName, vlanData);
            
            // Add VLAN interface if IP is configured
            if (vlanConfig.getIpAddress() != null && vlanConfig.getSubnetMask() != null) {
                String cidr = NetworkUtils.toCidr(vlanConfig.getIpAddress(), vlanConfig.getSubnetMask());
                Map<String, Object> vlanInterfaceData = new LinkedHashMap<>();
                vlanInterfaceData.put("NULL", "NULL");
                
                sonicConfig.addVlanInterface(vlanName + "|" + cidr, vlanInterfaceData);
            }
        }
    }
}

