package com.cisco.sonic.converter.model;

import java.util.*;

/**
 * Represents a network interface configuration
 */
public class InterfaceConfig {
    private String name;
    private String description;
    private boolean enabled;
    private String ipAddress;
    private String subnetMask;
    private int mtu;
    private List<Integer> allowedVlans;
    private Integer accessVlan;
    private String mode; // access, trunk, routed
    private Integer speed;
    private String duplex;
    private boolean portSecurity;
    private Integer channelGroup;  // Port-Channel number (e.g., 1 for Port-channel1)
    private String lacpMode;       // LACP mode: active, passive, on
    private List<String> additionalCommands;
    
    public InterfaceConfig(String name) {
        this.name = name;
        this.enabled = true;
        this.mtu = 1500;
        this.allowedVlans = new ArrayList<>();
        this.additionalCommands = new ArrayList<>();
        this.mode = "routed";
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
    
    public int getMtu() {
        return mtu;
    }
    
    public void setMtu(int mtu) {
        this.mtu = mtu;
    }
    
    public List<Integer> getAllowedVlans() {
        return allowedVlans;
    }
    
    public void addAllowedVlan(Integer vlanId) {
        this.allowedVlans.add(vlanId);
    }
    
    public Integer getAccessVlan() {
        return accessVlan;
    }
    
    public void setAccessVlan(Integer accessVlan) {
        this.accessVlan = accessVlan;
    }
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public Integer getSpeed() {
        return speed;
    }
    
    public void setSpeed(Integer speed) {
        this.speed = speed;
    }
    
    public String getDuplex() {
        return duplex;
    }
    
    public void setDuplex(String duplex) {
        this.duplex = duplex;
    }
    
    public boolean isPortSecurity() {
        return portSecurity;
    }
    
    public void setPortSecurity(boolean portSecurity) {
        this.portSecurity = portSecurity;
    }
    
    public List<String> getAdditionalCommands() {
        return additionalCommands;
    }

    public void addAdditionalCommand(String command) {
        this.additionalCommands.add(command);
    }

    public Integer getChannelGroup() {
        return channelGroup;
    }

    public void setChannelGroup(Integer channelGroup) {
        this.channelGroup = channelGroup;
    }

    public String getLacpMode() {
        return lacpMode;
    }

    public void setLacpMode(String lacpMode) {
        this.lacpMode = lacpMode;
    }
}

