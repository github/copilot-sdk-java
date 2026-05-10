/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

/**
 * Context for an auto-mode-switch request invocation.
 *
 * @since 1.0.7
 */
public class AutoModeSwitchInvocation {

    private String sessionId = "";

    /** Gets the session ID that triggered the request. @return the session ID */
    public String getSessionId() {
        return sessionId;
    }

    /** Sets the session ID. @param sessionId the session ID @return this */
    public AutoModeSwitchInvocation setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }
}
