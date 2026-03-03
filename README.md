# Copilot SDK for Java

[![Build](https://github.com/copilot-community-sdk/copilot-sdk-java/actions/workflows/build-test.yml/badge.svg)](https://github.com/copilot-community-sdk/copilot-sdk-java/actions/workflows/build-test.yml)
[![Site](https://github.com/copilot-community-sdk/copilot-sdk-java/actions/workflows/deploy-site.yml/badge.svg)](https://github.com/copilot-community-sdk/copilot-sdk-java/actions/workflows/deploy-site.yml)
[![Coverage](.github/badges/jacoco.svg)](https://copilot-community-sdk.github.io/copilot-sdk-java/snapshot/jacoco/index.html)
[![Documentation](https://img.shields.io/badge/docs-online-brightgreen)](https://copilot-community-sdk.github.io/copilot-sdk-java/)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

#### Latest release
[![GitHub Release Date](https://img.shields.io/github/release-date/copilot-community-sdk/copilot-sdk-java)](https://github.com/copilot-community-sdk/copilot-sdk-java/releases)
[![GitHub Release](https://img.shields.io/github/v/release/copilot-community-sdk/copilot-sdk-java)](https://github.com/copilot-community-sdk/copilot-sdk-java/releases)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.copilot-community-sdk/copilot-sdk)](https://central.sonatype.com/artifact/io.github.copilot-community-sdk/copilot-sdk)
[![Documentation](https://img.shields.io/badge/docs-latest-brightgreen)](https://copilot-community-sdk.github.io/copilot-sdk-java/latest/)
[![Javadoc](https://javadoc.io/badge2/io.github.copilot-community-sdk/copilot-sdk/javadoc.svg?q=1)](https://javadoc.io/doc/io.github.copilot-community-sdk/copilot-sdk/latest/index.html)

## Background

> ⚠️ **Disclaimer:** This is an **unofficial, community-driven SDK** and is **not supported or endorsed by GitHub**. This SDK may change in breaking ways. Use at your own risk.

Java SDK for programmatic control of GitHub Copilot CLI, enabling you to build AI-powered applications and agentic workflows.

## Installation

### Requirements

- Java 17 or later
- GitHub Copilot CLI 0.0.411-1 or later installed and in PATH (or provide custom `cliPath`)

### Maven

```xml
<dependency>
    <groupId>io.github.copilot-community-sdk</groupId>
    <artifactId>copilot-sdk</artifactId>
    <version>1.0.9</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.copilot-community-sdk:copilot-sdk:1.0.9'
```

## Quick Start

```java
import com.github.copilot.sdk.*;
import com.github.copilot.sdk.events.*;
import com.github.copilot.sdk.json.*;
import java.util.concurrent.CompletableFuture;

public class CopilotSDK {
    public static void main(String[] args) throws Exception {
        // Create and start client
        try (var client = new CopilotClient()) {
            client.start().get();

            // Create a session
            var session = client.createSession(
                new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setModel("claude-sonnet-4.5")).get();

            // Handle assistant message events
            session.on(AssistantMessageEvent.class, msg -> {
                System.out.println(msg.getData().getContent());
            });

            // Handle session usage info events
            session.on(SessionUsageInfoEvent.class, usage -> {
                var data = usage.getData();
                System.out.println("\n--- Usage Metrics ---");
                System.out.println("Current tokens: " + (int) data.getCurrentTokens());
                System.out.println("Token limit: " + (int) data.getTokenLimit());
                System.out.println("Messages count: " + (int) data.getMessagesLength());
            });

            // Send a message
            var completable = session.sendAndWait(new MessageOptions().setPrompt("What is 2+2?"));
            // and wait for completion
            completable.get();
        }
    }
}
```

## Try it with JBang

You can run the SDK without setting up a full Java project, by using [JBang](https://www.jbang.dev/).

See the full source of [`jbang-example.java`](jbang-example.java) for a complete example with more features like session idle handling and usage info events.

Or run it directly from the repository:

```bash
jbang https://github.com/copilot-community-sdk/copilot-sdk-java/blob/latest/jbang-example.java
```

## Documentation

📚 **[Full Documentation](https://copilot-community-sdk.github.io/copilot-sdk-java/)** — Complete API reference, advanced usage examples, and guides.

### Quick Links

- [Getting Started](https://copilot-community-sdk.github.io/copilot-sdk-java/documentation.html)
- [Javadoc API Reference](https://copilot-community-sdk.github.io/copilot-sdk-java/apidocs/)
- [MCP Servers Integration](https://copilot-community-sdk.github.io/copilot-sdk-java/mcp.html)
- [Cookbook](src/site/markdown/cookbook/) — Practical recipes for common use cases

## Projects Using This SDK

| Project | Description |
|---------|-------------|
| [JMeter Copilot Plugin](https://github.com/brunoborges/jmeter-copilot-plugin) | JMeter plugin for AI-assisted load testing |

> Want to add your project? Open a PR!

## CI/CD Workflows

This project uses several GitHub Actions workflows for building, testing, releasing, and syncing with the upstream SDK. 

See [WORKFLOWS.md](docs/WORKFLOWS.md) for a full overview and details on each workflow.

## Contributing

Contributions are welcome! Please see the [Contributing Guide](CONTRIBUTING.md) for details.

### Agentic Upstream Merge and Sync

This SDK tracks the official [Copilot SDK](https://github.com/github/copilot-sdk) (.NET reference implementation) and ports changes to Java. The upstream merge process is automated with AI assistance:

**Weekly automated sync** — A [scheduled GitHub Actions workflow](.github/workflows/weekly-upstream-sync.yml) runs every Monday at 5 AM ET. It checks for new upstream commits since the last merge (tracked in [`.lastmerge`](.lastmerge)), and if changes are found, creates an issue labeled `upstream-sync` and assigns it to the GitHub Copilot coding agent. Any previously open `upstream-sync` issues are automatically closed.

**Reusable prompt** — The merge workflow is defined in [`agentic-merge-upstream.prompt.md`](.github/prompts/agentic-merge-upstream.prompt.md). It can be triggered manually from:
- **VS Code Copilot Chat** — type `/agentic-merge-upstream`
- **GitHub Copilot CLI** — use `copilot` CLI with the same skill reference

### Development Setup

```bash
# Clone the repository
git clone https://github.com/copilot-community-sdk/copilot-sdk-java.git
cd copilot-sdk-java

# Enable git hooks for code formatting
git config core.hooksPath .githooks

# Build and test
mvn clean verify
```

The tests require the official [copilot-sdk](https://github.com/github/copilot-sdk) test harness, which is automatically cloned during build.

## Support

See [SUPPORT.md](SUPPORT.md) for how to file issues and get help.

## Code of Conduct

This project has adopted the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). See [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for details.

## Security

See [SECURITY.md](SECURITY.md) for reporting security vulnerabilities.

## License

MIT — see [LICENSE](LICENSE) for details.

## Acknowledgement

- Initially developed with Copilot and [Bruno Borges](https://www.linkedin.com/in/brunocborges/).

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=copilot-community-sdk/copilot-sdk-java&type=Date)](https://www.star-history.com/#copilot-community-sdk/copilot-sdk-java&Date)

⭐ Drop a star if you find this useful!
