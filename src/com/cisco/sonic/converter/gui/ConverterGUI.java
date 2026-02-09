package com.cisco.sonic.converter.gui;

import com.cisco.sonic.converter.CiscoToSonicConverter;
import com.cisco.sonic.converter.model.CiscoConfig;
import com.cisco.sonic.converter.model.SonicConfig;
import com.cisco.sonic.converter.parser.CiscoConfigParser;
import com.cisco.sonic.converter.converter.ConfigConverter;
import com.cisco.sonic.converter.output.SonicConfigWriter;
import com.cisco.sonic.converter.output.SonicCliGenerator;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.Map;

/**
 * Graphical User Interface for Cisco to SONiC Configuration Converter
 */
public class ConverterGUI extends JFrame {

    // Platform options
    private static final String[] PLATFORM_OPTIONS = {
        "Broadcom Enterprise SONiC",
        "Dell Enterprise SONiC (DENT)",
        "Celestica SONiC Plus",
        "Community SONiC"
    };

    private static final String[] PLATFORM_STRINGS = {
        "x86_64-broadcom_enterprise_sonic",
        "x86_64-dell_enterprise_sonic",
        "x86_64-celestica_seastone_2-r0",
        "x86_64-kvm_x86_64-r0"
    };

    private JTextArea inputTextArea;
    private JTextArea outputJsonTextArea;
    private JTextArea outputCliTextArea;
    private JTextArea logTextArea;
    private JTabbedPane outputTabbedPane;
    private JButton loadFileButton;
    private JButton convertButton;
    private JButton saveOutputButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JComboBox<String> platformComboBox;
    private JLabel platformLabel;

    private CiscoConfigParser parser;
    private ConfigConverter converter;
    private SonicConfigWriter writer;
    private SonicCliGenerator cliGenerator;

    private File lastInputFile;
    private File lastOutputFile;
    private String selectedPlatform;
    
    public ConverterGUI() {
        super("Cisco to SONiC Configuration Converter");

        // Initialize converters
        parser = new CiscoConfigParser();
        converter = new ConfigConverter();
        writer = new SonicConfigWriter();
        cliGenerator = new SonicCliGenerator();

        // Default platform
        selectedPlatform = PLATFORM_STRINGS[0];

        // Setup GUI
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        // Window settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
    }
    
    private void initializeComponents() {
        // Text areas
        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputTextArea.setLineWrap(false);
        inputTextArea.setTabSize(2);

        // JSON output text area
        outputJsonTextArea = new JTextArea();
        outputJsonTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputJsonTextArea.setLineWrap(false);
        outputJsonTextArea.setTabSize(2);
        outputJsonTextArea.setEditable(false);

        // CLI output text area
        outputCliTextArea = new JTextArea();
        outputCliTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputCliTextArea.setLineWrap(false);
        outputCliTextArea.setTabSize(2);
        outputCliTextArea.setEditable(false);

        // Tabbed pane for output
        outputTabbedPane = new JTabbedPane();
        outputTabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        logTextArea = new JTextArea();
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setEditable(false);
        logTextArea.setBackground(new Color(245, 245, 245));
        
        // Buttons
        loadFileButton = new JButton("Load Cisco Config File");
        loadFileButton.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        
        convertButton = new JButton("Convert to SONiC");
        convertButton.setIcon(UIManager.getIcon("FileView.computerIcon"));
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setBackground(new Color(76, 175, 80));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);
        convertButton.setOpaque(true);
        convertButton.setBorderPainted(false);
        convertButton.setPreferredSize(new Dimension(200, 45));
        convertButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        saveOutputButton = new JButton("Save SONiC Config");
        saveOutputButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        saveOutputButton.setEnabled(false);
        
        clearButton = new JButton("Clear All");
        clearButton.setIcon(UIManager.getIcon("FileChooser.newFolderIcon"));

        // Platform selection
        platformComboBox = new JComboBox<>(PLATFORM_OPTIONS);
        platformComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        platformComboBox.setPreferredSize(new Dimension(280, 30));
        platformComboBox.setMaximumRowCount(4);

        platformLabel = new JLabel("Target Platform:");
        platformLabel.setFont(new Font("Arial", Font.BOLD, 13));

