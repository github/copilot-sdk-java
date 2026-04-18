/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Parameters for sessionFs.cp. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionFsCpParams {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("src")
    private String src;

    @JsonProperty("dst")
    private String dst;

    @JsonProperty("recursive")
    private Boolean recursive;

    /** Returns the session ID. */
    public String getSessionId() {
        return sessionId;
    }

    /** Sets the session ID. */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /** Returns the source path. */
    public String getSrc() {
        return src;
    }

    /** Sets the source path. */
    public void setSrc(String src) {
        this.src = src;
    }

    /** Returns the destination path. */
    public String getDst() {
        return dst;
    }

    /** Sets the destination path. */
    public void setDst(String dst) {
        this.dst = dst;
    }

    /** Returns whether to copy recursively. */
    public Boolean getRecursive() {
        return recursive;
    }

    /** Sets whether to copy recursively. */
    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }
}
