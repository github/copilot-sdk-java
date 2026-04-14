/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------------------------------------------*/

/**
 * Java code generator for session-events and RPC types.
 * Generates Java source files under src/generated/java/ from JSON Schema files.
 */

import fs from "fs/promises";
import path from "path";
import { fileURLToPath } from "url";
import type { JSONSchema7 } from "json-schema";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

/** Root of the copilot-sdk-java repo */
const REPO_ROOT = path.resolve(__dirname, "../..");

/** Event types to exclude from generation (internal/legacy types) */
const EXCLUDED_EVENT_TYPES = new Set(["session.import_legacy"]);

const AUTO_GENERATED_HEADER = `// AUTO-GENERATED FILE - DO NOT EDIT`;
const GENERATED_FROM_SESSION_EVENTS = `// Generated from: session-events.schema.json`;
const GENERATED_FROM_API = `// Generated from: api.schema.json`;
const GENERATED_ANNOTATION = `@javax.annotation.processing.Generated("copilot-sdk-codegen")`;
const COPYRIGHT = `/*---------------------------------------------------------------------------------------------\n *  Copyright (c) Microsoft Corporation. All rights reserved.\n *--------------------------------------------------------------------------------------------*/`;

// ── Naming utilities ─────────────────────────────────────────────────────────

function toPascalCase(name: string): string {
    return name.split(/[-_.]/).map((p) => p.charAt(0).toUpperCase() + p.slice(1)).join("");
}

function toJavaClassName(typeName: string): string {
    return typeName.split(/[._]/).map((p) => p.charAt(0).toUpperCase() + p.slice(1)).join("");
}

function toCamelCase(name: string): string {
    const pascal = toPascalCase(name);
    return pascal.charAt(0).toLowerCase() + pascal.slice(1);
}

function toEnumConstant(value: string): string {
    return value.toUpperCase().replace(/[-. ]/g, "_");
}

// ── Schema path resolution ───────────────────────────────────────────────────

async function getSessionEventsSchemaPath(): Promise<string> {
    const candidates = [
        path.join(REPO_ROOT, "scripts/codegen/node_modules/@github/copilot/schemas/session-events.schema.json"),
        path.join(REPO_ROOT, "nodejs/node_modules/@github/copilot/schemas/session-events.schema.json"),
    ];
    for (const p of candidates) {
        try {
            await fs.access(p);
            return p;
        } catch {
            // try next
        }
    }
    throw new Error("session-events.schema.json not found. Run 'npm ci' in scripts/codegen first.");
}

async function getApiSchemaPath(): Promise<string> {
    const candidates = [
        path.join(REPO_ROOT, "scripts/codegen/node_modules/@github/copilot/schemas/api.schema.json"),
        path.join(REPO_ROOT, "nodejs/node_modules/@github/copilot/schemas/api.schema.json"),
    ];
    for (const p of candidates) {
        try {
            await fs.access(p);
            return p;
        } catch {
            // try next
        }
    }
    throw new Error("api.schema.json not found. Run 'npm ci' in scripts/codegen first.");
}

// ── File writing ─────────────────────────────────────────────────────────────

async function writeGeneratedFile(relativePath: string, content: string): Promise<string> {
    const fullPath = path.join(REPO_ROOT, relativePath);
    await fs.mkdir(path.dirname(fullPath), { recursive: true });
    await fs.writeFile(fullPath, content, "utf-8");
    console.log(`  ✓ ${relativePath}`);
    return fullPath;
}

// ── Java type mapping ─────────────────────────────────────────────────────────

interface JavaTypeResult {
    javaType: string;
    imports: Set<string>;
}

