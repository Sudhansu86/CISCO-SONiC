package com.cisco.sonic.converter.util;

/**
 * Utility class for network-related conversions
 */
public class NetworkUtils {
    
    /**
     * Convert IP address and subnet mask to CIDR notation
     * Example: 192.168.1.1 255.255.255.0 -> 192.168.1.1/24
     */
    public static String toCidr(String ipAddress, String subnetMask) {
        if (ipAddress == null || subnetMask == null) {
            return null;
        }
        
        int prefixLength = subnetMaskToPrefixLength(subnetMask);
        return ipAddress + "/" + prefixLength;
    }
    
    /**
     * Convert subnet mask to prefix length
     * Example: 255.255.255.0 -> 24
     */
    public static int subnetMaskToPrefixLength(String subnetMask) {
        String[] octets = subnetMask.split("\\.");
        if (octets.length != 4) {
            return 32; // Default to /32 if invalid
        }
        
        int prefixLength = 0;
        for (String octet : octets) {
            try {
                int value = Integer.parseInt(octet);
                prefixLength += Integer.bitCount(value);
            } catch (NumberFormatException e) {
                return 32;
            }
        }
        
        return prefixLength;
    }
    
    /**
     * Convert wildcard mask to subnet mask
     * Example: 0.0.0.255 -> 255.255.255.0
     */
    public static String wildcardToSubnetMask(String wildcardMask) {
        String[] octets = wildcardMask.split("\\.");
        if (octets.length != 4) {
            return "255.255.255.255";
        }
        
        StringBuilder subnetMask = new StringBuilder();
        for (int i = 0; i < octets.length; i++) {
            try {
                int value = Integer.parseInt(octets[i]);
                int inverted = 255 - value;
                subnetMask.append(inverted);
                if (i < octets.length - 1) {
                    subnetMask.append(".");
                }
            } catch (NumberFormatException e) {
                return "255.255.255.255";
            }
        }
        
        return subnetMask.toString();
    }
    
    /**
     * Validate IP address format
     */
    public static boolean isValidIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }
        
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            return false;
        }
        
        for (String octet : octets) {
            try {
                int value = Integer.parseInt(octet);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        return true;
    }
}

