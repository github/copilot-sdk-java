<!-- Custom instructions for the Copilot coding agent when triggered by the reference implementation sync workflow. -->
<!-- This file is read by .github/workflows/reference-impl-sync.yml and passed as custom_instructions. -->

Follow the agentic-merge-reference-impl prompt at .github/prompts/agentic-merge-reference-impl.prompt.md
to port reference implementation changes to the Java SDK.

Use the utility scripts in .github/scripts/ subfolders for initialization, diffing, formatting, and testing.
Commit changes incrementally. Update .lastmerge when done.

IMPORTANT: A pull request has already been created automatically for you — do NOT create a new
one. Push your commits to the current branch, and the existing PR will be updated.

Add the 'reference-impl-sync' label to the existing PR by running this command in a terminal:

    gh pr edit --add-label "reference-impl-sync"

If after analyzing the reference implementation diff there are no relevant changes to port to the Java SDK,
push an empty commit with a message explaining why no changes were needed, so the PR reflects
the analysis outcome. The repository maintainer will close the PR and issue manually.

❌❌❌ ABSOLUTE PROHIBITION ❌❌❌

NEVER MODIFY ANY FILE UNDER src/generated/java/ — THESE FILES ARE AUTO-GENERATED AND FORBIDDEN.

If any change requires modifying src/generated/java/:
1. STOP IMMEDIATELY — do not make the change
2. FAIL the sync with an explanatory commit message
3. Instruct the maintainer to re-run update-copilot-dependency.yml to regenerate these files

See the ABSOLUTE PROHIBITION section in .github/prompts/agentic-merge-reference-impl.prompt.md
for the full required procedure and commit message template.

❌❌❌ END ABSOLUTE PROHIBITION ❌❌❌
