#!/bin/sh
# compare-standalone-to-monorepo.sh
#
# Compare POM, Java source, and Java properties files between the
# standalone copilot-sdk-java repo and the monorepo's java/ directory.
#
# Usage:
#   ./scripts/compare-standalone-to-monorepo.sh /path/to/standalone /path/to/monorepo [--diff]
#
# The standalone path should point to the root of copilot-sdk-java.
# The monorepo path should point to the root of copilot-sdk (the java/ subdir
# is appended automatically).
#
# Compatible with macOS zsh and POSIX sh/bash.

set -e

# ── Parse arguments ──────────────────────────────────────────────────
SHOW_DIFF=false
STANDALONE=""
MONOREPO=""

for arg in "$@"; do
    case "$arg" in
        --diff)
            SHOW_DIFF=true
            ;;
        *)
            if [ -z "$STANDALONE" ]; then
                STANDALONE="$arg"
            elif [ -z "$MONOREPO" ]; then
                MONOREPO="$arg"
            else
                echo "Error: unexpected argument: $arg" >&2
                echo "Usage: $0 <standalone-path> <monorepo-path> [--diff]" >&2
                exit 1
            fi
            ;;
    esac
done

if [ -z "$STANDALONE" ] || [ -z "$MONOREPO" ]; then
    echo "Usage: $0 <standalone-path> <monorepo-path> [--diff]" >&2
    exit 1
fi

MONO_JAVA="${MONOREPO}/java"

if [ ! -d "$STANDALONE" ]; then
    echo "Error: standalone directory does not exist: $STANDALONE" >&2
    exit 1
fi

if [ ! -d "$MONO_JAVA" ]; then
    echo "Error: monorepo java directory does not exist: $MONO_JAVA" >&2
    exit 1
fi

# ── Collect comparable files from the standalone repo ────────────────
# Excludes: target/, node_modules/, temporary-prompts/, scripts/codegen/
# (these are build artifacts or standalone-only content)
TMPFILE=$(mktemp)
trap 'rm -f "$TMPFILE"' EXIT

(cd "$STANDALONE" && find . -type f \( -name "pom.xml" -o -name "*.java" -o -name "*.properties" \) \
    | grep -v '/target/' \
    | grep -v '/node_modules/' \
    | grep -v '^\./temporary-prompts/' \
    | grep -v '^\./scripts/codegen/' \
    | sed 's|^\./||' \
    | sort) > "$TMPFILE"

# ── Compare ──────────────────────────────────────────────────────────
DIFFER_COUNT=0
MISSING_COUNT=0
SAME_COUNT=0
DIFFER_LIST=""
MISSING_LIST=""

while IFS= read -r relpath; do
    standalone_file="${STANDALONE}/${relpath}"
    mono_file="${MONO_JAVA}/${relpath}"

    if [ ! -f "$mono_file" ]; then
        MISSING_COUNT=$((MISSING_COUNT + 1))
        MISSING_LIST="${MISSING_LIST}${relpath}
"
    elif ! diff -q "$standalone_file" "$mono_file" >/dev/null 2>&1; then
        DIFFER_COUNT=$((DIFFER_COUNT + 1))
        DIFFER_LIST="${DIFFER_LIST}${relpath}
"
    else
        SAME_COUNT=$((SAME_COUNT + 1))
    fi
done < "$TMPFILE"

TOTAL_COUNT=$(wc -l < "$TMPFILE" | tr -d ' ')

# ── Output ───────────────────────────────────────────────────────────
echo "Compared ${TOTAL_COUNT} files (pom.xml, *.java, *.properties)"
echo "  Identical: ${SAME_COUNT}"
echo "  Differ:    ${DIFFER_COUNT}"
echo "  Missing from monorepo: ${MISSING_COUNT}"
echo ""

if [ "$DIFFER_COUNT" -gt 0 ]; then
    echo "The following files differ between the standalone and monorepo:"
    echo ""
    printf '%s' "$DIFFER_LIST" | while IFS= read -r f; do
        [ -n "$f" ] && echo "  $f"
    done
    echo ""
fi

if [ "$MISSING_COUNT" -gt 0 ]; then
    echo "The following files exist in standalone but NOT in monorepo:"
    echo ""
    printf '%s' "$MISSING_LIST" | while IFS= read -r f; do
        [ -n "$f" ] && echo "  $f"
    done
    echo ""
fi

if [ "$DIFFER_COUNT" -eq 0 ] && [ "$MISSING_COUNT" -eq 0 ]; then
    echo "All files are identical."
fi

# ── Optional unified diffs ───────────────────────────────────────────
if [ "$SHOW_DIFF" = true ] && [ "$DIFFER_COUNT" -gt 0 ]; then
    echo "================================================================================"
    echo "Unified diffs for differing files:"
    echo "================================================================================"
    printf '%s' "$DIFFER_LIST" | while IFS= read -r f; do
        if [ -n "$f" ]; then
            echo ""
            echo "--- standalone/$f"
            echo "+++ monorepo/java/$f"
            diff -u "${STANDALONE}/${f}" "${MONO_JAVA}/${f}" || true
        fi
    done
fi
