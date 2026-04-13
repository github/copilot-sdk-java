/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Per-property overrides for model capabilities, deep-merged over runtime
 * defaults.
 * <p>
 * Use this class to override specific model capabilities when creating a
 * session or switching models. Only the fields you set will be overridden;
 * others remain at runtime defaults.
 *
 * <h2>Example: Disable vision</h2>
 *
 * <pre>{@code
 * var session = client.createSession(new SessionConfig().setModel("claude-sonnet-4.5").setModelCapabilities(
 * 		new ModelCapabilitiesOverride().setSupports(new ModelCapabilitiesOverrideSupports().setVision(false))))
 * 		.get();
 * }</pre>
 *
 * @see SessionConfig#setModelCapabilities(ModelCapabilitiesOverride)
 * @see com.github.copilot.sdk.CopilotSession#setModel(String, String,
 *      ModelCapabilitiesOverride)
 * @since 1.4.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelCapabilitiesOverride {

    @JsonProperty("supports")
    private ModelCapabilitiesOverrideSupports supports;

    @JsonProperty("limits")
    private ModelCapabilitiesOverrideLimits limits;

    /** Returns the feature flag overrides. */
    public ModelCapabilitiesOverrideSupports getSupports() {
        return supports;
    }

    /** Sets the feature flag overrides. */
    public ModelCapabilitiesOverride setSupports(ModelCapabilitiesOverrideSupports supports) {
        this.supports = supports;
        return this;
    }

    /** Returns the token limit overrides. */
    public ModelCapabilitiesOverrideLimits getLimits() {
        return limits;
    }

    /** Sets the token limit overrides. */
    public ModelCapabilitiesOverride setLimits(ModelCapabilitiesOverrideLimits limits) {
        this.limits = limits;
        return this;
    }
}
