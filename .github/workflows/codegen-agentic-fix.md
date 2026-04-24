---
description: |
  Agentic fix for codegen-related build/test failures. Invoked when
  mvn verify fails after code generation changes.

on:
  workflow_dispatch:
    inputs:
      branch:
        description: 'Branch to fix'
        required: true
        type: string
      pr_number:
        description: 'PR number to push fixes to'
        required: true
        type: string
      error_summary:
        description: 'Summary of mvn verify failures'
        required: true
        type: string

permissions:
  contents: read
  actions: read

network:
  allowed:
    - defaults
    - github

tools:
  github:
    toolsets: [context, repos]

safe-outputs:
  push-to-pull-request-branch:
    max: 3
  add-comment:
    target: "*"
    max: 5
  noop:
    report-as-issue: false
---
# Codegen Agentic Fix

You are an automation agent that fixes Java compilation and test failures caused by code generation changes in the `copilot-sdk-java` repository.

## Context

A code generation step updated files under `src/generated/java/` and `mvn verify` subsequently failed. Your job is to fix the **handwritten** source code so it compiles and passes tests with the new generated code.

The branch to fix is: `${{ inputs.branch }}`
The PR number is: `${{ inputs.pr_number }}`

The error summary from the failing build is:
```
${{ inputs.error_summary }}
```

## Instructions

Follow these steps exactly. You have a maximum of **3 attempts** to fix the build.

### Step 0: Setup

Check out the branch and ensure the environment is ready:

```bash
git checkout "${{ inputs.branch }}"
git pull origin "${{ inputs.branch }}"
```

Set up the Java 17 environment and verify Maven is available:

```bash
java -version
mvn --version
```

### Step 1: Reproduce the failure

Run `mvn verify` to see the current errors:

```bash
mvn verify 2>&1 | tee /tmp/mvn-verify.log
```

Review the full log at `/tmp/mvn-verify.log` if the tail output is insufficient. The earliest errors are often the root cause.

If `mvn verify` succeeds (exit code 0), there is nothing to fix. Call the `noop` safe-output with message "mvn verify already passes on branch ${{ inputs.branch }}. No fixes needed." and stop.

### Step 2: Analyze and fix (up to 3 attempts)

For each attempt:

1. **Read the errors carefully.** Look for:
   - Compilation errors (missing methods, type mismatches, import issues)
   - Test failures (assertion errors, runtime exceptions)
   - The specific files and line numbers mentioned in the errors

2. **Fix the affected source files.** You may modify files under:
   - `src/main/java/` — handwritten SDK source code
   - `src/test/java/` — handwritten test code

   **CRITICAL: NEVER modify files under `src/generated/java/`.** Those files are auto-generated and must not be hand-edited. If the error appears to be in generated code, the fix must be in the handwritten code that consumes it.

   **CRITICAL: NEVER modify `pom.xml`.** The build configuration is not in scope.

3. **Run formatting after making changes:**
   ```bash
   mvn spotless:apply
   ```

4. **Verify the fix:**
   ```bash
   mvn verify 2>&1 | tee /tmp/mvn-verify.log
   ```

   If the output is long, check `/tmp/mvn-verify.log` for the full error details — root causes often appear early in the log.

5. If `mvn verify` passes, proceed to Step 3.
   If it fails and you have attempts remaining, go back to sub-step 1.

### Step 3: Push fixes

After `mvn verify` passes, commit the changes and use the `push_to_pull_request_branch` safe-output tool to push to PR #${{ inputs.pr_number }}:

```bash
git add -A
git commit -m "Fix build failures after codegen update

Automated fix applied by codegen-agentic-fix workflow."
```

Then call the `push_to_pull_request_branch` tool to push your commits to the PR branch.

### Step 4: Failure handling

If all 3 attempts fail:

1. Call the `add_comment` tool on PR #${{ inputs.pr_number }} explaining:
   - What errors remain
   - What fixes were attempted
   - That manual intervention is needed

2. Call the `noop` safe-output with a message summarizing the failure.

Do **NOT** push broken code.

## Important constraints

- **NEVER** modify files under `src/generated/java/` — these are auto-generated
- **NEVER** modify `pom.xml` — build config is not in scope
- **NEVER** modify files under `scripts/codegen/` — codegen scripts are not in scope
- **NEVER** modify files under `.github/` — workflow files are not in scope
- Always run `mvn spotless:apply` before committing to ensure code formatting
- Maximum 3 fix attempts before reporting failure via `noop`
- Only push if `mvn verify` passes
