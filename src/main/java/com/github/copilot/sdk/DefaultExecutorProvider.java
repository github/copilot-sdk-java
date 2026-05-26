/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

enum DefaultExecutorProvider {
    INSTANCE;

    private final Executor executor = ForkJoinPool.commonPool();

    Executor get() {
        return executor;
    }
}
