import assert from "node:assert/strict";
import { readFileSync, readdirSync, statSync, writeFileSync } from "node:fs";
import { join } from "node:path";
import { pathToFileURL } from "node:url";

const VERSION_PATTERN = /^\d+\.\d+\.\d+(?:-[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?$/;
const SNAPSHOT_REPOSITORY = "https://central.sonatype.com/repository/maven-snapshots/";

function markdownFiles(directory) {
    return readdirSync(directory).flatMap((name) => {
        const path = join(directory, name);
        return statSync(path).isDirectory()
            ? markdownFiles(path)
            : path.endsWith(".md")
              ? [path]
              : [];
    });
}

export function prepareSiteVersions(siteVersion, artifactVersion, markdownDirectory) {
    assert.match(siteVersion, VERSION_PATTERN, "Invalid site version");
    assert.match(artifactVersion, VERSION_PATTERN, "Invalid artifact version");
    const siteBase = /^\d+\.\d+\.\d+/.exec(siteVersion)?.[0];
    if (artifactVersion.endsWith("-SNAPSHOT")) {
        assert.equal(
            artifactVersion,
            `${siteBase}-SNAPSHOT`,
            "Snapshot artifact version must use the site version's base"
        );
    } else {
        assert.equal(
            artifactVersion,
            siteVersion,
            "Non-snapshot artifact version must match the site version"
        );
    }

    const snapshot = artifactVersion.endsWith("-SNAPSHOT");
    const replacements = new Map([
        [
            "<!-- ARTIFACT_MAVEN_REPOSITORY -->",
            snapshot
                ? `Snapshot builds require this repository:

\`\`\`xml
<repositories>
    <repository>
        <id>central-snapshots</id>
        <url>${SNAPSHOT_REPOSITORY}</url>
        <releases><enabled>false</enabled></releases>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
\`\`\``
                : "",
        ],
        [
            "<!-- ARTIFACT_GRADLE_REPOSITORY -->",
            snapshot
                ? `Snapshot builds require this repository:

\`\`\`groovy
repositories {
    mavenCentral()
    maven {
        url = uri('${SNAPSHOT_REPOSITORY}')
        mavenContent { snapshotsOnly() }
    }
}
\`\`\``
                : "",
        ],
        [
            "//ARTIFACT_REPOSITORY",
            snapshot ? `//REPOS central,central-snapshots=${SNAPSHOT_REPOSITORY}` : "",
        ],
    ]);

    let hardcodedCoordinates = 0;
    const markerCounts = new Map([...replacements.keys()].map((marker) => [marker, 0]));
    for (const path of markdownFiles(markdownDirectory)) {
        let content = readFileSync(path, "utf8");
        content = content.replace(
            /copilot-sdk-java:\d+\.\d+\.\d+(?:-[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?/g,
            () => {
                hardcodedCoordinates++;
                return `copilot-sdk-java:${artifactVersion}`;
            }
        );
        for (const [marker, replacement] of replacements) {
            const count = content.split(marker).length - 1;
            markerCounts.set(marker, markerCounts.get(marker) + count);
            content = content.replaceAll(marker, replacement);
        }
        writeFileSync(path, content);
    }
    assert(hardcodedCoordinates > 0, "Expected hardcoded Java coordinates in site content");
    for (const [marker, count] of markerCounts) {
        assert(count > 0, `Expected site marker: ${marker}`);
    }
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
    assert.equal(
        process.argv.length,
        5,
        "Usage: prepare-site-versions.mjs <site-version> <artifact-version> <markdown-directory>"
    );
    prepareSiteVersions(process.argv[2], process.argv[3], process.argv[4]);
}
