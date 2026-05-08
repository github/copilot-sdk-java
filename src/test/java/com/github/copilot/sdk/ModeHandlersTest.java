/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.copilot.sdk.json.AutoModeSwitchRequest;
import com.github.copilot.sdk.json.AutoModeSwitchResponse;
import com.github.copilot.sdk.json.CopilotClientOptions;
import com.github.copilot.sdk.json.ExitPlanModeRequest;
import com.github.copilot.sdk.json.ExitPlanModeResult;
import com.github.copilot.sdk.json.MessageOptions;
import com.github.copilot.sdk.json.PermissionHandler;
import com.github.copilot.sdk.json.SessionConfig;

/**
 * Tests for mode handler (exit-plan-mode and auto-mode-switch) functionality.
 *
 * <p>
 * These tests use the shared CapiProxy infrastructure for deterministic API
 * response replay. Snapshots are stored in test/snapshots/mode_handlers/.
 * </p>
 */
public class ModeHandlersTest {

    private static final String TOKEN = "mode-handler-token";

    private static E2ETestContext ctx;

    @BeforeAll
    static void setup() throws Exception {
        ctx = E2ETestContext.create();
    }

    @AfterAll
    static void teardown() throws Exception {
        if (ctx != null) {
            ctx.close();
        }
    }

    /**
     * Verifies that exit-plan-mode handler is invoked when model uses
     * exit_plan_mode tool.
     *
     * @see Snapshot:
     *      mode_handlers/should_invoke_exit_plan_mode_handler_when_model_uses_tool
     */
    @Disabled("Requires test harness snapshots from updated .lastmerge commit - will be enabled after sync completes")
    @Test
    void testShouldInvokeExitPlanModeHandlerWhenModelUsesTool() throws Exception {
        ctx.configureForTest("mode_handlers", "should_invoke_exit_plan_mode_handler_when_model_uses_tool");
        configureAuthenticatedUser();

        final var exitPlanModeRequests = new java.util.ArrayList<ExitPlanModeRequest>();
        final String[] sessionIdHolder = new String[1];

        var config = new SessionConfig().setGitHubToken(TOKEN).setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
                .setOnExitPlanMode((request, invocation) -> {
                    exitPlanModeRequests.add(request);
                    assertEquals(sessionIdHolder[0], invocation.getSessionId());
                    return CompletableFuture.completedFuture(new ExitPlanModeResult().setApproved(true)
                            .setSelectedAction("interactive").setFeedback("Approved by the Java E2E test"));
                });

        try (CopilotClient client = createAuthenticatedClient()) {
            CopilotSession session = client.createSession(config).get();
            sessionIdHolder[0] = session.getSessionId();

            var response = session.sendAndWait(new MessageOptions().setMode("plan")
                    .setPrompt("Create a brief implementation plan for adding a greeting.txt file, "
                            + "then request approval with exit_plan_mode."))
                    .get(120, TimeUnit.SECONDS);

            assertNotNull(response, "Response should not be null");
            assertFalse(exitPlanModeRequests.isEmpty(), "Should have received exit plan mode requests");

            ExitPlanModeRequest request = exitPlanModeRequests.get(0);
            assertEquals("Greeting file implementation plan", request.getSummary());
            assertNotNull(request.getActions());
            assertNotNull(request.getRecommendedAction());
        }
    }

    /**
     * Verifies that auto-mode-switch handler is invoked when rate limited.
     *
     * @see Snapshot:
     *      mode_handlers/should_invoke_auto_mode_switch_handler_when_rate_limited
     */
    @Disabled("Requires test harness snapshots from updated .lastmerge commit - will be enabled after sync completes")
    @Test
    void testShouldInvokeAutoModeSwitchHandlerWhenRateLimited() throws Exception {
        ctx.configureForTest("mode_handlers", "should_invoke_auto_mode_switch_handler_when_rate_limited");
        configureAuthenticatedUser();

        final var autoModeSwitchRequests = new java.util.ArrayList<AutoModeSwitchRequest>();
        final String[] sessionIdHolder = new String[1];

        var config = new SessionConfig().setGitHubToken(TOKEN).setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
                .setOnAutoModeSwitch((request, invocation) -> {
                    autoModeSwitchRequests.add(request);
                    assertEquals(sessionIdHolder[0], invocation.getSessionId());
                    return CompletableFuture.completedFuture(AutoModeSwitchResponse.YES);
                });

        try (CopilotClient client = createAuthenticatedClient()) {
            CopilotSession session = client.createSession(config).get();
            sessionIdHolder[0] = session.getSessionId();

            String messageId = session
                    .send(new MessageOptions()
                            .setPrompt("Explain that auto mode recovered from a rate limit in one short sentence."))
                    .get(30, TimeUnit.SECONDS);
            assertNotNull(messageId, "Message ID should not be null");

            // Wait for the auto mode switch handler to be invoked
            for (int i = 0; i < 30 && autoModeSwitchRequests.isEmpty(); i++) {
                Thread.sleep(1000);
            }

            assertFalse(autoModeSwitchRequests.isEmpty(), "Should have received auto mode switch requests");

            AutoModeSwitchRequest request = autoModeSwitchRequests.get(0);
            assertEquals("user_weekly_rate_limited", request.getErrorCode());
            assertEquals(1.0, request.getRetryAfterSeconds());
        }
    }

    private CopilotClient createAuthenticatedClient() {
        var env = new HashMap<>(ctx.getEnvironment());
        env.put("COPILOT_DEBUG_GITHUB_API_URL", ctx.getProxyUrl());

        var options = new CopilotClientOptions().setEnvironment(env);
        return ctx.createClient(options);
    }

    private void configureAuthenticatedUser() throws Exception {
        ctx.setCopilotUserByToken(TOKEN, "mode-handler-user", "individual_pro", ctx.getProxyUrl(),
                "https://localhost:1/telemetry", "mode-handler-tracking-id");
    }
}
