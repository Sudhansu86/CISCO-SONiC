package com.cisco.sonic.converter.converter;

import com.cisco.sonic.converter.model.*;
import java.util.*;

/**
 * Converts Cisco ACL configurations to SONiC format
 */
public class AclConverter {
    
    /**
     * Convert ACLs from Cisco to SONiC
     */
    public void convert(CiscoConfig ciscoConfig, SonicConfig sonicConfig) {
        for (Map.Entry<String, AccessListConfig> entry : ciscoConfig.getAccessLists().entrySet()) {
            String aclName = entry.getKey();
            AccessListConfig aclConfig = entry.getValue();
            
            // Create ACL table
            Map<String, Object> tableData = new LinkedHashMap<>();
            tableData.put("type", "L3");
            tableData.put("policy_desc", aclName);
            
            List<String> ports = new ArrayList<>();
            // Note: In real implementation, you'd need to determine which ports this ACL applies to
            tableData.put("ports", ports);
            
            sonicConfig.addAclTable(aclName, tableData);
            
            // Convert ACL entries
            int sequenceNum = 10;
            for (AccessListConfig.AclEntry aclEntry : aclConfig.getEntries()) {
                String ruleKey = aclName + "|RULE_" + sequenceNum;
                
                Map<String, Object> ruleData = new LinkedHashMap<>();
                ruleData.put("PACKET_ACTION", aclEntry.getAction().toUpperCase());
                ruleData.put("PRIORITY", String.valueOf(sequenceNum));
                
                if (aclEntry.getProtocol() != null) {
                    ruleData.put("IP_PROTOCOL", aclEntry.getProtocol());
                }
                
                if (aclEntry.getSourceIp() != null) {
                    ruleData.put("SRC_IP", aclEntry.getSourceIp());
                }
                
                if (aclEntry.getDestIp() != null) {
                    ruleData.put("DST_IP", aclEntry.getDestIp());
                }
                
                sonicConfig.addAclRule(ruleKey, ruleData);
                sequenceNum += 10;
            }
        }
    }
}

