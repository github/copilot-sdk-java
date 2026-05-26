/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

enum DefaultExecutorProvider {
    INSTANCE;

    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    Executor get() {
        return executor;
    }
}
