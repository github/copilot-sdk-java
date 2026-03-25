/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.copilot.sdk.json.McpServerInfo;

import java.util.List;

/**
 * Event: session.mcp_servers_loaded
 * <p>
 * Fired after all MCP servers have been initialized for the session. Contains a
 * summary of each server's connection status.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SessionMcpServersLoadedEvent extends AbstractSessionEvent {

    @JsonProperty("data")
    private SessionMcpServersLoadedData data;

    @Override
    public String getType() {
        return "session.mcp_servers_loaded";
    }

    public SessionMcpServersLoadedData getData() {
        return data;
    }

    public void setData(SessionMcpServersLoadedData data) {
        this.data = data;
    }

    /**
     * Data payload for the {@code session.mcp_servers_loaded} event.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SessionMcpServersLoadedData {

        @JsonProperty("servers")
        private List<McpServerInfo> servers;

        /**
         * Gets the list of MCP server status summaries.
         *
         * @return the servers list
         */
        public List<McpServerInfo> getServers() {
            return servers;
        }

        /**
         * Sets the list of MCP server status summaries.
         *
         * @param servers
         *            the servers list
         */
        public void setServers(List<McpServerInfo> servers) {
            this.servers = servers;
        }
    }
}
