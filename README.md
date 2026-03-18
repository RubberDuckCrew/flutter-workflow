# Flutter Workflow

[![Release](https://img.shields.io/github/v/release/RubberDuckCrew/flutter-workflow?style=flat-square&color=blue)](https://github.com/RubberDuckCrew/flutter-workflow/releases)
[![License: MIT](https://img.shields.io/github/license/RubberDuckCrew/flutter-workflow?style=flat-square&color=yellow)](LICENSE)

A reusable GitHub Actions workflow to **test, build, analyze, and release Flutter applications** with minimal setup.

## Features

- Runs **formatting, analysis, and tests** automatically.
- Builds **APK and App Bundle (AAB)** artifacts.
- Supports **size analysis builds**.
- Automatically **signs builds** when keystore secrets are provided.
- Uploads build artifacts (APK, AAB, symbols).
- Can **comment artifact links on pull requests** (via artifact2pr).
- Supports **on-demand builds via PR label** (`⚗️ Request Build`).
- Creates **draft GitHub releases** with attached artifacts.

## Inputs

| Name                | Description                                                    | Required | Default |
| ------------------- | -------------------------------------------------------------- | -------- | ------- |
| `release-version`   | Version to release (e.g., `1.2.3`, `v` is added automatically) | No       | —       |
| `app-name`          | Name of the app (used for artifact naming)                     | No       | —       |
| `build-type`        | Type of build (`test`, `build`, `size-analysis`, `release`)    | No       | `test`  |
| `java-version`      | Java version used for Android build                            | No       | `21`    |
| `working-directory` | Directory of the Flutter project                               | No       | `.`     |
| `bot-app-id`        | GitHub App ID used for release creation                        | No       | —       |

## Secrets

| Name                  | Description             | Required |
| --------------------- | ----------------------- | -------- |
| `KEY_PASSWORD`        | Keystore key password   | No       |
| `KEYSTORE_PASSWORD`   | Keystore password       | No       |
| `KEY_ALIAS`           | Keystore alias          | No       |
| `KEYSTORE_BASE64`     | Base64 encoded keystore | No       |
| `BOT_APP_PRIVATE_KEY` | GitHub App private key  | No       |

## Usage

Call this workflow from another repository:

```yaml
name: Use Flutter Workflow

on:
  pull_request:
  push:
    tags:
      - "*"

jobs:
  flutter:
    uses: RubberDuckCrew/flutter-workflow/.github/workflows/workflow-build.yml@v1
    with:
      app-name: my-app
      build-type: build
```

### Release Example

```yaml
jobs:
  release:
    uses: RubberDuckCrew/flutter-workflow/.github/workflows/workflow-build.yml@v1
    with:
      app-name: my-app
      build-type: release
      release-version: 1.2.3
    secrets:
      KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
      KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
      KEYSTORE_BASE64: ${{ secrets.KEYSTORE_BASE64 }}
      BOT_APP_PRIVATE_KEY: ${{ secrets.BOT_APP_PRIVATE_KEY }}
```

## Build Triggers

The build job runs when:

- A **tag is pushed**
- The PR has the label **`⚗️ Request Build`**
- `build-type` is set to `build`, `size-analysis`, or `release`

## Artifacts

The workflow uploads:

- 📦 APK file
- 📦 App Bundle (AAB)
- 🧩 Debug symbols
- 📊 Size analysis report (optional)

## Pull Request Integration

When using the label:

```
⚗️ Request Build
```

- A build is triggered
- The label is automatically removed
- A comment with artifact links is posted (via artifact2pr)

## Release Flow

When `build-type: release` and `release-version` is set:

- Updates `pubspec.yaml` version
- Builds signed artifacts (if secrets available)
- Commits updated version
- Creates a **draft GitHub release**
- Uploads:
  - APK
  - AAB
  - Zipped symbols

## Notes

- Signing is skipped automatically if no keystore is provided.
- Forked PRs do not have access to secrets → no signing.
- Artifacts are retained according to GitHub’s default retention policy.
- The workflow uses concurrency control to cancel outdated runs.

## License

[MIT](LICENSE)
