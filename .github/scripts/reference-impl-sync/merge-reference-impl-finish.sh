#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────
# merge-reference-impl-finish.sh
#
# Finalises a reference implementation merge:
#   1. Runs format + test + build  (via format-and-test.sh)
#   2. Updates .lastmerge to reference implementation HEAD
#   3. Syncs the @github/copilot version property in pom.xml from the
#      cloned reference implementation's nodejs/package.json
#   4. Commits the .lastmerge + pom.xml updates
#   5. Pushes the branch to origin
#
# Usage:  ./.github/scripts/reference-impl-sync/merge-reference-impl-finish.sh
#         ./.github/scripts/reference-impl-sync/merge-reference-impl-finish.sh --skip-tests
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

SKIP_TESTS=false
if [[ "${1:-}" == "--skip-tests" ]]; then
    SKIP_TESTS=true
fi

cd "$ROOT_DIR"

# ── 1. Format, test, build ───────────────────────────────────
if $SKIP_TESTS; then
    echo "▸ Formatting only (tests skipped)…"
    mvn spotless:apply
    mvn clean package -DskipTests
else
    echo "▸ Running format + test + build…"
    "$ROOT_DIR/.github/scripts/build/format-and-test.sh"
fi

# ── 2. Update .lastmerge ─────────────────────────────────────
echo "▸ Updating .lastmerge…"
NEW_COMMIT=$(cd "$REFERENCE_IMPL_DIR" && git rev-parse origin/main)
echo "$NEW_COMMIT" > "$ROOT_DIR/.lastmerge"

# ── 2b. Sync pom.xml @github/copilot version ─────────────────
# Keeps the canonical CLI version in pom.xml aligned with what the
# reference implementation pinned in .lastmerge depends on.
echo "▸ Syncing @github/copilot version in pom.xml from reference implementation…"
"$ROOT_DIR/.github/scripts/reference-impl-sync/sync-cli-version-from-reference-impl.sh" "$REFERENCE_IMPL_DIR"

git add .lastmerge pom.xml
git commit -m "Update .lastmerge to $NEW_COMMIT and sync pom.xml CLI version"

# ── 3. Push branch ───────────────────────────────────────────
echo "▸ Pushing branch $BRANCH_NAME to origin…"
git push -u origin "$BRANCH_NAME"

echo ""
echo "✅  Branch pushed. Next step:"
echo "  Create a Pull Request (base: main, head: $BRANCH_NAME)"
echo ""
echo "  Suggested title:  Merge reference implementation SDK changes ($(date +%Y-%m-%d))"
echo "  Don't forget to add the 'reference-impl-sync' label."
