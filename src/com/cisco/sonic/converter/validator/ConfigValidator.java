package com.cisco.sonic.converter.validator;

import com.cisco.sonic.converter.model.*;
import com.cisco.sonic.converter.util.NetworkUtils;
import java.util.*;

/**
 * Validates Cisco and SONiC configurations for common issues
 */
public class ConfigValidator {
    
    private List<String> errors;
    private List<String> warnings;
    
    public ConfigValidator() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    /**
     * Validate Cisco configuration
     */
    public ValidationResult validateCiscoConfig(CiscoConfig config) {
        errors.clear();
        warnings.clear();
        
        // Validate hostname
        if (config.getHostname() == null || config.getHostname().isEmpty()) {
            warnings.add("No hostname configured");
        }
        
        // Validate interfaces
        for (Map.Entry<String, InterfaceConfig> entry : config.getInterfaces().entrySet()) {
            validateInterface(entry.getKey(), entry.getValue());
        }
        
        // Validate VLANs
        for (Map.Entry<Integer, VlanConfig> entry : config.getVlans().entrySet()) {
            validateVlan(entry.getKey(), entry.getValue());
        }
        
        // Validate static routes
        for (RouteConfig route : config.getStaticRoutes()) {
            validateRoute(route);
        }
        
        return new ValidationResult(errors, warnings);
    }
    
    /**
     * Validate interface configuration
     */
    private void validateInterface(String name, InterfaceConfig config) {
        // Check for IP address without subnet mask
        if (config.getIpAddress() != null && config.getSubnetMask() == null) {
            errors.add("Interface " + name + " has IP address but no subnet mask");
        }
        
        // Validate IP address format
        if (config.getIpAddress() != null && !NetworkUtils.isValidIpAddress(config.getIpAddress())) {
            errors.add("Interface " + name + " has invalid IP address: " + config.getIpAddress());
        }
        
        // Check for conflicting configurations
        if (config.getIpAddress() != null && config.getAccessVlan() != null) {
            warnings.add("Interface " + name + " has both IP address and access VLAN configured");
        }
        
        // Check MTU range
        if (config.getMtu() < 64 || config.getMtu() > 9216) {
            warnings.add("Interface " + name + " has unusual MTU value: " + config.getMtu());
        }
    }
    
    /**
     * Validate VLAN configuration
     */
    private void validateVlan(Integer vlanId, VlanConfig config) {
        // Check VLAN ID range
        if (vlanId < 1 || vlanId > 4094) {
            errors.add("Invalid VLAN ID: " + vlanId + " (must be 1-4094)");
        }
        
        // Validate VLAN interface IP
        if (config.getIpAddress() != null) {
            if (!NetworkUtils.isValidIpAddress(config.getIpAddress())) {
                errors.add("VLAN " + vlanId + " has invalid IP address: " + config.getIpAddress());
            }
            if (config.getSubnetMask() == null) {
                errors.add("VLAN " + vlanId + " has IP address but no subnet mask");
            }
        }
    }
    
    /**
     * Validate static route
     */
    private void validateRoute(RouteConfig route) {
        // Validate network address
        if (!NetworkUtils.isValidIpAddress(route.getNetwork())) {
            errors.add("Invalid network address in static route: " + route.getNetwork());
        }
        
        // Validate netmask
        if (!NetworkUtils.isValidIpAddress(route.getNetmask())) {
            errors.add("Invalid netmask in static route: " + route.getNetmask());
        }
        
        // Validate next hop
        if (!NetworkUtils.isValidIpAddress(route.getNextHop())) {
            errors.add("Invalid next-hop address in static route: " + route.getNextHop());
        }
    }
    
    /**
     * Validate SONiC configuration
     */
    public ValidationResult validateSonicConfig(SonicConfig config) {
        errors.clear();
        warnings.clear();
        
        // Check for empty configuration
        if (config.getPorts().isEmpty() && config.getVlans().isEmpty() && 
            config.getInterfaces().isEmpty()) {
            warnings.add("Generated SONiC configuration appears to be empty");
        }
        
        // Validate VLAN members reference existing VLANs
        Set<String> definedVlans = config.getVlans().keySet();
        for (String memberKey : config.getVlanMembers().keySet()) {
            String vlanName = memberKey.split("\\|")[0];
            if (!definedVlans.contains(vlanName)) {
                errors.add("VLAN member references undefined VLAN: " + vlanName);
            }
        }
        
        return new ValidationResult(errors, warnings);
    }
    
    /**
     * Validation result container
     */
    public static class ValidationResult {
        private final List<String> errors;
        private final List<String> warnings;
        
        public ValidationResult(List<String> errors, List<String> warnings) {
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        public void printReport() {
            if (!errors.isEmpty()) {
                System.out.println("\n❌ Validation Errors:");
                for (String error : errors) {
                    System.out.println("  - " + error);
                }
            }
            
            if (!warnings.isEmpty()) {
                System.out.println("\n⚠️  Validation Warnings:");
                for (String warning : warnings) {
                    System.out.println("  - " + warning);
                }
            }
            
            if (errors.isEmpty() && warnings.isEmpty()) {
                System.out.println("\n✅ Validation passed with no issues");
            }
        }
    }
}

