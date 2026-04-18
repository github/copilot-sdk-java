/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Parameters for sessionFs.rm. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionFsRmParams {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("path")
    private String path;

    @JsonProperty("recursive")
    private Boolean recursive;

    @JsonProperty("force")
    private Boolean force;

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

    /** Returns whether to remove directories recursively. */
    public Boolean getRecursive() {
        return recursive;
    }

    /** Sets whether to remove directories recursively. */
    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }

    /** Returns whether to ignore non-existent files. */
    public Boolean getForce() {
        return force;
    }

    /** Sets whether to ignore non-existent files. */
    public void setForce(Boolean force) {
        this.force = force;
    }
}
