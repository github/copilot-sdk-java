/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for exit-plan-mode requests from the agent.
 * <p>
 * Implement this interface to handle requests from the agent to exit plan mode.
 * The handler receives the plan summary and available actions, and returns the
 * user's decision.
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * ExitPlanModeHandler handler = (request, invocation) -> {
 * 	System.out.println("Plan summary: " + request.getSummary());
 * 	return CompletableFuture
 * 			.completedFuture(new ExitPlanModeResult().setApproved(true).setSelectedAction("autopilot"));
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
     *            the exit-plan-mode request containing the summary and actions
     * @param invocation
     *            context information about the invocation
     * @return a future that resolves with the user's decision
     */
    CompletableFuture<ExitPlanModeResult> handle(ExitPlanModeRequest request, ExitPlanModeInvocation invocation);
}
