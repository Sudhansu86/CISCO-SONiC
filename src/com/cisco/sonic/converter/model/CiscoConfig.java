package com.cisco.sonic.converter.model;

import java.util.*;

/**
 * Represents a parsed Cisco configuration with all network elements
 */
public class CiscoConfig {
    private String hostname;
    private Map<String, InterfaceConfig> interfaces;
    private Map<Integer, VlanConfig> vlans;
    private List<RouteConfig> staticRoutes;
    private List<String> rawLines;
    private RoutingConfig routingConfig;
    private Map<String, AccessListConfig> accessLists;
    
    public CiscoConfig() {
        this.interfaces = new LinkedHashMap<>();
        this.vlans = new LinkedHashMap<>();
        this.staticRoutes = new ArrayList<>();
        this.rawLines = new ArrayList<>();
        this.accessLists = new LinkedHashMap<>();
        this.routingConfig = new RoutingConfig();
    }
    
    // Getters and Setters
    public String getHostname() {
        return hostname;
    }
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    public Map<String, InterfaceConfig> getInterfaces() {
        return interfaces;
    }
    
    public void addInterface(String name, InterfaceConfig config) {
        this.interfaces.put(name, config);
    }
    
    public Map<Integer, VlanConfig> getVlans() {
        return vlans;
    }
    
    public void addVlan(Integer vlanId, VlanConfig config) {
        this.vlans.put(vlanId, config);
    }
    
    public List<RouteConfig> getStaticRoutes() {
        return staticRoutes;
    }
    
    public void addStaticRoute(RouteConfig route) {
        this.staticRoutes.add(route);
    }
    
    public List<String> getRawLines() {
        return rawLines;
    }
    
    public void addRawLine(String line) {
        this.rawLines.add(line);
    }
    
    public RoutingConfig getRoutingConfig() {
        return routingConfig;
    }
    
    public void setRoutingConfig(RoutingConfig routingConfig) {
        this.routingConfig = routingConfig;
    }
    
    public Map<String, AccessListConfig> getAccessLists() {
        return accessLists;
    }
    
    public void addAccessList(String name, AccessListConfig acl) {
        this.accessLists.put(name, acl);
    }
}

