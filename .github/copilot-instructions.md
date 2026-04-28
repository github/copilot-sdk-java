# Copilot Instructions for copilot-sdk-java

A Java SDK for programmatic control of GitHub Copilot CLI. This is a port of the official .NET SDK, targeting Java 17+.

## About These Instructions

These instructions guide GitHub Copilot when assisting with this repository. They cover:

- **Tech Stack**: Java 17+, Maven, Jackson for JSON, JUnit for testing
- **Purpose**: Provide a Java SDK for programmatic control of GitHub Copilot CLI
- **Architecture**: JSON-RPC client communicating with Copilot CLI over stdio
- **Key Goals**: Maintain parity with reference implementation .NET SDK while following Java idioms

## Build & Test Commands

```bash
# Build and run all tests
mvn clean verify

# Run a single test class
mvn test -Dtest=CopilotClientTest

# Run a single test method
mvn test -Dtest=ToolsTest#testToolInvocation

# Format code (required before commit)
mvn spotless:apply

# Check formatting only
mvn spotless:check

# Build without tests
mvn clean package -DskipTests

# Run tests with debug logging
mvn test -Pdebug
```

### Running Tests from AI Agents / Copilot

When running tests to verify changes, **always use `mvn verify` without `-q` and without piping through `grep`**. The full output is needed to diagnose failures. Do NOT use commands like:

```bash
# BAD - hides critical failure details, often requires a second run
mvn verify -q 2>&1 | grep -E 'Tests run:|BUILD'
mvn verify 2>&1 | grep -E 'Tests run.*in com|BUILD|test failure'
```

Instead, use one of these approaches:

```bash
# GOOD - run tests with full output (preferred for investigating failures)
mvn verify

# GOOD - run tests showing just the summary and result using Maven's built-in log level
mvn verify -B --fail-at-end 2>&1 | tail -30

# GOOD - run a single test class when debugging a specific test
mvn test -Dtest=CopilotClientTest
```

**Interpreting results:**
- `BUILD SUCCESS` at the end means all tests passed. No further investigation needed.
- `BUILD FAILURE` means something failed. The lines immediately above `BUILD FAILURE` will contain the relevant error information. Look for `[ERROR]` lines near the bottom of the output.
- The Surefire summary line `Tests run: X, Failures: Y, Errors: Z, Skipped: W` appears near the end and gives the counts.
- **Do NOT run tests a second time** just to get different output formatting. One run with full output is sufficient.

## Architecture

### Core Components

- **CopilotClient** - Main entry point. Manages connection to Copilot CLI server via JSON-RPC over stdio. Spawns CLI process or connects to existing server.
- **CopilotSession** - Represents a conversation session. Handles event subscriptions, tool registration, permissions, and message sending.
- **JsonRpcClient** - Low-level JSON-RPC protocol implementation using Jackson for serialization.

### Package Structure

- `com.github.copilot.sdk` - Core classes (CopilotClient, CopilotSession, JsonRpcClient)
- `com.github.copilot.sdk.json` - DTOs, request/response types, handler interfaces (SessionConfig, MessageOptions, ToolDefinition, etc.)
- `com.github.copilot.sdk.generated` - Generated event types for session streaming (SessionEvent, AssistantMessageEvent, SessionIdleEvent, ToolExecutionStartEvent, etc.)

### Test Infrastructure

Tests use the official copilot-sdk test harness from `https://github.com/github/copilot-sdk`. The harness is automatically cloned during `generate-test-resources` phase to `target/copilot-sdk/`.

- **E2ETestContext** - Manages test environment with CapiProxy for deterministic API responses
- **CapiProxy** - Node.js-based replaying proxy using YAML snapshots from `test/snapshots/`
- Test snapshots are stored in the reference implementation repo's `test/snapshots/` directory

## Key Conventions

### Reference Implementation Merging

This SDK tracks the official .NET implementation at `github/copilot-sdk`. The `.lastmerge` file contains the last merged reference implementation commit hash. Use the `agentic-merge-reference-impl` skill (see `.github/prompts/agentic-merge-reference-impl.prompt.md`) to port changes.

When porting from .NET:
- Adapt to Java idioms, don't copy C# patterns directly
- Convert `async/await` → `CompletableFuture`
- Convert C# properties → Java getters/setters or fluent setters
- Use Jackson for JSON (`ObjectMapper`, `@JsonProperty`)

### Code Style