function schemaTypeToJava(
    schema: JSONSchema7,
    required: boolean,
    context: string,
    propName: string,
    nestedTypes: Map<string, JavaClassDef>
): JavaTypeResult {
    const imports = new Set<string>();

    if (schema.anyOf) {
        const hasNull = schema.anyOf.some((s) => typeof s === "object" && (s as JSONSchema7).type === "null");
        const nonNull = schema.anyOf.filter((s) => typeof s === "object" && (s as JSONSchema7).type !== "null");
        if (nonNull.length === 1) {
            const result = schemaTypeToJava(nonNull[0] as JSONSchema7, required && !hasNull, context, propName, nestedTypes);
            return result;
        }
        // When exactly two non-null types and one of them is string, prefer String
        // over Object to avoid unnecessary type erasure on common wire-level unions
        // (e.g., string | null, string | boolean).  For wider unions keep Object.
        if (nonNull.length === 2) {
            const hasString = nonNull.some((s) => typeof s === "object" && (s as JSONSchema7).type === "string");
            if (hasString) {
                return { javaType: "String", imports };
            }
        }
        console.warn(`[codegen] ${context}.${propName}: anyOf with ${nonNull.length} non-null branches — falling back to Object`);
        return { javaType: "Object", imports };
    }

    if (schema.type === "string") {
        if (schema.format === "uuid") {
            imports.add("java.util.UUID");
            return { javaType: "UUID", imports };
        }
        if (schema.format === "date-time") {
            imports.add("java.time.OffsetDateTime");
            return { javaType: "OffsetDateTime", imports };
        }
        if (schema.enum && Array.isArray(schema.enum)) {
            const enumName = `${context}${toPascalCase(propName)}`;
            nestedTypes.set(enumName, {
                kind: "enum",
                name: enumName,
                values: schema.enum as string[],
                description: schema.description,
            });
            return { javaType: enumName, imports };
        }
        return { javaType: "String", imports };
    }

    if (Array.isArray(schema.type)) {
        const nonNullTypes = schema.type.filter((t) => t !== "null");
        if (nonNullTypes.length === 1) {
            const baseSchema = { ...schema, type: nonNullTypes[0] };
            return schemaTypeToJava(baseSchema as JSONSchema7, required, context, propName, nestedTypes);
        }
    }

    if (schema.type === "integer") {
        // JSON Schema "integer" maps to Long (boxed — always used for records).
        // Use primitive long for required fields in mutable-bean contexts if needed.
        return { javaType: required ? "long" : "Long", imports };
    }

    if (schema.type === "number") {
        return { javaType: required ? "double" : "Double", imports };
    }

    if (schema.type === "boolean") {
        return { javaType: required ? "boolean" : "Boolean", imports };
    }

    if (schema.type === "array") {
        const items = schema.items as JSONSchema7 | undefined;
        if (items) {
            const itemResult = schemaTypeToJava(items, true, context, propName + "Item", nestedTypes);
            imports.add("java.util.List");
            for (const imp of itemResult.imports) imports.add(imp);
            return { javaType: `List<${itemResult.javaType}>`, imports };
        }
        imports.add("java.util.List");
        console.warn(`[codegen] ${context}.${propName}: array without typed items — falling back to List<Object>`);
        return { javaType: "List<Object>", imports };
    }

    if (schema.type === "object") {
        if (schema.properties && Object.keys(schema.properties).length > 0) {
            const nestedName = `${context}${toPascalCase(propName)}`;
            if (!nestedTypes.has(nestedName)) {
                nestedTypes.set(nestedName, {
                    kind: "class",
                    name: nestedName,
                    schema,
                    description: schema.description,
                });
            }
            return { javaType: nestedName, imports };
        }
        if (schema.additionalProperties) {
            const valueSchema = typeof schema.additionalProperties === "object"
                ? schema.additionalProperties as JSONSchema7
                : { type: "object" } as JSONSchema7;
            const valueResult = schemaTypeToJava(valueSchema, true, context, propName + "Value", nestedTypes);
            imports.add("java.util.Map");
            for (const imp of valueResult.imports) imports.add(imp);
            return { javaType: `Map<String, ${valueResult.javaType}>`, imports };
        }
        imports.add("java.util.Map");
        console.warn(`[codegen] ${context}.${propName}: object without typed properties or additionalProperties — falling back to Map<String, Object>`);
        return { javaType: "Map<String, Object>", imports };
    }

    if (schema.$ref) {
        const refName = schema.$ref.split("/").pop()!;
        return { javaType: refName, imports };
    }

    console.warn(`[codegen] ${context}.${propName}: unrecognized schema (type=${JSON.stringify(schema.type)}) — falling back to Object`);
    return { javaType: "Object", imports };
}

