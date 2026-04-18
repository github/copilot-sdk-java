/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Path conventions used by the session filesystem provider.
 *
 * @since 1.4.0
 */
public enum SessionFsConventions {

    /** Windows-style path conventions (backslash separator). */
    @JsonProperty("windows")
    WINDOWS,

    /** POSIX-style path conventions (forward slash separator). */
    @JsonProperty("posix")
    POSIX;
}
