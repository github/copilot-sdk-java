/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event: session.tools_updated
 *
 * @since 1.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SessionToolsUpdatedEvent extends AbstractSessionEvent {

    @JsonProperty("data")
    private SessionToolsUpdatedData data;

    @Override
    public String getType() {
        return "session.tools_updated";
    }

    public SessionToolsUpdatedData getData() {
        return data;
    }

    public void setData(SessionToolsUpdatedData data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SessionToolsUpdatedData(@JsonProperty("model") String model) {
    }
}
