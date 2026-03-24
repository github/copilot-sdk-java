---
description: |
  Weekly reference implementation sync workflow. Checks for new commits in the official
  Copilot SDK (github/copilot-sdk) and assigns to Copilot to port changes.

on:
  schedule: weekly
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
    assignees: [copilot]
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
# Weekly Reference Implementation Sync Agentic Workflow
This document describes the `weekly-reference-impl-sync.yml` GitHub Actions workflow, which automates the detection of new changes in the official [Copilot SDK](https://github.com/github/copilot-sdk) and delegates the merge work to the Copilot coding agent.

## Overview

The workflow runs on a **weekly schedule** (every Monday at 10:00 UTC) and can also be triggered manually. It does **not** perform the actual merge — instead, it detects reference implementation changes and creates a GitHub issue assigned to `copilot`, instructing the agent to follow the [agentic-merge-reference-impl](../prompts/agentic-merge-reference-impl.prompt.md) prompt to port the changes.

The agent must also create the Pull Request with the label `reference-impl-sync`. This allows the workflow to track the merge progress and avoid creating duplicate issues if the agent is still working on a previous sync.

## Trigger

| Trigger | Schedule |
|---|---|
| `schedule` | Every Monday at 10:00 UTC (`0 10 * * 1`) |
| `workflow_dispatch` | Manual trigger from the Actions tab |

## Workflow Steps

### 1. Checkout repository

Checks out the repo to read the `.lastmerge` file, which contains the SHA of the last reference implementation commit that was merged into the Java SDK.

### 2. Check for reference implementation changes

- Reads the last merged commit hash from `.lastmerge`
- Clones the reference implementation `github/copilot-sdk` repository
- Compares `.lastmerge` against reference implementation `HEAD`
- If they match: sets `has_changes=false`
- If they differ: counts new commits, generates a summary (up to 20 most recent), and sets outputs (`commit_count`, `reference_impl_head`, `last_merge`, `summary`)

### 3. Close previous reference-impl-sync issues (when changes found)

**Condition:** `has_changes == true`

Before creating a new issue, closes any existing open issues with the `reference-impl-sync` label. This prevents stale issues from accumulating when previous sync attempts were incomplete or superseded. Each closed issue receives a comment explaining it was superseded.

### 4. Close stale reference-impl-sync issues (when no changes found)

**Condition:** `has_changes == false`

If the reference implementation is already up to date, closes any lingering open `reference-impl-sync` issues with a comment noting that no changes were detected. This handles the case where a previous issue was created but the changes were merged manually (updating `.lastmerge`) before the agent completed.

### 5. Create issue and assign to Copilot

**Condition:** `has_changes == true`

Creates a new GitHub issue with:

- **Title:** `Reference implementation sync: N new commits (YYYY-MM-DD)`
- **Label:** `reference-impl-sync`
- **Assignee:** `copilot`
- **Body:** Contains commit count, commit range links, a summary of recent commits, and a link to the merge prompt

The Copilot coding agent picks up the issue, creates a branch and PR, then follows the merge prompt to port the changes.

### 6. Summary

Writes a GitHub Actions step summary with:

- Whether changes were detected
- Commit count and range
- Recent reference implementation commits
- Link to the created issue (if any)

## Flow Diagram

```
┌─────────────────────┐
│  Schedule / Manual  │
└──────────┬──────────┘
           │
           ▼
┌──────────────────────────┐
│ Read .lastmerge          │
│ Clone reference impl SDK │
│ Compare commits          │
└──────────┬───────────────┘
           │
     ┌─────┴─────┐
     │           │
  changes?     no changes
     │           │
     ▼           ▼
┌──────────┐  ┌──────────────────┐
│ Close old│  │ Close stale      │
│ issues   │  │ issues           │
└────┬─────┘  └──────────────────┘
     │
     ▼
┌──────────────────────────┐
│ Create issue assigned to │
│ copilot                  │
└──────────────────────────┘
     │
     ▼
┌──────────────────────────┐
│ Agent follows prompt to  │
│ port changes → PR        │
└──────────────────────────┘
```

## Related Files

| File | Purpose |
|---|---|
| `.lastmerge` | Stores the SHA of the last merged reference implementation commit |
| [agentic-merge-reference-impl.prompt.md](../prompts/agentic-merge-reference-impl.prompt.md) | Detailed instructions the Copilot agent follows to port changes |
| `.github/scripts/reference-impl-sync/` | Helper scripts used by the merge prompt |
