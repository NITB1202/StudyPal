#!/bin/bash
set -e

HOOKS_DIR=".github/hooks"
GIT_HOOKS_DIR=".git/hooks"

if [ ! -d ".git" ]; then
  echo "Error: .git directory not found. Are you sure this is a git repository?"
  exit 1
fi

if [ ! -d "$GIT_HOOKS_DIR" ]; then
  echo ".git/hooks directory not found. Creating it now..."
  mkdir -p "$GIT_HOOKS_DIR"
fi

if [ ! -d "$HOOKS_DIR" ]; then
  echo "Error: $HOOKS_DIR directory not found."
  exit 1
fi

echo "Setting up git hooks..."

for hook in "$HOOKS_DIR"/*; do
  hook_name=$(basename "$hook")
  echo "Installing hook: $hook_name"
  cp "$hook" "$GIT_HOOKS_DIR/$hook_name"
  chmod +x "$GIT_HOOKS_DIR/$hook_name"
done

echo "All hooks installed successfully."
