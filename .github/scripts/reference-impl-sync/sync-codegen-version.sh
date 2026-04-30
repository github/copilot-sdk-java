#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────
# sync-codegen-version.sh
#
# Updates the @github/copilot dependency in scripts/codegen/package.json
# to match the version used by the reference implementation. This keeps
# the code generator schemas in lockstep with the CLI version used for
# testing, eliminating the gap where Dependabot could race ahead.
#
# Usage:
#   ./sync-codegen-version.sh <reference-impl-dir>
#
# Or, when invoked from merge-reference-impl-finish.sh, the directory
# is passed as $1.
# ──────────────────────────────────────────────────────────────
set -euo pipefail

# Locate the repo root by walking up from this script until we find a pom.xml.
find_repo_root() {
    local dir
    dir="$(cd "$(dirname "$0")" && pwd)"
    while [[ "$dir" != "/" ]]; do
        if [[ -f "$dir/pom.xml" ]]; then
            echo "$dir"
            return 0
        fi
        dir="$(dirname "$dir")"
    done
    echo "❌ Could not locate repo root (no pom.xml found above $(dirname "$0"))" >&2
    return 1
}
ROOT_DIR="$(find_repo_root)"

REFERENCE_IMPL_DIR="${1:-${REFERENCE_IMPL_DIR:-}}"
if [[ -z "$REFERENCE_IMPL_DIR" ]]; then
    echo "❌ Usage: $0 <reference-impl-dir>" >&2
    echo "   or set REFERENCE_IMPL_DIR in the environment." >&2
    exit 1
fi

PKG_JSON="$REFERENCE_IMPL_DIR/nodejs/package.json"
if [[ ! -f "$PKG_JSON" ]]; then
    echo "❌ Cannot find $PKG_JSON" >&2
    exit 1
fi

# Extract the @github/copilot version from the reference implementation.
CLI_VERSION=$(node -e \
    "const fs=require('fs');const p=JSON.parse(fs.readFileSync(process.argv[1],'utf8'));const v=(p.dependencies&&p.dependencies['@github/copilot'])||(p.devDependencies&&p.devDependencies['@github/copilot']);process.stdout.write(v||'');" \
    "$PKG_JSON")

if [[ -z "$CLI_VERSION" ]]; then
    echo "❌ Could not extract @github/copilot version from $PKG_JSON" >&2
    exit 1
fi

CODEGEN_DIR="$ROOT_DIR/scripts/codegen"
CODEGEN_PKG="$CODEGEN_DIR/package.json"

if [[ ! -f "$CODEGEN_PKG" ]]; then
    echo "❌ Cannot find $CODEGEN_PKG" >&2
    exit 1
fi

# Update scripts/codegen/package.json with the new version and regenerate the lock file.
# Intentionally omit --save-exact to preserve the version specifier used by the reference
# implementation (e.g. a caret range like '^1.0.36-0' rather than an exact pin '1.0.36-0').
echo "▸ Updating scripts/codegen/package.json: @github/copilot → ${CLI_VERSION}"
cd "$CODEGEN_DIR"
npm install "@github/copilot@${CLI_VERSION}"
echo "▸ Updated scripts/codegen to @github/copilot@${CLI_VERSION}"
