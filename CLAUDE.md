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
# Primary offline setup (run once with internet)
./setup.sh

# Build offline after setup
./gradlew assembleDebug --offline
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
   - Target SDK: 34 (Android 14)
   - Compile SDK: 34 (Android 14)
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

## Current Issue: Offline Build Challenge

### Problem Statement
The project is designed to support complete offline Android builds in restricted environments (like Docker containers without internet access). However, we're encountering persistent issues with Android SDK recognition during offline builds.

### Error Encountered
```
Failed to find target with hash string 'android-34' in: /root/android-sdk
```

### Solutions Attempted

1. **Plugin Resolution** ✅ SOLVED
   - Downloaded Android Gradle Plugin (AGP) 8.8.2 and dependencies
   - Downloaded Kotlin Gradle Plugin and Compose Plugin artifacts
   - Fixed repository configuration for offline mode
   - Result: All plugins now resolve correctly offline

2. **Dependency Caching** ✅ SOLVED  
   - Implemented online build first to populate Gradle cache
   - Download all transitive dependencies automatically
   - Use standard Gradle offline mode with complete cache
   - Result: Gradle tasks work offline, dependency resolution successful

3. **Android SDK Structure** 🔄 IN PROGRESS
   - Created android-34 platform directory structure
   - Generated proper source.properties with API level metadata
   - Created stub android.jar with Android classes
   - Added build.prop and SDK configuration files
   - Switched from API 35 to stable API 34 for better compatibility
   - Result: SDK structure exists but Gradle still cannot find target

### Current Status
- **Working**: Plugin resolution, dependency caching, Gradle tasks offline
- **Not Working**: Android platform recognition for compilation
- **Next**: Need to investigate why Gradle cannot recognize the android-34 platform despite proper SDK structure

### Next Steps to Try

1. **Investigate Gradle SDK Detection**
   - Check how AGP searches for Android platforms
   - Verify if additional SDK metadata files are needed
   - Test with different SDK directory structures

2. **Alternative Approaches**
   - Try using pre-built Android SDK platform from official sources
   - Investigate using Android SDK manager for proper platform setup
   - Consider using container with pre-installed Android SDK

3. **Debugging Enhancement**
   - Add more detailed SDK structure validation
   - Test platform recognition independently from build
   - Compare with working Android SDK installations

The goal remains: achieve complete offline Android builds with setup.sh preparing all necessary components for subsequent offline development.