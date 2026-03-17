/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

package com.github.copilot.sdk.json;

/**
 * OpenTelemetry configuration for the Copilot CLI server.
 * <p>
 * When a non-{@code null} instance is set on
 * {@link CopilotClientOptions#setTelemetry(TelemetryConfig)}, the CLI server is
 * started with OpenTelemetry instrumentation enabled.
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * var telemetry = new TelemetryConfig().setOtlpEndpoint("http://localhost:4318").setExporterType("otlp-http")
 * 		.setSourceName("my-app");
 *
 * var options = new CopilotClientOptions().setTelemetry(telemetry);
 * }</pre>
 *
 * @see CopilotClientOptions#setTelemetry(TelemetryConfig)
 * @since 1.1.0
 */
public class TelemetryConfig {

    private String otlpEndpoint;
    private String filePath;
    private String exporterType;
    private String sourceName;
    private Boolean captureContent;

    /**
     * Gets the OTLP exporter endpoint URL.
     * <p>
     * Maps to the {@code OTEL_EXPORTER_OTLP_ENDPOINT} environment variable.
     *
     * @return the endpoint URL, or {@code null} if not set
     */
    public String getOtlpEndpoint() {
        return otlpEndpoint;
    }

    /**
     * Sets the OTLP exporter endpoint URL.
     * <p>
     * Maps to the {@code OTEL_EXPORTER_OTLP_ENDPOINT} environment variable.
     *
     * @param otlpEndpoint
     *            the endpoint URL (e.g., {@code "http://localhost:4318"})
     * @return this instance for method chaining
     */
    public TelemetryConfig setOtlpEndpoint(String otlpEndpoint) {
        this.otlpEndpoint = otlpEndpoint;
        return this;
    }

    /**
     * Gets the file path for the file exporter.
     * <p>
     * Maps to the {@code COPILOT_OTEL_FILE_EXPORTER_PATH} environment variable.
     *
     * @return the file path, or {@code null} if not set
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path for the file exporter.
     * <p>
     * Maps to the {@code COPILOT_OTEL_FILE_EXPORTER_PATH} environment variable.
     *
     * @param filePath
     *            the output file path
     * @return this instance for method chaining
     */
    public TelemetryConfig setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    /**
     * Gets the exporter type.
     * <p>
     * Maps to the {@code COPILOT_OTEL_EXPORTER_TYPE} environment variable.
     *
     * @return the exporter type (e.g., {@code "otlp-http"} or {@code "file"}), or
     *         {@code null} if not set
     */
    public String getExporterType() {
        return exporterType;
    }

    /**
     * Sets the exporter type.
     * <p>
     * Maps to the {@code COPILOT_OTEL_EXPORTER_TYPE} environment variable.
     *
     * @param exporterType
     *            the exporter type (e.g., {@code "otlp-http"} or {@code "file"})
     * @return this instance for method chaining
     */
    public TelemetryConfig setExporterType(String exporterType) {
        this.exporterType = exporterType;
        return this;
    }

    /**
     * Gets the source name for telemetry spans.
     * <p>
     * Maps to the {@code COPILOT_OTEL_SOURCE_NAME} environment variable.
     *
     * @return the source name, or {@code null} if not set
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Sets the source name for telemetry spans.
     * <p>
     * Maps to the {@code COPILOT_OTEL_SOURCE_NAME} environment variable.
     *
     * @param sourceName
     *            the source name
     * @return this instance for method chaining
     */
    public TelemetryConfig setSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    /**
     * Gets whether to capture message content as part of telemetry.
     * <p>
     * Maps to the {@code OTEL_INSTRUMENTATION_GENAI_CAPTURE_MESSAGE_CONTENT}
     * environment variable.
     *
     * @return {@code true} to capture content, {@code false} to suppress it, or
     *         {@code null} to use the server default
     */
    public Boolean getCaptureContent() {
        return captureContent;
    }

    /**
     * Sets whether to capture message content as part of telemetry.
     * <p>
     * Maps to the {@code OTEL_INSTRUMENTATION_GENAI_CAPTURE_MESSAGE_CONTENT}
     * environment variable.
     *
     * @param captureContent
     *            {@code true} to capture content, {@code false} to suppress it,
     *            {@code null} to use the server default
     * @return this instance for method chaining
     */
    public TelemetryConfig setCaptureContent(Boolean captureContent) {
        this.captureContent = captureContent;
        return this;
    }
}
