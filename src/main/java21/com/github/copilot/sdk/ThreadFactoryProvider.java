/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Java 21+ override that uses virtual threads for the SDK's internal thread
 * creation.
 * <p>
 * This class is placed under {@code META-INF/versions/21/} in the multi-release
 * JAR and replaces the baseline {@code ThreadFactoryProvider} when running on
 * Java 21 or later.
 *
 * @since 0.2.2-java.1
 */
final class ThreadFactoryProvider {

    private static final Logger LOG = Logger.getLogger(ThreadFactoryProvider.class.getName());

    private ThreadFactoryProvider() {
    }

    /**
     * Creates a new virtual thread with the given name and runnable.
     *
     * @param runnable
     *            the task to run
     * @param name
     *            the thread name for debuggability
     * @return the new (unstarted) virtual thread
     */
    static Thread newThread(Runnable runnable, String name) {
        return Thread.ofVirtual().name(name).unstarted(runnable);
    }

    /**
     * Creates a virtual-thread-per-task executor for the JSON-RPC reader loop.
     *
     * @param name
     *            the thread name prefix for debuggability
     * @return a virtual-thread {@link ExecutorService}
     */
    static ExecutorService newSingleThreadExecutor(String name) {
        return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name(name).factory());
    }

    /**
     * Returns {@code true} — this is the virtual-thread overlay.
     *
     * @return {@code true}
     */
    static boolean isVirtualThreads() {
        return true;
    }
}
