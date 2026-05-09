/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Response to an auto-mode-switch request.
 *
 * @since 1.4.0
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

    /**
     * Gets the JSON string value of this response.
     *
     * @return the JSON value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Returns the enum constant for the given JSON value.
     *
     * @param value
     *            the JSON string value
     * @return the matching enum constant
     * @throws IllegalArgumentException
     *             if the value does not match any constant
     */
    @JsonCreator
    public static AutoModeSwitchResponse fromValue(String value) {
        for (AutoModeSwitchResponse response : values()) {
            if (response.value.equals(value)) {
                return response;
            }
        }
        throw new IllegalArgumentException("Unknown AutoModeSwitchResponse value: " + value);
    }
}
