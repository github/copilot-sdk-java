# Merge Reference Implementation SDK Changes

You are an expert Java developer tasked with porting changes from the reference implementation of the Copilot SDK (primarily the .NET implementation) to this Java SDK.

## ⚠️ IMPORTANT: Java SDK Design Takes Priority

**The current design and architecture of the Java SDK is the priority.** When porting changes from the reference implementation:

1. **Adapt, don't copy** - Translate reference implementation features to fit the Java SDK's existing patterns, naming conventions, and architecture
2. **Preserve Java idioms** - The Java SDK should feel natural to Java developers, not like a C# port
3. **Maintain consistency** - New code must match the existing codebase style and structure
4. **Evaluate before porting** - Not every reference implementation change needs to be ported; some may not be applicable or may conflict with Java SDK design decisions

Before making any changes, **read and understand the existing Java SDK implementation** to ensure new code integrates seamlessly.

## Utility Scripts

The `.github/scripts/` directory contains helper scripts that automate the repeatable parts of this workflow. **Use these scripts instead of running the commands manually.**

| Script | Purpose |
|---|---|
| `.github/scripts/reference-impl-sync/merge-reference-impl-start.sh` | Creates branch, updates CLI, clones reference implementation, reads `.lastmerge`, prints commit summary |
| `.github/scripts/reference-impl-sync/merge-reference-impl-diff.sh` | Detailed diff analysis grouped by area (`.NET src`, tests, snapshots, docs, etc.) |
| `.github/scripts/reference-impl-sync/merge-reference-impl-finish.sh` | Runs format + test + build, updates `.lastmerge`, commits, pushes branch |
| `.github/scripts/build/format-and-test.sh` | Standalone `spotless:apply` + `mvn clean verify` (useful during porting too) |

All scripts write/read a `.merge-env` file (git-ignored) to share state (branch name, reference-impl dir, last-merge commit).

## Workflow Overview

1. Run `./.github/scripts/reference-impl-sync/merge-reference-impl-start.sh` (creates branch, clones reference implementation, shows summary)
2. Run `./.github/scripts/reference-impl-sync/merge-reference-impl-diff.sh` (analyze changes)
3. Update README with minimum CLI version requirement
4. Identify reference implementation changes to port
5. Apply changes to Java SDK (commit as you go)
6. Port/adjust tests from reference implementation changes
7. Run `./.github/scripts/build/format-and-test.sh` frequently while porting
8. Build the package
9. Update documentation (**required for every user-facing reference implementation change**)
10. Run `./.github/scripts/reference-impl-sync/merge-reference-impl-finish.sh` (final test + push) and finalize Pull Request (see note below about coding agent vs. manual workflow)
11. Perform final review before handing off

---

## Step 1: Initialize Reference Implementation Sync

Run the start script to create a branch, update the CLI, clone the reference implementation repo, and see a summary of new commits:

```bash
./.github/scripts/reference-impl-sync/merge-reference-impl-start.sh
```

This writes a `.merge-env` file used by the other scripts. It outputs:
- The branch name created
- The Copilot CLI version
- The reference-impl dir path
- A short log of reference implementation commits since `.lastmerge`

## Step 2: Analyze reference implementation Changes

Run the diff script for a detailed breakdown by area:

```bash
./.github/scripts/reference-impl-sync/merge-reference-impl-diff.sh          # stat only
./.github/scripts/reference-impl-sync/merge-reference-impl-diff.sh --full   # full diffs
```

The diff script groups changes into: .NET source, .NET tests, test snapshots, documentation, protocol/config, Go/Node.js/Python SDKs, and other files.

## Step 3: Update README with CLI Version

After the start script runs, check the CLI version it printed (also saved in `.merge-env` as `CLI_VERSION`). Update the Requirements section in `README.md` and `src/site/markdown/index.md` to specify the minimum CLI version requirement.

Commit this change before proceeding:

```bash
git add README.md src/site/markdown/index.md
git commit -m "Update Copilot CLI minimum version requirement"
```

## Step 4: Identify Changes to Port

Using the output from `merge-reference-impl-diff.sh`, focus on:
- `dotnet/src/` - Primary reference implementation
- `dotnet/test/` - Test cases to port
- `docs/` - Documentation updates
- `sdk-protocol-version.json` - Protocol version changes

For each change in the reference implementation diff, determine:

1. **New Features**: New methods, classes, or capabilities added to the SDK
2. **Bug Fixes**: Corrections to existing functionality
3. **API Changes**: Changes to public interfaces or method signatures
4. **Protocol Updates**: Changes to the JSON-RPC protocol or message types
5. **Test Updates**: New or modified test cases

### Key Files to Compare

