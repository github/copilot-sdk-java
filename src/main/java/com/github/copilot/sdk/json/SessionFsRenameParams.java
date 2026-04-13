/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Parameters for sessionFs.rename. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionFsRenameParams {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("oldPath")
    private String oldPath;

    @JsonProperty("newPath")
    private String newPath;

    @JsonProperty("overwrite")
    private Boolean overwrite;

    /** Returns the session ID. */
    public String getSessionId() {
        return sessionId;
    }

    /** Sets the session ID. */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /** Returns the source path. */
    public String getOldPath() {
        return oldPath;
    }

    /** Sets the source path. */
    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    /** Returns the destination path. */
    public String getNewPath() {
        return newPath;
    }

    /** Sets the destination path. */
    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    /** Returns whether to overwrite an existing destination. */
    public Boolean getOverwrite() {
        return overwrite;
    }

    /** Sets whether to overwrite an existing destination. */
    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }
}
