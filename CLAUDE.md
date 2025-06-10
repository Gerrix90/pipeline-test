# CLAUDE.md

## 🚨 MANDATORY: Memory Bank Management

**BEFORE ANY TASK**: 
1. **MUST** read ALL memory bank files in `/memory-bank/` directory
2. **MUST** understand current project state from memory bank
3. **NEVER** start coding without consulting memory bank first

**AFTER ANY TASK**:
1. **MUST** update relevant memory bank files
2. **MUST** document changes in `activeContext.md`
3. **MUST** update `progress.md` with completed/pending items
4. Memory Bank is LIVING documentation - keep it current!

See `/memory-bank/memory-bank.md` for complete structure and workflow.

## 🚨 MANDATORY: Android Project Structure

**MUST** follow the architectural patterns and structure defined in `@ANDROID_PROJECT_STRUCTURE.md`:
- Clean Architecture + MVVM pattern is MANDATORY
- Three-layer architecture: Presentation → Domain → Data
- Feature-based module organization
- Proper separation of concerns
- See `@ANDROID_PROJECT_STRUCTURE.md` for detailed requirements

---

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application called "Time Fomo" built with Kotlin and Jetpack Compose. The app provides countdown timers for various time periods (daily, yearly, custom events) and a life visualization feature showing past/current/future years as hourglasses. It includes Android home screen widgets for quick access to countdowns.

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
   - Target SDK: 35 (Android 14 UPSIDE_DOWN_CAKE)
   - Compile SDK: 35 (Android 14)
   - Compose BOM: 2024.04.01
   - Kotlin version: 2.0.0
   - AGP version: 8.8.2
   - Java target: 11

3. **App Architecture**:
   - **MVVM Pattern**: `MainViewModel` manages app state with Compose state
   - **Navigation**: Bottom navigation bar for switching between Countdowns and Life screens
   - **Data Persistence**: `Prefs` wrapper around SharedPreferences for settings/events
   - **UI Screens**:
     - `CountdownsScreen`: Shows daily, yearly, and custom event countdowns
     - `LifeHourglassScreen`: Visualizes life years as hourglasses
     - `SettingsScreen`: Manages birthdate and custom events
   - **Widgets**: Three home screen widgets (`DailyCountdownWidget`, `CircularProgressWidget`, `EventCountdownWidget`)

4. **Key Components**:
   - `MainActivity.kt`: Entry point with navigation setup
   - `MainViewModel.kt`: Central state management
   - `CountdownScreens.kt`: Countdown UI implementations
   - `Prefs.kt`: SharedPreferences wrapper for data persistence
   - `model/CustomEvent.kt`: Data model for custom countdown events
   - `*Widget.kt` files: Android home screen widget implementations

## Android Build Solution ✅ RESOLVED

### Problem Statement
The project needed to support Android builds in restricted environments (like Docker containers without pre-installed Android development tools).

### Root Cause Discovered
The issue was missing Android SDK in Docker containers. Most Docker environments don't have Android SDK pre-installed, causing build failures with "SDK location not found" errors.

### Final Solution

The setup.sh script now provides **automatic Android SDK installation**:

1. **SDK Detection** ✅ SOLVED
   - Checks common Android SDK locations (`/opt/android-sdk`, `/usr/lib/android-sdk`, `$HOME/Android/Sdk`, etc.)
   - Automatically detects existing installations

2. **Automatic SDK Installation** ✅ SOLVED
   - Downloads Android command-line tools when SDK not found
   - Installs required components: `platform-tools`, `platforms;android-35`, `build-tools;35.0.0`
   - Automatically accepts SDK licenses
   - Sets up proper environment variables and `local.properties`

3. **Fallback Support** ✅ SOLVED
   - Creates minimal SDK structure if download fails
   - Ensures builds can proceed even in restricted environments

### Verification
```bash
# Docker container test result:
✓ Android SDK installed
✓ Android build successful!
🎉 Setup complete!
```

### Usage
```bash
# Simple one-command setup that works everywhere
./setup.sh

# Automatically handles:
# - Android SDK detection
# - SDK installation if missing  
# - Environment configuration
# - Build verification
```

The Android build solution is now complete and works in both local environments and Docker containers without pre-installed Android tools.

## Critical Validation Requirements

**IMPORTANT**: Always run `./gradlew assembleDebug --offline` after making ANY code changes to ensure the build compiles correctly. Never proceed with broken builds.