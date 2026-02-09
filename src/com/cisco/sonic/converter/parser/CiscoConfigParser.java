package com.cisco.sonic.converter.parser;

import com.cisco.sonic.converter.model.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Parser for Cisco IOS/IOS-XE configuration files
 */
public class CiscoConfigParser {
    private static final Pattern INTERFACE_PATTERN = Pattern.compile("^interface\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern INTERFACE_RANGE_PATTERN = Pattern.compile("^interface\\s+range\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VLAN_PATTERN = Pattern.compile("^vlan\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VLAN_RANGE_PATTERN = Pattern.compile("^vlan\\s+([\\d,-]+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^hostname\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("^\\s*ip address\\s+(\\S+)\\s+(\\S+).*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern STATIC_ROUTE_PATTERN = Pattern.compile("^ip route\\s+(\\S+)\\s+(\\S+)\\s+(\\S+).*$", Pattern.CASE_INSENSITIVE);
    
    /**
     * Parse Cisco configuration from a file
     */
    public CiscoConfig parseFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return parse(reader);
        }
    }
    
    /**
     * Parse Cisco configuration from a string
     */
    public CiscoConfig parseString(String configText) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(configText))) {
            return parse(reader);
        }
    }
    
    /**
     * Main parsing logic
     */
    private CiscoConfig parse(BufferedReader reader) throws IOException {
        CiscoConfig config = new CiscoConfig();
        String line;
        String currentContext = null;
        InterfaceConfig currentInterface = null;
        VlanConfig currentVlan = null;
        List<InterfaceConfig> currentInterfaceRange = null;

        while ((line = reader.readLine()) != null) {
            config.addRawLine(line);
            String trimmedLine = line.trim();

            // Skip empty lines and comments
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("!")) {
                continue;
            }

            // Check for hostname
            Matcher hostnameMatcher = HOSTNAME_PATTERN.matcher(trimmedLine);
            if (hostnameMatcher.matches()) {
                config.setHostname(hostnameMatcher.group(1));
                continue;
            }

            // Check for interface range configuration
            Matcher interfaceRangeMatcher = INTERFACE_RANGE_PATTERN.matcher(trimmedLine);
            if (interfaceRangeMatcher.matches()) {
                String rangeSpec = interfaceRangeMatcher.group(1);
                currentInterfaceRange = expandInterfaceRange(rangeSpec, config);
                currentContext = "interface-range";
                currentInterface = null;
                currentVlan = null;
                continue;
            }

            // Check for interface configuration
            Matcher interfaceMatcher = INTERFACE_PATTERN.matcher(trimmedLine);
            if (interfaceMatcher.matches()) {
                String interfaceName = interfaceMatcher.group(1);
                currentInterface = new InterfaceConfig(interfaceName);
                config.addInterface(interfaceName, currentInterface);
                currentContext = "interface";
                currentInterfaceRange = null;
                currentVlan = null;
                continue;
            }

            // Check for VLAN range configuration
            Matcher vlanRangeMatcher = VLAN_RANGE_PATTERN.matcher(trimmedLine);
            if (vlanRangeMatcher.matches()) {
                String rangeSpec = vlanRangeMatcher.group(1);
                expandVlanRange(rangeSpec, config);
                currentContext = null;
                currentInterface = null;
                currentInterfaceRange = null;
                currentVlan = null;
                continue;
            }

            // Check for VLAN configuration
            Matcher vlanMatcher = VLAN_PATTERN.matcher(trimmedLine);
            if (vlanMatcher.matches()) {
                int vlanId = Integer.parseInt(vlanMatcher.group(1));
                currentVlan = new VlanConfig(vlanId);
                config.addVlan(vlanId, currentVlan);
                currentContext = "vlan";
                currentInterface = null;
                currentInterfaceRange = null;
                continue;
            }
            
            // Check for static route
            Matcher routeMatcher = STATIC_ROUTE_PATTERN.matcher(trimmedLine);
            if (routeMatcher.matches()) {
                String network = routeMatcher.group(1);
                String netmask = routeMatcher.group(2);
                String nextHop = routeMatcher.group(3);
                config.addStaticRoute(new RouteConfig(network, netmask, nextHop));
                continue;
            }
            
            // Parse context-specific commands
            if (currentContext != null) {
                if (currentContext.equals("interface") && currentInterface != null) {
                    parseInterfaceCommand(trimmedLine, currentInterface);
                } else if (currentContext.equals("interface-range") && currentInterfaceRange != null) {
                    // Apply command to all interfaces in range
                    for (InterfaceConfig iface : currentInterfaceRange) {
                        parseInterfaceCommand(trimmedLine, iface);
                    }
                } else if (currentContext.equals("vlan") && currentVlan != null) {
                    parseVlanCommand(trimmedLine, currentVlan);
                }
            }
            
            // Parse routing protocols
            parseRoutingProtocol(trimmedLine, config);
        }
        
        return config;
    }
    
    /**
     * Parse interface-specific commands
     */
    private void parseInterfaceCommand(String line, InterfaceConfig interfaceConfig) {
        // IP address
        Matcher ipMatcher = IP_ADDRESS_PATTERN.matcher(line);
        if (ipMatcher.matches()) {
            interfaceConfig.setIpAddress(ipMatcher.group(1));
            interfaceConfig.setSubnetMask(ipMatcher.group(2));
            return;
        }
        
        // Description
        if (line.startsWith("description ")) {
            interfaceConfig.setDescription(line.substring(12).trim());
            return;
        }
        
        // Shutdown
        if (line.equals("shutdown")) {
            interfaceConfig.setEnabled(false);
            return;
        }
        
        if (line.equals("no shutdown")) {
            interfaceConfig.setEnabled(true);
            return;
        }
        
        // MTU
        if (line.startsWith("mtu ")) {
            try {
                interfaceConfig.setMtu(Integer.parseInt(line.substring(4).trim()));
            } catch (NumberFormatException e) {
                // Ignore invalid MTU
            }
            return;
        }
        
        // Switchport mode
        if (line.startsWith("switchport mode ")) {
            String mode = line.substring(16).trim();
            interfaceConfig.setMode(mode);
            return;
        }
        
        // Switchport access VLAN
        if (line.startsWith("switchport access vlan ")) {
            try {
                interfaceConfig.setAccessVlan(Integer.parseInt(line.substring(23).trim()));
            } catch (NumberFormatException e) {
                // Ignore invalid VLAN
            }
            return;
        }

        // Switchport trunk allowed VLANs
        if (line.startsWith("switchport trunk allowed vlan ")) {
            String vlanList = line.substring(30).trim();
            parseVlanList(vlanList, interfaceConfig);
            return;
        }

        // Channel-group (Port-Channel/LAG membership)
        if (line.startsWith("channel-group ")) {
            // Parse: "channel-group 1 mode active"
            Pattern pattern = Pattern.compile("channel-group\\s+(\\d+)\\s+mode\\s+(\\S+)");
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                interfaceConfig.setChannelGroup(Integer.parseInt(matcher.group(1)));
                interfaceConfig.setLacpMode(matcher.group(2));
            }
            return;
        }

        // Store other commands for reference
        interfaceConfig.addAdditionalCommand(line);
    }

    /**
     * Parse VLAN list (e.g., "10,20,30,40,50" or "10-15,20,30-35")
     */
    private void parseVlanList(String vlanList, InterfaceConfig interfaceConfig) {
        // Split by comma
        String[] parts = vlanList.split(",");

        for (String part : parts) {
            part = part.trim();

            // Check if it's a range (e.g., "10-15")
            if (part.contains("-")) {
                String[] range = part.split("-");
                try {
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    for (int vlanId = start; vlanId <= end; vlanId++) {
                        interfaceConfig.addAllowedVlan(vlanId);
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid range
                }
            } else {
                // Single VLAN ID
                try {
                    int vlanId = Integer.parseInt(part);
                    interfaceConfig.addAllowedVlan(vlanId);
                } catch (NumberFormatException e) {
                    // Ignore invalid VLAN
                }
            }
        }
    }

    /**
     * Parse VLAN-specific commands
     */
    private void parseVlanCommand(String line, VlanConfig vlanConfig) {
        // VLAN name
        if (line.startsWith("name ")) {
            vlanConfig.setName(line.substring(5).trim());
            return;
        }

        // VLAN state
        if (line.equals("shutdown")) {
            vlanConfig.setEnabled(false);
            return;
        }

        if (line.equals("no shutdown")) {
            vlanConfig.setEnabled(true);
            return;
        }
    }

    /**
     * Parse routing protocol configurations
     */
    private void parseRoutingProtocol(String line, CiscoConfig config) {
        // OSPF configuration
        if (line.startsWith("router ospf ")) {
            try {
                int processId = Integer.parseInt(line.substring(12).trim());
                config.getRoutingConfig().getOspf().setProcessId(processId);
            } catch (NumberFormatException e) {
                // Ignore invalid process ID
            }
            return;
        }

        // BGP configuration
        if (line.startsWith("router bgp ")) {
            try {
                int asNumber = Integer.parseInt(line.substring(11).trim());
                config.getRoutingConfig().getBgp().setAsNumber(asNumber);
            } catch (NumberFormatException e) {
                // Ignore invalid AS number
            }
            return;
        }
    }

    /**
     * Expand interface range specification into individual interfaces
     * Examples:
     *   "GigabitEthernet0/1-10" -> Gi0/1, Gi0/2, ..., Gi0/10
     *   "GigabitEthernet0/1-5, GigabitEthernet0/10-15" -> Multiple ranges
     */
    private List<InterfaceConfig> expandInterfaceRange(String rangeSpec, CiscoConfig config) {
        List<InterfaceConfig> interfaces = new ArrayList<>();

        // Split by comma for multiple ranges
        String[] ranges = rangeSpec.split(",");

        for (String range : ranges) {
            range = range.trim();

            // Check if it's a range (contains hyphen)
            if (range.contains("-")) {
                // Extract interface type and range
                // Example: "GigabitEthernet0/1-10"
                Pattern rangePattern = Pattern.compile("^([A-Za-z-]+)(\\d+)/(\\d+)-(\\d+)$");
                Matcher matcher = rangePattern.matcher(range);

                if (matcher.matches()) {
                    String interfaceType = matcher.group(1);  // "GigabitEthernet"
                    String module = matcher.group(2);          // "0"
                    int startPort = Integer.parseInt(matcher.group(3));  // 1
                    int endPort = Integer.parseInt(matcher.group(4));    // 10

                    // Create interface for each port in range
                    for (int port = startPort; port <= endPort; port++) {
                        String interfaceName = interfaceType + module + "/" + port;
                        InterfaceConfig iface = new InterfaceConfig(interfaceName);
                        config.addInterface(interfaceName, iface);
                        interfaces.add(iface);
                    }
                } else {
                    // Try simpler pattern without module: "Ethernet1-10"
                    Pattern simplePattern = Pattern.compile("^([A-Za-z-]+)(\\d+)-(\\d+)$");
                    Matcher simpleMatcher = simplePattern.matcher(range);

                    if (simpleMatcher.matches()) {
                        String interfaceType = simpleMatcher.group(1);  // "Ethernet"
                        int startPort = Integer.parseInt(simpleMatcher.group(2));  // 1
                        int endPort = Integer.parseInt(simpleMatcher.group(3));    // 10

                        for (int port = startPort; port <= endPort; port++) {
                            String interfaceName = interfaceType + port;
                            InterfaceConfig iface = new InterfaceConfig(interfaceName);
                            config.addInterface(interfaceName, iface);
                            interfaces.add(iface);
                        }
                    }
                }
            } else {
                // Single interface (no range)
                InterfaceConfig iface = new InterfaceConfig(range);
                config.addInterface(range, iface);
                interfaces.add(iface);
            }
        }

        return interfaces;
    }

    /**
     * Expand VLAN range specification into individual VLANs
     * Examples:
     *   "10-20" -> VLANs 10, 11, 12, ..., 20
     *   "10,20,30-35" -> VLANs 10, 20, 30, 31, 32, 33, 34, 35
     */
    private void expandVlanRange(String rangeSpec, CiscoConfig config) {
        // Split by comma
        String[] parts = rangeSpec.split(",");

        for (String part : parts) {
            part = part.trim();

            // Check if it's a range (contains hyphen)
            if (part.contains("-")) {
                String[] range = part.split("-");
                try {
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());

                    for (int vlanId = start; vlanId <= end; vlanId++) {
                        VlanConfig vlan = new VlanConfig(vlanId);
                        config.addVlan(vlanId, vlan);
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid range
                }
            } else {
                // Single VLAN ID
                try {
                    int vlanId = Integer.parseInt(part);
                    VlanConfig vlan = new VlanConfig(vlanId);
                    config.addVlan(vlanId, vlan);
                } catch (NumberFormatException e) {
                    // Ignore invalid VLAN
                }
            }
        }
    }
}

