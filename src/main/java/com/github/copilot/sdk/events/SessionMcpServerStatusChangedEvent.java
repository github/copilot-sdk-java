/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event: session.mcp_server_status_changed
 * <p>
 * Fired when the connection status of an MCP server changes during the session
 * (e.g., from {@code pending} to {@code connected} or {@code failed}).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SessionMcpServerStatusChangedEvent extends AbstractSessionEvent {

    @JsonProperty("data")
    private SessionMcpServerStatusChangedData data;

    @Override
    public String getType() {
        return "session.mcp_server_status_changed";
    }

    public SessionMcpServerStatusChangedData getData() {
        return data;
    }

    public void setData(SessionMcpServerStatusChangedData data) {
        this.data = data;
    }

    /**
     * Data payload for the {@code session.mcp_server_status_changed} event.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SessionMcpServerStatusChangedData {

        @JsonProperty("serverName")
        private String serverName;

        @JsonProperty("status")
        private String status;

        /**
         * Gets the name of the MCP server whose status changed.
         *
         * @return the server name
         */
        public String getServerName() {
            return serverName;
        }

        /**
         * Sets the server name.
         *
         * @param serverName
         *            the server name
         */
        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        /**
         * Gets the new connection status.
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
         * Sets the new connection status.
         *
         * @param status
         *            the status string
         */
        public void setStatus(String status) {
            this.status = status;
        }
    }
}