// ── Class definitions ─────────────────────────────────────────────────────────

interface JavaClassDef {
    kind: "class" | "enum";
    name: string;
    description?: string;
    schema?: JSONSchema7;
    values?: string[];  // for enum
}

// ── Session Events codegen ────────────────────────────────────────────────────

interface EventVariant {
    typeName: string;
    className: string;
    dataSchema: JSONSchema7 | null;
    description?: string;
}

function extractEventVariants(schema: JSONSchema7): EventVariant[] {
    const sessionEvent = (schema.definitions as Record<string, JSONSchema7>)?.SessionEvent;
    if (!sessionEvent?.anyOf) throw new Error("Schema must have SessionEvent definition with anyOf");

    return (sessionEvent.anyOf as JSONSchema7[])
        .map((variant) => {
            const typeSchema = variant.properties?.type as JSONSchema7;
            const typeName = typeSchema?.const as string;
            if (!typeName) throw new Error("Variant must have type.const");
            const baseName = toJavaClassName(typeName);
            const dataSchema = variant.properties?.data as JSONSchema7 | undefined;
            return {
                typeName,
                className: `${baseName}Event`,
                dataSchema: dataSchema ?? null,
                description: variant.description,
            };
        })
        .filter((v) => !EXCLUDED_EVENT_TYPES.has(v.typeName));
}

async function generateSessionEvents(schemaPath: string): Promise<void> {
    console.log("\n📋 Generating session event classes...");
    const schemaContent = await fs.readFile(schemaPath, "utf-8");
    const schema = JSON.parse(schemaContent) as JSONSchema7;

    const variants = extractEventVariants(schema);
    const packageName = "com.github.copilot.sdk.generated";
    const packageDir = `src/generated/java/com/github/copilot/sdk/generated`;

    // Generate base SessionEvent class
    await generateSessionEventBaseClass(variants, packageName, packageDir);

    // Generate one class file per event variant
    for (const variant of variants) {
        await generateEventVariantClass(variant, packageName, packageDir);
    }

    console.log(`✅ Generated ${variants.length + 1} session event files`);
}

