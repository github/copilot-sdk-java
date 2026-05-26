# Test Coverage Assessment

You are an expert Java developer tasked with analyzing test coverage for this Java SDK. Your goal is to produce a comprehensive report of what is tested and what gaps exist.

## Objective

Analyze the test coverage of the SDK by examining:
1. **Event types** - All session events defined in `SessionEventParser`
2. **Hook types** - All hooks defined in `SessionHooks`
3. **Core functionality** - Session management, tools, permissions, etc.

## Assessment Process

### Step 1: Identify All Testable Components

First, examine the source code to identify all components that should be tested:

```bash
# List all event classes
ls src/main/java/com/github/copilot/events/

# Check the event type mapping in SessionEventParser
grep -n "TYPE_MAP.put" src/main/java/com/github/copilot/events/SessionEventParser.java
```

Extract the list of all registered event types from `SessionEventParser.java`.

### Step 2: Identify All Hook Types

Check `SessionHooks.java` for all available hook handlers:

```bash
grep -E "private.*Handler" src/main/java/com/github/copilot/rpc/SessionHooks.java
```

### Step 3: Analyze Existing Tests

Examine the test files to understand current coverage:

```bash
# List all test files
ls src/test/java/com/github/copilot/

# Check for event-related tests
grep -r "import.*events\." src/test/java/com/github/copilot/ | grep -v "\.class"

# Check for hook tests
grep -l "SessionHooks\|Hook" src/test/java/com/github/copilot/*.java
```

### Step 4: Categorize Test Coverage

For each component, determine:
- **Unit Test Coverage**: Tests that verify JSON parsing/serialization without E2E flow
- **E2E Test Coverage**: Tests that verify the component works in a real session flow

## Report Format

Generate a comprehensive report in the following format:

### Event Types Coverage

| Event Type | Event Class | Unit Test | E2E Test | Notes |
|------------|-------------|:---------:|:--------:|-------|
| `session.start` | `SessionStartEvent` | ✅/❌ | ✅/❌ | Any notes |
| ... | ... | ... | ... | ... |

### Hook Types Coverage

| Hook Type | Handler Interface | Unit Test | E2E Test | Notes |
|-----------|-------------------|:---------:|:--------:|-------|
| `preToolUse` | `PreToolUseHandler` | ✅/❌ | ✅/❌ | Any notes |
| ... | ... | ... | ... | ... |

### Coverage Summary

| Category | Total | Unit Tested | E2E Tested | Coverage % |
|----------|-------|-------------|------------|------------|
| Events | X | X | X | X% |
| Hooks | X | X | X | X% |

### Gaps Identified

List components that lack tests:
1. **Missing Unit Tests**: Components without JSON parsing tests
2. **Missing E2E Tests**: Components not exercised in integration tests
3. **Partially Tested**: Components with incomplete test coverage

### Recommendations

Prioritized list of tests to add:
1. High priority: Critical paths without coverage
2. Medium priority: Common use cases without coverage
3. Low priority: Edge cases and rare events

## Key Files to Examine

- `src/main/java/com/github/copilot/events/SessionEventParser.java` - Event type registry
- `src/main/java/com/github/copilot/rpc/SessionHooks.java` - Hook definitions
- `src/main/java/com/github/copilot/CopilotSession.java` - Hook handling logic
- `src/test/java/com/github/copilot/SessionEventParserTest.java` - Event parsing tests
- `src/test/java/com/github/copilot/SessionEventsE2ETest.java` - Event E2E tests
- `src/test/java/com/github/copilot/HooksTest.java` - Hook tests
- `src/test/java/com/github/copilot/SessionEventHandlingTest.java` - Event handling tests

## Verification

After producing the report, verify by running:

```bash
# Count total tests
mvn test 2>&1 | grep "Tests run:"

# Run specific test categories
mvn test -Dtest=SessionEventParserTest
mvn test -Dtest=SessionEventsE2ETest
mvn test -Dtest=HooksTest
```

## Output

Provide:
1. The complete coverage report in markdown table format
2. A prioritized list of recommended improvements
3. Optionally: Skeleton test code for missing high-priority tests
