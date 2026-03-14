# Copilot SDK for Java

> ⚠️ **Disclaimer:** This is the official Java SDK for GitHub Copilot. This repository treats the official .NET and nodejs SDKs for GitHub Copilot as reference implementations. These SDKS are all officially supported as GitHub open source projects. The Java implementation follows the backward compatibility guarantees offered by the reference implementations. As such this implementation may introduce breaking changes, according to the policy declared by the reference implementations. Use at your own risk.

Welcome to the documentation for the **Copilot SDK for Java** — a Java SDK for programmatic control of GitHub Copilot CLI, enabling you to build AI-powered applications and agentic workflows.

## Getting Started

### Requirements

- Java 17 or later
- GitHub Copilot CLI 0.0.411-1 or later installed and in PATH (or provide custom `cliPath`)

### Installation

Add the dependency to your project:

> **Note:** If this is a `-SNAPSHOT` version, clone the project repository and run `./mvnw install` locally first so the artifact is available in your local Maven cache. Otherwise, the version is available on Maven Central.

**Maven:**

```xml
<dependency>
    <groupId>com.github</groupId>
    <artifactId>copilot-sdk-java</artifactId>
    <version>${project.version}</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'com.github:copilot-sdk-java:${project.version}'
```

### Quick Example

```java
import com.github.copilot.sdk.*;
import com.github.copilot.sdk.events.*;
import com.github.copilot.sdk.json.*;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) throws Exception {
        try (var client = new CopilotClient()) {
            client.start().get();
            
            var session = client.createSession(
                new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setModel("claude-sonnet-4.5")
            ).get();

            var done = new CompletableFuture<Void>();
            session.on(AssistantMessageEvent.class, msg -> {
                System.out.println(msg.getData().content());
            });
            session.on(SessionIdleEvent.class, idle -> done.complete(null));

            session.send(new MessageOptions().setPrompt("What is 2+2?")).get();
            done.get();
        }
    }
}
```

## Documentation

| Document | Description |
|----------|-------------|
| [Documentation](documentation.html) | Basic usage, streaming, handling responses, and session management |
| [Advanced Usage](advanced.html) | Tools, BYOK, MCP servers, infinite sessions, skills, and more |
| [MCP Servers](mcp.html) | Integrating Model Context Protocol servers |
| [Javadoc](apidocs/index.html) | Generated API documentation |

## Try it with JBang

You can quickly try the SDK without setting up a full project using [JBang](https://www.jbang.dev/):

```bash
# Install JBang (if not already installed)
# macOS: brew install jbang
# Linux/Windows: curl -Ls https://sh.jbang.dev | bash -s - app setup

# Create a simple script
cat > hello-copilot.java << 'EOF'
//DEPS com.github:copilot-sdk-java:${project.version}
import com.github.copilot.sdk.*;
import com.github.copilot.sdk.events.*;
import com.github.copilot.sdk.json.*;
import java.util.concurrent.CompletableFuture;

class hello {
    public static void main(String[] args) throws Exception {
        try (var client = new CopilotClient()) {
            client.start().get();
            var session = client.createSession(new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)).get();
            var done = new CompletableFuture<Void>();
            session.on(AssistantMessageEvent.class, msg -> {
                System.out.print(msg.getData().content());
            });
            session.on(SessionIdleEvent.class, idle -> done.complete(null));
            session.send(new MessageOptions().setPrompt("Say hello!")).get();
            done.get();
        }
    }
}
EOF

# Run it
jbang hello-copilot.java
```

## Source Code

- [GitHub Repository](https://github.com/github/copilot-sdk-java)
- [Issue Tracker](https://github.com/github/copilot-sdk-java/issues)
- [Contributing Guide](https://github.com/github/copilot-sdk-java/blob/main/CONTRIBUTING.md)
