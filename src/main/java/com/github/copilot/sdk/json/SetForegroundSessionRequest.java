/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for session.setForeground RPC call.
 * <p>
 * This is only available when connecting to a server running in TUI+server mode
 * (--ui-server).
 *
 * @since 1.0.0
 */
public record SetForegroundSessionRequest(
        /** The ID of the session to bring to the foreground. */
        @JsonProperty("sessionId") String sessionId) {
}