- 4-space indentation (enforced by Spotless with Eclipse formatter)
- Fluent setter pattern for configuration classes (e.g., `new SessionConfig().setModel("gpt-5").setTools(tools)`)
- Public APIs require Javadoc (enforced by Checkstyle, except `json` and `events` packages)
- Pre-commit hook runs `mvn spotless:check` - Must be manually enabled with: `git config core.hooksPath .githooks`, except in the Copilot coding agent environment. This hook is explicitly enabled in the Copilot coding agent environment. See [copilot-setup-steps.yml](workflows/copilot-setup-steps.yml).

### Handler Pattern

Handlers use functional interfaces with `CompletableFuture` returns:

```java
session.createSession(new SessionConfig()
    .setOnPermissionRequest((request, invocation) -> 
        CompletableFuture.completedFuture(new PermissionRequestResult().setKind("allow")))
    .setOnUserInput((request, invocation) -> 
        CompletableFuture.completedFuture(new UserInputResponse().setResponse("user input")))
);
```

### Event Handling

Sessions emit typed events via `session.on()`:

```java
session.on(AssistantMessageEvent.class, msg -> System.out.println(msg.getData().content()));
session.on(SessionIdleEvent.class, idle -> done.complete(null));
```

### Sealed Event Hierarchy

`AbstractSessionEvent` is a sealed class permitting specific event types. Use pattern matching:

```java
switch (event) {
    case AssistantMessageEvent msg -> handleMessage(msg);
    case ToolExecutionStartEvent tool -> handleToolStart(tool);
    case SessionIdleEvent idle -> handleIdle();
    default -> { }
}
```

### Tool Definition Pattern

Custom tools use `ToolDefinition.create()` with JSON Schema parameters and a `ToolHandler`:

```java
var tool = ToolDefinition.create(
    "get_weather",
    "Get weather for a location",
    Map.of(
        "type", "object",
        "properties", Map.of("location", Map.of("type", "string")),
        "required", List.of("location")
    ),
    invocation -> {
        // Type-safe: invocation.getArgumentsAs(WeatherArgs.class)
        // Or Map-based: invocation.getArguments().get("location")
        return CompletableFuture.completedFuture(result);
    }
);
```

## Testing Conventions

### E2E Test Structure

Tests extend the shared context pattern:

```java
private static E2ETestContext ctx;

@BeforeAll
static void setup() throws Exception {
    ctx = E2ETestContext.create();
}

@AfterAll
static void teardown() throws Exception {
    if (ctx != null) ctx.close();
}

@Test
void testFeature() throws Exception {
    ctx.configureForTest("category", "test_name");  // Loads test/snapshots/category/test_name.yaml
    try (CopilotClient client = ctx.createClient()) {
        // Test logic
    }
}
```

### Snapshot Naming

Test method names are converted to lowercase snake_case for snapshot filenames to avoid case collisions on macOS/Windows.

## JSON Serialization

- Uses Jackson with `@JsonProperty` annotations
- `@JsonInclude(JsonInclude.Include.NON_NULL)` on DTOs to omit null fields
- `ObjectMapper` configured via `JsonRpcClient.getObjectMapper()` with:
  - `JavaTimeModule` for date/time handling
  - `FAIL_ON_UNKNOWN_PROPERTIES = false` for forward compatibility

## Documentation

- Site docs in `src/site/markdown/` (filtered for `${project.version}` substitution)
- Update `src/site/site.xml` when adding new documentation pages
- Javadoc required for public APIs except `json` and `events` packages (self-documenting DTOs)
- **Copilot CLI Version**: When updating the required Copilot CLI version in `README.md`, also update it in `src/site/markdown/index.md` to keep them in sync

## Boundaries and Restrictions

### What NOT to Modify

- **DO NOT** edit, create, or delete any file under `src/generated/java/` — these are **auto-generated** by `scripts/codegen/java.ts` and will be overwritten. To update generated code, run `mvn generate-sources -Pcodegen` or trigger the `update-copilot-dependency.yml` workflow.
   If you need to get a new version of the schemas, run `mvn generate-sources -Pupdate-schemas-from-npm-artifact` before running the code generator.
- **DO NOT** edit `.github/agents/` directory - these contain instructions for other agents
- **DO NOT** modify `target/` directory - this contains build artifacts
- **DO NOT** edit `pom.xml` dependencies without careful consideration - this SDK has minimal dependencies by design
- **DO NOT** change the Jackson version without testing against all serialization patterns
- **DO NOT** modify test snapshots in `target/copilot-sdk/test/snapshots/` - these come from reference implementation
- **DO NOT** alter the Eclipse formatter configuration in `pom.xml` without team consensus
- **DO NOT** remove or skip Checkstyle or Spotless checks
- **YOU MUST ALWAYS** run `gh aw compile <workflow-name>` after editing any `.github/workflows/*.md` agentic workflow source file to regenerate the corresponding `.lock.yml`. The lock file contains a content hash of the frontmatter — any edit to the `.md` without recompiling will cause the workflow to fail at runtime with a "lock file out of sync" error.
- **DO NOT** edit `.github/workflows/*.lock.yml` directly — these are auto-generated by `gh aw compile` from the `.md` source files.

