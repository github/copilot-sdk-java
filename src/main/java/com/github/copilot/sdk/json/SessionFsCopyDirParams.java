/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Parameters for sessionFs.copyDir. */
public class SessionFsCopyDirParams {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("src")
    private String src;

    @JsonProperty("dst")
    private String dst;

    /** Returns the session ID. */
    public String getSessionId() {
        return sessionId;
    }

    /** Sets the session ID. */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /** Returns the source directory path. */
    public String getSrc() {
        return src;
    }

    /** Sets the source directory path. */
    public void setSrc(String src) {
        this.src = src;
    }

    /** Returns the destination directory path. */
    public String getDst() {
        return dst;
    }

    /** Sets the destination directory path. */
    public void setDst(String dst) {
        this.dst = dst;
    }
}
