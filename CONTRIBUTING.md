## Contributing

[fork]: https://github.com/github/copilot-sdk-java/fork
[pr]: https://github.com/github/copilot-sdk-java/compare

Hi there! We're thrilled that you'd like to contribute to this project. Your help is essential for keeping it great.

This repository contains the **Copilot SDK for Java**, the official Java variant of the official [GitHub Copilot SDK](https://github.com/github/copilot-sdk). For issues or features related to the upstream SDK, please contribute there instead.

Contributions to this project are [released](https://help.github.com/articles/github-terms-of-service/#6-contributions-under-repository-license) to the public under the [project's open source license](LICENSE).

Please note that this project is released with a [Contributor Code of Conduct](CODE_OF_CONDUCT.md). By participating in this project you agree to abide by its terms.

## What kinds of contributions we're looking for

We'd love your help with:

 * Fixing any bugs in the existing feature set
 * Making the SDK more idiomatic and nice to use for Java developers
 * Improving documentation

If you have ideas for entirely new features, please post an issue or start a discussion. We're very open to new features but need to make sure they align with the direction of the upstream [Copilot SDK](https://github.com/github/copilot-sdk) and can be maintained in sync. Note that this repo periodically merges upstream changes — see the [README](README.md#agentic-upstream-merge-and-sync) for details on how that works.

## Prerequisites for running and testing code

1. Install [Java 17+](https://openjdk.org/) (JDK)
1. Install [Maven 3.9+](https://maven.apache.org/download.cgi) (or use the included `mvnw` wrapper)
1. Install [Node.js](https://nodejs.org/) (v18+) — required for the E2E test harness

## Submitting a pull request

1. [Fork][fork] and clone the repository
1. Enable git hooks: `git config core.hooksPath .githooks`
1. Make sure the tests pass on your machine: `mvn clean verify`
1. Make sure formatting passes: `mvn spotless:check`
1. Create a new branch: `git checkout -b my-branch-name`
1. Make your change, add tests, and make sure the tests and linter still pass
1. Push to your fork and [submit a pull request][pr]
1. Pat yourself on the back and wait for your pull request to be reviewed and merged.

### Running tests and linters

```bash
# Build and run all tests
mvn clean verify

# Run a single test class
mvn test -Dtest=CopilotClientTest

# Format code (required before commit)
mvn spotless:apply

# Check formatting only
mvn spotless:check
```

Here are a few things you can do that will increase the likelihood of your pull request being accepted:

- Write tests.
- Keep your change as focused as possible. If there are multiple changes you would like to make that are not dependent upon each other, consider submitting them as separate pull requests.
- Write a [good commit message](http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html).

## Resources

- [How to Contribute to Open Source](https://opensource.guide/how-to-contribute/)
- [Using Pull Requests](https://help.github.com/articles/about-pull-requests/)
- [GitHub Help](https://help.github.com)
