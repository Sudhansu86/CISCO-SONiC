package com.cisco.sonic.converter.model;

import java.util.*;

/**
 * Represents a VLAN configuration
 */
public class VlanConfig {
    private int vlanId;
    private String name;
    private String ipAddress;
    private String subnetMask;
    private boolean enabled;
    private List<String> memberPorts;
    
    public VlanConfig(int vlanId) {
        this.vlanId = vlanId;
        this.enabled = true;
        this.memberPorts = new ArrayList<>();
    }
    
    // Getters and Setters
    public int getVlanId() {
        return vlanId;
    }
    
    public void setVlanId(int vlanId) {
        this.vlanId = vlanId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getSubnetMask() {
        return subnetMask;
    }
    
    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public List<String> getMemberPorts() {
        return memberPorts;
    }
    
    public void addMemberPort(String port) {
        this.memberPorts.add(port);
    }
}

