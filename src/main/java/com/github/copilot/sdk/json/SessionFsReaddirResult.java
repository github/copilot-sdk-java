/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Result from sessionFs.readdir. */
public class SessionFsReaddirResult {

    @JsonProperty("entries")
    private List<String> entries;

    /** Returns the entry names in the directory. */
    public List<String> getEntries() {
        return entries;
    }

    /** Sets the entry names in the directory. */
    public void setEntries(List<String> entries) {
        this.entries = entries;
    }
}
