/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration for a local/stdio MCP server.
 * <p>
 * Use this class to configure an MCP server that communicates over
 * stdin/stdout.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * var server = new McpStdioServerConfig().setCommand("node").setArgs(List.of("mcp-server.js"))
 * 		.setEnv(Map.of("API_KEY", "secret")).setTools(List.of("*"));
 * }</pre>
 *
 * @see McpServerConfig
 * @since 1.4.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class McpStdioServerConfig extends McpServerConfig {

    @JsonProperty("command")
    private String command;

    @JsonProperty("args")
    private List<String> args;

    @JsonProperty("env")
    private Map<String, String> env;

    @JsonProperty("cwd")
    private String cwd;

    /** Returns the command to run the MCP server. */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command to run the MCP server.
     *
     * @param command
     *            the executable command
     * @return this instance for method chaining
     */
    public McpStdioServerConfig setCommand(String command) {
        this.command = command;
        return this;
    }

    /** Returns the arguments to pass to the command. */
    public List<String> getArgs() {
        return args;
    }

    /**
     * Sets the arguments to pass to the command.
     *
     * @param args
     *            command-line arguments
     * @return this instance for method chaining
     */
    public McpStdioServerConfig setArgs(List<String> args) {
        this.args = args;
        return this;
    }

    /** Returns the environment variables to pass to the server process. */
    public Map<String, String> getEnv() {
        return env;
    }

    /**
     * Sets the environment variables to pass to the server process.
     *
     * @param env
     *            environment variable map
     * @return this instance for method chaining
     */
    public McpStdioServerConfig setEnv(Map<String, String> env) {
        this.env = env;
        return this;
    }

    /** Returns the working directory for the server process. */
    public String getCwd() {
        return cwd;
    }

    /**
     * Sets the working directory for the server process.
     *
     * @param cwd
     *            working directory path
     * @return this instance for method chaining
     */
    public McpStdioServerConfig setCwd(String cwd) {
        this.cwd = cwd;
        return this;
    }

    @Override
    public McpStdioServerConfig setTools(java.util.List<String> tools) {
        super.setTools(tools);
        return this;
    }

    @Override
    public McpStdioServerConfig setTimeout(Integer timeout) {
        super.setTimeout(timeout);
        return this;
    }
}
