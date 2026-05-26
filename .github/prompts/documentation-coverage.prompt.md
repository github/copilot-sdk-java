# Documentation Coverage Assessment

You are an expert Java developer tasked with assessing whether the documentation in `src/site/markdown/` adequately covers the functionality implemented in the Java SDK.

## Objective

Analyze the Java SDK source code and compare it against the existing documentation to:
1. **Identify undocumented features** - Functionality in code that lacks documentation
2. **Find outdated documentation** - Docs that don't match current implementation
3. **Assess documentation quality** - Are documented features explained with examples?

## Assessment Process

### Step 1: Inventory Public API

Extract all public classes, methods, and features from the SDK:

```bash
# List all public classes in core package
grep -l "public class\|public interface\|public enum" src/main/java/com/github/copilot/*.java

# List all public classes in json package (DTOs)
grep -l "public class" src/main/java/com/github/copilot/rpc/*.java

# List all event types
ls src/main/java/com/github/copilot/events/
```

### Step 2: Inventory Documentation

List all documentation files:

```bash
ls src/site/markdown/
```

Read each documentation file to understand current coverage.

### Step 3: Map Features to Documentation

For each major feature area, determine if it's documented:

#### CopilotClient Features

Examine `CopilotClient.java` for public methods:

```bash
grep "public.*(" src/main/java/com/github/copilot/CopilotClient.java | grep -v "@"
```

| Method | Purpose | Documented In | Status |
|--------|---------|---------------|--------|
| `start()` | Start the client | ? | ✅/❌ |
| `stop()` | Stop the client | ? | ✅/❌ |
| `createSession()` | Create a new session | ? | ✅/❌ |
| `resumeSession()` | Resume existing session | ? | ✅/❌ |
| `deleteSession()` | Delete a session | ? | ✅/❌ |
| `listSessions()` | List all sessions | ? | ✅/❌ |
| `listModels()` | List available models | ? | ✅/❌ |
| `getStatus()` | Get client status | ? | ✅/❌ |
| `getAuthStatus()` | Get auth status | ? | ✅/❌ |
| `ping()` | Ping the server | ? | ✅/❌ |

#### CopilotSession Features

Examine `CopilotSession.java` for public methods:

```bash
grep "public.*(" src/main/java/com/github/copilot/CopilotSession.java | grep -v "@"
```

| Method | Purpose | Documented In | Status |
|--------|---------|---------------|--------|
| `send()` | Send a message | ? | ✅/❌ |
| `sendAndWait()` | Send and wait for response | ? | ✅/❌ |
| `on()` | Subscribe to events | ? | ✅/❌ |
| `registerTools()` | Register custom tools | ? | ✅/❌ |
| `getMessages()` | Get conversation history | ? | ✅/❌ |
| `abort()` | Abort current operation | ? | ✅/❌ |

#### SessionConfig Options

Examine `SessionConfig.java` for configurable options:

```bash
grep "public.*set\|private.*;" src/main/java/com/github/copilot/rpc/SessionConfig.java
```

| Option | Purpose | Documented | Example Provided |
|--------|---------|:----------:|:----------------:|
| `model` | Model to use | ✅/❌ | ✅/❌ |
| `tools` | Custom tools | ✅/❌ | ✅/❌ |
| `systemMessage` | System prompt | ✅/❌ | ✅/❌ |
| `streaming` | Enable streaming | ✅/❌ | ✅/❌ |
| `mcpServers` | MCP integration | ✅/❌ | ✅/❌ |
| `hooks` | Session hooks | ✅/❌ | ✅/❌ |
| `infiniteSessions` | Long sessions | ✅/❌ | ✅/❌ |
| `skillDirectories` | Skills config | ✅/❌ | ✅/❌ |
| `customAgents` | Custom agents | ✅/❌ | ✅/❌ |

#### Event Types

Check which events are documented:

```bash
grep "TYPE_MAP.put" src/main/java/com/github/copilot/events/SessionEventParser.java
```

| Event Type | Event Class | Documented | Example |
|------------|-------------|:----------:|:-------:|
| `session.start` | `SessionStartEvent` | ✅/❌ | ✅/❌ |
| `session.idle` | `SessionIdleEvent` | ✅/❌ | ✅/❌ |
| `assistant.message` | `AssistantMessageEvent` | ✅/❌ | ✅/❌ |
| ... | ... | ... | ... |

#### Hooks

Check `SessionHooks.java` for hook types:

```bash
grep "private.*Handler" src/main/java/com/github/copilot/rpc/SessionHooks.java
```

| Hook | Handler Interface | Documented | Example |
|------|-------------------|:----------:|:-------:|
| Pre-tool use | `PreToolUseHandler` | ✅/❌ | ✅/❌ |
| Post-tool use | `PostToolUseHandler` | ✅/❌ | ✅/❌ |
| User prompt submitted | `UserPromptSubmittedHandler` | ✅/❌ | ✅/❌ |
| Session lifecycle | `SessionLifecycleHandler` | ✅/❌ | ✅/❌ |

### Step 4: Check Documentation Quality

For each documented feature, verify:

1. **Accurate description** - Does it match current implementation?
2. **Code example** - Is there a working code snippet?
3. **Complete coverage** - Are all parameters/options explained?
4. **Up to date** - Does it reflect the current API?

## Report Format

### Documentation Coverage Summary

| Category | Total Features | Documented | Coverage |
|----------|----------------|------------|----------|
| Client Methods | X | X | X% |
| Session Methods | X | X | X% |
| Config Options | X | X | X% |
| Events | X | X | X% |
| Hooks | X | X | X% |
| **Overall** | **X** | **X** | **X%** |

### Undocumented Features

Features that exist in code but have no documentation:

| Feature | Location | Priority | Notes |
|---------|----------|----------|-------|
| Feature name | Class/method | High/Medium/Low | Why it matters |

### Documentation Gaps

Existing docs that need enhancement:

| Document | Gap | Recommendation |
|----------|-----|----------------|
| `getting-started.md` | Missing X | Add section on X |
| `advanced.md` | Outdated API | Update to match code |

### Missing Documentation Files

Topics that warrant dedicated documentation:

| Topic | Why Needed | Suggested File |
|-------|------------|----------------|
| Hooks | Complex feature | `hooks.md` |
| Error Handling | Common need | `error-handling.md` |

### Recommendations

#### High Priority
1. Document feature X - used frequently, no docs
2. Update docs for Y - API changed

#### Medium Priority
1. Add examples for Z
2. Improve explanation of W

#### Nice to Have
1. Add troubleshooting guide
2. Add FAQ section

## Key Files

### Source Code
- `src/main/java/com/github/copilot/CopilotClient.java`
- `src/main/java/com/github/copilot/CopilotSession.java`
- `src/main/java/com/github/copilot/rpc/SessionConfig.java`
- `src/main/java/com/github/copilot/rpc/SessionHooks.java`
- `src/main/java/com/github/copilot/events/SessionEventParser.java`

### Documentation
- `src/site/markdown/index.md` - Landing page
- `src/site/markdown/getting-started.md` - Quick start guide
- `src/site/markdown/documentation.md` - API reference
- `src/site/markdown/advanced.md` - Advanced topics
- `src/site/markdown/mcp.md` - MCP integration
