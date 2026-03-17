#!/bin/bash
set -e

# Script to update CHANGELOG.md during release process
# Usage: ./update-changelog.sh <version> [upstream-hash]
# Example: ./update-changelog.sh 1.0.8
# Example: ./update-changelog.sh 1.0.8 05e3c46c8c23130c9c064dc43d00ec78f7a75eab

if [ -z "$1" ]; then
    echo "Error: Version argument required"
    echo "Usage: $0 <version> [upstream-hash]"
    exit 1
fi

VERSION="$1"
UPSTREAM_HASH="${2:-}"
CHANGELOG_FILE="${CHANGELOG_FILE:-CHANGELOG.md}"
RELEASE_DATE=$(date +%Y-%m-%d)

echo "Updating CHANGELOG.md for version ${VERSION} (${RELEASE_DATE})"
if [ -n "$UPSTREAM_HASH" ]; then
    echo "  Upstream SDK sync: ${UPSTREAM_HASH:0:7}"
fi

# Check if CHANGELOG.md exists
if [ ! -f "$CHANGELOG_FILE" ]; then
    echo "Error: CHANGELOG.md not found"
    exit 1
fi

# Check if there's an [Unreleased] section
if ! grep -q "## \[Unreleased\]" "$CHANGELOG_FILE"; then
    echo "Error: No [Unreleased] section found in CHANGELOG.md"
    exit 1
fi

# Create a temporary file
TEMP_FILE=$(mktemp)

# Process the CHANGELOG
awk -v version="$VERSION" -v date="$RELEASE_DATE" -v upstream_hash="$UPSTREAM_HASH" '
BEGIN {
    unreleased_found = 0
    content_found = 0
    links_section = 0
    first_version_link = ""
    repo_url = ""
}

# Track if we are in the links section at the bottom
/^\[/ {
    links_section = 1
}

# Capture the repository URL from the first version link
links_section && repo_url == "" && /^\[[0-9]+\.[0-9]+\.[0-9]+(-java\.[0-9]+)?\]:/ {
    match($0, /(https:\/\/github\.com\/[^\/]+\/[^\/]+)\//, arr)
    if (arr[1] != "") {
        repo_url = arr[1]
    }
}

# Replace [Unreleased] with the version and date
/^## \[Unreleased\]/ {
    if (!unreleased_found) {
        print "## [Unreleased]"
        print ""
        if (upstream_hash != "") {
            short_hash = substr(upstream_hash, 1, 7)
            print "> **Upstream sync:** [`github/copilot-sdk@" short_hash "`](https://github.com/github/copilot-sdk/commit/" upstream_hash ")"
            print ""
        }
        print "## [" version "] - " date
        if (upstream_hash != "") {
            print ""
            print "> **Upstream sync:** [`github/copilot-sdk@" short_hash "`](https://github.com/github/copilot-sdk/commit/" upstream_hash ")"
        }
        unreleased_found = 1
        skip_old_upstream = 1
        next
    }
}

# Skip the old upstream sync line and surrounding blank lines from the previous [Unreleased] section
skip_old_upstream && /^[[:space:]]*$/ { next }
skip_old_upstream && /^> \*\*Upstream sync:\*\*/ { next }
skip_old_upstream && !/^[[:space:]]*$/ && !/^> \*\*Upstream sync:\*\*/ { skip_old_upstream = 0 }

# Capture the first version link to get the previous version
links_section && first_version_link == "" && /^\[[0-9]+\.[0-9]+\.[0-9]+(-java\.[0-9]+)?\]:/ {
    match($0, /\[([0-9]+\.[0-9]+\.[0-9]+(-java\.[0-9]+)?)\]:/, arr)
    if (arr[1] != "" && repo_url != "") {
        first_version_link = arr[1]
        # Insert Unreleased and new version links before first version link
        print "[Unreleased]: " repo_url "/compare/v" version "...HEAD"
        print "[" version "]: " repo_url "/compare/v" arr[1] "...v" version
    }
}

# Update existing [Unreleased] link if present
links_section && /^\[Unreleased\]:/ {
    # Get the previous version and repo URL from the existing link
    match($0, /(https:\/\/github\.com\/[^\/]+\/[^\/]+)\/compare\/v([0-9]+\.[0-9]+\.[0-9]+(-java\.[0-9]+)?)\.\.\.HEAD/, arr)
    if (arr[1] != "" && arr[2] != "") {
        print "[Unreleased]: " arr[1] "/compare/v" version "...HEAD"
        print "[" version "]: " arr[1] "/compare/v" arr[2] "...v" version
        next
    }
}

# Print all other lines unchanged
{ print }
' "$CHANGELOG_FILE" > "$TEMP_FILE"

# Replace the original file
mv "$TEMP_FILE" "$CHANGELOG_FILE"

echo "✓ CHANGELOG.md updated successfully"
echo "  - Added version ${VERSION} with date ${RELEASE_DATE}"
echo "  - Created new [Unreleased] section"
echo "  - Updated version comparison links"