### Security Guidelines

- **NEVER** commit secrets, API keys, tokens, or credentials to the repository
- **NEVER** commit `.env` files or any files containing sensitive configuration
- **NEVER** log sensitive data in code (API keys, tokens, user data)
- Always use `try-with-resources` for streams and readers to prevent resource leaks
- Always use `StandardCharsets.UTF_8` when creating InputStreamReader/OutputStreamWriter
- Review any new dependencies for known security vulnerabilities before adding them
- When handling user input in tools or handlers, consider injection risks

## Dependency Management

This SDK is designed to be **lightweight with minimal dependencies**:

- Core dependencies: Jackson (JSON), JUnit (tests only)
- Before adding new dependencies:
  1. Consider if the functionality can be implemented without a new dependency
  2. Check if Jackson already provides the needed functionality
  3. Ensure the dependency is actively maintained and widely used
  4. Verify compatibility with Java 17+
  5. Check for security vulnerabilities
  6. Get team approval for non-trivial additions

## Pre-commit Hooks and Formatting (Coding Agent)

The repository has a pre-commit hook (`.githooks/pre-commit`) that is **automatically enabled** in the Copilot coding agent environment via `copilot-setup-steps.yml`. The hook runs `mvn spotless:check` on any commit that includes changes under `src/`.

**If a commit fails due to the pre-commit hook:**

1. Run `mvn spotless:apply` to auto-fix formatting issues.
2. Re-stage the changed files with `git add -u`.
3. Retry the commit.

**Best practice:** Always run `mvn spotless:apply` before committing Java source changes to avoid hook failures in the first place. If you forget and the hook rejects the commit, follow the three steps above and continue.

## Commit and PR Guidelines

### Commit Messages

- Use clear, descriptive commit messages
- Start with a verb in present tense (e.g., "Add", "Fix", "Update", "Refactor")
- Keep the first line under 72 characters
- Add details in the body if needed
- Examples:
  - `Add support for streaming responses`
  - `Fix resource leak in JsonRpcClient`
  - `Update documentation for tool handlers`
  - `Refactor event handling to use sealed classes`

### Pull Requests

- Keep PRs focused and minimal - one feature/fix per PR
- Ensure all tests pass before requesting review
- Run `mvn spotless:apply` before committing
- Include tests for new functionality
- Update documentation if adding/changing public APIs
- Reference related issues using `#issue-number`
- For reference implementation merges, follow the `agentic-merge-reference-impl` skill workflow

## Development Workflow

1. **Setup**: Enable git hooks with `git config core.hooksPath .githooks`
2. **Branch**: Create feature branches from `main`
3. **Code**: Write code following the conventions above
4. **Format**: Run `mvn spotless:apply` to format code
5. **Test**: Run `mvn clean verify` to ensure all tests pass
6. **Commit**: Make focused commits with clear messages
7. **Push**: Push your branch and create a PR
8. **Review**: Address review feedback and iterate

## Release Process

The release process is automated via the `publish-maven.yml` GitHub Actions workflow. Key steps:

1. **CHANGELOG Update**: The script `.github/scripts/release/update-changelog.sh` automatically:
   - Converts the `## [Unreleased]` section to `## [version] - date`
   - Creates a new empty `## [Unreleased]` section at the top
   - Updates version comparison links at the bottom of CHANGELOG.md
   - Injects the reference implementation SDK commit hash (from `.lastmerge`) as a `> **Reference implementation sync:**` blockquote in both the new `[Unreleased]` section and the released version section

2. **Reference Implementation Sync Tracking**: Each release records which commit from the official `github/copilot-sdk` it is synced to:
   - The `.lastmerge` file is read during the release workflow
   - The commit hash is injected into `CHANGELOG.md` under the release heading
   - Format: `> **Reference implementation sync:** [\`github/copilot-sdk@SHORT_HASH\`](link-to-commit)`

3. **Documentation Updates**: README.md and jbang-example.java are updated with the new version.

4. **Maven Release**: Uses `maven-release-plugin` to:
   - Update pom.xml version
   - Create a git tag
   - Deploy to Maven Central

5. **Rollback**: If the release fails, the documentation commit is automatically reverted

The workflow is triggered manually via workflow_dispatch with optional version parameters.