async function generateSessionEventBaseClass(
    variants: EventVariant[],
    packageName: string,
    packageDir: string
): Promise<void> {
    const lines: string[] = [];
    lines.push(COPYRIGHT);
    lines.push("");
    lines.push(AUTO_GENERATED_HEADER);
    lines.push(GENERATED_FROM_SESSION_EVENTS);
    lines.push("");
    lines.push(`package ${packageName};`);
    lines.push("");
    lines.push(`import com.fasterxml.jackson.annotation.JsonIgnoreProperties;`);
    lines.push(`import com.fasterxml.jackson.annotation.JsonInclude;`);
    lines.push(`import com.fasterxml.jackson.annotation.JsonProperty;`);
    lines.push(`import com.fasterxml.jackson.annotation.JsonSubTypes;`);
    lines.push(`import com.fasterxml.jackson.annotation.JsonTypeInfo;`);
    lines.push(`import java.time.OffsetDateTime;`);
    lines.push(`import java.util.UUID;`);
    lines.push(`import javax.annotation.processing.Generated;`);
    lines.push("");
    lines.push(`/**`);
    lines.push(` * Base class for all generated session events.`);
    lines.push(` *`);
    lines.push(` * @since 1.0.0`);
    lines.push(` */`);
    lines.push(`@JsonIgnoreProperties(ignoreUnknown = true)`);
    lines.push(`@JsonInclude(JsonInclude.Include.NON_NULL)`);
    lines.push(`@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = UnknownSessionEvent.class)`);
    lines.push(`@JsonSubTypes({`);
    for (let i = 0; i < variants.length; i++) {
        const v = variants[i];
        const comma = i < variants.length - 1 ? "," : "";
        lines.push(`    @JsonSubTypes.Type(value = ${v.className}.class, name = "${v.typeName}")${comma}`);
    }
    lines.push(`})`);
    lines.push(GENERATED_ANNOTATION);

    // Build the permits clause (all variant classes + UnknownSessionEvent last)
    const allPermitted = [...variants.map((v) => v.className), "UnknownSessionEvent"];
    lines.push(`public abstract sealed class SessionEvent permits`);
    for (let i = 0; i < allPermitted.length; i++) {
        const comma = i < allPermitted.length - 1 ? "," : " {";
        lines.push(`        ${allPermitted[i]}${comma}`);
    }
    lines.push("");
    lines.push(`    /** Unique event identifier (UUID v4), generated when the event is emitted. */`);
    lines.push(`    @JsonProperty("id")`);
    lines.push(`    private UUID id;`);
    lines.push("");
    lines.push(`    /** ISO 8601 timestamp when the event was created. */`);
    lines.push(`    @JsonProperty("timestamp")`);
    lines.push(`    private OffsetDateTime timestamp;`);
    lines.push("");
    lines.push(`    /** ID of the chronologically preceding event in the session. Null for the first event. */`);
    lines.push(`    @JsonProperty("parentId")`);
    lines.push(`    private UUID parentId;`);
    lines.push("");
    lines.push(`    /** When true, the event is transient and not persisted to the session event log on disk. */`);
    lines.push(`    @JsonProperty("ephemeral")`);
    lines.push(`    private Boolean ephemeral;`);
    lines.push("");
    lines.push(`    /**`);
    lines.push(`     * Returns the event-type discriminator string (e.g., {@code "session.idle"}).`);
    lines.push(`     *`);
    lines.push(`     * @return the event type`);
    lines.push(`     */`);
    lines.push(`    public abstract String getType();`);
    lines.push("");
    lines.push(`    public UUID getId() { return id; }`);
    lines.push(`    public void setId(UUID id) { this.id = id; }`);
    lines.push("");
    lines.push(`    public OffsetDateTime getTimestamp() { return timestamp; }`);
    lines.push(`    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }`);
    lines.push("");
    lines.push(`    public UUID getParentId() { return parentId; }`);
    lines.push(`    public void setParentId(UUID parentId) { this.parentId = parentId; }`);
    lines.push("");
    lines.push(`    public Boolean getEphemeral() { return ephemeral; }`);
    lines.push(`    public void setEphemeral(Boolean ephemeral) { this.ephemeral = ephemeral; }`);
    lines.push(`}`);
    lines.push("");

    await writeGeneratedFile(`${packageDir}/SessionEvent.java`, lines.join("\n"));

    // Also generate the UnknownSessionEvent fallback
    await generateUnknownEventClass(packageName, packageDir);
}

async function generateUnknownEventClass(packageName: string, packageDir: string): Promise<void> {
    const lines: string[] = [];
    lines.push(COPYRIGHT);
    lines.push("");
    lines.push(AUTO_GENERATED_HEADER);
    lines.push(GENERATED_FROM_SESSION_EVENTS);
    lines.push("");
    lines.push(`package ${packageName};`);
    lines.push("");
    lines.push(`import com.fasterxml.jackson.annotation.JsonIgnoreProperties;`);
    lines.push(`import javax.annotation.processing.Generated;`);
    lines.push("");
    lines.push(`/**`);
    lines.push(` * Fallback for event types not yet known to this SDK version.`);
    lines.push(` *`);
    lines.push(` * @since 1.0.0`);
    lines.push(` */`);
    lines.push(`@JsonIgnoreProperties(ignoreUnknown = true)`);
    lines.push(GENERATED_ANNOTATION);
    lines.push(`public final class UnknownSessionEvent extends SessionEvent {`);
    lines.push("");
    lines.push(`    @Override`);
    lines.push(`    public String getType() { return "unknown"; }`);
    lines.push(`}`);
    lines.push("");

    await writeGeneratedFile(`${packageDir}/UnknownSessionEvent.java`, lines.join("\n"));
}

