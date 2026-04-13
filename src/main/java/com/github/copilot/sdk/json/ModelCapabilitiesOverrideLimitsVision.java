/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Per-property overrides for vision limits within model capabilities.
 *
 * @since 1.4.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelCapabilitiesOverrideLimitsVision {

    @JsonProperty("supported_media_types")
    private List<String> supportedMediaTypes;

    @JsonProperty("max_prompt_images")
    private Double maxPromptImages;

    @JsonProperty("max_prompt_image_size")
    private Double maxPromptImageSize;

    /** Returns the supported media types. */
    public List<String> getSupportedMediaTypes() {
        return supportedMediaTypes;
    }

    /** Sets the supported media types. */
    public ModelCapabilitiesOverrideLimitsVision setSupportedMediaTypes(List<String> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
        return this;
    }

    /** Returns the maximum number of images per prompt. */
    public Double getMaxPromptImages() {
        return maxPromptImages;
    }

    /** Sets the maximum number of images per prompt. */
    public ModelCapabilitiesOverrideLimitsVision setMaxPromptImages(Double maxPromptImages) {
        this.maxPromptImages = maxPromptImages;
        return this;
    }

    /** Returns the maximum image size in bytes. */
    public Double getMaxPromptImageSize() {
        return maxPromptImageSize;
    }

    /** Sets the maximum image size in bytes. */
    public ModelCapabilitiesOverrideLimitsVision setMaxPromptImageSize(Double maxPromptImageSize) {
        this.maxPromptImageSize = maxPromptImageSize;
        return this;
    }
}
