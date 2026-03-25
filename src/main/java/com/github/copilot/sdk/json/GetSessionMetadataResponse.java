/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Internal response object from getting session metadata.
 * <p>
 * This is a low-level class for JSON-RPC communication containing the metadata
 * for a specific session, or {@code null} if the session was not found.
 *
 * @see com.github.copilot.sdk.CopilotClient#getSessionMetadata(String)
 * @see SessionMetadata
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetSessionMetadataResponse(
        /** The session metadata, or {@code null} if the session was not found. */
        @JsonProperty("session") SessionMetadata session) {
}
