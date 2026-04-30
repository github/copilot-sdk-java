---
description: |
  Reference implementation sync workflow. Checks for new commits in the official
  Copilot SDK (github/copilot-sdk) and assigns to Copilot to port changes.

on:
  schedule: daily
  workflow_dispatch:

permissions:
  contents: read
  actions: read
  issues: read

network:
  allowed:
    - defaults
    - github

tools:
  github:
    toolsets: [context, repos, issues]

safe-outputs:
  create-issue:
    title-prefix: "[reference-impl-sync] "
    assignees: [copilot-swe-agent]
    labels: [reference-impl-sync]
    expires: 6
  close-issue:
    required-labels: [reference-impl-sync]
    target: "*"
    max: 10
  add-comment:
    target: "*"
    max: 10
  assign-to-agent:
    name: "copilot"
    model: "claude-opus-4.6"
    target: "*"
  noop:
    report-as-issue: false
---
# Reference Implementation Sync

You are an automation agent that detects new reference implementation changes and creates GitHub issues. You do **NOT** perform any code merges, edits, or pushes. Do **NOT** invoke any skills (especially `agentic-merge-reference-impl`). Your only job is to check for changes and use safe-output tools to create or close issues.

## Instructions

Follow these steps exactly:

### Step 1: Read `.lastmerge`

Read the file `.lastmerge` in the repository root. It contains the SHA of the last reference implementation commit that was merged into this Java SDK.

### Step 2: Check for reference implementation changes

Clone the reference implementation repository and compare commits:

```bash
LAST_MERGE=$(cat .lastmerge)
git clone --quiet https://github.com/github/copilot-sdk.git /tmp/gh-aw/agent/reference-impl
cd /tmp/gh-aw/agent/reference-impl
REFERENCE_IMPL_HEAD=$(git rev-parse HEAD)
```

If `LAST_MERGE` equals `REFERENCE_IMPL_HEAD`, there are **no new changes**. Go to Step 3a.

If they differ, count the new commits and generate a summary:

```bash
COMMIT_COUNT=$(git rev-list --count "$LAST_MERGE".."$REFERENCE_IMPL_HEAD")
SUMMARY=$(git log --oneline "$LAST_MERGE".."$REFERENCE_IMPL_HEAD" | head -20)
```

Go to Step 3b.

### Step 3a: No changes detected

1. Search for any open issues with the `reference-impl-sync` label using the GitHub MCP tools.
2. If there are open `reference-impl-sync` labeled issues, close each one using the `close_issue` safe-output tool with a comment: "No new reference implementation changes detected. The Java SDK is up to date. Closing."
3. Call the `noop` safe-output tool with message: "No new reference implementation changes since last merge (<LAST_MERGE>)."
4. **Stop here.** Do not proceed further.

### Step 3b: Changes detected

1. Search for any open issues with the `reference-impl-sync` label using the GitHub MCP tools.
2. Close each existing open `reference-impl-sync` issue using the `close_issue` safe-output tool with a comment: "Superseded by a newer reference implementation sync check."
3. Create a new issue using the `create_issue` safe-output tool with:
   - **Title:** `Reference Implementation sync: <COMMIT_COUNT> new commits (<YYYY-MM-DD>)`
   - **Body:** Include the following information:
     ```
     ## Automated Reference Implementation Sync

     There are **<COMMIT_COUNT>** new commits in the [official Copilot SDK](https://github.com/github/copilot-sdk) since the last merge.

     - **Last merged commit:** [`<LAST_MERGE>`](https://github.com/github/copilot-sdk/commit/<LAST_MERGE>)
     - **Reference Implementation HEAD:** [`<REFERENCE_IMPL_HEAD>`](https://github.com/github/copilot-sdk/commit/<REFERENCE_IMPL_HEAD>)

     ### Recent reference implementation commits

     ```
     <SUMMARY>
     ```

     ### Instructions

     Follow the [agentic-merge-reference-impl](.github/prompts/agentic-merge-reference-impl.prompt.md) prompt to port these changes to the Java SDK.
     ```
4. After creating the issue, use the `assign_to_agent` safe-output tool to assign Copilot to the newly created issue.

## Important constraints

- **Do NOT edit any files**, create branches, or push code.
- **Do NOT invoke any skills** such as `agentic-merge-agentic-merge-reference-impl` or `commit-as-pull-request`.
- **Do NOT attempt to merge or port reference implementation changes.** That is done by a separate agent that picks up the issue you create.
- You **MUST** call at least one safe-output tool (`create_issue`, `close_issue`, `noop`, etc.) before finishing.
