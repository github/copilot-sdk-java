/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Result from sessionFs.readFile. */
public class SessionFsReadFileResult {

    @JsonProperty("content")
    private String content;

    /** Returns the file content. */
    public String getContent() {
        return content;
    }

    /** Sets the file content. */
    public void setContent(String content) {
        this.content = content;
    }
}
