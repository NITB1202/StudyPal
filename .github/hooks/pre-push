#!/bin/bash
set -e

echo "Running spotless check..."

# Run spotless check to verify code formatting
mvn spotless:check

echo "Detecting test files in changes..."

# Get list of staged files for push
changed_files=$(git diff --cached --name-only)

run_tests=0

for file in $changed_files; do
  # If the file is a test file (name contains Test or IT), set flag to run tests
  if [[ "$file" =~ (Test|IT)\.java$ ]]; then
    echo "Test file detected: $file"
    run_tests=1
    break
  fi
done

if [[ $run_tests -eq 1 ]]; then
  echo "Running tests before push..."

  # Run unit and integration tests
  mvn test

  echo "Tests passed!"
else
  echo "No test files changed, skipping test run."
fi

exit 0
