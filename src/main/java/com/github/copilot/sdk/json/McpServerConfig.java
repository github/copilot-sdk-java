/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract base class for MCP server configurations.
 * <p>
 * Use {@link McpStdioServerConfig} for local/stdio MCP servers or
 * {@link McpHttpServerConfig} for remote HTTP/SSE MCP servers.
 *
 * <h2>Example: Local server</h2>
 *
 * <pre>{@code
 * var servers = Map.of("my-server",
 * 		new McpStdioServerConfig().setCommand("node").setArgs(List.of("mcp-server.js")).setTools(List.of("*")));
 * var session = client.createSession(new SessionConfig().setMcpServers(servers)).get();
 * }</pre>
 *
 * <h2>Example: Remote server</h2>
 *
 * <pre>{@code
 * var servers = Map.of("remote-server",
 * 		new McpHttpServerConfig().setUrl("https://example.com/mcp").setTools(List.of("*")));
 * var session = client.createSession(new SessionConfig().setMcpServers(servers)).get();
 * }</pre>
 *
 * @see McpStdioServerConfig
 * @see McpHttpServerConfig
 * @see SessionConfig#setMcpServers(java.util.Map)
 * @since 1.4.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = McpStdioServerConfig.class)
@JsonSubTypes({@JsonSubTypes.Type(value = McpStdioServerConfig.class, name = "stdio"),
        @JsonSubTypes.Type(value = McpHttpServerConfig.class, name = "http")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class McpServerConfig {

    @JsonProperty("tools")
    private List<String> tools;

    @JsonProperty("timeout")
    private Integer timeout;

    /**
     * Private constructor to prevent direct subclassing outside this package.
     */
    McpServerConfig() {
    }

    /**
     * Returns the list of tools to include from this server. An empty list means
     * none; use {@code "*"} for all.
     */
    public List<String> getTools() {
        return tools;
    }

    /**
     * Sets the list of tools to include from this server. Use {@code "*"} to
     * include all tools.
     *
     * @param tools
     *            tool names, or {@code List.of("*")} for all
     * @return this instance for method chaining
     */
    public McpServerConfig setTools(List<String> tools) {
        this.tools = tools;
        return this;
    }

    /**
     * Returns the optional timeout in milliseconds for tool calls to this server.
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Sets the optional timeout in milliseconds for tool calls to this server.
     *
     * @param timeout
     *            timeout in milliseconds, or {@code null} for no override
     * @return this instance for method chaining
     */
    public McpServerConfig setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }
}
