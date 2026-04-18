/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Parameters for sessionFs.mkdir. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionFsMkdirParams {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("path")
    private String path;

    @JsonProperty("recursive")
    private Boolean recursive;

    @JsonProperty("mode")
    private Double mode;

    /** Returns the session ID. */
    public String getSessionId() {
        return sessionId;
    }

    /** Sets the session ID. */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /** Returns the path. */
    public String getPath() {
        return path;
    }

    /** Sets the path. */
    public void setPath(String path) {
        this.path = path;
    }

    /** Returns whether to create parent directories as needed. */
    public Boolean getRecursive() {
        return recursive;
    }

    /** Sets whether to create parent directories as needed. */
    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }

    /** Returns the optional POSIX-style mode for newly created directories. */
    public Double getMode() {
        return mode;
    }

    /** Sets the optional POSIX-style mode for newly created directories. */
    public void setMode(Double mode) {
        this.mode = mode;
    }
}