| reference implementation (.NET)                    | Java SDK Equivalent                                    |
|------------------------------------|--------------------------------------------------------|
| `dotnet/src/Client.cs`             | `src/main/java/com/github/copilot/sdk/CopilotClient.java` |
| `dotnet/src/Session.cs`            | `src/main/java/com/github/copilot/sdk/CopilotSession.java` |
| `dotnet/src/Types.cs`              | `src/main/java/com/github/copilot/sdk/types/*.java`    |
| `dotnet/src/Generated/*.cs`        | `src/main/java/com/github/copilot/sdk/types/*.java`    |
| `dotnet/test/*.cs`                 | `src/test/java/com/github/copilot/sdk/*Test.java`      |
| `docs/getting-started.md`          | `README.md` and `src/site/markdown/*.md`               |
| `docs/*.md` (new files)            | `src/site/markdown/*.md` + update `src/site/site.xml`  |
| `sdk-protocol-version.json`        | (embedded in Java code or resource file)               |

> **⚠️ Important:** When adding new documentation pages, always update `src/site/site.xml` to include them in the navigation menu.

## Step 5: Apply Changes to Java SDK

When porting changes:

### ⚠️ Priority: Preserve Java SDK Design

Before modifying any code:

1. **Read the existing Java implementation first** - Understand current patterns, class structure, and naming
2. **Identify the Java equivalent approach** - Don't replicate C# patterns; find the idiomatic Java way
3. **Check for existing abstractions** - The Java SDK may already have mechanisms that differ from .NET
4. **Preserve backward compatibility** - Existing API signatures should not break unless absolutely necessary
5. **When in doubt, match existing code** - Follow what's already in the Java SDK, not the reference implementation

### Commit Changes Incrementally

**Important:** Commit your changes as you work, grouping related changes together:

```bash
# After porting a feature or fix, commit with a descriptive message
git add <changed-files>
git commit -m "Port <feature/fix name> from the reference implementation"

# Example commits:
# git commit -m "Port new authentication flow from the reference implementation"
# git commit -m "Add new message types from the reference implementation protocol update"
# git commit -m "Port bug fix for session handling from the reference implementation"
```

This creates a clear history of changes that can be reviewed in the Pull Request.

### General Guidelines

- **Naming Conventions**: Convert C# PascalCase to Java camelCase for methods/variables
- **Async Patterns**: C# `async/await` → Java `CompletableFuture` or synchronous equivalents
- **Nullable Types**: C# `?` nullable → Java `@Nullable` annotations or `Optional<T>`
- **Properties**: C# properties → Java getters/setters or records
- **Records**: C# records → Java records (Java 17+)
- **Events**: C# events → Java callbacks or listeners

### Type Mappings

| C# Type                | Java Equivalent              |
|------------------------|------------------------------|
| `string`               | `String`                     |
| `int`                  | `int` / `Integer`            |
| `bool`                 | `boolean` / `Boolean`        |
| `Task<T>`              | `CompletableFuture<T>`       |
| `CancellationToken`    | (custom implementation)      |
| `IAsyncEnumerable<T>`  | `Stream<T>` or `Iterator<T>` |
| `JsonElement`          | `JsonNode` (Jackson)         |
| `Dictionary<K,V>`      | `Map<K,V>`                   |
| `List<T>`              | `List<T>`                    |

### Code Style

Follow the existing Java SDK patterns:
- Use Jackson for JSON serialization (`ObjectMapper`)
- Use Java records for DTOs where appropriate
- Follow the existing package structure under `com.github.copilot.sdk`
- Maintain backward compatibility when possible
- **Match the style of surrounding code** - Consistency with existing code is more important than reference implementation patterns
- **Prefer existing abstractions** - If the Java SDK already solves a problem differently than .NET, keep the Java approach

## Step 6: Port Tests

After porting implementation changes, **always check for new or updated tests** in the reference implementation repository:

### Check for New Tests

```bash
cd "$TEMP_DIR/copilot-sdk"
git diff "$LAST_REFERENCE_IMPL_COMMIT"..origin/main --stat -- dotnet/test/
git diff "$LAST_REFERENCE_IMPL_COMMIT"..origin/main --stat -- test/snapshots/
```

### Port Test Cases

For each new or modified test file in `dotnet/test/`:

1. **Create corresponding Java test class** in `src/test/java/com/github/copilot/sdk/`
2. **Follow existing test patterns** - Look at existing tests like `PermissionsTest.java` for structure
3. **Use the E2ETestContext** infrastructure for tests that need the test harness
4. **Match snapshot directory names** - Test snapshots in `test/snapshots/` must match the directory name used in `ctx.configureForTest()`

### Test File Mapping

