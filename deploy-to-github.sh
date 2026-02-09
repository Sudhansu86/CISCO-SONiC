#!/bin/bash

# Cisco to SONiC Configuration Converter - GitHub Deployment Script
# This script helps you push your code to GitHub and set up GitHub Pages

echo "🚀 Cisco to SONiC Configuration Converter - GitHub Deployment"
echo "=============================================================="
echo ""

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo "📦 Initializing Git repository..."
    git init
    echo "✅ Git repository initialized"
else
    echo "✅ Git repository already initialized"
fi

# Check if remote exists
if git remote | grep -q "origin"; then
    echo "✅ Remote 'origin' already configured"
    REMOTE_URL=$(git remote get-url origin)
    echo "   Remote URL: $REMOTE_URL"
else
    echo "🔗 Adding remote repository..."
    git remote add origin https://github.com/Sudhansu86/CISCO-SONiC.git
    echo "✅ Remote 'origin' added"
fi

# Show current status
echo ""
echo "📊 Current Git Status:"
git status --short

# Add all files
echo ""
echo "📝 Adding files to Git..."
git add .

# Show what will be committed
echo ""
echo "📋 Files to be committed:"
git status --short

# Prompt for commit
echo ""
read -p "💬 Enter commit message (or press Enter for default): " COMMIT_MSG

if [ -z "$COMMIT_MSG" ]; then
    COMMIT_MSG="Update: Cisco to SONiC Converter with Phase 1 features and web demo"
fi

# Commit
echo ""
echo "💾 Committing changes..."
git commit -m "$COMMIT_MSG"

# Check current branch
CURRENT_BRANCH=$(git branch --show-current)
if [ -z "$CURRENT_BRANCH" ]; then
    echo "🌿 Creating main branch..."
    git branch -M main
    CURRENT_BRANCH="main"
fi

# Push to GitHub
echo ""
echo "🚀 Pushing to GitHub..."
echo "   Branch: $CURRENT_BRANCH"
echo ""

if git push -u origin $CURRENT_BRANCH; then
    echo ""
    echo "✅ Successfully pushed to GitHub!"
    echo ""
    echo "🎉 Next Steps:"
    echo "=============="
    echo ""
    echo "1. 🌐 Enable GitHub Pages:"
    echo "   - Go to: https://github.com/Sudhansu86/CISCO-SONiC/settings/pages"
    echo "   - Under 'Source', select:"
    echo "     • Branch: main"
    echo "     • Folder: /docs"
    echo "   - Click 'Save'"
    echo ""
    echo "2. ⏳ Wait 2-3 minutes for GitHub Pages to deploy"
    echo ""
    echo "3. 🌍 Access your web demo at:"
    echo "   https://sudhansu86.github.io/CISCO-SONiC/"
    echo ""
    echo "4. 📖 View your repository at:"
    echo "   https://github.com/Sudhansu86/CISCO-SONiC"
    echo ""
else
    echo ""
    echo "❌ Push failed. This might be because:"
    echo "   1. You need to authenticate with GitHub"
    echo "   2. The repository doesn't exist yet"
    echo "   3. You don't have push permissions"
    echo ""
    echo "💡 Try these solutions:"
    echo "   1. Make sure you're logged into GitHub"
    echo "   2. Create the repository at: https://github.com/new"
    echo "   3. Use SSH instead: git remote set-url origin git@github.com:Sudhansu86/CISCO-SONiC.git"
    echo ""
fi