/** Render a nested type (enum or record) indented at the given level. */
function renderNestedType(nested: JavaClassDef, indentLevel: number, nestedTypes: Map<string, JavaClassDef>, allImports: Set<string>): string[] {
    const ind = "    ".repeat(indentLevel);
    const lines: string[] = [];

    if (nested.kind === "enum") {
        lines.push("");
        if (nested.description) {
            lines.push(`${ind}/** ${nested.description} */`);
        }
        lines.push(`${ind}public enum ${nested.name} {`);
        for (let i = 0; i < (nested.values || []).length; i++) {
            const v = nested.values![i];
            const comma = i < nested.values!.length - 1 ? "," : ";";
            lines.push(`${ind}    /** The {@code ${v}} variant. */`);
            lines.push(`${ind}    ${toEnumConstant(v)}("${v}")${comma}`);
        }
        lines.push("");
        lines.push(`${ind}    private final String value;`);
        lines.push(`${ind}    ${nested.name}(String value) { this.value = value; }`);
        lines.push(`${ind}    @com.fasterxml.jackson.annotation.JsonValue`);
        lines.push(`${ind}    public String getValue() { return value; }`);
        lines.push(`${ind}    @com.fasterxml.jackson.annotation.JsonCreator`);
        lines.push(`${ind}    public static ${nested.name} fromValue(String value) {`);
        lines.push(`${ind}        for (${nested.name} v : values()) {`);
        lines.push(`${ind}            if (v.value.equals(value)) return v;`);
        lines.push(`${ind}        }`);
        lines.push(`${ind}        throw new IllegalArgumentException("Unknown ${nested.name} value: " + value);`);
        lines.push(`${ind}    }`);
        lines.push(`${ind}}`);
    } else if (nested.kind === "class" && nested.schema?.properties) {
        const localNestedTypes = new Map<string, JavaClassDef>();
        const requiredSet = new Set(nested.schema.required || []);
        const fields: { jsonName: string; javaName: string; javaType: string; description?: string }[] = [];

        for (const [propName, propSchema] of Object.entries(nested.schema.properties)) {
            if (typeof propSchema !== "object") continue;
            const prop = propSchema as JSONSchema7;
            // Record components are always boxed (nullable by design).
            const result = schemaTypeToJava(prop, false, nested.name, propName, localNestedTypes);
            for (const imp of result.imports) allImports.add(imp);
            fields.push({ jsonName: propName, javaName: toCamelCase(propName), javaType: result.javaType, description: prop.description });
        }

        lines.push("");
        if (nested.description) {
            lines.push(`${ind}/** ${nested.description} */`);
        }
        lines.push(`${ind}@JsonIgnoreProperties(ignoreUnknown = true)`);
        lines.push(`${ind}@JsonInclude(JsonInclude.Include.NON_NULL)`);
        if (fields.length === 0) {
            lines.push(`${ind}public record ${nested.name}() {`);
        } else {
            lines.push(`${ind}public record ${nested.name}(`);
            for (let i = 0; i < fields.length; i++) {
                const f = fields[i];
                const comma = i < fields.length - 1 ? "," : "";
                if (f.description) lines.push(`${ind}    /** ${f.description} */`);
                lines.push(`${ind}    @JsonProperty("${f.jsonName}") ${f.javaType} ${f.javaName}${comma}`);
            }
            lines.push(`${ind}) {`);
        }
        // Render any further nested types inside this record
        for (const [, localNested] of localNestedTypes) {
            lines.push(...renderNestedType(localNested, indentLevel + 1, nestedTypes, allImports));
        }
        if (lines[lines.length - 1] !== "") lines.push("");
        lines.pop(); // remove trailing blank before closing brace
        lines.push(`${ind}}`);
    }

    return lines;
}

