#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────
# sync-cli-version-from-reference-impl.sh
#
# Reads the @github/copilot version specifier from the cloned
# reference implementation's nodejs/package.json, and updates the
# corresponding property in pom.xml:
#
#   <readonly-copilot-sdk-ref-impl-version-from-lastmerge-file-updated-by-weekly-reference-impl-sync>
#
# This keeps the canonical Copilot CLI version (declared in pom.xml)
# in sync with whatever the reference implementation pinned in
# .lastmerge depends on. All workflows that install the Copilot CLI
# (build-test.yml — implicitly via cloned SDK, run-smoke-test.yml and
# update-copilot-dependency.yml — via the setup-copilot action) read
# this single property so every CI path uses the same CLI version.
#
# Usage:
#   ./sync-cli-version-from-reference-impl.sh <reference-impl-dir>
#
# Or, when invoked from merge-reference-impl-finish.sh, sources
# REFERENCE_IMPL_DIR from the .merge-env file.
# ──────────────────────────────────────────────────────────────
set -euo pipefail

# Locate the repo root by walking up from this script until we find a pom.xml.
# This is resilient to the script being moved to a different depth under
# .github/scripts/ in the future.
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

# node is always available since the reference implementation uses npm.
CLI_VERSION=$(node -e \
    "const fs=require('fs');const p=JSON.parse(fs.readFileSync(process.argv[1],'utf8'));const v=(p.dependencies&&p.dependencies['@github/copilot'])||(p.devDependencies&&p.devDependencies['@github/copilot']);process.stdout.write(v||'');" \
    "$PKG_JSON")

if [[ -z "$CLI_VERSION" ]]; then
    echo "❌ Could not extract @github/copilot version from $PKG_JSON" >&2
    exit 1
fi

POM="$ROOT_DIR/pom.xml"
PROP="readonly-copilot-sdk-ref-impl-version-from-lastmerge-file-updated-by-weekly-reference-impl-sync"

if ! grep -q "<${PROP}>" "$POM"; then
    echo "❌ Property <${PROP}> not found in $POM" >&2
    exit 1
fi

# Use a portable sed invocation (works on both BSD/macOS and GNU/Linux).
TMP="$(mktemp)"
sed -E "s|<${PROP}>[^<]*</${PROP}>|<${PROP}>${CLI_VERSION}</${PROP}>|" "$POM" > "$TMP"
mv "$TMP" "$POM"

echo "▸ Updated pom.xml: <${PROP}> = ${CLI_VERSION}"
