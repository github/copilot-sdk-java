#!/usr/bin/env python3
"""Refactor Java source files to use Optional return types instead of nullable boxed primitives."""

import re
import os

BASE = "/home/runner/work/copilot-sdk-java/copilot-sdk-java/src/main/java/com/github/copilot/sdk"
JSON_PKG = os.path.join(BASE, "json")


def read_file(path):
    with open(path, 'r') as f:
        return f.read()


def write_file(path, content):
    with open(path, 'w') as f:
        f.write(content)


def add_import(content, import_stmt):
    """Add an import statement if not already present."""
    if import_stmt in content:
        return content
    # Find the last import line and add after it
    lines = content.split('\n')
    last_import_idx = -1
    for i, line in enumerate(lines):
        if line.startswith('import '):
            last_import_idx = i
    if last_import_idx >= 0:
        lines.insert(last_import_idx + 1, import_stmt)
    return '\n'.join(lines)


def refactor_fluent_boolean_getter(content, field_name):
    """Refactor a Boolean getter to return Optional<Boolean> (fluent class)."""
    # Match the getter method - various javadoc styles
    getter_name = f'get{field_name[0].upper()}{field_name[1:]}'
    
    # Replace return type and body
    # Pattern: public Boolean getXxx() {\n        return xxx;\n    }
    pattern = rf'(    public )Boolean ({getter_name}\(\)) \{{\n(\s+)return {field_name};\n(\s+)\}}'
    replacement = rf'\1Optional<Boolean> \2 {{\n\3return Optional.ofNullable({field_name});\n\4}}'
    content = re.sub(pattern, replacement, content)
    return content


def refactor_fluent_boolean_setter(content, field_name, class_name):
    """Refactor a Boolean setter to take primitive boolean (fluent class)."""
    setter_name = f'set{field_name[0].upper()}{field_name[1:]}'
    
    # Pattern: public ClassName setXxx(Boolean xxx) {\n        this.xxx = xxx;\n        return this;\n    }
    pattern = rf'(    public {class_name} {setter_name}\()Boolean ({field_name})\)( \{{)'
    replacement = rf'\1boolean \2)\3'
    content = re.sub(pattern, replacement, content)
    return content


def refactor_void_boolean_getter(content, field_name):
    """Refactor a Boolean getter to return Optional<Boolean> (void setter class)."""
    getter_name = f'get{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(    public )Boolean ({getter_name}\(\)) \{{\n(\s+)return {field_name};\n(\s+)\}}'
    replacement = rf'\1Optional<Boolean> \2 {{\n\3return Optional.ofNullable({field_name});\n\4}}'
    content = re.sub(pattern, replacement, content)
    return content


def refactor_void_boolean_setter(content, field_name):
    """Refactor a void Boolean setter to take primitive boolean."""
    setter_name = f'set{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(    public void {setter_name}\()Boolean ({field_name})\)'
    replacement = rf'\1boolean \2)'
    content = re.sub(pattern, replacement, content)
    return content


def add_fluent_clear_method(content, field_name, class_name, after_setter=True):
    """Add a clearXxx() method after the setter for fluent classes."""
    setter_name = f'set{field_name[0].upper()}{field_name[1:]}'
    clear_name = f'clear{field_name[0].upper()}{field_name[1:]}'
    
    # Check if clear method already exists
    if f'{clear_name}()' in content:
        return content
    
    clear_method = f'''
    /**
     * Clears the {field_name} setting, reverting to the default behavior.
     *
     * @return this instance for method chaining
     */
    public {class_name} {clear_name}() {{
        this.{field_name} = null;
        return this;
    }}
'''
    
    # Find the end of the setter method and insert after it
    # Look for the setter method's closing brace
    setter_pattern = rf'(    public {class_name} {setter_name}\([^)]+\) \{{[^}}]*\}})'
    match = re.search(setter_pattern, content, re.DOTALL)
    if match:
        insert_pos = match.end()
        content = content[:insert_pos] + '\n' + clear_method + content[insert_pos:]
    
    return content


