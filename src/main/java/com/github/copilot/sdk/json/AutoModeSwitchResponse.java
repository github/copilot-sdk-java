/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Response to an auto-mode-switch request.
 *
 * @since 1.0.7
 */
public enum AutoModeSwitchResponse {

    /** Approve the switch for this rate-limit cycle. */
    YES("yes"),

    /** Approve and remember the choice for this session. */
    YES_ALWAYS("yes_always"),

    /** Decline the switch. */
    NO("no");

    private final String value;

    AutoModeSwitchResponse(String value) {
        this.value = value;
    }

    /** Gets the JSON string value. @return the value */
    @JsonValue
    public String getValue() {
        return value;
    }
}
