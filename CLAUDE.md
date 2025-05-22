# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application project called "Pipeline test" built with Kotlin and Jetpack Compose. It's a simple Android app that displays a "Hello Android!" greeting.

## Build System

The project uses Gradle with the Gradle Kotlin DSL for build configuration. Dependencies are managed via the version catalog in `gradle/libs.versions.toml`.

## Development Commands

### Building and Running

```bash
# Build the project
./gradlew build

# Build the debug variant
./gradlew assembleDebug

# Build the release variant
./gradlew assembleRelease

# Install the debug variant on a connected device or emulator
./gradlew installDebug

# Run the app on a connected device or emulator
./gradlew app:installDebug
```

### Offline Environment Support

This project supports building in offline/restricted environments:

```bash
# Setup for offline builds (extracts Gradle from system)
./extract-gradle.sh

# Run tests in offline mode
./gradlew test --offline

# Run unit tests only (works without Android dependencies)
./test-only.sh

# Complete offline build setup
./setup-offline-build.sh
```

### Testing

```bash
# Run all unit tests
./gradlew test

# Run a specific unit test class
./gradlew testDebugUnitTest --tests "com.jahi.pipelinetest.ExampleUnitTest"

# Run all instrumented tests (requires a connected device or emulator)
./gradlew connectedAndroidTest

# Run a specific instrumented test class
./gradlew connectedDebugAndroidTest --tests "com.jahi.pipelinetest.ExampleInstrumentedTest"
```

### Linting and Static Analysis

```bash
# Run Lint
./gradlew lint

# Run Kotlin Linter
./gradlew lintKotlin

# Generate Lint report
./gradlew lintDebug
```

### Clean Build

```bash
# Clean the project build files
./gradlew clean
```

## Code Architecture

The application follows a standard Android architecture:

1. **UI Layer**: Built with Jetpack Compose, defined in `MainActivity.kt` and theme resources.
   - Uses Material3 design components
   - Implements edge-to-edge design

2. **Project Configuration**:
   - Minimum SDK: 30 (Android 11)
   - Target SDK: 35
   - Compose BOM: 2024.04.01
   - Kotlin version: 2.0.0
   - AGP version: 8.8.2
   - Java target: 11

3. **Build Configuration**:
   - Uses Gradle version catalog (`gradle/libs.versions.toml`)
   - Supports both online and offline build environments
   - Includes pre-built dependencies for offline scenarios
   - Multiple shell scripts for different deployment scenarios

The app currently has minimal functionality, serving as a template or starting point for a more fully-featured application. The project includes extensive offline build support for deployment in restricted environments.