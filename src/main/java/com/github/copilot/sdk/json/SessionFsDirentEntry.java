/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/** An entry in a directory listing with type information. */
public class SessionFsDirentEntry {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    /** Returns the entry name. */
    public String getName() {
        return name;
    }

    /** Sets the entry name. */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns the entry type (e.g., "file", "directory"). */
    public String getType() {
        return type;
    }

    /** Sets the entry type. */
    public void setType(String type) {
        this.type = type;
    }
}
