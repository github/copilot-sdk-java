/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ThreadFactoryProvider}, verifying that the factory methods
 * produce working threads and executors regardless of the Java version.
 */
class ThreadFactoryProviderTest {

    @Test
    void newThreadCreatesNamedThread() {
        var ran = new AtomicReference<String>();
        Thread t = ThreadFactoryProvider.newThread(() -> ran.set(Thread.currentThread().getName()), "test-thread");
        assertNotNull(t);
        assertEquals("test-thread", t.getName());
        t.start();
        try {
            t.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertEquals("test-thread", ran.get());
    }

    @Test
    void newSingleThreadExecutorRunsTask() throws Exception {
        ExecutorService executor = ThreadFactoryProvider.newSingleThreadExecutor("test-executor");
        try {
            var ran = new AtomicReference<Boolean>(false);
            executor.submit(() -> ran.set(true)).get(5, TimeUnit.SECONDS);
            assertTrue(ran.get());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void isVirtualThreadsReturnsBoolean() {
        // Unit tests run against exploded classes rather than the packaged
        // multi-release JAR, so Java 21+ may still load the base implementation
        // and report false here. Verify only behavior that does not depend on
        // multi-release class selection.
        boolean result = ThreadFactoryProvider.isVirtualThreads();
        int javaVersion = Runtime.version().feature();
        if (javaVersion < 21) {
            assertFalse(result, "Expected platform threads on Java < 21");
        } else if (result) {
            assertTrue(javaVersion >= 21, "Virtual threads are only supported on Java 21+");
        }
    }
}
