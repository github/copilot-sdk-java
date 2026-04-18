/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Per-property overrides for token limits within model capabilities.
 *
 * @since 1.4.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelCapabilitiesOverrideLimits {

    @JsonProperty("max_prompt_tokens")
    private Double maxPromptTokens;

    @JsonProperty("max_output_tokens")
    private Double maxOutputTokens;

    @JsonProperty("max_context_window_tokens")
    private Double maxContextWindowTokens;

    @JsonProperty("vision")
    private ModelCapabilitiesOverrideLimitsVision vision;

    /** Returns the maximum prompt tokens override. */
    public Double getMaxPromptTokens() {
        return maxPromptTokens;
    }

    /** Sets the maximum prompt tokens override. */
    public ModelCapabilitiesOverrideLimits setMaxPromptTokens(Double maxPromptTokens) {
        this.maxPromptTokens = maxPromptTokens;
        return this;
    }

    /** Returns the maximum output tokens override. */
    public Double getMaxOutputTokens() {
        return maxOutputTokens;
    }

    /** Sets the maximum output tokens override. */
    public ModelCapabilitiesOverrideLimits setMaxOutputTokens(Double maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
        return this;
    }

    /** Returns the maximum context window tokens override. */
    public Double getMaxContextWindowTokens() {
        return maxContextWindowTokens;
    }

    /** Sets the maximum context window tokens override. */
    public ModelCapabilitiesOverrideLimits setMaxContextWindowTokens(Double maxContextWindowTokens) {
        this.maxContextWindowTokens = maxContextWindowTokens;
        return this;
    }

    /** Returns the vision limits override. */
    public ModelCapabilitiesOverrideLimitsVision getVision() {
        return vision;
    }

    /** Sets the vision limits override. */
    public ModelCapabilitiesOverrideLimits setVision(ModelCapabilitiesOverrideLimitsVision vision) {
        this.vision = vision;
        return this;
    }
}