        // Status components
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel with title and logo
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with input/output
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with log and status
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Add padding
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(76, 175, 80)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Left side - Title
        JLabel titleLabel = new JLabel("Cisco to SONiC Configuration Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 33, 33));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(titleLabel);

        panel.add(titlePanel, BorderLayout.WEST);

        // Center - Platform selection
        JPanel platformPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        platformPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        platformPanel.setBackground(new Color(250, 250, 250));

        platformPanel.add(platformLabel);
        platformPanel.add(platformComboBox);

        panel.add(platformPanel, BorderLayout.CENTER);

        // Right side - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loadFileButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            "Cisco Configuration (Input)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        JLabel inputHint = new JLabel("Paste Cisco configuration or load from file");
        inputHint.setFont(new Font("Arial", Font.ITALIC, 11));
        inputHint.setForeground(Color.GRAY);
        inputPanel.add(inputHint, BorderLayout.SOUTH);

        // Output panel with tabs
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            "SONiC Configuration (Output)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));

        // Create scroll panes for each output format
        JScrollPane jsonScrollPane = new JScrollPane(outputJsonTextArea);
        jsonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JScrollPane cliScrollPane = new JScrollPane(outputCliTextArea);
        cliScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add tabs
        outputTabbedPane.addTab("JSON (config_db.json)", jsonScrollPane);
        outputTabbedPane.addTab("CLI Commands", cliScrollPane);
        outputTabbedPane.setToolTipTextAt(0, "SONiC JSON configuration format");
        outputTabbedPane.setToolTipTextAt(1, "SONiC CLI commands (executable script)");

        outputPanel.add(outputTabbedPane, BorderLayout.CENTER);

        JPanel outputButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel outputHint = new JLabel("Switch tabs to view different output formats");
        outputHint.setFont(new Font("Arial", Font.ITALIC, 11));
        outputHint.setForeground(Color.GRAY);
        outputButtonPanel.add(outputHint);
        outputButtonPanel.add(saveOutputButton);
        outputPanel.add(outputButtonPanel, BorderLayout.SOUTH);

        panel.add(inputPanel);
        panel.add(outputPanel);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            "Conversion Log",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11)
        ));

        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        logScrollPane.setPreferredSize(new Dimension(0, 120));
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        panel.add(logPanel, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel leftStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftStatusPanel.add(statusLabel);
        leftStatusPanel.add(progressBar);

        JPanel rightStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightStatusPanel.add(convertButton);

        statusPanel.add(leftStatusPanel, BorderLayout.WEST);
        statusPanel.add(rightStatusPanel, BorderLayout.EAST);

        panel.add(statusPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setupEventHandlers() {
        // Load file button
        loadFileButton.addActionListener(e -> loadFile());

        // Convert button
        convertButton.addActionListener(e -> performConversion());

        // Save output button
        saveOutputButton.addActionListener(e -> saveOutput());

        // Clear button
        clearButton.addActionListener(e -> clearAll());

        // Platform selection
        platformComboBox.addActionListener(e -> {
            int selectedIndex = platformComboBox.getSelectedIndex();
            selectedPlatform = PLATFORM_STRINGS[selectedIndex];
            logMessage("Platform changed to: " + PLATFORM_OPTIONS[selectedIndex]);
            statusLabel.setText("Platform: " + PLATFORM_OPTIONS[selectedIndex]);
        });
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Cisco Configuration File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt") ||
                       f.getName().toLowerCase().endsWith(".cfg");
            }
            public String getDescription() {
                return "Configuration Files (*.txt, *.cfg)";
            }
        });

        if (lastInputFile != null) {
            fileChooser.setCurrentDirectory(lastInputFile.getParentFile());
        }

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            lastInputFile = file;

            try {
                String content = Files.readString(file.toPath());
                inputTextArea.setText(content);
                logMessage("Loaded file: " + file.getName());
                statusLabel.setText("File loaded: " + file.getName());
            } catch (IOException ex) {
                showError("Error loading file: " + ex.getMessage());
            }
        }
    }

    private void performConversion() {
        String inputText = inputTextArea.getText().trim();

        if (inputText.isEmpty()) {
            showError("Please provide Cisco configuration input");
            return;
        }

        // Disable buttons during conversion
        setButtonsEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Converting...");
        logTextArea.setText("");

        // Perform conversion in background thread
        SwingWorker<String, String> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                publish("Starting conversion...");
                publish("Target Platform: " + PLATFORM_OPTIONS[platformComboBox.getSelectedIndex()]);

                // Parse Cisco config
                publish("Parsing Cisco configuration...");
                CiscoConfig ciscoConfig = parser.parseString(inputText);

                publish("Found " + ciscoConfig.getInterfaces().size() + " interfaces");
                publish("Found " + ciscoConfig.getVlans().size() + " VLANs");
                publish("Found " + ciscoConfig.getStaticRoutes().size() + " static routes");

                // Convert to SONiC
                publish("Converting to SONiC format...");
                SonicConfig sonicConfig = converter.convert(ciscoConfig);

                // Update platform based on user selection
                publish("Applying platform: " + selectedPlatform);
                Map<String, Object> metadata = sonicConfig.getDeviceMetadata();
                if (metadata.containsKey("localhost")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> localhost = (Map<String, Object>) metadata.get("localhost");
                    localhost.put("platform", selectedPlatform);
                }

                // Display warnings
                if (!sonicConfig.getWarnings().isEmpty()) {
                    publish("\n⚠️  Warnings:");
                    for (String warning : sonicConfig.getWarnings()) {
                        publish("  - " + warning);
                    }
                }

                // Display unsupported features
                if (!sonicConfig.getUnsupportedFeatures().isEmpty()) {
                    publish("\n❌ Unsupported Features:");
                    for (String feature : sonicConfig.getUnsupportedFeatures()) {
                        publish("  - " + feature);
                    }
                }

                // Generate JSON output
                publish("\nGenerating JSON output...");
                String jsonOutput = writer.writeToString(sonicConfig);

                // Generate CLI commands
                publish("Generating CLI commands...");
                String cliOutput = cliGenerator.generateCommands(sonicConfig);

                publish("\n✅ Conversion completed successfully!");
                publish("✓ JSON format: " + jsonOutput.split("\n").length + " lines");
                publish("✓ CLI format: " + cliOutput.split("\n").length + " lines");

                return jsonOutput + "\n---CLI---\n" + cliOutput;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    logMessage(message);
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get();

                    // Split the combined output into JSON and CLI
                    String[] parts = result.split("\n---CLI---\n", 2);
                    if (parts.length == 2) {
                        outputJsonTextArea.setText(parts[0]);
                        outputCliTextArea.setText(parts[1]);
                    } else {
                        outputJsonTextArea.setText(result);
                        outputCliTextArea.setText("# CLI generation failed");
                    }

                    saveOutputButton.setEnabled(true);
                    statusLabel.setText("Conversion completed successfully");
                } catch (Exception ex) {
                    showError("Conversion failed: " + ex.getMessage());
                    statusLabel.setText("Conversion failed");
                } finally {
                    progressBar.setVisible(false);
                    setButtonsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void saveOutput() {
        // Determine which tab is active
        int selectedTab = outputTabbedPane.getSelectedIndex();
        String outputText;
        String defaultFileName;
        String fileExtension;
        String fileDescription;

        if (selectedTab == 0) {
            // JSON tab
            outputText = outputJsonTextArea.getText();
            defaultFileName = "config_db.json";
            fileExtension = ".json";
            fileDescription = "JSON Files (*.json)";
        } else {
            // CLI tab
            outputText = outputCliTextArea.getText();
            defaultFileName = "sonic_config.sh";
            fileExtension = ".sh";
            fileDescription = "Shell Scripts (*.sh)";
        }

        if (outputText.isEmpty()) {
            showError("No output to save");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save SONiC Configuration");
        fileChooser.setSelectedFile(new File(defaultFileName));

        final String ext = fileExtension;
        final String desc = fileDescription;
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(ext);
            }
            public String getDescription() {
                return desc;
            }
        });

        if (lastOutputFile != null) {
            fileChooser.setCurrentDirectory(lastOutputFile.getParentFile());
        }

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Add appropriate extension if not present
            if (!file.getName().toLowerCase().endsWith(fileExtension)) {
                file = new File(file.getAbsolutePath() + fileExtension);
            }

            lastOutputFile = file;

            try {
                Files.writeString(file.toPath(), outputText);
                logMessage("Saved output to: " + file.getName());
                statusLabel.setText("Saved: " + file.getName());
                JOptionPane.showMessageDialog(this,
                    "Configuration saved successfully to:\n" + file.getAbsolutePath(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Error saving file: " + ex.getMessage());
            }
        }
    }

    private void clearAll() {
        int result = JOptionPane.showConfirmDialog(this,
            "Clear all input and output?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            inputTextArea.setText("");
            outputJsonTextArea.setText("");
            outputCliTextArea.setText("");
            logTextArea.setText("");
            saveOutputButton.setEnabled(false);
            statusLabel.setText("Ready");
            logMessage("Cleared all fields");
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        loadFileButton.setEnabled(enabled);
        convertButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
    }

    private void logMessage(String message) {
        logTextArea.append(message + "\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        logMessage("ERROR: " + message);
    }

    /**
     * Main method to launch the GUI
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConverterGUI gui = new ConverterGUI();
            gui.setVisible(true);
        });
    }
}
