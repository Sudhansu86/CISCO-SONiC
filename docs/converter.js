// Cisco to SONiC Configuration Converter - Web Demo
// Simplified JavaScript implementation

class CiscoToSonicConverter {
    constructor() {
        this.config = {
            hostname: '',
            interfaces: {},
            vlans: {},
            vlanMembers: {},
            portChannels: {},
            portChannelMembers: {},
            staticRoutes: {},
            interfaceIPs: {}
        };
        this.platform = 'x86_64-broadcom_enterprise_sonic';
    }

    setPlatform(platform) {
        this.platform = platform;
    }

    convert(ciscoConfig) {
        const startTime = performance.now();
        this.config = {
            hostname: '',
            interfaces: {},
            vlans: {},
            vlanMembers: {},
            portChannels: {},
            portChannelMembers: {},
            staticRoutes: {},
            interfaceIPs: {}
        };

        const lines = ciscoConfig.split('\n');
        let currentInterface = null;
        let currentInterfaceRange = [];
        let interfaceCounter = 1;

        for (let line of lines) {
            line = line.trim();

            // Hostname
            if (line.startsWith('hostname ')) {
                this.config.hostname = line.substring(9).trim();
            }

            // VLAN range
            else if (line.match(/^vlan\s+([\d,-]+)$/i)) {
                const vlanSpec = line.match(/^vlan\s+([\d,-]+)$/i)[1];
                const vlans = this.expandVlanRange(vlanSpec);
                vlans.forEach(vlanId => {
                    this.config.vlans[`Vlan${vlanId}`] = { vlanid: vlanId.toString() };
                });
            }

            // Interface range
            else if (line.match(/^interface\s+range\s+(.+)$/i)) {
                const rangeSpec = line.match(/^interface\s+range\s+(.+)$/i)[1];
                currentInterfaceRange = this.expandInterfaceRange(rangeSpec);
                currentInterface = null;
            }

            // Single interface
            else if (line.match(/^interface\s+(\S+)$/i)) {
                const intfName = line.match(/^interface\s+(\S+)$/i)[1];
                currentInterface = this.convertInterfaceName(intfName);
                currentInterfaceRange = [];

                if (!this.config.interfaces[currentInterface]) {
                    this.config.interfaces[currentInterface] = { admin_status: 'down' };
                }
            }

            // Interface commands
            else if (currentInterface || currentInterfaceRange.length > 0) {
                const interfaces = currentInterfaceRange.length > 0 ? currentInterfaceRange : [currentInterface];

                interfaces.forEach(intf => {
                    if (!this.config.interfaces[intf]) {
                        this.config.interfaces[intf] = { admin_status: 'down' };
                    }

                    // Description
                    if (line.match(/^description\s+(.+)$/i)) {
                        this.config.interfaces[intf].description = line.match(/^description\s+(.+)$/i)[1];
                    }

                    // No shutdown
                    else if (line.match(/^no\s+shutdown$/i)) {
                        this.config.interfaces[intf].admin_status = 'up';
                    }

                    // Switchport mode access
                    else if (line.match(/^switchport\s+mode\s+access$/i)) {
                        this.config.interfaces[intf].switchport_mode = 'access';
                    }

                    // Switchport mode trunk
                    else if (line.match(/^switchport\s+mode\s+trunk$/i)) {
                        this.config.interfaces[intf].switchport_mode = 'trunk';
                    }

                    // Switchport access vlan
                    else if (line.match(/^switchport\s+access\s+vlan\s+(\d+)$/i)) {
                        const vlanId = line.match(/^switchport\s+access\s+vlan\s+(\d+)$/i)[1];
                        const vlanKey = `Vlan${vlanId}|${intf}`;
                        this.config.vlanMembers[vlanKey] = { tagging_mode: 'untagged' };
                    }

                    // Switchport trunk allowed vlan
                    else if (line.match(/^switchport\s+trunk\s+allowed\s+vlan\s+(.+)$/i)) {
                        const vlanSpec = line.match(/^switchport\s+trunk\s+allowed\s+vlan\s+(.+)$/i)[1];
                        const vlans = this.expandVlanRange(vlanSpec);
                        vlans.forEach(vlanId => {
                            const vlanKey = `Vlan${vlanId}|${intf}`;
                            this.config.vlanMembers[vlanKey] = { tagging_mode: 'tagged' };
                        });
                    }

                    // Channel-group
                    else if (line.match(/^channel-group\s+(\d+)\s+mode\s+(\S+)$/i)) {
                        const match = line.match(/^channel-group\s+(\d+)\s+mode\s+(\S+)$/i);
                        const pcNum = match[1];
                        const pcName = `PortChannel${pcNum}`;
                        const memberKey = `${pcName}|${intf}`;
                        this.config.portChannelMembers[memberKey] = {};

                        if (!this.config.portChannels[pcName]) {
                            this.config.portChannels[pcName] = { admin_status: 'up', min_links: '1' };
                        }
                    }

                    // IP address
                    else if (line.match(/^ip\s+address\s+(\S+)\s+(\S+)$/i)) {
                        const match = line.match(/^ip\s+address\s+(\S+)\s+(\S+)$/i);
                        const ip = match[1];
                        const mask = match[2];
                        const cidr = this.maskToCidr(mask);
                        const ipKey = `${intf}|${ip}/${cidr}`;
                        this.config.interfaceIPs[ipKey] = { NULL: null };
                    }

                    // MTU
                    else if (line.match(/^mtu\s+(\d+)$/i)) {
                        this.config.interfaces[intf].mtu = line.match(/^mtu\s+(\d+)$/i)[1];
                    }
                });
            }


    expandVlanRange(vlanSpec) {
        const vlans = [];
        const parts = vlanSpec.split(',');

        for (let part of parts) {
            part = part.trim();
            if (part.includes('-')) {
                const [start, end] = part.split('-').map(v => parseInt(v.trim()));
                for (let i = start; i <= end; i++) {
                    vlans.push(i);
                }
            } else {
                vlans.push(parseInt(part));
            }
        }

        return vlans;
    }

    expandInterfaceRange(rangeSpec) {
        const interfaces = [];
        // Simple implementation: GigabitEthernet0/1-10 -> Ethernet1-10
        const match = rangeSpec.match(/(\w+)(\d+\/)?(\d+)-(\d+)/);

        if (match) {
            const start = parseInt(match[3]);
            const end = parseInt(match[4]);

            for (let i = start; i <= end; i++) {
                interfaces.push(`Ethernet${i}`);
            }
        }

        return interfaces;
    }

    convertInterfaceName(ciscoName) {
        // GigabitEthernet0/1 -> Ethernet1
        // TenGigabitEthernet0/1 -> Ethernet1
        // Port-channel1 -> PortChannel1

        if (ciscoName.match(/Port-channel(\d+)/i)) {
            return `PortChannel${ciscoName.match(/Port-channel(\d+)/i)[1]}`;
        }

        if (ciscoName.match(/Vlan(\d+)/i)) {
            return `Vlan${ciscoName.match(/Vlan(\d+)/i)[1]}`;
        }

        const match = ciscoName.match(/\d+\/(\d+)$/);
        if (match) {
            return `Ethernet${match[1]}`;
        }

        return ciscoName;
    }

    maskToCidr(mask) {
        const parts = mask.split('.').map(p => parseInt(p));
        let cidr = 0;

        for (let part of parts) {
            cidr += part.toString(2).split('1').length - 1;
        }

        return cidr;
    }

    generateJSON() {
        const output = {
            DEVICE_METADATA: {
                localhost: {
                    hostname: this.config.hostname || 'SONiC-Switch',
                    platform: this.platform,
                    mac: '00:00:00:00:00:00',
                    type: 'LeafRouter'
                }
            }
        };

        if (Object.keys(this.config.interfaces).length > 0) {
            output.PORT = this.config.interfaces;
        }

        if (Object.keys(this.config.vlans).length > 0) {
            output.VLAN = this.config.vlans;
        }

        if (Object.keys(this.config.vlanMembers).length > 0) {
            output.VLAN_MEMBER = this.config.vlanMembers;
        }

        if (Object.keys(this.config.interfaceIPs).length > 0) {
            output.INTERFACE = this.config.interfaceIPs;
        }

        if (Object.keys(this.config.staticRoutes).length > 0) {
            output.STATIC_ROUTE = this.config.staticRoutes;
        }

        if (Object.keys(this.config.portChannels).length > 0) {
            output.PORTCHANNEL = this.config.portChannels;
        }

        if (Object.keys(this.config.portChannelMembers).length > 0) {
            output.PORTCHANNEL_MEMBER = this.config.portChannelMembers;
        }

        return JSON.stringify(output, null, 2);
    }

    generateCLI() {
        let cli = '#!/bin/bash\n';
        cli += '# SONiC Configuration Commands\n';
        cli += '# Generated by Cisco to SONiC Converter\n\n';

        cli += 'echo "Starting SONiC configuration..."\n\n';

        // VLANs
        if (Object.keys(this.config.vlans).length > 0) {
            cli += '# Configure VLANs\n';
            cli += 'echo "Configuring VLANs..."\n';
            for (let vlanName in this.config.vlans) {
                const vlanId = this.config.vlans[vlanName].vlanid;
                cli += `sudo config vlan add ${vlanId}\n`;
            }
            cli += '\n';
        }

        // Port-Channels
        if (Object.keys(this.config.portChannels).length > 0) {
            cli += '# Configure Port-Channels\n';
            cli += 'echo "Configuring Port-Channels..."\n';
            for (let pcName in this.config.portChannels) {
                cli += `sudo config portchannel add ${pcName}\n`;
            }
            cli += '\n';

            // Port-Channel members
            if (Object.keys(this.config.portChannelMembers).length > 0) {
                cli += '# Add Port-Channel members\n';
                for (let memberKey in this.config.portChannelMembers) {
                    const [pcName, port] = memberKey.split('|');
                    cli += `sudo config portchannel member add ${pcName} ${port}\n`;
                }
                cli += '\n';
            }
        }

        cli += 'echo "Configuration complete!"\n';
        return cli;
    }

    generateStats() {
        return {
            interfaces: Object.keys(this.config.interfaces).length,
            vlans: Object.keys(this.config.vlans).length,
            portChannels: Object.keys(this.config.portChannels).length,
            vlanMembers: Object.keys(this.config.vlanMembers).length,
            routes: Object.keys(this.config.staticRoutes).length
        };
    }
}

// UI Event Handlers
document.addEventListener('DOMContentLoaded', () => {
    const converter = new CiscoToSonicConverter();
    const ciscoInput = document.getElementById('ciscoInput');
    const sonicOutput = document.getElementById('sonicOutput');
    const cliOutput = document.getElementById('cliOutput');
    const convertBtn = document.getElementById('convertBtn');
    const platformSelect = document.getElementById('platform');
    const loadExampleBtn = document.getElementById('loadExample');
    const clearInputBtn = document.getElementById('clearInput');
    const downloadBtn = document.getElementById('downloadBtn');
    const statsDiv = document.getElementById('stats');

    // Tab switching
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabName = btn.dataset.tab;

            tabBtns.forEach(b => b.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));

            btn.classList.add('active');
            document.getElementById(`${tabName}Tab`).classList.add('active');
        });
    });

    // Load example configuration
    loadExampleBtn.addEventListener('click', () => {
        ciscoInput.value = `!
! Data Center ToR Switch Example
hostname DC-TOR-SW-01

! Bulk VLAN creation
vlan 10-20

! Bulk server access ports
interface range GigabitEthernet0/1-10
 description Server Access Ports
 switchport mode access
 switchport access vlan 10
 no shutdown

! Port-Channel configuration
interface Port-channel1
 description Server LAG
 switchport mode trunk
 switchport trunk allowed vlan 10-20
 no shutdown

interface GigabitEthernet0/11
 description Server NIC1
 channel-group 1 mode active
 no shutdown

interface GigabitEthernet0/12
 description Server NIC2
 channel-group 1 mode active
 no shutdown

! Trunk port
interface GigabitEthernet0/48
 description Uplink to Core
 switchport mode trunk
 switchport trunk allowed vlan 10-20
 no shutdown

! Management VLAN
interface Vlan10
 description Management
 ip address 192.168.10.1 255.255.255.0
 no shutdown

! Static route
ip route 0.0.0.0 0.0.0.0 192.168.10.254
`;
    });

    // Clear input
    clearInputBtn.addEventListener('click', () => {
        ciscoInput.value = '';
        sonicOutput.textContent = 'SONiC JSON configuration will appear here...';
        cliOutput.textContent = 'SONiC CLI commands will appear here...';
        statsDiv.style.display = 'none';
    });

    // Convert configuration
    convertBtn.addEventListener('click', () => {
        const ciscoConfig = ciscoInput.value.trim();

        if (!ciscoConfig) {
            alert('Please enter a Cisco configuration first!');
            return;
        }

        converter.setPlatform(platformSelect.value);
        const result = converter.convert(ciscoConfig);

        sonicOutput.textContent = result.json;
        cliOutput.textContent = result.cli;

        // Update stats
        document.getElementById('statInterfaces').textContent = result.stats.interfaces;
        document.getElementById('statVlans').textContent = result.stats.vlans;
        document.getElementById('statPortChannels').textContent = result.stats.portChannels;
        document.getElementById('statVlanMembers').textContent = result.stats.vlanMembers;
        document.getElementById('statRoutes').textContent = result.stats.routes;
        document.getElementById('statTime').textContent = `${result.time}ms`;

        statsDiv.style.display = 'block';
    });

    // Download output
    downloadBtn.addEventListener('click', () => {
        const activeTab = document.querySelector('.tab-btn.active').dataset.tab;
        const content = activeTab === 'json' ? sonicOutput.textContent : cliOutput.textContent;
        const filename = activeTab === 'json' ? 'config_db.json' : 'sonic_config.sh';
        const mimeType = activeTab === 'json' ? 'application/json' : 'text/plain';

        const blob = new Blob([content], { type: mimeType });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        URL.revokeObjectURL(url);
    });
});
