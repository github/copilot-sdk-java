/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.copilot.sdk.json.MessageOptions;
import com.github.copilot.sdk.json.PermissionHandler;
import com.github.copilot.sdk.json.ResumeSessionConfig;
import com.github.copilot.sdk.json.SessionConfig;

/**
 * Tests for session configuration features.
 *
 * <p>
 * These tests verify that session config options like instructionDirectories
 * are correctly forwarded to the CLI. Snapshots are stored in
 * test/snapshots/session_config/.
 * </p>
 */
public class SessionConfigTest {

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

    @Test
    void testShouldApplyInstructionDirectoriesOnCreate() throws Exception {
        ctx.configureForTest("session_config", "should_apply_instructiondirectories_on_create");

        Path projectDir = ctx.getWorkDir().resolve("instruction-create-project");
        Path instructionDir = ctx.getWorkDir().resolve("extra-create-instructions");
        Path instructionFilesDir = instructionDir.resolve(".github").resolve("instructions");
        Files.createDirectories(projectDir);
        Files.createDirectories(instructionFilesDir);
        Files.writeString(instructionFilesDir.resolve("extra.instructions.md"),
                "Always include JAVA_CREATE_INSTRUCTION_DIRECTORIES_SENTINEL.");

        try (CopilotClient client = ctx.createClient()) {
            CopilotSession session = client.createSession(new SessionConfig().setWorkingDirectory(projectDir.toString())
                    .setInstructionDirectories(List.of(instructionDir.toString()))
                    .setOnPermissionRequest(PermissionHandler.APPROVE_ALL)).get(30, TimeUnit.SECONDS);

            assertNotNull(session.getSessionId());

            var reply = session.sendAndWait(new MessageOptions().setPrompt("What is 1+1?")).get(30, TimeUnit.SECONDS);

            assertNotNull(reply, "Expected a reply from the assistant");
        }
    }

    @Test
    void testShouldApplyInstructionDirectoriesOnResume() throws Exception {
        ctx.configureForTest("session_config", "should_apply_instructiondirectories_on_resume");

        Path projectDir = ctx.getWorkDir().resolve("instruction-resume-project");
        Path instructionDir = ctx.getWorkDir().resolve("extra-resume-instructions");
        Path instructionFilesDir = instructionDir.resolve(".github").resolve("instructions");
        Files.createDirectories(projectDir);
        Files.createDirectories(instructionFilesDir);
        Files.writeString(instructionFilesDir.resolve("extra.instructions.md"),
                "Always include JAVA_RESUME_INSTRUCTION_DIRECTORIES_SENTINEL.");

        try (CopilotClient client = ctx.createClient()) {
            CopilotSession session1 = client.createSession(new SessionConfig()
                    .setWorkingDirectory(projectDir.toString()).setOnPermissionRequest(PermissionHandler.APPROVE_ALL))
                    .get(30, TimeUnit.SECONDS);

            CopilotSession session2 = client.resumeSession(session1.getSessionId(),
                    new ResumeSessionConfig().setWorkingDirectory(projectDir.toString())
                            .setInstructionDirectories(List.of(instructionDir.toString()))
                            .setOnPermissionRequest(PermissionHandler.APPROVE_ALL))
                    .get(30, TimeUnit.SECONDS);

            assertNotNull(session2.getSessionId());

            var reply = session2.sendAndWait(new MessageOptions().setPrompt("What is 1+1?")).get(30, TimeUnit.SECONDS);

            assertNotNull(reply, "Expected a reply from the assistant");
        }
    }
}
