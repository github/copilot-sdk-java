/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Result from sessionFs.glob. */
public class SessionFsGlobResult {

    @JsonProperty("matches")
    private List<String> matches;

    /** Returns the matching paths. */
    public List<String> getMatches() {
        return matches;
    }

    /** Sets the matching paths. */
    public void setMatches(List<String> matches) {
        this.matches = matches;
    }
}
