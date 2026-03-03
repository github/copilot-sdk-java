# Advanced Usage

> ⚠️ **Disclaimer:** This is an **unofficial, community-driven SDK** and is **not supported or endorsed by GitHub**. Use at your own risk.

This guide covers advanced scenarios for extending and customizing your Copilot integration.

## Table of Contents

- [Custom Tools](#Custom_Tools)
- [System Messages](#System_Messages)
  - [Adding Rules](#Adding_Rules)
  - [Full Control](#Full_Control)
- [File Attachments](#File_Attachments)
- [Bring Your Own Key (BYOK)](#Bring_Your_Own_Key_BYOK)
- [Infinite Sessions](#Infinite_Sessions)
  - [Compaction Events](#Compaction_Events)
- [MCP Servers](#MCP_Servers)
- [Custom Agents](#Custom_Agents)
- [Skills Configuration](#Skills_Configuration)
  - [Loading Skills](#Loading_Skills)
  - [Disabling Skills](#Disabling_Skills)
- [Custom Configuration Directory](#Custom_Configuration_Directory)
- [User Input Handling](#User_Input_Handling)
- [Permission Handling](#Permission_Handling)
- [Session Hooks](#Session_Hooks)
- [Manual Server Control](#Manual_Server_Control)
- [Session Context and Filtering](#Session_Context_and_Filtering)
  - [Listing Sessions with Context](#Listing_Sessions_with_Context)
  - [Filtering Sessions by Context](#Filtering_Sessions_by_Context)
  - [Context Changed Events](#Context_Changed_Events)
- [Session Lifecycle Events](#Session_Lifecycle_Events)
  - [Subscribing to All Lifecycle Events](#Subscribing_to_All_Lifecycle_Events)
  - [Subscribing to Specific Event Types](#Subscribing_to_Specific_Event_Types)
- [Foreground Session Control (TUI+Server Mode)](#Foreground_Session_Control_TUIServer_Mode)
  - [Getting the Foreground Session](#Getting_the_Foreground_Session)
  - [Setting the Foreground Session](#Setting_the_Foreground_Session)
- [Error Handling](#Error_Handling)
  - [Event Handler Exceptions](#Event_Handler_Exceptions)
  - [Custom Event Error Handler](#Custom_Event_Error_Handler)
  - [Event Error Policy](#Event_Error_Policy)

---

## Custom Tools

Let the AI call back into your application to fetch data or perform actions.

```java
// Define strongly-typed arguments with a record
record IssueArgs(String id) {}

var lookupTool = ToolDefinition.create(
    "lookup_issue",
    "Fetch issue details from our tracker",
    Map.of(
        "type", "object",
        "properties", Map.of(
            "id", Map.of("type", "string", "description", "Issue identifier")
        ),
        "required", List.of("id")
    ),
    invocation -> {
        IssueArgs args = invocation.getArgumentsAs(IssueArgs.class);
        return CompletableFuture.completedFuture(fetchIssue(args.id()));
    }
);

var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setTools(List.of(lookupTool))
).get();
```

See [ToolDefinition](apidocs/com/github/copilot/sdk/json/ToolDefinition.html) Javadoc for schema details.

---

## System Messages

Customize the AI's behavior by adding rules or replacing the default prompt.

### Adding Rules

Use `APPEND` mode to add constraints while keeping default guardrails:

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setSystemMessage(new SystemMessageConfig()
            .setMode(SystemMessageMode.APPEND)
            .setContent("""
                <rules>
                - Always check for security vulnerabilities
                - Suggest performance improvements
                </rules>
            """))
).get();
```

### Full Control

Use `REPLACE` mode for complete control (removes default guardrails):

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setSystemMessage(new SystemMessageConfig()
            .setMode(SystemMessageMode.REPLACE)
            .setContent("You are a helpful coding assistant."))
).get();
```

---

## File Attachments

Include files as context for the AI to analyze. The `Attachment` record takes three parameters:

| Parameter | Type | Description |
|-----------|------|-------------|
| `type` | String | The attachment type — use `"file"` for filesystem files |
| `path` | String | The absolute path to the file on disk |
| `displayName` | String | A human-readable label shown to the AI (e.g., the filename or a description) |

```java
session.send(new MessageOptions()
    .setPrompt("Review this file for bugs")
    .setAttachments(List.of(
        new Attachment("file", "/path/to/file.java", "MyService.java")
    ))
).get();
```

You can attach multiple files in a single message:

```java
session.send(new MessageOptions()
    .setPrompt("Compare these two implementations")
    .setAttachments(List.of(
        new Attachment("file", "/src/main/OldImpl.java", "Old Implementation"),
        new Attachment("file", "/src/main/NewImpl.java", "New Implementation")
    ))
).get();
```

---

## Bring Your Own Key (BYOK)

Use your own OpenAI or Azure OpenAI API key instead of GitHub Copilot.

Supported providers:

| Provider | Type value | Notes |
|----------|-----------|-------|
| OpenAI | `"openai"` | Standard OpenAI API |
| Azure OpenAI / Azure AI Foundry | `"azure"` | Azure-hosted models |
| Anthropic | `"anthropic"` | Claude models |
| Ollama | `"openai"` | Local models via OpenAI-compatible API |
| Microsoft Foundry Local | `"openai"` | Run AI models locally on your device via OpenAI-compatible API |
| Other OpenAI-compatible | `"openai"` | vLLM, LiteLLM, etc. |

### API Key Authentication

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setProvider(new ProviderConfig()
            .setType("openai")
            .setBaseUrl("https://api.openai.com/v1")
            .setApiKey("sk-..."))
).get();
```

### Bearer Token Authentication

Some providers require bearer token authentication instead of API keys:

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setProvider(new ProviderConfig()
            .setType("openai")
            .setBaseUrl("https://my-custom-endpoint.example.com/v1")
            .setBearerToken(System.getenv("MY_BEARER_TOKEN")))
).get();
```

> **Note:** The `bearerToken` option accepts a **static token string** only. The SDK does not refresh this token automatically. If your token expires, requests will fail and you'll need to create a new session with a fresh token.

### Microsoft Foundry Local

[Microsoft Foundry Local](https://foundrylocal.ai) lets you run AI models locally on your own device with an OpenAI-compatible API. Install it via the Foundry Local CLI, then point the SDK at your local endpoint:

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setProvider(new ProviderConfig()
            .setType("openai")
            .setBaseUrl("http://localhost:<PORT>/v1"))
            // No apiKey needed for local Foundry Local
).get();
```

> **Note:** Foundry Local starts on a **dynamic port** — the port is not fixed. Use `foundry service status` to confirm the port the service is currently listening on, then use that port in your `baseUrl`.

To get started with Foundry Local:

```bash
# Windows: Install Foundry Local CLI (requires winget)
winget install Microsoft.FoundryLocal

# macOS / Linux: see https://foundrylocal.ai for installation instructions

# List available models
foundry model list

# Run a model (starts the local server automatically)
foundry model run phi-4-mini

# Check the port the service is running on
foundry service status
```

### Limitations

When using BYOK, be aware of these limitations:

#### Identity Limitations

BYOK authentication uses **static credentials only**. The following identity providers are NOT supported:

- ❌ **Microsoft Entra ID (Azure AD)** - No support for Entra managed identities or service principals
- ❌ **Third-party identity providers** - No OIDC, SAML, or other federated identity
- ❌ **Managed identities** - Azure Managed Identity is not supported

You must use an API key or static bearer token that you manage yourself.

**Why not Entra ID?** While Entra ID does issue bearer tokens, these tokens are short-lived (typically 1 hour) and require automatic refresh via the Azure Identity SDK. The `bearerToken` option only accepts a static string—there is no callback mechanism for the SDK to request fresh tokens. For long-running workloads requiring Entra authentication, you would need to implement your own token refresh logic and create new sessions with updated tokens.

---

## Infinite Sessions

Run long conversations without hitting context limits.

When enabled (default), the session automatically compacts older messages as the context window fills up.

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setInfiniteSessions(new InfiniteSessionConfig()
            .setEnabled(true)
            .setBackgroundCompactionThreshold(0.80)  // Start compacting at 80%
            .setBufferExhaustionThreshold(0.95))     // Block at 95%
).get();

// Access the workspace where session state is persisted
var workspace = session.getWorkspacePath();
```

### Compaction Events

When compaction occurs, the session emits events that you can listen for:

```java
session.on(SessionCompactionStartEvent.class, start -> {
    System.out.println("Compaction started");
});
session.on(SessionCompactionCompleteEvent.class, complete -> {
    var data = complete.getData();
    System.out.println("Compaction completed - success: " + data.isSuccess() 
        + ", tokens removed: " + data.getTokensRemoved());
});
```

For short conversations, disable to avoid overhead:

```java
new InfiniteSessionConfig().setEnabled(false)
```

---

## MCP Servers

Extend the AI with external tools via the Model Context Protocol.

```java
Map<String, Object> server = Map.of(
    "type", "local",
    "command", "npx",
    "args", List.of("-y", "@modelcontextprotocol/server-filesystem", "/tmp"),
    "tools", List.of("*")
);

var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setMcpServers(Map.of("filesystem", server))
).get();
```

📖 **[Full MCP documentation →](mcp.html)** for local/remote servers and all options.

---

## Custom Agents

Extend the base Copilot assistant with specialized agents that have their own tools, prompts, and behavior. Users can invoke agents using the `@agent-name` mention syntax in messages.

```java
var reviewer = new CustomAgentConfig()
    .setName("code-reviewer")
    .setDisplayName("Code Reviewer")
    .setDescription("Reviews code for best practices and security")
    .setPrompt("You are a code review expert. Focus on security, performance, and maintainability.")
    .setTools(List.of("read_file", "search_code"));

var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setCustomAgents(List.of(reviewer))
).get();

// The user can now mention @code-reviewer in messages
session.send("@code-reviewer Review src/Main.java").get();
```

### Configuration Options

| Option | Type | Description |
|--------|------|-------------|
| `name` | String | Unique identifier used for `@mentions` (alphanumeric and hyphens) |
| `displayName` | String | Human-readable name shown to users |
| `description` | String | Describes the agent's capabilities |
| `prompt` | String | System prompt that defines the agent's behavior |
| `tools` | List&lt;String&gt; | Tool names available to this agent |
| `mcpServers` | Map | MCP servers available to this agent |
| `infer` | Boolean | Whether the agent can be auto-selected based on context |

### Multiple Agents

Register multiple agents for different tasks:

```java
var agents = List.of(
    new CustomAgentConfig()
        .setName("reviewer")
        .setDescription("Code review")
        .setPrompt("You review code for issues."),
    new CustomAgentConfig()
        .setName("documenter")
        .setDescription("Documentation writer")
        .setPrompt("You write clear documentation.")
        .setInfer(true)  // Auto-select when appropriate
);

var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setCustomAgents(agents)
).get();
```

See [CustomAgentConfig](apidocs/com/github/copilot/sdk/json/CustomAgentConfig.html) Javadoc for full details.

---

## Skills Configuration

Load custom skills from directories to extend the AI's capabilities with domain-specific knowledge.

### Loading Skills

Skills are loaded from `SKILL.md` files in subdirectories of the specified skill directories:

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setSkillDirectories(List.of("/path/to/skills"))
).get();
```

Each skill subdirectory should contain a `SKILL.md` file with YAML frontmatter:

```markdown
---
name: my-skill
description: A skill that provides domain-specific knowledge
---

# Skill Instructions

Your skill instructions go here...
```

### Disabling Skills

Disable specific skills by name:

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setSkillDirectories(List.of("/path/to/skills"))
        .setDisabledSkills(List.of("my-skill"))
).get();
```

---

## Custom Configuration Directory

Use a custom configuration directory for session settings:

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setConfigDir("/path/to/custom/config")
).get();
```

This is useful when you need to isolate session configuration or use different settings for different environments.

---

## User Input Handling

Handle user input requests when the AI uses the `ask_user` tool to gather information from the user.

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setOnUserInputRequest((request, invocation) -> {
            System.out.println("Agent asks: " + request.getQuestion());
            
            // Check if choices are provided
            if (request.getChoices() != null && !request.getChoices().isEmpty()) {
                System.out.println("Options: " + request.getChoices());
                // Return one of the provided choices
                var selectedChoice = request.getChoices().get(0);
                return CompletableFuture.completedFuture(
                    new UserInputResponse()
                        .setAnswer(selectedChoice)
                        .setWasFreeform(false)
                );
            }
            
            // Freeform input
            var userAnswer = getUserInput(); // your input method
            return CompletableFuture.completedFuture(
                new UserInputResponse()
                    .setAnswer(userAnswer)
                    .setWasFreeform(true)
            );
        })
).get();
```

The `UserInputRequest` contains:
- `getQuestion()` - The question the AI is asking
- `getChoices()` - Optional list of choices for the user to select from

The `UserInputResponse` should include:
- `setAnswer(String)` - The user's answer
- `setWasFreeform(boolean)` - `true` if the answer was freeform text, `false` if it was from the provided choices

See [UserInputHandler](apidocs/com/github/copilot/sdk/json/UserInputHandler.html) Javadoc for more details.

---

## Permission Handling

Approve or deny permission requests from the AI.

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setOnPermissionRequest((request, invocation) -> {
            // Inspect request and approve/deny
            var result = new PermissionRequestResult();
            result.setKind("user-approved");
            return CompletableFuture.completedFuture(result);
        })
).get();
```

---

## Session Hooks

Intercept tool execution and session lifecycle events using hooks.

```java
var hooks = new SessionHooks()
    .setOnPreToolUse((input, invocation) -> {
        System.out.println("Tool: " + input.getToolName());
        return CompletableFuture.completedFuture(PreToolUseHookOutput.allow());
    })
    .setOnPostToolUse((input, invocation) -> {
        System.out.println("Result: " + input.getToolResult());
        return CompletableFuture.completedFuture(null);
    });

var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setHooks(hooks)
).get();
```

📖 **[Full Session Hooks documentation →](hooks.html)** for all 5 hook types, inputs/outputs, and examples.

---

## Manual Server Control

Control the CLI lifecycle yourself instead of auto-start.

```java
var client = new CopilotClient(
    new CopilotClientOptions().setAutoStart(false)
);

client.start().get();   // Start manually
// ... use client ...
client.stop().get();    // Stop manually
```

### Graceful Stop vs Force Stop

The SDK provides two shutdown methods:

| Method | Behavior |
|--------|----------|
| `stop()` | Gracefully closes all open sessions, then shuts down the connection |
| `forceStop()` | Immediately clears sessions and shuts down — no graceful session cleanup |

Use `stop()` for normal shutdown — it ensures each session is properly closed (flushing pending operations) before terminating the connection:

```java
// Graceful: closes all sessions, then disconnects
client.stop().get();
```

Use `forceStop()` when you need to terminate immediately, such as during error recovery or when the server is unresponsive:

```java
// Immediate: skips session cleanup, kills connection
client.forceStop().get();
```

> **Tip:** In `try-with-resources` blocks, `close()` delegates to `stop()`, so graceful session cleanup happens automatically.
> `close()` is blocking and waits up to `CopilotClient.AUTOCLOSEABLE_TIMEOUT_SECONDS` seconds for shutdown to complete.

---

## Session Context and Filtering

Track and filter sessions by their working directory context including the current directory, git repository, and branch information.

### Listing Sessions with Context

Session metadata may include context information for persisted sessions:

```java
var sessions = client.listSessions().get();
for (var session : sessions) {
    var context = session.getContext();
    if (context != null) {
        System.out.println("Session: " + session.getSessionId());
        System.out.println("  Working dir: " + context.getCwd());
        System.out.println("  Repository: " + context.getRepository());
        System.out.println("  Branch: " + context.getBranch());
        System.out.println("  Git root: " + context.getGitRoot());
    }
}
```

### Filtering Sessions by Context

Use `SessionListFilter` to filter sessions by context fields:

```java
// Find sessions for a specific repository
var filter = new SessionListFilter()
    .setRepository("owner/myproject")
    .setBranch("main");

var sessions = client.listSessions(filter).get();
```

Filter options:
- `setCwd(String)` - Filter by exact working directory match
- `setGitRoot(String)` - Filter by git repository root
- `setRepository(String)` - Filter by repository in "owner/repo" format
- `setBranch(String)` - Filter by git branch name

### Context Changed Events

Listen for changes to the working directory context:

```java
session.on(SessionContextChangedEvent.class, event -> {
    var newContext = event.getData();
    System.out.println("Context changed:");
    System.out.println("  New CWD: " + newContext.getCwd());
    System.out.println("  Repository: " + newContext.getRepository());
    System.out.println("  Branch: " + newContext.getBranch());
});
```

The `session.context_changed` event fires when the working directory context changes between conversation turns.

---

## Session Lifecycle Events

Subscribe to lifecycle events to be notified when sessions are created, deleted, updated, or change foreground/background state.

### Subscribing to All Lifecycle Events

```java
var subscription = client.onLifecycle(event -> {
    System.out.println("Session " + event.getSessionId() + ": " + event.getType());
    
    if (event.getMetadata() != null) {
        System.out.println("  Summary: " + event.getMetadata().getSummary());
    }
});

// Later, when done listening:
subscription.close();
```

### Subscribing to Specific Event Types

```java
import com.github.copilot.sdk.json.SessionLifecycleEventTypes;

// Listen only for session creation
var subscription = client.onLifecycle(
    SessionLifecycleEventTypes.CREATED,
    event -> System.out.println("New session: " + event.getSessionId())
);
```

Available event types:
- `SessionLifecycleEventTypes.CREATED` - Session was created
- `SessionLifecycleEventTypes.DELETED` - Session was deleted
- `SessionLifecycleEventTypes.UPDATED` - Session was updated
- `SessionLifecycleEventTypes.FOREGROUND` - Session moved to foreground (TUI+server mode)
- `SessionLifecycleEventTypes.BACKGROUND` - Session moved to background (TUI+server mode)

---

## Foreground Session Control (TUI+Server Mode)

When connecting to a server running in TUI+server mode (`--ui-server`), you can control which session is displayed in the TUI.

### Getting the Foreground Session

```java
var sessionId = client.getForegroundSessionId().get();
if (sessionId != null) {
    System.out.println("TUI is displaying session: " + sessionId);
}
```

### Setting the Foreground Session

```java
client.setForegroundSessionId("session-123").get();
```

---

## Error Handling

All SDK methods return `CompletableFuture`. Errors surface via `ExecutionException`:

```java
try {
    session.send(new MessageOptions().setPrompt("Hello")).get();
} catch (ExecutionException ex) {
    System.err.println("Error: " + ex.getCause().getMessage());
}
```

For reactive error handling, use `exceptionally()` or `handle()`:

```java
session.send(new MessageOptions().setPrompt("Hello"))
    .exceptionally(ex -> {
        System.err.println("Failed: " + ex.getMessage());
        return null;
    });
```

### Event Handler Exceptions

If an event handler registered via `session.on()` throws an exception, the SDK
catches it and logs it at `WARNING` level. By default, dispatch **stops** after
the first handler error (`PROPAGATE_AND_LOG_ERRORS` policy). You can opt in to
continue dispatching despite errors using `SUPPRESS_AND_LOG_ERRORS`:

```java
// With SUPPRESS_AND_LOG_ERRORS, second handler still runs
session.setEventErrorPolicy(EventErrorPolicy.SUPPRESS_AND_LOG_ERRORS);

session.on(AssistantMessageEvent.class, msg -> {
    throw new RuntimeException("bug in handler 1");
});

session.on(AssistantMessageEvent.class, msg -> {
    // This handler executes normally despite the exception above
    System.out.println(msg.getData().getContent());
});
```

Errors are **always logged** at `WARNING` level regardless of the policy or
whether a custom error handler is set.

### Custom Event Error Handler

Set a custom `EventErrorHandler` for additional handling beyond the default
logging — such as metrics, alerts, or integration with external
error-reporting systems:

```java
session.setEventErrorHandler((event, exception) -> {
    metrics.increment("handler.errors");
    logger.error("Handler failed on {}: {}",
        event.getType(), exception.getMessage());
});
```

The error handler receives both the event that was being dispatched and the
exception that was thrown. If the error handler itself throws, that exception
is caught and logged at `SEVERE`, and dispatch is stopped to prevent cascading
failures.

Pass `null` to use only the default logging behavior:

```java
session.setEventErrorHandler(null);
```

### Event Error Policy

By default, the SDK propagates errors and stops dispatch on the first handler
error (`EventErrorPolicy.PROPAGATE_AND_LOG_ERRORS`). You can opt in to
**suppress** errors so that all handlers execute despite errors:

```java
session.setEventErrorPolicy(EventErrorPolicy.SUPPRESS_AND_LOG_ERRORS);
```

The `EventErrorHandler` (if set) is always invoked regardless of the policy —
the policy only controls whether remaining handlers execute after the error
handler returns. Errors are always logged at `WARNING` level.

| Policy | Behavior |
|---|---|
| `PROPAGATE_AND_LOG_ERRORS` (default) | Log the error; dispatch halts after the first error |
| `SUPPRESS_AND_LOG_ERRORS` | Log the error; all remaining handlers execute |

You can combine both for full control:

```java
// Log errors via custom handler and suppress (continue dispatching)
session.setEventErrorPolicy(EventErrorPolicy.SUPPRESS_AND_LOG_ERRORS);
session.setEventErrorHandler((event, ex) ->
    logger.error("Handler failed, continuing: {}", ex.getMessage(), ex));
```

Or switch policies dynamically:

```java
// Start strict (propagate errors, stop dispatch)
session.setEventErrorPolicy(EventErrorPolicy.PROPAGATE_AND_LOG_ERRORS);

// Later, switch to lenient mode (suppress errors, continue)
session.setEventErrorPolicy(EventErrorPolicy.SUPPRESS_AND_LOG_ERRORS);
```

See [EventErrorPolicy](apidocs/com/github/copilot/sdk/EventErrorPolicy.html) and [EventErrorHandler](apidocs/com/github/copilot/sdk/EventErrorHandler.html) Javadoc for details.

---

## Next Steps

- 📖 **[Documentation](documentation.html)** - Core concepts, events, streaming, models, tool filtering, reasoning effort
- 📖 **[Session Hooks](hooks.html)** - All 5 hook types with inputs, outputs, and examples
- 📖 **[MCP Servers](mcp.html)** - Local and remote MCP server integration
- 📖 **[Setup & Deployment](setup.html)** - OAuth, backend services, scaling, configuration reference
- 📖 **[API Javadoc](apidocs/index.html)** - Complete API reference
