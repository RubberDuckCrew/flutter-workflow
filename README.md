# Flutter Workflow

[![Release](https://img.shields.io/github/v/release/RubberDuckCrew/flutter-workflow?style=flat-square&color=blue)](https://github.com/RubberDuckCrew/flutter-workflow/releases)
[![Build Status](https://img.shields.io/github/actions/workflow/status/RubberDuckCrew/flutter-workflow/check-release.yml?style=flat-square&label=Check%20and%20Release&color=lime)](https://github.com/RubberDuckCrew/flutter-workflow/actions/workflows/check-release.yml)
[![Last Commit](https://img.shields.io/github/last-commit/RubberDuckCrew/flutter-workflow?style=flat-square&color=orange)](https://github.com/RubberDuckCrew/flutter-workflow/commits/main)
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

| Name                  | Description                                                    | Required | Default |
| --------------------- | -------------------------------------------------------------- | -------- | ------- |
| `release-version`     | Version to release (e.g., `1.2.3`, `v` is added automatically) | No       | —       |
| `app-name`            | Name of the app (used for artifact naming)                     | No       | —       |
| `build-type`          | Type of build (`test`, `build`, `size-analysis`, `release`)    | No       | `test`  |
| `java-version`        | Java version used for Android build                            | No       | `21`    |
| `working-directory`   | Directory of the Flutter project                               | No       | `.`     |
| `bot-app-id`          | GitHub App ID used for release creation                        | No       | —       |
| `skip-version-update` | Whether to skip updating the version in pubspec.yaml           | No       | `false` |

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

## Sync Dart SDK Workflow

Reusable workflow that aligns the exact `sdk:` pin in `pubspec.yaml` with the
Dart SDK version bundled with the configured Flutter version, so Renovate
Flutter updates don't fail version solving. It runs `flutter pub get` and
commits the sync to the current branch (never `main`).

### Inputs

| Name                | Description                                      | Required | Default |
| ------------------- | ------------------------------------------------ | -------- | ------- |
| `working-directory` | Directory of the Flutter project                 | No       | `.`     |
| `ref`               | Commit, branch, or tag to check out              | No       | —       |
| `bot-app-id`        | GitHub App ID for the token that pushes the sync | No       | —       |

### Secrets

| Name                  | Description            | Required |
| --------------------- | ---------------------- | -------- |
| `BOT_APP_PRIVATE_KEY` | GitHub App private key | No       |

### Usage

```yaml
jobs:
  sync-dart-sdk:
    uses: RubberDuckCrew/flutter-workflow/.github/workflows/workflow-sync-dart-sdk.yml@v1
    with:
      working-directory: my-app
      bot-app-id: ${{ vars.BOT_APP_ID }}
    secrets:
      BOT_APP_PRIVATE_KEY: ${{ secrets.BOT_APP_PRIVATE_KEY }}
```

### GitHub App

Prefer setting `bot-app-id` and `BOT_APP_PRIVATE_KEY`: commits pushed with a
GitHub App token re-trigger workflow runs, so CI re-runs on the sync commit
automatically (e.g., the failing checks of a Renovate Flutter bump turn green).
Without it, the sync is pushed with `GITHUB_TOKEN`, which does not trigger new
workflow runs and requires a manual re-run of the checks.

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
