/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Test;

class DefaultExecutorProviderTest {

    @Test
    void baseProviderUsesCompletableFutureDefaultExecutor() {
        if (Runtime.version().feature() >= 25) {
            return;
        }

        assertNull(DefaultExecutorProvider.create());
    }

    @Test
    void multiReleaseProviderUsesVirtualThreadsOnJdk25() throws Exception {
        if (Runtime.version().feature() < 25) {
            return;
        }

        Path classes = Path.of("target", "classes");
        Path baseClass = classes.resolve("com/github/copilot/sdk/DefaultExecutorProvider.class");
        Path java25Class = classes.resolve("META-INF/versions/25/com/github/copilot/sdk/DefaultExecutorProvider.class");
        assertTrue(Files.exists(baseClass), "Base DefaultExecutorProvider class must be compiled");
        assertTrue(Files.exists(java25Class), "JDK 25 build must compile the multi-release executor provider");

        Path jar = Files.createTempFile("copilot-sdk-default-executor", ".jar");
        try {
            createProviderJar(jar, baseClass, java25Class);

            try (var loader = new URLClassLoader(new URL[]{jar.toUri().toURL()}, null)) {
                Class<?> provider = Class.forName("com.github.copilot.sdk.DefaultExecutorProvider", true, loader);
                Method create = provider.getDeclaredMethod("create");
                create.setAccessible(true);

                Object result = create.invoke(null);
                assertNotNull(result, "JDK 25 multi-release provider must create a default executor");
                assertTrue(result instanceof Executor, "Default provider must return an Executor");

                var executor = (Executor) result;
                try {
                    var ranOnVirtualThread = new CompletableFuture<Boolean>();
                    executor.execute(() -> ranOnVirtualThread.complete(isCurrentThreadVirtual()));
                    assertTrue(ranOnVirtualThread.get(10, TimeUnit.SECONDS),
                            "JDK 25 default executor must run tasks on virtual threads");
                } finally {
                    if (result instanceof ExecutorService executorService) {
                        executorService.shutdownNow();
                    }
                }
            }
        } finally {
            Files.deleteIfExists(jar);
        }
    }

    private static void createProviderJar(Path jar, Path baseClass, Path java25Class) throws IOException {
        var manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.putValue("Multi-Release", "true");

        try (var out = new JarOutputStream(Files.newOutputStream(jar), manifest)) {
            addEntry(out, baseClass, "com/github/copilot/sdk/DefaultExecutorProvider.class");
            addEntry(out, java25Class, "META-INF/versions/25/com/github/copilot/sdk/DefaultExecutorProvider.class");
        }
    }

    private static void addEntry(JarOutputStream out, Path source, String entryName) throws IOException {
        out.putNextEntry(new JarEntry(entryName));
        Files.copy(source, out);
        out.closeEntry();
    }

    private static boolean isCurrentThreadVirtual() {
        try {
            Method isVirtual = Thread.class.getMethod("isVirtual");
            return (Boolean) isVirtual.invoke(Thread.currentThread());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Thread.isVirtual() is not available", e);
        }
    }
}
