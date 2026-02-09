# GitHub Setup Guide

This guide will help you push the Cisco to SONiC Configuration Converter to GitHub and set up the web demo with GitHub Pages.

## 📋 Prerequisites

- Git installed on your system
- GitHub account
- Repository created on GitHub (or ready to create one)

## 🚀 Step 1: Initialize Git Repository

If you haven't already initialized a Git repository:

```bash
cd /Users/batman/IdeaProjects/CISCO-SONiC
git init
```

## 📝 Step 2: Add Files to Git

```bash
# Add all files (respecting .gitignore)
git add .

# Check what will be committed
git status

# Commit the files
git commit -m "Initial commit: Cisco to SONiC Converter with Phase 1 features

- Interface range support (bulk port configuration)
- VLAN range support (bulk VLAN creation)
- Port-Channel/LAG support with LACP
- Dual output format (JSON + CLI commands)
- GUI application with platform selection
- Web-based demo for GitHub Pages
- Comprehensive documentation and examples"
```

## 🌐 Step 3: Create GitHub Repository

1. Go to [GitHub](https://github.com)
2. Click the "+" icon in the top right
3. Select "New repository"
4. Fill in the details:
   - **Repository name**: `CISCO-SONiC` (or your preferred name)
   - **Description**: "Convert Cisco IOS/IOS-XE configurations to SONiC format - Production-ready bulk migration tool"
   - **Visibility**: Public (required for GitHub Pages)
   - **Do NOT** initialize with README, .gitignore, or license (we already have these)
5. Click "Create repository"

## 🔗 Step 4: Connect Local Repository to GitHub

Replace `YOUR_USERNAME` with your GitHub username:

```bash
git remote add origin https://github.com/YOUR_USERNAME/CISCO-SONiC.git
git branch -M main
git push -u origin main
```

## 🌍 Step 5: Enable GitHub Pages

1. Go to your repository on GitHub
2. Click "Settings" tab
3. Scroll down to "Pages" in the left sidebar
4. Under "Source", select:
   - **Branch**: `main`
   - **Folder**: `/docs`
5. Click "Save"
6. Wait a few minutes for GitHub to build your site
7. Your web demo will be available at: `https://YOUR_USERNAME.github.io/CISCO-SONiC/`

## 📝 Step 6: Update README with Web Demo Link

Update the `docs/index.html` file to replace `YOUR_USERNAME` with your actual GitHub username:

```bash
# macOS/Linux
sed -i '' 's/YOUR_USERNAME/your-actual-username/g' docs/index.html

# Or manually edit docs/index.html and replace YOUR_USERNAME
```

Then commit and push the change:

```bash
git add docs/index.html
git commit -m "Update web demo with actual GitHub username"
git push
```

## ✅ Step 7: Verify Everything Works

1. **Check Repository**: Visit `https://github.com/YOUR_USERNAME/CISCO-SONiC`
2. **Check Web Demo**: Visit `https://YOUR_USERNAME.github.io/CISCO-SONiC/`
3. **Test Conversion**: Try the example configuration in the web demo

## 🎨 Optional: Add Repository Topics

Add topics to make your repository more discoverable:

1. Go to your repository on GitHub
2. Click the gear icon next to "About"
3. Add topics: `sonic`, `cisco`, `network-automation`, `configuration-converter`, `network-migration`, `broadcom-sonic`
4. Click "Save changes"

## 📊 Optional: Add Repository Description

1. Go to your repository on GitHub
2. Click the gear icon next to "About"
3. Add description: "Convert Cisco IOS/IOS-XE configurations to SONiC format - Production-ready bulk migration tool with interface ranges, VLAN ranges, and Port-Channel support"
4. Add website: `https://YOUR_USERNAME.github.io/CISCO-SONiC/`
5. Click "Save changes"

## 🔄 Future Updates

To push future changes:

```bash
# Make your changes
git add .
git commit -m "Description of changes"
git push
```

GitHub Pages will automatically rebuild your site within a few minutes.

## 🎉 You're Done!

Your Cisco to SONiC Configuration Converter is now:
- ✅ Hosted on GitHub
- ✅ Available as a web demo
- ✅ Accessible to anyone with the link
- ✅ Ready for collaboration and contributions

Share your web demo link with colleagues and the community!

## 📚 Next Steps

- Add screenshots to README.md
- Create a CONTRIBUTING.md file
- Add a LICENSE file (MIT or Apache 2.0 recommended)
- Set up GitHub Actions for automated testing
- Create release tags for version management