| reference implementation Test (.NET)        | Java SDK Test                                          |
|-----------------------------|--------------------------------------------------------|
| `dotnet/test/AskUserTests.cs`  | `src/test/java/com/github/copilot/sdk/AskUserTest.java`  |
| `dotnet/test/HooksTests.cs`    | `src/test/java/com/github/copilot/sdk/HooksTest.java`    |
| `dotnet/test/ClientTests.cs`   | `src/test/java/com/github/copilot/sdk/CopilotClientTest.java` |
| `dotnet/test/*Tests.cs`        | `src/test/java/com/github/copilot/sdk/*Test.java`        |

### Test Snapshot Compatibility

New test snapshots are stored in `test/snapshots/` in the reference implementation repository. These snapshots are automatically cloned during the Maven build process.

If tests fail with errors like `TypeError: Cannot read properties of undefined`, the test harness may not yet support the new RPC methods. In this case:

1. **Mark tests as `@Disabled`** with a clear reason (e.g., `@Disabled("Requires test harness update with X support - see reference implementation PR #NNN")`)
2. **Document the dependency** in the test class Javadoc
3. **Enable tests later** once the harness is updated

### Unit Tests vs E2E Tests

- **Unit tests** (like auth option validation) can run without the test harness
- **E2E tests** require the test harness with matching snapshots

Commit tests separately or together with their corresponding implementation changes.

## Step 7: Format and Run Tests

After applying changes, use the convenience script:

```bash
./.github/scripts/build/format-and-test.sh          # format + full verify
./.github/scripts/build/format-and-test.sh --debug  # with debug logging
```

Or for quicker iteration during porting:

```bash
./.github/scripts/build/format-and-test.sh --format-only   # just spotless
./.github/scripts/build/format-and-test.sh --test-only     # skip formatting
```

### If Tests Fail

1. Read the test output carefully
2. Identify the root cause (compilation error, runtime error, assertion failure)
3. Fix the issue in the Java code
4. Re-run tests
5. Repeat until all tests pass

### Common Issues

- **Missing imports**: Add required import statements
- **Type mismatches**: Ensure proper type conversions
- **Null handling**: Add null checks where C# had nullable types
- **JSON serialization**: Verify Jackson annotations are correct

## Step 8: Build the Package

Once tests pass, build the complete package:

```bash
mvn clean package -DskipTests
```

Verify:
- No compilation errors
- No warnings (if possible)
- JAR file is generated in `target/`

## Step 9: Update Documentation

**Documentation is critical for new features.** Every new feature ported from the reference implementation must be documented before the merge is complete.
Review and complete this documentation checklist before proceeding to Step 10.
If you determine no docs changes are needed, document that decision and rationale in the PR body under a clear heading (for example, `Documentation Impact`).

### Documentation Checklist

For each new feature or significant change:

1. **README.md**: Update the main README if there are user-facing changes
2. **src/site/markdown/index.md**: Update if requirements or quick start examples change
3. **src/site/markdown/documentation.md**: Add sections for new basic usage patterns
4. **src/site/markdown/advanced.md**: Add sections for new advanced features (tools, handlers, configurations)
5. **src/site/markdown/mcp.md**: Update if MCP-related changes are made
6. **Javadoc**: Add/update Javadoc comments for all new/changed public APIs
7. **src/site/site.xml**: Update if new documentation pages were added

### Documentation Requirements for New Features

When adding a new feature, ensure the documentation includes:

- **What it does**: Clear explanation of the feature's purpose
- **How to use it**: Code example showing typical usage
- **API reference**: Link to relevant Javadoc
- **Configuration options**: All available settings/properties

### Example: Documenting a New Handler

If a new handler (like `UserInputHandler`, `PermissionHandler`) is added, create a section in `advanced.md`:

```markdown
## Feature Name

Brief description of what the feature does.

\`\`\`java
var session = client.createSession(
    new SessionConfig()
        .setOnFeatureRequest((request, invocation) -> {
            // Handle the request
            return CompletableFuture.completedFuture(result);
        })
).get();
\`\`\`

Explain the request/response objects and their properties.

See [FeatureHandler](apidocs/com/github/copilot/sdk/json/FeatureHandler.html) Javadoc for more details.
```

### Verify Documentation Consistency

Ensure consistency across all documentation files:

- Requirements section should match in `README.md` and `src/site/markdown/index.md`
- Code examples should use the same patterns and be tested
- Links to Javadoc should use correct paths (`apidocs/...`)

## Step 10: Finish, Push, and Finalize Pull Request

Run the finish script which updates `.lastmerge`, runs a final build, and pushes the branch:

