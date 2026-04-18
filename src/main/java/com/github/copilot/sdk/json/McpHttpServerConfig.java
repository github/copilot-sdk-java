/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration for a remote HTTP/SSE MCP server.
 * <p>
 * Use this class to configure an MCP server accessed via HTTP or Server-Sent
 * Events.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * var server = new McpHttpServerConfig().setUrl("https://example.com/mcp")
 * 		.setHeaders(Map.of("Authorization", "Bearer token")).setTools(List.of("*"));
 * }</pre>
 *
 * @see McpServerConfig
 * @since 1.4.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class McpHttpServerConfig extends McpServerConfig {

    @JsonProperty("url")
    private String url;

    @JsonProperty("headers")
    private Map<String, String> headers;

    /** Returns the URL of the remote server. */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the remote server.
     *
     * @param url
     *            the server URL
     * @return this instance for method chaining
     */
    public McpHttpServerConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    /** Returns the optional HTTP headers to include in requests. */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets optional HTTP headers to include in requests.
     *
     * @param headers
     *            HTTP header map
     * @return this instance for method chaining
     */
    public McpHttpServerConfig setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public McpHttpServerConfig setTools(java.util.List<String> tools) {
        super.setTools(tools);
        return this;
    }

    @Override
    public McpHttpServerConfig setTimeout(Integer timeout) {
        super.setTimeout(timeout);
        return this;
    }
}
