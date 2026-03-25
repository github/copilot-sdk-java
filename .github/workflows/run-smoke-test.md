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
    - java

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

### Step 0 — Install JDK 17 and Maven

Before anything else, install the required build tools:

1. **Microsoft Build of OpenJDK 17** (latest stable). Download and install from the official Microsoft packages:
   ```bash
   wget -q https://packages.microsoft.com/config/ubuntu/$(lsb_release -rs)/packages-microsoft-prod.deb -O /tmp/packages-microsoft-prod.deb
   sudo dpkg -i /tmp/packages-microsoft-prod.deb
   sudo apt-get update
   sudo apt-get install -y msopenjdk-17
   export JAVA_HOME=/usr/lib/jvm/msopenjdk-17-amd64
   export PATH="$JAVA_HOME/bin:$PATH"
   ```

2. **Maven 3.9.14**:
   ```bash
   wget -q https://archive.apache.org/dist/maven/maven-3/3.9.14/binaries/apache-maven-3.9.14-bin.tar.gz -O /tmp/maven.tar.gz
   sudo tar -xzf /tmp/maven.tar.gz -C /opt
   export PATH="/opt/apache-maven-3.9.14/bin:$PATH"
   ```

3. **Verify** both are working:
   ```bash
   java -version    # Must show "Microsoft" and version 17
   mvn --version    # Must show 3.9.14
   ```
   If either installation fails, report failure and stop.

### Step 1 — Build the SDK locally

Run the following command from the repository root to install the SDK snapshot into the local Maven repository:

```bash
mvn -DskipTests clean install
```

This must complete with `BUILD SUCCESS`. If it fails, report failure and stop.

### Step 2 — Execute the smoke test prompt

Read the file `src/test/prompts/PROMPT-smoke-test.md` and follow its instructions, with the override described below.

**Critical override:** The goal of this workflow is to validate the SDK snapshot you just built and installed locally in Step 1, not any newer SNAPSHOT that might exist in a remote repository. To ensure Maven does not download a newer timestamped SNAPSHOT, you must run the smoke-test Maven build in offline mode and without `-U`, so that it uses the locally installed artifact.

**Critical override — authentication:** The Quick Start code creates a `CopilotClient` which spawns the Copilot CLI and needs authentication. The `COPILOT_GITHUB_TOKEN` environment variable is available in your environment. You **must** ensure it is passed through when running the `java -jar` command. For example:
```bash
COPILOT_GITHUB_TOKEN="$COPILOT_GITHUB_TOKEN" java -jar ./target/copilot-sdk-smoketest-1.0-SNAPSHOT.jar
```

Follow every step in the prompt: create the `smoke-test/` directory, create `pom.xml` and the Java source file exactly as specified, build with `mvn -o clean package` (offline and without \`-U\`), run with `java -jar` (with `COPILOT_GITHUB_TOKEN` passed through), and verify the exit code.

### Step 3 — Report result

- **Success** (exit code 0): use the `noop` tool with a message like "Smoke test passed — Quick Start code compiled and ran successfully against SDK version X.Y.Z-SNAPSHOT".
- **Failure** (SDK build failure, smoke test build failure, smoke test run failure, or non-zero exit code): use the `missing_data` tool with `reason` set to a description of what failed and `context` containing a brief summary or the most relevant one or two error lines (keep this under ~200 characters). Do NOT call `noop` on failure. Do not paste full logs into `context`. Examples:
  - SDK build fails: `missing_data(reason="SDK build failed with mvn -DskipTests clean install", context="[ERROR] Failed to execute goal ...")`
  - Smoke test compilation fails: `missing_data(reason="Smoke test Maven build failed", context="[ERROR] Compilation failure: ...")`
  - Smoke test run returns non-zero: `missing_data(reason="Smoke test exited with code N", context="Main class threw <ExceptionType>: <message>")`
