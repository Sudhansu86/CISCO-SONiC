package com.cisco.sonic.converter.model;

import java.util.*;

/**
 * Represents a SONiC configuration structure (config_db.json format)
 * Designed for Broadcom Enterprise SONiC
 */
public class SonicConfig {
    private Map<String, Object> deviceMetadata;
    private Map<String, Map<String, Object>> ports;
    private Map<String, Map<String, Object>> vlans;
    private Map<String, Map<String, Object>> vlanInterfaces;
    private Map<String, Map<String, Object>> vlanMembers;
    private Map<String, Map<String, Object>> interfaces;
    private Map<String, Map<String, Object>> staticRoutes;
    private Map<String, Map<String, Object>> portChannels;
    private Map<String, Map<String, Object>> portChannelMembers;
    private Map<String, Map<String, Object>> aclTables;
    private Map<String, Map<String, Object>> aclRules;
    private List<String> warnings;
    private List<String> unsupportedFeatures;
    
    public SonicConfig() {
        this.deviceMetadata = new LinkedHashMap<>();
        this.ports = new LinkedHashMap<>();
        this.vlans = new LinkedHashMap<>();
        this.vlanInterfaces = new LinkedHashMap<>();
        this.vlanMembers = new LinkedHashMap<>();
        this.interfaces = new LinkedHashMap<>();
        this.staticRoutes = new LinkedHashMap<>();
        this.portChannels = new LinkedHashMap<>();
        this.portChannelMembers = new LinkedHashMap<>();
        this.aclTables = new LinkedHashMap<>();
        this.aclRules = new LinkedHashMap<>();
        this.warnings = new ArrayList<>();
        this.unsupportedFeatures = new ArrayList<>();
    }
    
    // Getters and Setters
    public Map<String, Object> getDeviceMetadata() {
        return deviceMetadata;
    }
    
    public void setDeviceMetadata(Map<String, Object> deviceMetadata) {
        this.deviceMetadata = deviceMetadata;
    }
    
    public Map<String, Map<String, Object>> getPorts() {
        return ports;
    }
    
    public void addPort(String portName, Map<String, Object> config) {
        this.ports.put(portName, config);
    }
    
    public Map<String, Map<String, Object>> getVlans() {
        return vlans;
    }
    
    public void addVlan(String vlanName, Map<String, Object> config) {
        this.vlans.put(vlanName, config);
    }
    
    public Map<String, Map<String, Object>> getVlanInterfaces() {
        return vlanInterfaces;
    }
    
    public void addVlanInterface(String vlanInterfaceName, Map<String, Object> config) {
        this.vlanInterfaces.put(vlanInterfaceName, config);
    }
    
    public Map<String, Map<String, Object>> getVlanMembers() {
        return vlanMembers;
    }
    
    public void addVlanMember(String memberKey, Map<String, Object> config) {
        this.vlanMembers.put(memberKey, config);
    }
    
    public Map<String, Map<String, Object>> getInterfaces() {
        return interfaces;
    }
    
    public void addInterface(String interfaceName, Map<String, Object> config) {
        this.interfaces.put(interfaceName, config);
    }
    
    public Map<String, Map<String, Object>> getStaticRoutes() {
        return staticRoutes;
    }

    public void addStaticRoute(String routeKey, Map<String, Object> config) {
        this.staticRoutes.put(routeKey, config);
    }

    public Map<String, Map<String, Object>> getPortChannels() {
        return portChannels;
    }

    public void addPortChannel(String portChannelName, Map<String, Object> config) {
        this.portChannels.put(portChannelName, config);
    }

    public Map<String, Map<String, Object>> getPortChannelMembers() {
        return portChannelMembers;
    }

    public void addPortChannelMember(String memberKey, Map<String, Object> config) {
        this.portChannelMembers.put(memberKey, config);
    }

    public Map<String, Map<String, Object>> getAclTables() {
        return aclTables;
    }
    
    public void addAclTable(String tableName, Map<String, Object> config) {
        this.aclTables.put(tableName, config);
    }
    
    public Map<String, Map<String, Object>> getAclRules() {
        return aclRules;
    }
    
    public void addAclRule(String ruleKey, Map<String, Object> config) {
        this.aclRules.put(ruleKey, config);
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public void addWarning(String warning) {
        this.warnings.add(warning);
    }
    
    public List<String> getUnsupportedFeatures() {
        return unsupportedFeatures;
    }
    
    public void addUnsupportedFeature(String feature) {
        this.unsupportedFeatures.add(feature);
    }
}

