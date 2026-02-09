import com.cisco.sonic.converter.CiscoToSonicConverter;

/**
 * Main entry point for the Cisco to SONiC Configuration Converter
 *
 * This application converts Cisco IOS/IOS-XE network configurations to
 * SONiC (Software for Open Networking in the Cloud) format, specifically
 * targeting Broadcom Enterprise SONiC.
 *
 * Usage:
 *   - No arguments: Interactive mode
 *   - One argument: Input file path (output defaults to config_db.json)
 *   - Two arguments: Input file path and output file path
 */
void main(String[] args) {
    System.out.println("╔════════════════════════════════════════════════════════════╗");
    System.out.println("║   Cisco to SONiC Configuration Converter                  ║");
    System.out.println("║   Target Platform: Broadcom Enterprise SONiC              ║");
    System.out.println("║   Version: 1.0.0                                          ║");
    System.out.println("╚════════════════════════════════════════════════════════════╝");
    System.out.println();

    // Delegate to the main converter application
    CiscoToSonicConverter.main(args);
}
