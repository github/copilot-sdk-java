/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides thread factories for the SDK's internal thread creation.
 * <p>
 * On Java 17, this class returns standard platform-thread factories. On Java
 * 21+, the multi-release JAR overlay replaces this class with one that returns
 * virtual-thread factories, giving the SDK lightweight threads for its
 * I/O-bound JSON-RPC communication without any user configuration.
 * <p>
 * The {@link java.util.concurrent.ScheduledExecutorService} used for
 * {@code sendAndWait} timeouts in {@link CopilotSession} is <em>not</em>
 * affected, because the JDK offers no virtual-thread-based scheduled executor.
 *
 * @since 0.2.2-java.1
 */
final class ThreadFactoryProvider {

    private ThreadFactoryProvider() {
    }

    /**
     * Creates a new daemon thread with the given name and runnable.
     *
     * @param runnable
     *            the task to run
     * @param name
     *            the thread name for debuggability
     * @return the new (unstarted) thread
     */
    static Thread newThread(Runnable runnable, String name) {
        Thread t = new Thread(runnable, name);
        t.setDaemon(true);
        return t;
    }

    /**
     * Creates a single-thread executor suitable for the JSON-RPC reader loop.
     *
     * @param name
     *            the thread name for debuggability
     * @return a single-thread {@link ExecutorService}
     */
    static ExecutorService newSingleThreadExecutor(String name) {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, name);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Returns {@code true} when this class uses virtual threads (Java 21+
     * multi-release overlay), {@code false} for platform threads.
     *
     * @return whether virtual threads are in use
     */
    static boolean isVirtualThreads() {
        return false;
    }
}
