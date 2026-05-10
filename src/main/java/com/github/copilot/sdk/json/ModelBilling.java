/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model billing information.
 *
 * @since 1.0.1
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelBilling {

    @JsonProperty("multiplier")
    private Double multiplier;

    public Double getMultiplier() {
        return multiplier;
    }

    public ModelBilling setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
        return this;
    }
}