def add_void_clear_method(content, field_name, after_setter=True):
    """Add a void clearXxx() method after the setter for request classes."""
    setter_name = f'set{field_name[0].upper()}{field_name[1:]}'
    clear_name = f'clear{field_name[0].upper()}{field_name[1:]}'
    
    if f'{clear_name}()' in content:
        return content
    
    clear_method = f'''
    /**
     * Clears the {field_name} setting, reverting to the default behavior.
     */
    public void {clear_name}() {{
        this.{field_name} = null;
    }}
'''
    
    # Find the end of the setter method
    setter_pattern = rf'(    public void {setter_name}\([^)]+\) \{{[^}}]*\}})'
    match = re.search(setter_pattern, content, re.DOTALL)
    if match:
        insert_pos = match.end()
        content = content[:insert_pos] + '\n' + clear_method + content[insert_pos:]
    
    return content


def refactor_fluent_int_getter(content, field_name):
    """Refactor an Integer getter to return OptionalInt."""
    getter_name = f'get{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(    public )Integer ({getter_name}\(\)) \{{\n(\s+)return {field_name};\n(\s+)\}}'
    replacement = rf'\1OptionalInt \2 {{\n\3return {field_name} == null ? OptionalInt.empty() : OptionalInt.of({field_name});\n\4}}'
    content = re.sub(pattern, replacement, content)
    return content


def refactor_fluent_int_setter(content, field_name, class_name):
    """Refactor an Integer setter to take primitive int (fluent)."""
    setter_name = f'set{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(    public {class_name} {setter_name}\()Integer ({field_name})\)( \{{)'
    replacement = rf'\1int \2)\3'
    content = re.sub(pattern, replacement, content)
    return content


def refactor_fluent_double_getter(content, field_name):
    """Refactor a Double getter to return OptionalDouble."""
    getter_name = f'get{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(    public )Double ({getter_name}\(\)) \{{\n(\s+)return {field_name};\n(\s+)\}}'
    replacement = rf'\1OptionalDouble \2 {{\n\3return {field_name} == null ? OptionalDouble.empty() : OptionalDouble.of({field_name});\n\4}}'
    content = re.sub(pattern, replacement, content)
    return content


def refactor_fluent_double_setter(content, field_name, class_name):
    """Refactor a Double setter to take primitive double (fluent)."""
    setter_name = f'set{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(    public {class_name} {setter_name}\()Double ({field_name})\)( \{{)'
    replacement = rf'\1double \2)\3'
    content = re.sub(pattern, replacement, content)
    return content


# Also handle inner class patterns (indented with 8 spaces)
def refactor_inner_boolean_getter(content, field_name):
    """Refactor a Boolean getter in an inner class."""
    getter_name = f'get{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(        public )Boolean ({getter_name}\(\)) \{{\n(\s+)return {field_name};\n(\s+)\}}'
    replacement = rf'\1Optional<Boolean> \2 {{\n\3return Optional.ofNullable({field_name});\n\4}}'
    content = re.sub(pattern, replacement, content)
    return content


def refactor_inner_boolean_setter(content, field_name, class_name):
    """Refactor a Boolean setter in an inner class."""
    setter_name = f'set{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(        public {class_name} {setter_name}\()Boolean ({field_name})\)( \{{)'
    replacement = rf'\1boolean \2)\3'
    content = re.sub(pattern, replacement, content)
    return content


def refactor_inner_int_getter(content, field_name):
    """Refactor an Integer getter in an inner class."""
    getter_name = f'get{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(        public )Integer ({getter_name}\(\)) \{{\n(\s+)return {field_name};\n(\s+)\}}'
    replacement = rf'\1OptionalInt \2 {{\n\3return {field_name} == null ? OptionalInt.empty() : OptionalInt.of({field_name});\n\4}}'
    content = re.sub(pattern, replacement, content)
    return content


def refactor_inner_int_setter(content, field_name, class_name):
    """Refactor an Integer setter in an inner class."""
    setter_name = f'set{field_name[0].upper()}{field_name[1:]}'
    
    pattern = rf'(        public {class_name} {setter_name}\()Integer ({field_name})\)( \{{)'
    replacement = rf'\1int \2)\3'
    content = re.sub(pattern, replacement, content)
    return content


