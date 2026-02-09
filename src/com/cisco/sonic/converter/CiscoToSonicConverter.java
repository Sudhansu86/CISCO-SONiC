package com.cisco.sonic.converter;

import com.cisco.sonic.converter.converter.ConfigConverter;
import com.cisco.sonic.converter.model.CiscoConfig;
import com.cisco.sonic.converter.model.SonicConfig;
import com.cisco.sonic.converter.output.SonicConfigWriter;
import com.cisco.sonic.converter.parser.CiscoConfigParser;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

/**
 * Main application class for Cisco to SONiC configuration converter
 * Supports Broadcom Enterprise SONiC as the target platform
 */
public class CiscoToSonicConverter {
    
    private final CiscoConfigParser parser;
    private final ConfigConverter converter;
    private final SonicConfigWriter writer;
    
    public CiscoToSonicConverter() {
        this.parser = new CiscoConfigParser();
        this.converter = new ConfigConverter();
        this.writer = new SonicConfigWriter();
    }
    
    /**
     * Convert a Cisco configuration file to SONiC format
     */
    public void convertFile(String inputPath, String outputPath) throws IOException {
        System.out.println("Reading Cisco configuration from: " + inputPath);
        
        // Parse Cisco configuration
        CiscoConfig ciscoConfig = parser.parseFile(inputPath);
        System.out.println("Successfully parsed Cisco configuration");
        System.out.println("  - Hostname: " + ciscoConfig.getHostname());
        System.out.println("  - Interfaces: " + ciscoConfig.getInterfaces().size());
        System.out.println("  - VLANs: " + ciscoConfig.getVlans().size());
        System.out.println("  - Static Routes: " + ciscoConfig.getStaticRoutes().size());
        
        // Convert to SONiC
        System.out.println("\nConverting to SONiC format...");
        SonicConfig sonicConfig = converter.convert(ciscoConfig);
        
        // Display warnings if any
        if (!sonicConfig.getWarnings().isEmpty()) {
            System.out.println("\nWarnings:");
            for (String warning : sonicConfig.getWarnings()) {
                System.out.println("  - " + warning);
            }
        }
        
        // Display unsupported features if any
        if (!sonicConfig.getUnsupportedFeatures().isEmpty()) {
            System.out.println("\nUnsupported Features:");
            for (String feature : sonicConfig.getUnsupportedFeatures()) {
                System.out.println("  - " + feature);
            }
        }
        
        // Write SONiC configuration
        System.out.println("\nWriting SONiC configuration to: " + outputPath);
        writer.writeToFile(sonicConfig, outputPath);
        System.out.println("Conversion completed successfully!");
    }
    
    /**
     * Convert Cisco configuration text to SONiC format
     */
    public String convertText(String ciscoConfigText) throws IOException {
        // Parse Cisco configuration
        CiscoConfig ciscoConfig = parser.parseString(ciscoConfigText);
        
        // Convert to SONiC
        SonicConfig sonicConfig = converter.convert(ciscoConfig);
        
        // Return as JSON string
        return writer.writeToString(sonicConfig);
    }
    
    /**
     * Interactive mode - allows user to paste configuration
     */
    public void interactiveMode() throws IOException {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Cisco to SONiC Configuration Converter ===");
        System.out.println("Target Platform: Broadcom Enterprise SONiC\n");
        System.out.println("Choose input method:");
        System.out.println("1. Load from file");
        System.out.println("2. Paste configuration text");
        System.out.print("\nEnter choice (1 or 2): ");
        
        String choice = scanner.nextLine().trim();
        
        if ("1".equals(choice)) {
            System.out.print("Enter path to Cisco configuration file: ");
            String inputPath = scanner.nextLine().trim();
            
            System.out.print("Enter path for output SONiC configuration file (default: config_db.json): ");
            String outputPath = scanner.nextLine().trim();
            if (outputPath.isEmpty()) {
                outputPath = "config_db.json";
            }
            
            convertFile(inputPath, outputPath);
            
        } else if ("2".equals(choice)) {
            System.out.println("\nPaste your Cisco configuration below.");
            System.out.println("Enter 'END' on a new line when finished:\n");
            
            StringBuilder configText = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).equals("END")) {
                configText.append(line).append("\n");
            }
            
            System.out.print("\nEnter path for output file (default: config_db.json): ");
            String outputPath = scanner.nextLine().trim();
            if (outputPath.isEmpty()) {
                outputPath = "config_db.json";
            }
            
            // Parse and convert
            CiscoConfig ciscoConfig = parser.parseString(configText.toString());
            SonicConfig sonicConfig = converter.convert(ciscoConfig);
            
            // Display summary
            System.out.println("\nConversion Summary:");
            System.out.println("  - Hostname: " + ciscoConfig.getHostname());
            System.out.println("  - Interfaces: " + ciscoConfig.getInterfaces().size());
            System.out.println("  - VLANs: " + ciscoConfig.getVlans().size());
            System.out.println("  - Static Routes: " + ciscoConfig.getStaticRoutes().size());
            
            // Write output
            writer.writeToFile(sonicConfig, outputPath);
            System.out.println("\nConfiguration written to: " + outputPath);
            
        } else {
            System.out.println("Invalid choice. Exiting.");
        }
    }
    
    /**
     * Main entry point
     */
    public static void main(String[] args) {
        CiscoToSonicConverter converter = new CiscoToSonicConverter();

        try {
            if (args.length >= 2) {
                // Command-line mode with file paths
                String inputPath = args[0];
                String outputPath = args[1];
                converter.convertFile(inputPath, outputPath);
            } else if (args.length == 1) {
                // Check for GUI flag
                if ("--gui".equals(args[0]) || "-g".equals(args[0])) {
                    // Launch GUI
                    launchGUI();
                } else {
                    // Single argument - input file, default output
                    String inputPath = args[0];
                    String outputPath = "config_db.json";
                    converter.convertFile(inputPath, outputPath);
                }
            } else {
                // No arguments - show usage and offer GUI
                showUsage();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Launch the GUI application
     */
    private static void launchGUI() {
        try {
            Class<?> guiClass = Class.forName("com.cisco.sonic.converter.gui.ConverterGUI");
            java.lang.reflect.Method mainMethod = guiClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[0]);
        } catch (Exception e) {
            System.err.println("Error launching GUI: " + e.getMessage());
            System.err.println("Make sure the GUI classes are compiled.");
            System.exit(1);
        }
    }

    /**
     * Show usage information
     */
    private static void showUsage() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   Cisco to SONiC Configuration Converter                  ║");
        System.out.println("║   Target Platform: Broadcom Enterprise SONiC              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java CiscoToSonicConverter [options] [input] [output]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --gui, -g              Launch graphical user interface");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  # Launch GUI");
        System.out.println("  java CiscoToSonicConverter --gui");
        System.out.println();
        System.out.println("  # Convert with default output (config_db.json)");
        System.out.println("  java CiscoToSonicConverter cisco-config.txt");
        System.out.println();
        System.out.println("  # Convert with custom output");
        System.out.println("  java CiscoToSonicConverter cisco-config.txt sonic-config.json");
        System.out.println();
        System.out.println("Would you like to:");
        System.out.println("  1. Launch GUI");
        System.out.println("  2. Use interactive mode");
        System.out.println("  3. Exit");
        System.out.print("\nEnter choice (1-3): ");

        try {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                launchGUI();
            } else if ("2".equals(choice)) {
                CiscoToSonicConverter converter = new CiscoToSonicConverter();
                converter.interactiveMode();
            } else {
                System.out.println("Exiting...");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