```bash
./.github/scripts/reference-impl-sync/merge-reference-impl-finish.sh            # full format + test + push
./.github/scripts/reference-impl-sync/merge-reference-impl-finish.sh --skip-tests  # if tests already passed
```

### PR Handling: Coding Agent vs. Manual Workflow

**If running as a Copilot coding agent** (triggered via GitHub issue assignment by the weekly sync workflow), a pull request has **already been created automatically** for you. Do NOT create a new one. Just push your commits to the current branch — the existing PR will be updated. Add the `reference-impl-sync` label to the existing PR by running this command in a terminal:

```bash
gh pr edit --add-label "reference-impl-sync"
```

> **No-changes scenario (coding agent only):** If after analyzing the reference implementation diff there are no relevant changes to port to the Java SDK, push an empty commit with a message explaining why no changes were needed, so the PR reflects the analysis outcome. The repository maintainer will close the PR and issue manually.

**If running manually** (e.g., from VS Code via the reusable prompt), create the Pull Request using `gh` CLI or the GitHub MCP tool. Then add the label:

```bash
gh pr create --base main --title "Merge reference implementation SDK changes (YYYY-MM-DD)" --body-file /dev/stdin <<< "$PR_BODY"
gh pr edit --add-label "reference-impl-sync"
```

The PR body should include:
1. **Title**: `Merge reference implementation SDK changes (YYYY-MM-DD)`
2. **Body** with:
   - Summary of reference implementation commits analyzed (with count and commit range)
   - Table of changes ported (commit hash + description)
   - List of changes intentionally not ported (with reasons)
   - Verification status (test count, build status)

### PR Body Template

```markdown
## Reference Implementation Merge

Ports changes from the official Copilot SDK ([github/copilot-sdk](https://github.com/github/copilot-sdk)) since last merge (`<OLD_COMMIT>`→`<NEW_COMMIT>`).

### Reference implementation commits analyzed (N commits)

- Brief description of each reference implementation change and whether it was ported or not

### Changes ported

| Commit | Description |
|---|---|
| `<hash>` | Description of change |

### Not ported (intentionally)

- **Feature name** — Reason why it wasn't ported

### Verification

- All **N tests pass** (`mvn clean test`)
- Package builds successfully (`mvn clean package -DskipTests`)
- Code formatted with Spotless
```

## Step 11: Final Review

Before finishing:

1. Run `git log --oneline main..$BRANCH_NAME` to review all commits
2. Run `git diff main..$BRANCH_NAME --stat` to see a summary of all changes
3. Ensure no unintended changes were made
4. Verify code follows project conventions
5. Confirm the branch was pushed to remote
6. Confirm the Pull Request is ready (created or updated) and provide the PR URL to the user

---

## Checklist

- [ ] New branch created from `main`
- [ ] Copilot CLI updated to latest version
- [ ] README.md updated with minimum CLI version requirement
- [ ] reference implementation repository cloned
- [ ] Diff analyzed between `.lastmerge` commit and HEAD
- [ ] New features/fixes identified
- [ ] Changes ported to Java SDK following conventions
- [ ] **New/updated tests ported from the reference implementation** (check `dotnet/test/` and `test/snapshots/`)
- [ ] Tests marked `@Disabled` if harness doesn't support new features yet
- [ ] Changes committed incrementally with descriptive messages
- [ ] `mvn test` passes
- [ ] `mvn package` builds successfully
- [ ] **Documentation updated for new features:**
  - [ ] `README.md` updated if user-facing changes
  - [ ] `src/site/markdown/index.md` updated if requirements changed
  - [ ] `src/site/markdown/documentation.md` updated for new basic usage
  - [ ] `src/site/markdown/advanced.md` updated for new advanced features
  - [ ] Javadoc added/updated for new public APIs
- [ ] If no documentation files were changed for user-facing reference implementation changes, PR body explicitly explains why documentation changes were not needed
- [ ] `src/site/site.xml` updated if new documentation pages were added
- [ ] `.lastmerge` file updated with new commit hash
- [ ] Branch pushed to remote
- [ ] **Pull Request finalized** (coding agent: push to existing PR; manual: create via `mcp_github_create_pull_request`)
- [ ] **`reference-impl-sync` label added** to the PR via `mcp_github_add_issue_labels`
- [ ] PR URL provided to user

---

## Notes

- The reference implementation SDK is at: `https://github.com/github/copilot-sdk.git`
- Primary reference implementation is in `dotnet/` folder
- This Java SDK targets Java 17+
- Uses Jackson for JSON processing
- Uses JUnit 5 for testing
- **Java SDK design decisions take precedence over reference implementation patterns**
- **Adapt reference implementation changes to fit Java idioms, not the other way around**

