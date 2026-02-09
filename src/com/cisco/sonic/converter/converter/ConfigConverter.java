package com.cisco.sonic.converter.converter;

import com.cisco.sonic.converter.model.*;
import java.util.*;

/**
 * Main converter class that orchestrates the conversion from Cisco to SONiC
 */
public class ConfigConverter {
    private final InterfaceConverter interfaceConverter;
    private final VlanConverter vlanConverter;
    private final RouteConverter routeConverter;
    private final AclConverter aclConverter;
    
    public ConfigConverter() {
        this.interfaceConverter = new InterfaceConverter();
        this.vlanConverter = new VlanConverter();
        this.routeConverter = new RouteConverter();
        this.aclConverter = new AclConverter();
    }
    
    /**
     * Convert Cisco configuration to SONiC configuration
     */
    public SonicConfig convert(CiscoConfig ciscoConfig) {
        SonicConfig sonicConfig = new SonicConfig();
        
        // Set device metadata
        convertDeviceMetadata(ciscoConfig, sonicConfig);
        
        // Convert interfaces
        interfaceConverter.convert(ciscoConfig, sonicConfig);
        
        // Convert VLANs
        vlanConverter.convert(ciscoConfig, sonicConfig);
        
        // Convert static routes
        routeConverter.convert(ciscoConfig, sonicConfig);
        
        // Convert ACLs
        aclConverter.convert(ciscoConfig, sonicConfig);
        
        return sonicConfig;
    }
    
    /**
     * Convert device metadata (hostname, etc.)
     */
    private void convertDeviceMetadata(CiscoConfig ciscoConfig, SonicConfig sonicConfig) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        Map<String, Object> localhost = new LinkedHashMap<>();
        
        if (ciscoConfig.getHostname() != null) {
            localhost.put("hostname", ciscoConfig.getHostname());
        } else {
            localhost.put("hostname", "sonic");
        }
        
        localhost.put("platform", "x86_64-broadcom_enterprise_sonic");
        localhost.put("mac", "00:00:00:00:00:00");
        localhost.put("type", "LeafRouter");
        
        metadata.put("localhost", localhost);
        sonicConfig.setDeviceMetadata(metadata);
    }
}

