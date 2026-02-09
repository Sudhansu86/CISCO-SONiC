package com.cisco.sonic.converter.model;

/**
 * Represents a static route configuration
 */
public class RouteConfig {
    private String network;
    private String netmask;
    private String nextHop;
    private Integer adminDistance;
    private String exitInterface;
    
    public RouteConfig(String network, String netmask, String nextHop) {
        this.network = network;
        this.netmask = netmask;
        this.nextHop = nextHop;
    }
    
    // Getters and Setters
    public String getNetwork() {
        return network;
    }
    
    public void setNetwork(String network) {
        this.network = network;
    }
    
    public String getNetmask() {
        return netmask;
    }
    
    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }
    
    public String getNextHop() {
        return nextHop;
    }
    
    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }
    
    public Integer getAdminDistance() {
        return adminDistance;
    }
    
    public void setAdminDistance(Integer adminDistance) {
        this.adminDistance = adminDistance;
    }
    
    public String getExitInterface() {
        return exitInterface;
    }
    
    public void setExitInterface(String exitInterface) {
        this.exitInterface = exitInterface;
    }
}

