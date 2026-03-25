/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the status of an MCP (Model Context Protocol) server connected to
 * a session.
 * <p>
 * Returned by {@link com.github.copilot.sdk.CopilotSession#listMcpServers()}
 * and carried in
 * {@link com.github.copilot.sdk.events.SessionMcpServersLoadedEvent} and
 * {@link com.github.copilot.sdk.events.SessionMcpServerStatusChangedEvent}.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class McpServerInfo {

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private String status;

    @JsonProperty("source")
    private String source;

    @JsonProperty("error")
    private String error;

    /**
     * Gets the server name (the config key used when registering the server).
     *
     * @return the server name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the server name.
     *
     * @param name
     *            the server name
     * @return this instance for chaining
     */
    public McpServerInfo setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets the connection status of the server.
     * <p>
     * Possible values: {@code "connected"}, {@code "failed"}, {@code "pending"},
     * {@code "disabled"}, {@code "not_configured"}.
     *
     * @return the status string
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the connection status.
     *
     * @param status
     *            the status string
     * @return this instance for chaining
     */
    public McpServerInfo setStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * Gets the configuration source of this server.
     * <p>
     * Possible values: {@code "user"}, {@code "workspace"}, {@code "plugin"},
     * {@code "builtin"}.
     *
     * @return the source, or {@code null} if not provided
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the configuration source.
     *
     * @param source
     *            the source string
     * @return this instance for chaining
     */
    public McpServerInfo setSource(String source) {
        this.source = source;
        return this;
    }

    /**
     * Gets the error message if the server failed to connect.
     *
     * @return the error message, or {@code null} if no error
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the error message.
     *
     * @param error
     *            the error message
     * @return this instance for chaining
     */
    public McpServerInfo setError(String error) {
        this.error = error;
        return this;
    }
}
