/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Parameters for sessionFs.appendFile. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionFsAppendFileParams {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("path")
    private String path;

    @JsonProperty("content")
    private String content;

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

    /** Returns the content to append. */
    public String getContent() {
        return content;
    }

    /** Sets the content to append. */
    public void setContent(String content) {
        this.content = content;
    }

    /** Returns the optional POSIX-style mode. */
    public Double getMode() {
        return mode;
    }

    /** Sets the optional POSIX-style mode. */
    public void setMode(Double mode) {
        this.mode = mode;
    }
}
