/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Per-property overrides for model capability feature flags.
 *
 * @since 1.4.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelCapabilitiesOverrideSupports {

    @JsonProperty("vision")
    private Boolean vision;

    @JsonProperty("reasoningEffort")
    private Boolean reasoningEffort;

    /** Returns the vision capability override. */
    public Boolean getVision() {
        return vision;
    }

    /** Sets the vision capability override. */
    public ModelCapabilitiesOverrideSupports setVision(Boolean vision) {
        this.vision = vision;
        return this;
    }

    /** Returns the reasoning effort capability override. */
    public Boolean getReasoningEffort() {
        return reasoningEffort;
    }

    /** Sets the reasoning effort capability override. */
    public ModelCapabilitiesOverrideSupports setReasoningEffort(Boolean reasoningEffort) {
        this.reasoningEffort = reasoningEffort;
        return this;
    }
}
