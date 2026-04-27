/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Internal request object for setting the foreground session.
 *
 * @param sessionId
 *            the ID of the session to set as the foreground session
 * @see com.github.copilot.sdk.CopilotClient#setForegroundSessionId(String)
 * @since 1.0.0
 */
public record SetForegroundSessionRequest(@JsonProperty("sessionId") String sessionId) {
}
