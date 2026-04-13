/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Result from sessionFs.readdirWithTypes. */
public class SessionFsReaddirWithTypesResult {

    @JsonProperty("entries")
    private List<SessionFsDirentEntry> entries;

    /** Returns the entries with type information. */
    public List<SessionFsDirentEntry> getEntries() {
        return entries;
    }

    /** Sets the entries with type information. */
    public void setEntries(List<SessionFsDirentEntry> entries) {
        this.entries = entries;
    }
}
