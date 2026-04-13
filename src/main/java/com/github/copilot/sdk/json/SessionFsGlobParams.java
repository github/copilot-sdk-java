/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Parameters for sessionFs.glob. */
public class SessionFsGlobParams {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("patterns")
    private List<String> patterns;

    /** Returns the session ID. */
    public String getSessionId() {
        return sessionId;
    }

    /** Sets the session ID. */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /** Returns the glob patterns. */
    public List<String> getPatterns() {
        return patterns;
    }

    /** Sets the glob patterns. */
    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }
}
