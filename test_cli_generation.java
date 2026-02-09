import com.cisco.sonic.converter.parser.CiscoConfigParser;
import com.cisco.sonic.converter.converter.ConfigConverter;
import com.cisco.sonic.converter.output.SonicCliGenerator;
import com.cisco.sonic.converter.model.CiscoConfig;
import com.cisco.sonic.converter.model.SonicConfig;
import java.nio.file.Files;
import java.nio.file.Paths;

void main(String[] args) throws Exception {
    System.out.println("=== Testing CLI Generation ===\n");
    
    // Read the complex config
    String configPath = "examples/complex_cisco_config.txt";
    String ciscoConfigText = Files.readString(Paths.get(configPath));
    
    // Parse
    System.out.println("Parsing Cisco configuration...");
    CiscoConfigParser parser = new CiscoConfigParser();
    CiscoConfig ciscoConfig = parser.parseString(ciscoConfigText);
    System.out.println("✓ Parsed: " + ciscoConfig.getHostname());
    System.out.println("  - Interfaces: " + ciscoConfig.getInterfaces().size());
    System.out.println("  - VLANs: " + ciscoConfig.getVlans().size());
    System.out.println("  - Routes: " + ciscoConfig.getStaticRoutes().size());
    
    // Convert
    System.out.println("\nConverting to SONiC...");
    ConfigConverter converter = new ConfigConverter();
    SonicConfig sonicConfig = converter.convert(ciscoConfig);
    System.out.println("✓ Converted successfully");
    
    // Generate CLI
    System.out.println("\nGenerating CLI commands...");
    SonicCliGenerator cliGenerator = new SonicCliGenerator();
    String cliCommands = cliGenerator.generateCommands(sonicConfig);
    
    // Save to file
    String outputPath = "complex_output_cli.sh";
    Files.writeString(Paths.get(outputPath), cliCommands);
    System.out.println("✓ CLI commands saved to: " + outputPath);
    System.out.println("  - Total lines: " + cliCommands.split("\n").length);
    
    // Show preview
    System.out.println("\n=== CLI Commands Preview (first 30 lines) ===\n");
    String[] lines = cliCommands.split("\n");
    for (int i = 0; i < Math.min(30, lines.length); i++) {
        System.out.println(lines[i]);
    }
    
    if (lines.length > 30) {
        System.out.println("\n... (" + (lines.length - 30) + " more lines) ...");
    }
    
    System.out.println("\n=== Test Complete ===");
}

