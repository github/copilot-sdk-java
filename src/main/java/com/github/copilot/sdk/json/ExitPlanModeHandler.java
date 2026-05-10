/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for exit-plan-mode requests from the agent.
 * <p>
 * Implement this interface to handle requests to exit plan mode. When the agent
 * finishes planning and wants to proceed, this handler is invoked to get the
 * user's approval and action selection.
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * ExitPlanModeHandler handler = (request, invocation) -> {
 * 	System.out.println("Plan summary: " + request.getSummary());
 * 	return CompletableFuture
 * 			.completedFuture(new ExitPlanModeResult().setApproved(true).setSelectedAction("interactive"));
 * };
 *
 * var session = client
 * 		.createSession(
 * 				new SessionConfig().setOnPermissionRequest(PermissionHandler.APPROVE_ALL).setOnExitPlanMode(handler))
 * 		.get();
 * }</pre>
 *
 * @since 1.0.7
 */
@FunctionalInterface
public interface ExitPlanModeHandler {

    /**
     * Handles an exit-plan-mode request from the agent.
     *
     * @param request
     *            the exit-plan-mode request containing the plan summary and
     *            available actions
     * @param invocation
     *            context information about the invocation
     * @return a future that resolves with the user's decision
     */
    CompletableFuture<ExitPlanModeResult> handle(ExitPlanModeRequest request, ExitPlanModeInvocation invocation);
}
