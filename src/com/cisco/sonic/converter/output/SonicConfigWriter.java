package com.cisco.sonic.converter.output;

import com.cisco.sonic.converter.model.SonicConfig;
import java.io.*;
import java.util.*;

/**
 * Writes SONiC configuration to JSON format (config_db.json)
 */
public class SonicConfigWriter {
    
    /**
     * Write SONiC configuration to a file
     */
    public void writeToFile(SonicConfig config, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            write(config, writer);
        }
    }
    
    /**
     * Convert SONiC configuration to JSON string
     */
    public String writeToString(SonicConfig config) {
        StringWriter stringWriter = new StringWriter();
        try {
            write(config, stringWriter);
        } catch (IOException e) {
            // StringWriter doesn't throw IOException
            return "";
        }
        return stringWriter.toString();
    }
    
    /**
     * Write SONiC configuration to a Writer
     */
    private void write(SonicConfig config, Writer writer) throws IOException {
        writer.write("{\n");
        
        boolean firstSection = true;
        
        // Write DEVICE_METADATA
        if (!config.getDeviceMetadata().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"DEVICE_METADATA\": ");
            writeMap(config.getDeviceMetadata(), writer, 2);
            firstSection = false;
        }
        
        // Write PORT
        if (!config.getPorts().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"PORT\": ");
            writeNestedMap(config.getPorts(), writer, 2);
            firstSection = false;
        }
        
        // Write VLAN
        if (!config.getVlans().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"VLAN\": ");
            writeNestedMap(config.getVlans(), writer, 2);
            firstSection = false;
        }
        
        // Write VLAN_INTERFACE
        if (!config.getVlanInterfaces().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"VLAN_INTERFACE\": ");
            writeNestedMap(config.getVlanInterfaces(), writer, 2);
            firstSection = false;
        }
        
        // Write VLAN_MEMBER
        if (!config.getVlanMembers().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"VLAN_MEMBER\": ");
            writeNestedMap(config.getVlanMembers(), writer, 2);
            firstSection = false;
        }
        
        // Write INTERFACE
        if (!config.getInterfaces().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"INTERFACE\": ");
            writeNestedMap(config.getInterfaces(), writer, 2);
            firstSection = false;
        }
        
        // Write STATIC_ROUTE
        if (!config.getStaticRoutes().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"STATIC_ROUTE\": ");
            writeNestedMap(config.getStaticRoutes(), writer, 2);
            firstSection = false;
        }

        // Write PORTCHANNEL
        if (!config.getPortChannels().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"PORTCHANNEL\": ");
            writeNestedMap(config.getPortChannels(), writer, 2);
            firstSection = false;
        }

        // Write PORTCHANNEL_MEMBER
        if (!config.getPortChannelMembers().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"PORTCHANNEL_MEMBER\": ");
            writeNestedMap(config.getPortChannelMembers(), writer, 2);
            firstSection = false;
        }

        // Write ACL_TABLE
        if (!config.getAclTables().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"ACL_TABLE\": ");
            writeNestedMap(config.getAclTables(), writer, 2);
            firstSection = false;
        }
        
        // Write ACL_RULE
        if (!config.getAclRules().isEmpty()) {
            if (!firstSection) writer.write(",\n");
            writer.write("  \"ACL_RULE\": ");
            writeNestedMap(config.getAclRules(), writer, 2);
            firstSection = false;
        }
        
        writer.write("\n}\n");
    }
    
    /**
     * Write a nested map (Map<String, Map<String, Object>>)
     */
    private void writeNestedMap(Map<String, Map<String, Object>> map, Writer writer, int indent) throws IOException {
        writer.write("{\n");
        
        List<String> keys = new ArrayList<>(map.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Map<String, Object> value = map.get(key);
            
            writeIndent(writer, indent + 1);
            writer.write("\"" + escapeJson(key) + "\": ");
            writeMap(value, writer, indent + 1);
            
            if (i < keys.size() - 1) {
                writer.write(",");
            }
            writer.write("\n");
        }
        
        writeIndent(writer, indent);
        writer.write("}");
    }
    
    /**
     * Write a map (Map<String, Object>)
     */
    private void writeMap(Map<String, Object> map, Writer writer, int indent) throws IOException {
        writer.write("{\n");
        
        List<String> keys = new ArrayList<>(map.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = map.get(key);
            
            writeIndent(writer, indent + 1);
            writer.write("\"" + escapeJson(key) + "\": ");
            writeValue(value, writer, indent + 1);
            
            if (i < keys.size() - 1) {
                writer.write(",");
            }
            writer.write("\n");
        }
        
        writeIndent(writer, indent);
        writer.write("}");
    }

    /**
     * Write a value (can be String, Number, List, Map, etc.)
     */
    @SuppressWarnings("unchecked")
    private void writeValue(Object value, Writer writer, int indent) throws IOException {
        if (value == null || "NULL".equals(value)) {
            writer.write("null");
        } else if (value instanceof String) {
            writer.write("\"" + escapeJson((String) value) + "\"");
        } else if (value instanceof Number) {
            writer.write(value.toString());
        } else if (value instanceof Boolean) {
            writer.write(value.toString());
        } else if (value instanceof List) {
            writeList((List<?>) value, writer, indent);
        } else if (value instanceof Map) {
            writeMap((Map<String, Object>) value, writer, indent);
        } else {
            writer.write("\"" + escapeJson(value.toString()) + "\"");
        }
    }

    /**
     * Write a list
     */
    private void writeList(List<?> list, Writer writer, int indent) throws IOException {
        writer.write("[");

        for (int i = 0; i < list.size(); i++) {
            writeValue(list.get(i), writer, indent);
            if (i < list.size() - 1) {
                writer.write(", ");
            }
        }

        writer.write("]");
    }

    /**
     * Write indentation
     */
    private void writeIndent(Writer writer, int indent) throws IOException {
        for (int i = 0; i < indent; i++) {
            writer.write("  ");
        }
    }

    /**
     * Escape special characters in JSON strings
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }

        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
