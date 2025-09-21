#!/bin/bash
set -e

# Get the list of staged .java files (added or modified)
staged_files=$(git diff --cached --name-only --diff-filter=ACM | grep '\.java$' || true)

if [[ -z "$staged_files" ]]; then
  echo "No staged Java files. Skipping formatting and import checks."
  exit 0
fi

echo "Running Spotless apply on staged Java files..."

# Run spotless:apply for the whole project (Spotless Maven plugin usually formats entire project)
mvn spotless:apply

echo "Adding formatted files back to staging..."

# Run spotless:check to verify formatting
mvn spotless:check

# Add the staged files back after formatting
git add $staged_files

echo "Checking for wildcard imports in staged Java files..."

# Check for wildcard imports in staged files
wildcard_imports_found=0

for file in $staged_files; do
  if grep -qE 'import\s+.*\.\*;' "$file"; then
    echo "Wildcard import found in $file"
    wildcard_imports_found=1
  fi
done

if [[ $wildcard_imports_found -eq 1 ]]; then
  echo "Error: Wildcard imports are not allowed. Please replace them with explicit imports."
  exit 1
fi

echo "Pre-commit checks passed."
exit 0
