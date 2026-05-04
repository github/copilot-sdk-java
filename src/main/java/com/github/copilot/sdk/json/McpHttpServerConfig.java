/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration for a remote HTTP/SSE MCP (Model Context Protocol) server.
 * <p>
 * Use this to configure an MCP server that communicates over HTTP or
 * Server-Sent Events (SSE).
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * var server = new McpHttpServerConfig().setUrl("https://mcp.example.com/sse").setTools(List.of("*"));
 *
 * var config = new SessionConfig().setMcpServers(Map.of("remote-server", server));
 * }</pre>
 *
 * @see McpServerConfig
 * @see SessionConfig#setMcpServers(java.util.Map)
 * @since 1.3.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class McpHttpServerConfig extends McpServerConfig {

    @JsonProperty("type")
    private final String type = "http";

    @JsonProperty("url")
    private String url;

    @JsonProperty("headers")
    private Map<String, String> headers;

    @JsonProperty("oauthClientId")
    private String oauthClientId;

    @JsonProperty("oauthPublicClient")
    private Boolean oauthPublicClient;

    @JsonProperty("oauthGrantType")
    private String oauthGrantType;

    /**
     * Gets the server type discriminator.
     *
     * @return always {@code "http"}
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the URL of the remote server.
     *
     * @return the server URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the remote server.
     *
     * @param url
     *            the server URL
     * @return this config for method chaining
     */
    public McpHttpServerConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Gets the optional HTTP headers to include in requests.
     *
     * @return the headers map, or {@code null}
     */
    public Map<String, String> getHeaders() {
        return headers == null ? null : Collections.unmodifiableMap(headers);
    }

    /**
     * Sets optional HTTP headers to include in requests to this server.
     *
     * @param headers
     *            the headers map
     * @return this config for method chaining
     */
    public McpHttpServerConfig setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Gets the optional OAuth client ID for the remote server.
     *
     * @return the OAuth client ID, or {@code null}
     * @since 1.4.0
     */
    public String getOauthClientId() {
        return oauthClientId;
    }

    /**
     * Sets the optional OAuth client ID for the remote server.
     *
     * @param oauthClientId
     *            the OAuth client ID
     * @return this config for method chaining
     * @since 1.4.0
     */
    public McpHttpServerConfig setOauthClientId(String oauthClientId) {
        this.oauthClientId = oauthClientId;
        return this;
    }

    /**
     * Gets whether this is a public OAuth client.
     *
     * @return {@code true} if public OAuth client, or {@code null}
     * @since 1.4.0
     */
    public Boolean getOauthPublicClient() {
        return oauthPublicClient;
    }

    /**
     * Sets whether this is a public OAuth client.
     *
     * @param oauthPublicClient
     *            whether this is a public OAuth client
     * @return this config for method chaining
     * @since 1.4.0
     */
    public McpHttpServerConfig setOauthPublicClient(Boolean oauthPublicClient) {
        this.oauthPublicClient = oauthPublicClient;
        return this;
    }

    /**
     * Gets the optional OAuth grant type for the remote server.
     *
     * @return the OAuth grant type (e.g., "authorization_code" or
     *         "client_credentials"), or {@code null}
     * @since 1.4.0
     */
    public String getOauthGrantType() {
        return oauthGrantType;
    }

    /**
     * Sets the optional OAuth grant type for the remote server.
     *
     * @param oauthGrantType
     *            the OAuth grant type (e.g., "authorization_code" or
     *            "client_credentials")
     * @return this config for method chaining
     * @since 1.4.0
     */
    public McpHttpServerConfig setOauthGrantType(String oauthGrantType) {
        this.oauthGrantType = oauthGrantType;
        return this;
    }

    @Override
    public McpHttpServerConfig setTools(List<String> tools) {
        super.setTools(tools);
        return this;
    }

    @Override
    public McpHttpServerConfig setTimeout(Integer timeout) {
        super.setTimeout(timeout);
        return this;
    }
}
