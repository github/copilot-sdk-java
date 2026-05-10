/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for auto-mode-switch requests from the agent.
 * <p>
 * Implement this interface to handle requests to switch to auto mode when a
 * rate limit is encountered. The handler decides whether to approve the switch.
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * AutoModeSwitchHandler handler = (request, invocation) -> {
 * 	System.out.println("Rate limited: " + request.getErrorCode());
 * 	return CompletableFuture.completedFuture(AutoModeSwitchResponse.YES);
 * };
 *
 * var session = client.createSession(
 * 		new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setOnAutoModeSwitch(handler))
 * 		.get();
 * }</pre>
 *
 * @since 1.0.7
 */
@FunctionalInterface
public interface AutoModeSwitchHandler {

    /**
     * Handles an auto-mode-switch request from the agent.
     *
     * @param request
     *            the auto-mode-switch request containing the error code and
     *            retry-after information
     * @param invocation
     *            context information about the invocation
     * @return a future that resolves with the user's decision
     */
    CompletableFuture<AutoModeSwitchResponse> handle(AutoModeSwitchRequest request,
            AutoModeSwitchInvocation invocation);
}
