#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────
# merge-reference-impl-diff.sh
#
# Generates a detailed diff analysis of reference implementation changes since
# the last merge, grouped by area of interest:
#   • .NET source (primary reference)
#   • .NET tests
#   • Test snapshots
#   • Documentation
#   • Protocol / config files
#
# Usage:  ./.github/scripts/reference-impl-sync/merge-reference-impl-diff.sh [--full]
#         --full   Show actual diffs, not just stats
#
# Requires: .merge-env written by merge-reference-impl-start.sh
# ──────────────────────────────────────────────────────────────
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ENV_FILE="$ROOT_DIR/.merge-env"

if [[ ! -f "$ENV_FILE" ]]; then
    echo "❌ $ENV_FILE not found. Run ./.github/scripts/reference-impl-sync/merge-reference-impl-start.sh first."
    exit 1
fi

# shellcheck source=/dev/null
source "$ENV_FILE"

SHOW_FULL=false
if [[ "${1:-}" == "--full" ]]; then
    SHOW_FULL=true
fi

cd "$REFERENCE_IMPL_DIR"
git fetch origin main 2>/dev/null

RANGE="$LAST_MERGE_COMMIT..origin/main"

echo "════════════════════════════════════════════════════════════"
echo "  Reference implementation diff analysis: $RANGE"
echo "════════════════════════════════════════════════════════════"

# ── Commit log ────────────────────────────────────────────────
echo ""
echo "── Commit log ──"
git log --oneline --no-decorate "$RANGE"
echo ""

# Helper to print a section
section() {
    local title="$1"; shift
    local paths=("$@")

    echo "── $title ──"
    local stat
    stat=$(git diff "$RANGE" --stat -- "${paths[@]}" 2>/dev/null || true)
    if [[ -z "$stat" ]]; then
        echo "  (no changes)"
    else
        echo "$stat"
        if $SHOW_FULL; then
            echo ""
            git diff "$RANGE" -- "${paths[@]}" 2>/dev/null || true
        fi
    fi
    echo ""
}

# ── Sections ──────────────────────────────────────────────────
section ".NET source (dotnet/src)"        "dotnet/src/"
section ".NET tests (dotnet/test)"        "dotnet/test/"
section "Test snapshots"                  "test/snapshots/"
section "Documentation (docs/)"           "docs/"
section "Protocol & config"               "sdk-protocol-version.json" "package.json" "justfile"
section "Go SDK"                          "go/"
section "Node.js SDK"                     "nodejs/"
section "Python SDK"                      "python/"
section "Other files"                     "README.md" "CONTRIBUTING.md" "SECURITY.md" "SUPPORT.md"

echo "════════════════════════════════════════════════════════════"
echo "  To see full diffs: $0 --full"
echo "  To see a specific path:"
echo "    cd $REFERENCE_IMPL_DIR && git diff $RANGE -- <path>"
echo "════════════════════════════════════════════════════════════"