async function generateEventVariantClass(
    variant: EventVariant,
    packageName: string,
    packageDir: string
): Promise<void> {
    const lines: string[] = [];
    const allImports = new Set<string>([
        "com.fasterxml.jackson.annotation.JsonIgnoreProperties",
        "com.fasterxml.jackson.annotation.JsonProperty",
        "com.fasterxml.jackson.annotation.JsonInclude",
        "javax.annotation.processing.Generated",
    ]);
    const nestedTypes = new Map<string, JavaClassDef>();

    // Collect data record fields
    interface FieldInfo {
        jsonName: string;
        javaName: string;
        javaType: string;
        description?: string;
    }

    const dataFields: FieldInfo[] = [];

    if (variant.dataSchema?.properties) {
        for (const [propName, propSchema] of Object.entries(variant.dataSchema.properties)) {
            if (typeof propSchema !== "object") continue;
            const prop = propSchema as JSONSchema7;
            // Record components are always boxed (nullable by design).
            const result = schemaTypeToJava(prop, false, `${variant.className}Data`, propName, nestedTypes);
            for (const imp of result.imports) allImports.add(imp);
            dataFields.push({
                jsonName: propName,
                javaName: toCamelCase(propName),
                javaType: result.javaType,
                description: prop.description,
            });
        }
    }

    // Whether a data record should be emitted (always when dataSchema is present)
    const hasDataSchema = variant.dataSchema !== null;

    // Build the file
    lines.push(COPYRIGHT);
    lines.push("");
    lines.push(AUTO_GENERATED_HEADER);
    lines.push(GENERATED_FROM_SESSION_EVENTS);
    lines.push("");
    lines.push(`package ${packageName};`);
    lines.push("");

    // Placeholder for imports
    const importPlaceholderIdx = lines.length;
    lines.push("__IMPORTS__");
    lines.push("");

    if (variant.description) {
        lines.push(`/**`);
        lines.push(` * ${variant.description}`);
        lines.push(` *`);
        lines.push(` * @since 1.0.0`);
        lines.push(` */`);
    } else {
        lines.push(`/**`);
        lines.push(` * The {@code ${variant.typeName}} session event.`);
        lines.push(` *`);
        lines.push(` * @since 1.0.0`);
        lines.push(` */`);
    }
    lines.push(`@JsonIgnoreProperties(ignoreUnknown = true)`);
    lines.push(`@JsonInclude(JsonInclude.Include.NON_NULL)`);
    lines.push(GENERATED_ANNOTATION);
    lines.push(`public final class ${variant.className} extends SessionEvent {`);
    lines.push("");
    lines.push(`    @Override`);
    lines.push(`    public String getType() { return "${variant.typeName}"; }`);

    if (hasDataSchema) {
        lines.push("");
        lines.push(`    @JsonProperty("data")`);
        lines.push(`    private ${variant.className}Data data;`);
        lines.push("");
        lines.push(`    public ${variant.className}Data getData() { return data; }`);
        lines.push(`    public void setData(${variant.className}Data data) { this.data = data; }`);
        lines.push("");
        // Generate data inner record
        lines.push(`    /** Data payload for {@link ${variant.className}}. */`);
        lines.push(`    @JsonIgnoreProperties(ignoreUnknown = true)`);
        lines.push(`    @JsonInclude(JsonInclude.Include.NON_NULL)`);
        if (dataFields.length === 0) {
            lines.push(`    public record ${variant.className}Data() {`);
        } else {
            lines.push(`    public record ${variant.className}Data(`);
            for (let i = 0; i < dataFields.length; i++) {
                const field = dataFields[i];
                const comma = i < dataFields.length - 1 ? "," : "";
                if (field.description) {
                    lines.push(`        /** ${field.description} */`);
                }
                lines.push(`        @JsonProperty("${field.jsonName}") ${field.javaType} ${field.javaName}${comma}`);
            }
            lines.push(`    ) {`);
        }
        // Render nested types inside Data record
        for (const [, nested] of nestedTypes) {
            lines.push(...renderNestedType(nested, 2, nestedTypes, allImports));
        }
        if (nestedTypes.size > 0 && lines[lines.length - 1] === "") lines.pop();
        lines.push(`    }`);
    }

    lines.push(`}`);
    lines.push("");

    // Replace import placeholder
    const sortedImports = [...allImports].sort();
    const importLines = sortedImports.map((i) => `import ${i};`).join("\n");
    lines[importPlaceholderIdx] = importLines;

    await writeGeneratedFile(`${packageDir}/${variant.className}.java`, lines.join("\n"));
}

