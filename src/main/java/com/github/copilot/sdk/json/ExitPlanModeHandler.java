/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for exit-plan-mode requests from the agent.
 * <p>
 * Implement this interface to handle requests when the agent wants to
 * transition out of plan mode and proceed with an implementation action.
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * ExitPlanModeHandler handler = (request, invocation) -> {
 * 	System.out.println("Plan summary: " + request.getSummary());
 * 	return CompletableFuture.completedFuture(new ExitPlanModeResult().setApproved(true));
 * };
 *
 * var session = client.createSession(new SessionConfig().setOnExitPlanMode(handler)).get();
 * }</pre>
 *
 * @since 1.4.0
 */
@FunctionalInterface
public interface ExitPlanModeHandler {

    /**
     * Handles an exit-plan-mode request from the agent.
     *
     * @param request
     *            the exit-plan-mode request containing summary, plan content, and
     *            available actions
     * @param invocation
     *            context information about the invocation
     * @return a future that resolves with the user's decision
     */
    CompletableFuture<ExitPlanModeResult> handle(ExitPlanModeRequest request, ExitPlanModeInvocation invocation);
}
