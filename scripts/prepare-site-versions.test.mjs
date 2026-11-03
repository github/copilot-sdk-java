import assert from "node:assert/strict";
import { cpSync, mkdtempSync, readFileSync, rmSync } from "node:fs";
import { tmpdir } from "node:os";
import { join, resolve } from "node:path";
import test from "node:test";
import { prepareSiteVersions } from "./prepare-site-versions.mjs";

function fixture(t) {
    const root = mkdtempSync(join(tmpdir(), "copilot-java-site-"));
    t.after(() => rmSync(root, { recursive: true, force: true }));
    const markdown = join(root, "markdown");
    cpSync(resolve("src/site/markdown"), markdown, { recursive: true });
    return markdown;
}

test("renders preview dependency coordinates and snapshot repositories", (t) => {
    const markdown = fixture(t);
    prepareSiteVersions("1.0.15-preview.2", "1.0.15-SNAPSHOT", markdown);

    const gettingStarted = readFileSync(join(markdown, "getting-started.md"), "utf8");
    assert.match(gettingStarted, /<version>\$\{artifact\.version\}<\/version>/);
    assert.match(gettingStarted, /implementation 'com\.github:copilot-sdk-java:\$\{artifact\.version\}'/);
    assert.match(gettingStarted, /<releases><enabled>false<\/enabled><\/releases>/);
    assert.match(gettingStarted, /mavenContent \{ snapshotsOnly\(\) \}/);
    assert.match(
        gettingStarted,
        /\/\/REPOS central,central-snapshots=https:\/\/central\.sonatype\.com\/repository\/maven-snapshots\//
    );
    const cookbook = readFileSync(join(markdown, "cookbook", "managing-local-files.md"), "utf8");
    assert.match(cookbook, /copilot-sdk-java:1\.0\.15-SNAPSHOT/);
    for (const match of cookbook.matchAll(/\/\/DEPS com\.github:copilot-sdk-java:1\.0\.15-SNAPSHOT/g)) {
        assert.equal(
            cookbook.slice(0, match.index).trimEnd().split("\n").at(-1),
            "//REPOS central,central-snapshots=https://central.sonatype.com/repository/maven-snapshots/"
        );
    }

    const cookbookContent = readFileSync(join(markdown, "cookbook", "error-handling.md"), "utf8");
    assert.equal(
        cookbookContent.match(/\/\/REPOS central,central-snapshots=/g)?.length,
        cookbookContent.match(/\/\/DEPS com\.github:copilot-sdk-java:1\.0\.15-SNAPSHOT/g)?.length
    );
});

test("keeps stable dependency rendering free of snapshot configuration", (t) => {
    const markdown = fixture(t);
    prepareSiteVersions("1.0.15", "1.0.15", markdown);

    const gettingStarted = readFileSync(join(markdown, "getting-started.md"), "utf8");
    assert.doesNotMatch(gettingStarted, /maven-snapshots/);
    assert.doesNotMatch(gettingStarted, /ARTIFACT_/);
    const cookbook = readFileSync(join(markdown, "cookbook", "managing-local-files.md"), "utf8");
    assert.match(cookbook, /copilot-sdk-java:1\.0\.15/);
});

test("rejects an artifact version from another shared base", (t) => {
    const markdown = fixture(t);
    assert.throws(
        () => prepareSiteVersions("1.0.15-preview.2", "1.0.16-SNAPSHOT", markdown),
        /site version's base/
    );
});

test("passes artifact inputs through the workflow environment and detects previews by version", () => {
    const workflow = readFileSync(resolve(".github/workflows/deploy-site.yml"), "utf8");
    const prepareStep = workflow.slice(
        workflow.indexOf("- name: Prepare site artifact versions"),
        workflow.indexOf("# 6. Build the Maven Site")
    );
    assert.match(prepareStep, /ARTIFACT_VERSION_INPUT: \$\{\{ inputs\.artifact_version \}\}/);
    assert.doesNotMatch(prepareStep.match(/run: \|([\s\S]*)/)?.[1] ?? "", /inputs\.artifact_version/);
    assert.match(prepareStep, /if \[\[ "\$\{VERSION\}" == \*-\* \]\]/);
    assert.doesNotMatch(prepareStep, /inputs\.publish_as_latest/);
});
