---
description: |
  Run smoke test. Builds the SDK locally, then executes the smoke test
  prompt to verify the Quick Start code works against the built snapshot.

on:
  workflow_dispatch:

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
  missing-data:
    max: 1
  noop:
    report-as-issue: false
---
# Run Smoke Test Agentic Workflow

This workflow builds the copilot-sdk-java SDK locally and runs the smoke test prompt to verify the Quick Start code compiles and runs successfully against the built snapshot.

## Instructions

You are running inside the copilot-sdk-java repository.

### Step 1 — Build the SDK locally

Run the following command from the repository root to install the SDK snapshot into the local Maven repository:

```bash
mvn -DskipTests clean install
```

This must complete with `BUILD SUCCESS`. If it fails, report failure and stop.

### Step 2 — Execute the smoke test prompt

Read the file `src/test/prompts/PROMPT-smoke-test.md` and follow its instructions exactly.

**Critical override:** Because the SDK was already built and installed into the local Maven repository in Step 1, the snapshot artifact is already available locally. Maven resolves local repository artifacts before remote ones, so the snapshot repository configuration in the prompt will still work correctly — it will just find the artifact locally instead of downloading it.

Follow every step in the prompt: create the `smoke-test/` directory, create `pom.xml` and the Java source file exactly as specified, build with `mvn -U clean package`, run with `java -jar`, and verify the exit code.

### Step 3 — Report result

- **Success** (exit code 0): use the `noop` tool with a message like "Smoke test passed — Quick Start code compiled and ran successfully against SDK version X.Y.Z-SNAPSHOT".
- **Failure** (SDK build failure, smoke test build failure, smoke test run failure, or non-zero exit code): use the `missing_data` tool with `reason` set to a description of what failed and `context` containing the relevant error output. Do NOT call `noop` on failure. Examples:
  - SDK build fails: `missing_data(reason="SDK build failed with mvn -DskipTests clean install", context="<error output>")`
  - Smoke test compilation fails: `missing_data(reason="Smoke test Maven build failed", context="<error output>")`
  - Smoke test run returns non-zero: `missing_data(reason="Smoke test exited with code N", context="<program output>")`
