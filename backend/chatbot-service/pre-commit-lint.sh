#!/bin/bash
# Pre-commit linting script for chatbot service
# Run this before committing to catch issues early

set -e  # Exit on first error

cd "$(dirname "$0")"  # Go to script directory

echo "🔍 Running linters and auto-fixes..."
echo ""

# Activate virtual environment if it exists
if [ -d ".venv" ]; then
    source .venv/bin/activate
fi

# Step 1: Auto-format code
echo "📝 Step 1/4: Formatting code with Ruff..."
ruff format .
echo "✅ Formatting complete"
echo ""

# Step 2: Auto-fix linting issues
echo "🔧 Step 2/4: Auto-fixing linting issues..."
ruff check --fix .
echo "✅ Auto-fix complete"
echo ""

# Step 3: Check remaining linting issues
echo "🔎 Step 3/4: Checking for remaining issues..."
ruff check .
echo "✅ Linting check passed"
echo ""

# Step 4: Type check
echo "🔢 Step 4/4: Type checking with Mypy..."
mypy .
echo "✅ Type check passed"
echo ""

echo "🎉 All checks passed! Ready to commit."
