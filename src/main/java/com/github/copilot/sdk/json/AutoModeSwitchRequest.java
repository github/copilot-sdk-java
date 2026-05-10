/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to switch to auto mode after an eligible rate limit.
 *
 * @since 1.0.7
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoModeSwitchRequest {

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("retryAfterSeconds")
    private Double retryAfterSeconds;

    /**
     * Gets the rate-limit error code that triggered the request. @return the error
     * code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /** Sets the error code. @param errorCode the error code @return this */
    public AutoModeSwitchRequest setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    /**
     * Gets the seconds until the rate limit resets, when known. @return the
     * retry-after seconds
     */
    public Double getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    /**
     * Sets the retry-after seconds. @param retryAfterSeconds the seconds @return
     * this
     */
    public AutoModeSwitchRequest setRetryAfterSeconds(Double retryAfterSeconds) {
        this.retryAfterSeconds = retryAfterSeconds;
        return this;
    }
}
