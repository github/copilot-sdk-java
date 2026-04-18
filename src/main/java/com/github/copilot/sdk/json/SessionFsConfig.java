/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

/**
 * Configuration for a custom session filesystem provider.
 * <p>
 * When set on {@link com.github.copilot.sdk.json.CopilotClientOptions}, the
 * client registers as the session filesystem provider on connect, routing
 * session-scoped file I/O through per-session handlers created by the
 * {@code createSessionFsHandler} function in {@link SessionConfig} or
 * {@link ResumeSessionConfig}.
 *
 * @see com.github.copilot.sdk.json.CopilotClientOptions#setSessionFs(SessionFsConfig)
 * @see SessionConfig#setCreateSessionFsHandler(java.util.function.Function)
 * @since 1.4.0
 */
public class SessionFsConfig {

    private String initialCwd;
    private String sessionStatePath;
    private SessionFsConventions conventions;

    /**
     * Returns the initial working directory for sessions (user's project
     * directory).
     */
    public String getInitialCwd() {
        return initialCwd;
    }

    /**
     * Sets the initial working directory for sessions.
     *
     * @param initialCwd
     *            the initial working directory
     * @return this instance for method chaining
     */
    public SessionFsConfig setInitialCwd(String initialCwd) {
        this.initialCwd = initialCwd;
        return this;
    }

    /**
     * Returns the path within each session's SessionFs where the runtime stores
     * session-scoped files (events, workspace, checkpoints, and temp files).
     */
    public String getSessionStatePath() {
        return sessionStatePath;
    }

    /**
     * Sets the path within each session's SessionFs where the runtime stores
     * session-scoped files.
     *
     * @param sessionStatePath
     *            the session state path
     * @return this instance for method chaining
     */
    public SessionFsConfig setSessionStatePath(String sessionStatePath) {
        this.sessionStatePath = sessionStatePath;
        return this;
    }

    /**
     * Returns the path conventions used by this filesystem provider.
     */
    public SessionFsConventions getConventions() {
        return conventions;
    }

    /**
     * Sets the path conventions used by this filesystem provider.
     *
     * @param conventions
     *            the path conventions
     * @return this instance for method chaining
     */
    public SessionFsConfig setConventions(SessionFsConventions conventions) {
        this.conventions = conventions;
        return this;
    }
}
