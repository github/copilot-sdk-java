/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

final class DefaultExecutorProvider {

    private DefaultExecutorProvider() {
    }

    static Executor create() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