// ── RPC types codegen ─────────────────────────────────────────────────────────

interface RpcMethod {
    rpcMethod: string;
    params: JSONSchema7 | null;
    result: JSONSchema7 | null;
    stability?: string;
}

function isRpcMethod(node: unknown): node is RpcMethod {
    return typeof node === "object" && node !== null && "rpcMethod" in node;
}

function collectRpcMethods(node: Record<string, unknown>): [string, RpcMethod][] {
    const results: [string, RpcMethod][] = [];
    for (const [key, value] of Object.entries(node)) {
        if (isRpcMethod(value)) {
            results.push([key, value]);
        } else if (typeof value === "object" && value !== null) {
            results.push(...collectRpcMethods(value as Record<string, unknown>));
        }
    }
    return results;
}

/** Convert an RPC method name to a Java class name prefix (e.g., "models.list" -> "ModelsList") */
function rpcMethodToClassName(rpcMethod: string): string {
    return rpcMethod.split(/[._-]/).map((p) => p.charAt(0).toUpperCase() + p.slice(1)).join("");
}

/** Generate a Java record for a JSON Schema object type. Returns the class content. */
function generateRpcClass(
    className: string,
    schema: JSONSchema7,
    _nestedTypes: Map<string, { code: string }>,
    _packageName: string,
    visibility: "public" | "internal" = "public"
): { code: string; imports: Set<string> } {
    const imports = new Set<string>();
    const localNestedTypes = new Map<string, JavaClassDef>();
    const lines: string[] = [];
    const visModifier = visibility === "public" ? "public " : "";

    const properties = Object.entries(schema.properties || {});
    const fields = properties.flatMap(([propName, propSchema]) => {
        if (typeof propSchema !== "object") return [];
        const prop = propSchema as JSONSchema7;
        // Record components are always boxed (nullable by design).
        const result = schemaTypeToJava(prop, false, className, propName, localNestedTypes);
        for (const imp of result.imports) imports.add(imp);
        return [{ propName, javaName: toCamelCase(propName), javaType: result.javaType, description: prop.description }];
    });

    lines.push(`@JsonInclude(JsonInclude.Include.NON_NULL)`);
    lines.push(`@JsonIgnoreProperties(ignoreUnknown = true)`);
    if (fields.length === 0) {
        lines.push(`${visModifier}record ${className}() {`);
    } else {
        lines.push(`${visModifier}record ${className}(`);
        for (let i = 0; i < fields.length; i++) {
            const f = fields[i];
            const comma = i < fields.length - 1 ? "," : "";
            if (f.description) {
                lines.push(`    /** ${f.description} */`);
            }
            lines.push(`    @JsonProperty("${f.propName}") ${f.javaType} ${f.javaName}${comma}`);
        }
        lines.push(`) {`);
    }

    // Add nested types as nested records/enums inside this record
    for (const [, nested] of localNestedTypes) {
        lines.push(...renderNestedType(nested, 1, new Map(), imports));
    }

    if (localNestedTypes.size > 0 && lines[lines.length - 1] === "") lines.pop();
    lines.push(`}`);

    return { code: lines.join("\n"), imports };
}

