/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event: session.background_tasks_changed
 *
 * @since 1.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SessionBackgroundTasksChangedEvent extends AbstractSessionEvent {

    @JsonProperty("data")
    private SessionBackgroundTasksChangedData data;

    @Override
    public String getType() {
        return "session.background_tasks_changed";
    }

    public SessionBackgroundTasksChangedData getData() {
        return data;
    }

    public void setData(SessionBackgroundTasksChangedData data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SessionBackgroundTasksChangedData() {
    }
}
