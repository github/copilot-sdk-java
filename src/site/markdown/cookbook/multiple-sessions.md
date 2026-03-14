# Working with Multiple Sessions

Manage multiple independent conversations simultaneously.

## Prerequisites

Install [JBang](https://www.jbang.dev/) to run these examples:

```bash
# macOS (using Homebrew)
brew install jbangdev/tap/jbang

# Linux/macOS (using curl)
curl -Ls https://sh.jbang.dev | bash -s - app setup

# Windows (using Scoop)
scoop install jbang
```

## Example scenario

You need to run multiple conversations in parallel, each with its own context and history.

## Java

**Usage:**
```bash
jbang MultipleSessions.java
```

**Code:**
```java
//DEPS com.github:copilot-sdk-java:${project.version}
import com.github.copilot.sdk.*;
import com.github.copilot.sdk.events.*;
import com.github.copilot.sdk.json.*;

public class MultipleSessions {
    public static void main(String[] args) throws Exception {
        try (var client = new CopilotClient()) {
            client.start().get();

            // Create multiple independent sessions
            var session1 = client.createSession(
                new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setModel("gpt-5")).get();
            var session2 = client.createSession(
                new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setModel("gpt-5")).get();
            var session3 = client.createSession(
                new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setModel("claude-sonnet-4.5")).get();

            // Set up event handlers for each session
            session1.on(AssistantMessageEvent.class, msg -> 
                System.out.println("Session 1: " + msg.getData().content()));
            session2.on(AssistantMessageEvent.class, msg -> 
                System.out.println("Session 2: " + msg.getData().content()));
            session3.on(AssistantMessageEvent.class, msg -> 
                System.out.println("Session 3: " + msg.getData().content()));

            // Each session maintains its own conversation history
            session1.send(new MessageOptions()
                .setPrompt("You are helping with a Python project"));
            session2.send(new MessageOptions()
                .setPrompt("You are helping with a TypeScript project"));
            session3.send(new MessageOptions()
                .setPrompt("You are helping with a Go project"));

            // Follow-up messages stay in their respective contexts
            session1.send(new MessageOptions()
                .setPrompt("How do I create a virtual environment?"));
            session2.send(new MessageOptions()
                .setPrompt("How do I set up tsconfig?"));
            session3.send(new MessageOptions()
                .setPrompt("How do I initialize a module?"));

            // Wait for all sessions to complete
            Thread.sleep(5000);

            // Clean up
            session1.close();
            session2.close();
            session3.close();
        }
    }
}
```

## Custom session IDs

Use custom IDs for easier tracking:

```java
var session = client.createSession(
    new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
        .setSessionId("user-123-chat")
        .setModel("gpt-5")
).get();

System.out.println(session.getSessionId()); // "user-123-chat"
```

## Listing sessions

```java
var sessions = client.listSessions().get();
for (var sessionInfo : sessions) {
    System.out.println("Session: " + sessionInfo.getSessionId());
}
```

## Deleting sessions

```java
// Delete a specific session
try {
    client.deleteSession("user-123-chat").get();
} catch (Exception ex) {
    System.err.println("Failed to delete session: " + ex.getMessage());
}
```

## Managing session lifecycle with CompletableFuture

```java
//DEPS com.github:copilot-sdk-java:${project.version}
import java.util.concurrent.CompletableFuture;
import java.util.List;

public class ParallelSessions {
    public static void runParallelSessions(CopilotClient client) throws Exception {
        // Create sessions in parallel
        var sessionFutures = List.of(
            client.createSession(new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setModel("gpt-5")),
            client.createSession(new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setModel("gpt-5")),
            client.createSession(new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setModel("claude-sonnet-4.5"))
        );

        // Wait for all sessions to be created
        CompletableFuture.allOf(sessionFutures.toArray(new CompletableFuture[0]))
            .get();

        // Get the created sessions
        var session1 = sessionFutures.get(0).get();
        var session2 = sessionFutures.get(1).get();
        var session3 = sessionFutures.get(2).get();

        // Send messages in parallel
        var messageFutures = List.of(
            session1.sendAndWait(new MessageOptions().setPrompt("Question 1")),
            session2.sendAndWait(new MessageOptions().setPrompt("Question 2")),
            session3.sendAndWait(new MessageOptions().setPrompt("Question 3"))
        );

        // Wait for all responses
        CompletableFuture.allOf(messageFutures.toArray(new CompletableFuture[0]))
            .get();

        // Clean up
        session1.close();
        session2.close();
        session3.close();
    }
}
```

## Use cases

- **Multi-user applications**: One session per user
- **Multi-task workflows**: Separate sessions for different tasks
- **A/B testing**: Compare responses from different models
- **Parallel processing**: Process multiple requests simultaneously
