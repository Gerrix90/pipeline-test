# Tech Context - Development Environment

## Technology Stack

### Core Technologies
- **Language**: Kotlin 2.0.0
- **UI Framework**: Jetpack Compose with Material3
- **Build System**: Gradle 8.8.2 with Kotlin DSL
- **Minimum SDK**: 30 (Android 11)
- **Target SDK**: 35 (Android 14)
- **Compile SDK**: 35

### Key Dependencies
- Compose BOM: 2024.04.01
- AndroidX Core KTX
- AndroidX Lifecycle
- Material3 Components
- Firebase for distribution
- Kotlin Coroutines
- **MediaPipe Tasks**: AI/ML inference framework
- **Timber**: Structured logging with DEBUG_FLOW tags
- **EncryptedSharedPreferences**: Secure API key storage
- **Kotlin Serialization**: JSON handling for AI Gallery

### Development Setup

#### Build Commands
```bash
./gradlew assembleDebug         # Build debug APK
./gradlew assembleDebug --offline  # Offline build
./gradlew test                  # Run unit tests
./gradlew lint                  # Run lint checks
```

#### Offline Build Support
- `setup.sh` script for environment preparation
- Automatic Android SDK installation
- Vendor libraries cached locally
- Gradle offline mode support

### Project Configuration

#### Important Files
- `gradle/libs.versions.toml` - Dependency versions
- `local.properties` - SDK location
- `CLAUDE.md` - AI assistant instructions
- `setup.sh` - Environment setup script

#### Build Features
- View Binding: disabled
- Compose: enabled
- BuildConfig: enabled
- Minification: disabled for debug

### Development Constraints
1. Must support offline builds
2. Docker container compatibility required
3. No external network dependencies during build
4. All dependencies must be in vendor cache

### AI/ML Integration
- **MediaPipe Framework**: Google's on-device ML inference
- **Model Support**: .task format models for LLM inference
- **Storage**: External files directory for model files
- **Backends**: GPU and CPU inference support
- **Privacy**: All AI processing happens on-device

### Testing
- Unit tests with JUnit
- Instrumented tests for Android
- Compose UI testing support
- Test naming convention: `*Test.kt`
- **AI Testing**: Model initialization and inference testing
- **Widget Testing**: Generate button and TTS functionality

### CI/CD
- GitHub Actions support
- Firebase App Distribution integration
- Automated build verification