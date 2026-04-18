/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

import java.util.concurrent.CompletableFuture;

/**
 * Handler interface for session filesystem operations.
 * <p>
 * Implement this interface to provide a custom filesystem backend for
 * session-scoped file I/O. The handler is called by the SDK when the Copilot
 * CLI routes file operations through the session filesystem provider.
 *
 * <h2>Example Implementation</h2>
 *
 * <pre>{@code
 * class LocalFsHandler implements SessionFsHandler {
 * 	private final String rootDir;
 *
 * 	LocalFsHandler(String sessionId, String baseDir) {
 * 		this.rootDir = baseDir + "/" + sessionId;
 * 	}
 *
 * 	public CompletableFuture<SessionFsReadFileResult> readFile(SessionFsReadFileParams params) {
 * 		var result = new SessionFsReadFileResult();
 * 		result.setContent(Files.readString(Path.of(rootDir + params.getPath())));
 * 		return CompletableFuture.completedFuture(result);
 * 	}
 * 	// ... implement other methods
 * }
 * }</pre>
 *
 * @see SessionFsConfig
 * @see SessionConfig#setCreateSessionFsHandler(java.util.function.Function)
 * @since 1.4.0
 */
public interface SessionFsHandler {

    /** Reads the contents of a file. */
    CompletableFuture<SessionFsReadFileResult> readFile(SessionFsReadFileParams params);

    /** Writes content to a file, creating or overwriting it. */
    CompletableFuture<Void> writeFile(SessionFsWriteFileParams params);

    /** Appends content to a file. */
    CompletableFuture<Void> appendFile(SessionFsAppendFileParams params);

    /** Checks whether a path exists. */
    CompletableFuture<SessionFsExistsResult> exists(SessionFsExistsParams params);

    /** Returns metadata about a file or directory. */
    CompletableFuture<SessionFsStatResult> stat(SessionFsStatParams params);

    /** Creates a directory. */
    CompletableFuture<Void> mkdir(SessionFsMkdirParams params);

    /** Lists the entries in a directory. */
    CompletableFuture<SessionFsReaddirResult> readdir(SessionFsReaddirParams params);

    /** Lists the entries in a directory with type information. */
    CompletableFuture<SessionFsReaddirWithTypesResult> readdirWithTypes(SessionFsReaddirParams params);

    /** Removes a file or directory. */
    CompletableFuture<Void> rm(SessionFsRmParams params);

    /** Renames a file or directory. */
    CompletableFuture<Void> rename(SessionFsRenameParams params);

    /** Copies a file or directory. */
    CompletableFuture<Void> cp(SessionFsCpParams params);

    /** Copies a directory and its contents. */
    CompletableFuture<Void> copyDir(SessionFsCopyDirParams params);

    /** Finds files matching glob patterns. */
    CompletableFuture<SessionFsGlobResult> glob(SessionFsGlobParams params);
}