async function generateRpcTypes(schemaPath: string): Promise<void> {
    console.log("\n🔌 Generating RPC types...");
    const schemaContent = await fs.readFile(schemaPath, "utf-8");
    const schema = JSON.parse(schemaContent) as Record<string, unknown> & {
        server?: Record<string, unknown>;
        session?: Record<string, unknown>;
        clientSession?: Record<string, unknown>;
    };

    const packageName = "com.github.copilot.sdk.generated.rpc";
    const packageDir = `src/generated/java/com/github/copilot/sdk/generated/rpc`;

    // Collect all RPC methods from all sections
    const sections: [string, Record<string, unknown>][] = [];
    if (schema.server) sections.push(["server", schema.server]);
    if (schema.session) sections.push(["session", schema.session]);
    if (schema.clientSession) sections.push(["clientSession", schema.clientSession]);

    const generatedClasses = new Map<string, boolean>();
    const allFiles: string[] = [];

    for (const [, sectionNode] of sections) {
        const methods = collectRpcMethods(sectionNode);
        for (const [, method] of methods) {
            const className = rpcMethodToClassName(method.rpcMethod);

            // Generate params class
            if (method.params && typeof method.params === "object" && (method.params as JSONSchema7).properties) {
                const paramsClassName = `${className}Params`;
                if (!generatedClasses.has(paramsClassName)) {
                    generatedClasses.set(paramsClassName, true);
                    allFiles.push(await generateRpcDataClass(paramsClassName, method.params as JSONSchema7, packageName, packageDir, method.rpcMethod, "params"));
                }
            }

            // Generate result class
            if (method.result && typeof method.result === "object" && (method.result as JSONSchema7).properties) {
                const resultClassName = `${className}Result`;
                if (!generatedClasses.has(resultClassName)) {
                    generatedClasses.set(resultClassName, true);
                    allFiles.push(await generateRpcDataClass(resultClassName, method.result as JSONSchema7, packageName, packageDir, method.rpcMethod, "result"));
                }
            }
        }
    }

    console.log(`✅ Generated ${allFiles.length} RPC type files`);
}

async function generateRpcDataClass(
    className: string,
    schema: JSONSchema7,
    packageName: string,
    packageDir: string,
    rpcMethod: string,
    kind: "params" | "result"
): Promise<string> {
    const nestedTypes = new Map<string, { code: string }>();
    const { code, imports } = generateRpcClass(className, schema, nestedTypes, packageName);

    const lines: string[] = [];
    lines.push(COPYRIGHT);
    lines.push("");
    lines.push(AUTO_GENERATED_HEADER);
    lines.push(GENERATED_FROM_API);
    lines.push("");
    lines.push(`package ${packageName};`);
    lines.push("");

    const allImports = new Set<string>([
        "com.fasterxml.jackson.annotation.JsonIgnoreProperties",
        "com.fasterxml.jackson.annotation.JsonProperty",
        "com.fasterxml.jackson.annotation.JsonInclude",
        "javax.annotation.processing.Generated",
        ...imports,
    ]);
    const sortedImports = [...allImports].sort();
    for (const imp of sortedImports) {
        lines.push(`import ${imp};`);
    }
    lines.push("");

    if (schema.description) {
        lines.push(`/**`);
        lines.push(` * ${schema.description}`);
        lines.push(` *`);
        lines.push(` * @since 1.0.0`);
        lines.push(` */`);
    } else {
        lines.push(`/**`);
        lines.push(` * ${kind === "params" ? "Request parameters" : "Result"} for the {@code ${rpcMethod}} RPC method.`);
        lines.push(` *`);
        lines.push(` * @since 1.0.0`);
        lines.push(` */`);
    }
    lines.push(GENERATED_ANNOTATION);
    lines.push(code);
    lines.push("");

    await writeGeneratedFile(`${packageDir}/${className}.java`, lines.join("\n"));
    return className;
}

// ── Main entry point ──────────────────────────────────────────────────────────

async function main(): Promise<void> {
    console.log("🚀 Java SDK code generator");
    console.log("============================");

    const sessionEventsSchemaPath = await getSessionEventsSchemaPath();
    console.log(`📄 Session events schema: ${sessionEventsSchemaPath}`);
    const apiSchemaPath = await getApiSchemaPath();
    console.log(`📄 API schema: ${apiSchemaPath}`);

    await generateSessionEvents(sessionEventsSchemaPath);
    await generateRpcTypes(apiSchemaPath);

    console.log("\n✅ Java code generation complete!");
}

main().catch((err) => {
    console.error("❌ Code generation failed:", err);
    process.exit(1);
});
