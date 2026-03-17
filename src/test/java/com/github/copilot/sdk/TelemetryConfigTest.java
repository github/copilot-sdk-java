/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.copilot.sdk.json.CopilotClientOptions;
import com.github.copilot.sdk.json.TelemetryConfig;

/**
 * Unit tests for {@link TelemetryConfig} and its integration with
 * {@link CopilotClientOptions}.
 */
public class TelemetryConfigTest {

    @Test
    void telemetryConfig_defaultValues_areNull() {
        var config = new TelemetryConfig();

        assertNull(config.getOtlpEndpoint());
        assertNull(config.getFilePath());
        assertNull(config.getExporterType());
        assertNull(config.getSourceName());
        assertNull(config.getCaptureContent());
    }

    @Test
    void telemetryConfig_canSetAllProperties() {
        var config = new TelemetryConfig().setOtlpEndpoint("http://localhost:4318").setFilePath("/tmp/traces.json")
                .setExporterType("otlp-http").setSourceName("my-app").setCaptureContent(true);

        assertEquals("http://localhost:4318", config.getOtlpEndpoint());
        assertEquals("/tmp/traces.json", config.getFilePath());
        assertEquals("otlp-http", config.getExporterType());
        assertEquals("my-app", config.getSourceName());
        assertTrue(config.getCaptureContent());
    }

    @Test
    void copilotClientOptions_telemetry_defaultsToNull() {
        var options = new CopilotClientOptions();
        assertNull(options.getTelemetry());
    }

    @Test
    void copilotClientOptions_setTelemetry_roundTrips() {
        var telemetry = new TelemetryConfig().setOtlpEndpoint("http://localhost:4318");
        var options = new CopilotClientOptions().setTelemetry(telemetry);

        assertSame(telemetry, options.getTelemetry());
        assertEquals("http://localhost:4318", options.getTelemetry().getOtlpEndpoint());
    }

    @Test
    void copilotClientOptions_clone_copiesTelemetry() {
        var telemetry = new TelemetryConfig().setOtlpEndpoint("http://localhost:4318").setSourceName("my-app");
        var original = new CopilotClientOptions().setTelemetry(telemetry);

        var clone = original.clone();

        assertNotNull(clone.getTelemetry());
        assertSame(telemetry, clone.getTelemetry(), "Clone should share the same TelemetryConfig reference");
        assertEquals("http://localhost:4318", clone.getTelemetry().getOtlpEndpoint());
        assertEquals("my-app", clone.getTelemetry().getSourceName());
    }

    @Test
    void copilotClientOptions_cloneWithNullTelemetry_isNull() {
        var original = new CopilotClientOptions();

        var clone = original.clone();

        assertNull(clone.getTelemetry());
    }
}
