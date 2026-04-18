/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Result from sessionFs.exists. */
public class SessionFsExistsResult {

    @JsonProperty("exists")
    private boolean exists;

    /** Returns whether the path exists. */
    public boolean isExists() {
        return exists;
    }

    /** Sets whether the path exists. */
    public void setExists(boolean exists) {
        this.exists = exists;
    }
}