def add_inner_fluent_clear(content, field_name, class_name):
    """Add clear method for inner class fields."""
    setter_name = f'set{field_name[0].upper()}{field_name[1:]}'
    clear_name = f'clear{field_name[0].upper()}{field_name[1:]}'
    
    if f'{clear_name}()' in content:
        return content
    
    clear_method = f'''
        /**
         * Clears the {field_name} setting, reverting to the default behavior.
         *
         * @return this instance for method chaining
         */
        public {class_name} {clear_name}() {{
            this.{field_name} = null;
            return this;
        }}
'''
    
    # Find the end of the setter in inner class (8 spaces indent)
    setter_pattern = rf'(        public {class_name} {setter_name}\([^)]+\) \{{[^}}]*\}})'
    match = re.search(setter_pattern, content, re.DOTALL)
    if match:
        insert_pos = match.end()
        content = content[:insert_pos] + '\n' + clear_method + content[insert_pos:]
    
    return content


# ==================== MAIN REFACTORING ====================

def refactor_copilot_client_options():
    path = os.path.join(JSON_PKG, "CopilotClientOptions.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    content = add_import(content, "import java.util.OptionalInt;")
    
    # sessionIdleTimeoutSeconds: Integer -> OptionalInt
    content = refactor_fluent_int_getter(content, "sessionIdleTimeoutSeconds")
    content = refactor_fluent_int_setter(content, "sessionIdleTimeoutSeconds", "CopilotClientOptions")
    content = add_fluent_clear_method(content, "sessionIdleTimeoutSeconds", "CopilotClientOptions")
    
    # useLoggedInUser: Boolean -> Optional<Boolean>
    content = refactor_fluent_boolean_getter(content, "useLoggedInUser")
    # Special case - the setter has custom logic, need to replace it differently
    # Old: public CopilotClientOptions setUseLoggedInUser(Boolean useLoggedInUser) {
    #          this.useLoggedInUser = useLoggedInUser != null ? useLoggedInUser : Boolean.FALSE;
    # New: public CopilotClientOptions setUseLoggedInUser(boolean useLoggedInUser) {
    #          this.useLoggedInUser = useLoggedInUser;
    old_setter = "public CopilotClientOptions setUseLoggedInUser(Boolean useLoggedInUser) {\n        this.useLoggedInUser = useLoggedInUser != null ? useLoggedInUser : Boolean.FALSE;"
    new_setter = "public CopilotClientOptions setUseLoggedInUser(boolean useLoggedInUser) {\n        this.useLoggedInUser = useLoggedInUser;"
    content = content.replace(old_setter, new_setter)
    content = add_fluent_clear_method(content, "useLoggedInUser", "CopilotClientOptions")
    
    # Update javadoc for getSessionIdleTimeoutSeconds
    content = content.replace(
        "@return the session idle timeout in seconds, or {@code null} to disable\n     *         (sessions live indefinitely)",
        "@return an {@link OptionalInt} containing the session idle timeout in seconds,\n     *         or empty to disable (sessions live indefinitely)"
    )
    # Update setter javadoc
    content = content.replace(
        "Sessions without activity for this duration are automatically cleaned up. Set\n     * to {@code 0} or leave as {@code null} to disable (sessions live\n     * indefinitely).",
        "Sessions without activity for this duration are automatically cleaned up. Set\n     * to {@code 0} to disable (sessions live indefinitely). Use\n     * {@link #clearSessionIdleTimeoutSeconds()} to revert to the default."
    )
    content = content.replace(
        "@param sessionIdleTimeoutSeconds\n     *            the idle timeout in seconds, or {@code null} to disable",
        "@param sessionIdleTimeoutSeconds\n     *            the idle timeout in seconds"
    )
    
    # Update useLoggedInUser getter javadoc
    content = content.replace(
        "@return {@code true} to use logged-in user auth, {@code false} to use only\n     *         explicit tokens, or {@code null} to use default behavior",
        "@return an {@link Optional} containing the boolean value, or empty if not set"
    )
    # Update useLoggedInUser setter javadoc
    content = content.replace(
        "Passing {@code null} is equivalent to passing {@link Boolean#FALSE}.\n     *\n     * @param useLoggedInUser\n     *            {@code true} to use logged-in user auth, {@code false} or\n     *            {@code null} otherwise",
        "@param useLoggedInUser\n     *            {@code true} to use logged-in user auth, {@code false} otherwise"
    )
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_session_config():
    path = os.path.join(JSON_PKG, "SessionConfig.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    
    for field in ["enableSessionTelemetry", "enableConfigDiscovery", "includeSubAgentStreamingEvents"]:
        content = refactor_fluent_boolean_getter(content, field)
        content = refactor_fluent_boolean_setter(content, field, "SessionConfig")
        content = add_fluent_clear_method(content, field, "SessionConfig")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_resume_session_config():
    path = os.path.join(JSON_PKG, "ResumeSessionConfig.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    
    for field in ["enableSessionTelemetry", "enableConfigDiscovery", "includeSubAgentStreamingEvents"]:
        content = refactor_fluent_boolean_getter(content, field)
        content = refactor_fluent_boolean_setter(content, field, "ResumeSessionConfig")
        content = add_fluent_clear_method(content, field, "ResumeSessionConfig")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_infinite_session_config():
    path = os.path.join(JSON_PKG, "InfiniteSessionConfig.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    content = add_import(content, "import java.util.OptionalDouble;")
    
    # enabled: Boolean -> Optional<Boolean>
    content = refactor_fluent_boolean_getter(content, "enabled")
    content = refactor_fluent_boolean_setter(content, "enabled", "InfiniteSessionConfig")
    content = add_fluent_clear_method(content, "enabled", "InfiniteSessionConfig")
    
    # backgroundCompactionThreshold: Double -> OptionalDouble
    content = refactor_fluent_double_getter(content, "backgroundCompactionThreshold")
    content = refactor_fluent_double_setter(content, "backgroundCompactionThreshold", "InfiniteSessionConfig")
    content = add_fluent_clear_method(content, "backgroundCompactionThreshold", "InfiniteSessionConfig")
    
    # bufferExhaustionThreshold: Double -> OptionalDouble
    content = refactor_fluent_double_getter(content, "bufferExhaustionThreshold")
    content = refactor_fluent_double_setter(content, "bufferExhaustionThreshold", "InfiniteSessionConfig")
    content = add_fluent_clear_method(content, "bufferExhaustionThreshold", "InfiniteSessionConfig")
    
    # Update javadoc
    content = content.replace(
        "@return {@code true} if enabled, {@code null} to use default (true)",
        "@return an {@link Optional} containing the boolean value, or empty to use default (true)"
    )
    content = content.replace(
        "@return the threshold (0.0-1.0), or {@code null} to use default\n     */\n    public OptionalDouble getBackgroundCompactionThreshold()",
        "@return an {@link OptionalDouble} containing the threshold (0.0-1.0), or empty to use default\n     */\n    public OptionalDouble getBackgroundCompactionThreshold()"
    )
    content = content.replace(
        "@return the threshold (0.0-1.0), or {@code null} to use default\n     */\n    public OptionalDouble getBufferExhaustionThreshold()",
        "@return an {@link OptionalDouble} containing the threshold (0.0-1.0), or empty to use default\n     */\n    public OptionalDouble getBufferExhaustionThreshold()"
    )
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_input_options():
    path = os.path.join(JSON_PKG, "InputOptions.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.OptionalInt;")
    
    for field in ["minLength", "maxLength"]:
        content = refactor_fluent_int_getter(content, field)
        content = refactor_fluent_int_setter(content, field, "InputOptions")
        content = add_fluent_clear_method(content, field, "InputOptions")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_model_capabilities_override():
    path = os.path.join(JSON_PKG, "ModelCapabilitiesOverride.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    content = add_import(content, "import java.util.OptionalInt;")
    
    # Inner class Supports: vision, reasoningEffort (Boolean -> Optional<Boolean>)
    for field in ["vision", "reasoningEffort"]:
        content = refactor_inner_boolean_getter(content, field)
        content = refactor_inner_boolean_setter(content, field, "Supports")
        content = add_inner_fluent_clear(content, field, "Supports")
    
    # Inner class Limits: maxPromptTokens, maxOutputTokens, maxContextWindowTokens (Integer -> OptionalInt)
    for field in ["maxPromptTokens", "maxOutputTokens", "maxContextWindowTokens"]:
        content = refactor_inner_int_getter(content, field)
        content = refactor_inner_int_setter(content, field, "Limits")
        content = add_inner_fluent_clear(content, field, "Limits")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_provider_config():
    path = os.path.join(JSON_PKG, "ProviderConfig.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.OptionalInt;")
    
    for field in ["maxPromptTokens", "maxOutputTokens"]:
        content = refactor_fluent_int_getter(content, field)
        content = refactor_fluent_int_setter(content, field, "ProviderConfig")
        content = add_fluent_clear_method(content, field, "ProviderConfig")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_telemetry_config():
    path = os.path.join(JSON_PKG, "TelemetryConfig.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    
    content = refactor_fluent_boolean_getter(content, "captureContent")
    content = refactor_fluent_boolean_setter(content, "captureContent", "TelemetryConfig")
    content = add_fluent_clear_method(content, "captureContent", "TelemetryConfig")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_session_ui_capabilities():
    path = os.path.join(JSON_PKG, "SessionUiCapabilities.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    
    content = refactor_fluent_boolean_getter(content, "elicitation")
    content = refactor_fluent_boolean_setter(content, "elicitation", "SessionUiCapabilities")
    content = add_fluent_clear_method(content, "elicitation", "SessionUiCapabilities")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_custom_agent_config():
    path = os.path.join(JSON_PKG, "CustomAgentConfig.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    
    content = refactor_fluent_boolean_getter(content, "infer")
    content = refactor_fluent_boolean_setter(content, "infer", "CustomAgentConfig")
    content = add_fluent_clear_method(content, "infer", "CustomAgentConfig")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_user_input_request():
    path = os.path.join(JSON_PKG, "UserInputRequest.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    
    content = refactor_fluent_boolean_getter(content, "allowFreeform")
    content = refactor_fluent_boolean_setter(content, "allowFreeform", "UserInputRequest")
    content = add_fluent_clear_method(content, "allowFreeform", "UserInputRequest")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_create_session_request():
    path = os.path.join(JSON_PKG, "CreateSessionRequest.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    
    fields = ["enableSessionTelemetry", "requestPermission", "requestUserInput", 
              "hooks", "streaming", "enableConfigDiscovery", 
              "includeSubAgentStreamingEvents", "requestElicitation"]
    
    for field in fields:
        content = refactor_void_boolean_getter(content, field)
        content = refactor_void_boolean_setter(content, field)
        content = add_void_clear_method(content, field)
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_resume_session_request():
    path = os.path.join(JSON_PKG, "ResumeSessionRequest.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.Optional;")
    
    fields = ["enableSessionTelemetry", "requestPermission", "requestUserInput",
              "hooks", "enableConfigDiscovery", "disableResume", "streaming",
              "includeSubAgentStreamingEvents", "requestElicitation"]
    
    for field in fields:
        content = refactor_void_boolean_getter(content, field)
        content = refactor_void_boolean_setter(content, field)
        content = add_void_clear_method(content, field)
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_mcp_server_config():
    path = os.path.join(JSON_PKG, "McpServerConfig.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.OptionalInt;")
    
    content = refactor_fluent_int_getter(content, "timeout")
    content = refactor_fluent_int_setter(content, "timeout", "McpServerConfig")
    content = add_fluent_clear_method(content, "timeout", "McpServerConfig")
    
    # Update javadoc
    content = content.replace(
        "@return the timeout in milliseconds, or {@code null} for the default",
        "@return an {@link OptionalInt} containing the timeout in milliseconds, or empty for the default"
    )
    content = content.replace(
        "@param timeout\n     *            the timeout in milliseconds, or {@code null} for the default",
        "@param timeout\n     *            the timeout in milliseconds"
    )
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_model_limits():
    path = os.path.join(JSON_PKG, "ModelLimits.java")
    content = read_file(path)
    
    content = add_import(content, "import java.util.OptionalInt;")
    
    content = refactor_fluent_int_getter(content, "maxPromptTokens")
    content = refactor_fluent_int_setter(content, "maxPromptTokens", "ModelLimits")
    content = add_fluent_clear_method(content, "maxPromptTokens", "ModelLimits")
    
    write_file(path, content)
    print(f"  Refactored: {path}")


def refactor_session_request_builder():
    """Update SessionRequestBuilder to handle Optional returns from config."""
    path = os.path.join(BASE, "SessionRequestBuilder.java")
    content = read_file(path)
    
    # The config getters now return Optional<Boolean>, so we need to use ifPresent
    # Old: request.setEnableSessionTelemetry(config.getEnableSessionTelemetry());
    # New: config.getEnableSessionTelemetry().ifPresent(request::setEnableSessionTelemetry);
    
    # For create request:
    content = content.replace(
        "request.setEnableSessionTelemetry(config.getEnableSessionTelemetry());\n        request.setRequestUserInput(config.getOnUserInputRequest() != null ? true : null);\n        request.setHooks(config.getHooks() != null && config.getHooks().hasHooks() ? true : null);",
        "config.getEnableSessionTelemetry().ifPresent(request::setEnableSessionTelemetry);\n        if (config.getOnUserInputRequest() != null) {\n            request.setRequestUserInput(true);\n        }\n        if (config.getHooks() != null && config.getHooks().hasHooks()) {\n            request.setHooks(true);\n        }"
    )
    
    content = content.replace(
        "request.setStreaming(config.isStreaming() ? true : null);\n        request.setIncludeSubAgentStreamingEvents(config.getIncludeSubAgentStreamingEvents());",
        "if (config.isStreaming()) {\n            request.setStreaming(true);\n        }\n        config.getIncludeSubAgentStreamingEvents().ifPresent(request::setIncludeSubAgentStreamingEvents);"
    )
    
    content = content.replace(
        "request.setEnableConfigDiscovery(config.getEnableConfigDiscovery());\n        request.setModelCapabilities(config.getModelCapabilities());",
        "config.getEnableConfigDiscovery().ifPresent(request::setEnableConfigDiscovery);\n        request.setModelCapabilities(config.getModelCapabilities());"
    )
    
    # For resume request:
    content = content.replace(
        "request.setEnableSessionTelemetry(config.getEnableSessionTelemetry());\n        request.setRequestUserInput(config.getOnUserInputRequest() != null ? true : null);\n        request.setHooks(config.getHooks() != null && config.getHooks().hasHooks() ? true : null);\n        request.setWorkingDirectory(config.getWorkingDirectory());\n        request.setConfigDir(config.getConfigDir());\n        request.setEnableConfigDiscovery(config.getEnableConfigDiscovery());\n        request.setDisableResume(config.isDisableResume() ? true : null);\n        request.setStreaming(config.isStreaming() ? true : null);\n        request.setIncludeSubAgentStreamingEvents(config.getIncludeSubAgentStreamingEvents());",
        "config.getEnableSessionTelemetry().ifPresent(request::setEnableSessionTelemetry);\n        if (config.getOnUserInputRequest() != null) {\n            request.setRequestUserInput(true);\n        }\n        if (config.getHooks() != null && config.getHooks().hasHooks()) {\n            request.setHooks(true);\n        }\n        request.setWorkingDirectory(config.getWorkingDirectory());\n        request.setConfigDir(config.getConfigDir());\n        config.getEnableConfigDiscovery().ifPresent(request::setEnableConfigDiscovery);\n        if (config.isDisableResume()) {\n            request.setDisableResume(true);\n        }\n        if (config.isStreaming()) {\n            request.setStreaming(true);\n        }\n        config.getIncludeSubAgentStreamingEvents().ifPresent(request::setIncludeSubAgentStreamingEvents);"
    )
    
    write_file(path, content)
    print(f"  Refactored: {path}")


if __name__ == "__main__":
    print("Refactoring to Optional return types...")
    
    refactor_copilot_client_options()
    refactor_session_config()
    refactor_resume_session_config()
    refactor_infinite_session_config()
    refactor_input_options()
    refactor_model_capabilities_override()
    refactor_provider_config()
    refactor_telemetry_config()
    refactor_session_ui_capabilities()
    refactor_custom_agent_config()
    refactor_user_input_request()
    refactor_create_session_request()
    refactor_resume_session_request()
    refactor_mcp_server_config()
    refactor_model_limits()
    refactor_session_request_builder()
    
    print("\nDone! Run 'mvn spotless:apply' then 'mvn verify' to validate.")
