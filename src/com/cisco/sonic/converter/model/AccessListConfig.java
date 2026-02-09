package com.cisco.sonic.converter.model;

import java.util.*;

/**
 * Represents an Access Control List configuration
 */
public class AccessListConfig {
    private String name;
    private String type; // standard, extended
    private List<AclEntry> entries;
    
    public AccessListConfig(String name, String type) {
        this.name = name;
        this.type = type;
        this.entries = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public List<AclEntry> getEntries() {
        return entries;
    }
    
    public void addEntry(AclEntry entry) {
        this.entries.add(entry);
    }
    
    public static class AclEntry {
        private String action; // permit, deny
        private String protocol;
        private String sourceIp;
        private String sourceWildcard;
        private String destIp;
        private String destWildcard;
        private Integer sequenceNumber;
        
        public AclEntry(String action) {
            this.action = action;
        }
        
        public String getAction() {
            return action;
        }
        
        public void setAction(String action) {
            this.action = action;
        }
        
        public String getProtocol() {
            return protocol;
        }
        
        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }
        
        public String getSourceIp() {
            return sourceIp;
        }
        
        public void setSourceIp(String sourceIp) {
            this.sourceIp = sourceIp;
        }
        
        public String getSourceWildcard() {
            return sourceWildcard;
        }
        
        public void setSourceWildcard(String sourceWildcard) {
            this.sourceWildcard = sourceWildcard;
        }
        
        public String getDestIp() {
            return destIp;
        }
        
        public void setDestIp(String destIp) {
            this.destIp = destIp;
        }
        
        public String getDestWildcard() {
            return destWildcard;
        }
        
        public void setDestWildcard(String destWildcard) {
            this.destWildcard = destWildcard;
        }
        
        public Integer getSequenceNumber() {
            return sequenceNumber;
        }
        
        public void setSequenceNumber(Integer sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
    }
}

