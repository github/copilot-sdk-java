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
