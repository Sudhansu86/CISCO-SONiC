package com.cisco.sonic.converter.model;

import java.util.*;

/**
 * Represents routing protocol configurations (OSPF, BGP, etc.)
 */
public class RoutingConfig {
    private OspfConfig ospf;
    private BgpConfig bgp;
    
    public RoutingConfig() {
        this.ospf = new OspfConfig();
        this.bgp = new BgpConfig();
    }
    
    public OspfConfig getOspf() {
        return ospf;
    }
    
    public void setOspf(OspfConfig ospf) {
        this.ospf = ospf;
    }
    
    public BgpConfig getBgp() {
        return bgp;
    }
    
    public void setBgp(BgpConfig bgp) {
        this.bgp = bgp;
    }
    
    public static class OspfConfig {
        private Integer processId;
        private String routerId;
        private List<NetworkStatement> networks;
        
        public OspfConfig() {
            this.networks = new ArrayList<>();
        }
        
        public Integer getProcessId() {
            return processId;
        }
        
        public void setProcessId(Integer processId) {
            this.processId = processId;
        }
        
        public String getRouterId() {
            return routerId;
        }
        
        public void setRouterId(String routerId) {
            this.routerId = routerId;
        }
        
        public List<NetworkStatement> getNetworks() {
            return networks;
        }
        
        public void addNetwork(String network, String wildcardMask, Integer area) {
            this.networks.add(new NetworkStatement(network, wildcardMask, area));
        }
    }
    
    public static class NetworkStatement {
        private String network;
        private String wildcardMask;
        private Integer area;
        
        public NetworkStatement(String network, String wildcardMask, Integer area) {
            this.network = network;
            this.wildcardMask = wildcardMask;
            this.area = area;
        }
        
        public String getNetwork() {
            return network;
        }
        
        public String getWildcardMask() {
            return wildcardMask;
        }
        
        public Integer getArea() {
            return area;
        }
    }
    
    public static class BgpConfig {
        private Integer asNumber;
        private String routerId;
        private List<BgpNeighbor> neighbors;
        private List<String> networks;
        
        public BgpConfig() {
            this.neighbors = new ArrayList<>();
            this.networks = new ArrayList<>();
        }
        
        public Integer getAsNumber() {
            return asNumber;
        }
        
        public void setAsNumber(Integer asNumber) {
            this.asNumber = asNumber;
        }
        
        public String getRouterId() {
            return routerId;
        }
        
        public void setRouterId(String routerId) {
            this.routerId = routerId;
        }
        
        public List<BgpNeighbor> getNeighbors() {
            return neighbors;
        }
        
        public void addNeighbor(BgpNeighbor neighbor) {
            this.neighbors.add(neighbor);
        }
        
        public List<String> getNetworks() {
            return networks;
        }
        
        public void addNetwork(String network) {
            this.networks.add(network);
        }
    }
    
    public static class BgpNeighbor {
        private String ipAddress;
        private Integer remoteAs;
        
        public BgpNeighbor(String ipAddress, Integer remoteAs) {
            this.ipAddress = ipAddress;
            this.remoteAs = remoteAs;
        }
        
        public String getIpAddress() {
            return ipAddress;
        }
        
        public Integer getRemoteAs() {
            return remoteAs;
        }
    }
}

