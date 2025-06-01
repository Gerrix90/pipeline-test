# GitHub Copilot Instructions for Android Pipeline Test Project

This file provides context and guidance for GitHub Copilot when working with this Android project.

## Project Overview

This is an Android application built with:
- **Language**: Kotlin with Jetpack Compose
- **Build System**: Gradle with Android Gradle Plugin 8.8.2
- **Target SDK**: Android API 35
- **Min SDK**: Android API 30
- **Package**: `com.jahi.pipelinetest`

## Development Environment

### Prerequisites
- Java JDK 17 or higher
- Android SDK (automatically handled by setup.sh)
- Internet access for initial setup

### Project Setup
1. **Initial setup** (run once with internet):
   ```bash
   chmod +x setup.sh gradlew
   ./setup.sh
   ```

2. **Build commands**:
   ```bash
   # Online build
   ./gradlew assembleDebug
   
   # Offline build (after initial setup)
   ./gradlew assembleDebug --offline
   ```

## Critical Development Requirements

### 🚨 MANDATORY: Always Validate Changes
After making ANY code changes, you MUST run:
```bash
./gradlew assembleDebug --offline
```

This ensures:
- Code compiles successfully
- No syntax errors exist
- Dependencies are correct
- APK can be generated

**Never commit broken builds** - always fix build errors before proceeding.

## Project Structure

```
├── app/                          # Main Android application module
│   ├── src/main/java/           # Kotlin source code
│   ├── src/main/res/            # Android resources
│   ├── build.gradle             # App-level build configuration
│   └── google-services.json     # Firebase configuration (generated)
├── .github/workflows/           # CI/CD workflows
├── gradle/                      # Gradle configuration
├── setup.sh                     # Environment setup script
└── gradlew                      # Gradle wrapper
```

## Key Files to Understand

- `build.gradle` (root): Project-level Gradle configuration
- `app/build.gradle`: Android app module configuration  
- `gradle/libs.versions.toml`: Dependency version catalog
- `setup.sh`: Automated development environment setup
- `.github/workflows/android-ci.yml`: CI/CD pipeline configuration

## Build System Details

### Gradle Configuration
- Uses Gradle 8.10.2
- Version catalog in `gradle/libs.versions.toml`
- Supports both online and offline builds
- Includes Firebase integration for app distribution

### Common Commands
```bash
# Build debug variant
./gradlew assembleDebug

# Build release variant  
./gradlew assembleRelease

# Run tests
./gradlew test

# Clean build
./gradlew clean

# Run linting
./gradlew lint
```

## Firebase Integration

The project includes Firebase App Distribution for CI/CD:
- Fake `google-services.json` created for offline builds
- Real Firebase credentials managed via GitHub Secrets
- Distribution configured in `android-ci.yml` workflow

## Development Workflow

1. Make code changes
2. **Validate immediately**: `./gradlew assembleDebug --offline`
3. Fix any build errors
4. Test functionality
5. Commit only working code

## Environment Support

This project supports:
- **Local development**: Full Android Studio integration
- **Offline environments**: Complete builds without internet after setup
- **CI/CD**: GitHub Actions with Firebase distribution
- **Docker containers**: Minimal Android SDK setup

## Code Style Guidelines

- Use Kotlin for all new code
- Follow Android/Kotlin coding conventions
- Leverage Jetpack Compose for UI
- Maintain compatibility with offline builds

## Troubleshooting

### Build Failures
1. Check error messages carefully
2. Common fixes:
   ```bash
   chmod +x gradlew
   ./gradlew clean assembleDebug --offline
   ./setup.sh  # Re-run setup if needed
   ```

### Setup Issues
- Ensure internet access during initial `./setup.sh`
- Look for "✓ Android build successful!" message
- Re-run setup.sh if builds consistently fail

## Notes for GitHub Copilot

- Always suggest running build validation after code changes
- Prefer Kotlin syntax and Jetpack Compose patterns
- Consider offline build compatibility when suggesting dependencies
- Follow existing project structure and naming conventions
- Validate suggestions work with the project's Android/Kotlin setup