# Time Fomo

Time Fomo is an Android application built with Kotlin and Jetpack Compose that helps you visualize the passage of time through countdown timers, life visualization, and AI-powered motivation. The app provides an intuitive interface to track time across different scales - from daily routines to your entire lifespan - enhanced with on-device artificial intelligence capabilities.

## Features

### Countdown Timers
- **Daily Countdown**: Track time remaining in the current day
- **Yearly Countdown**: See how much time is left in the current year
- **Custom Events**: Create and track countdowns to important personal events and milestones
- **Event Alarms**: Save events in Settings to automatically schedule alarms with dismissible notifications when your custom events occur
- **Event History**: View past custom events from the new History tab in Settings, listed most recent first
- **Task Management**: Manage tasks for each custom event with full CRUD operations

### Life Visualization
- **Life Hourglass**: Visualize your entire lifespan as hourglasses representing past, current, and future years
- **Interactive Timeline**: Explore different life periods with an intuitive hourglass metaphor

### Android Widgets
- **Daily Countdown Widget**: Quick access to daily time remaining from your home screen
- **Circular Progress Widget**: Visual progress indicators for your countdowns
- **Event Countdown Widget**: Track specific custom events directly from your home screen
- **AI-Powered Generate Button**: Get AI-generated motivational quotes with text-to-speech playback

### AI Features
- **On-Device AI**: Privacy-focused machine learning without internet dependency
- **Real-Time Text Generation**: AI-powered motivational content using MediaPipe LLM
- **AI Gallery**: Explore and experiment with various AI capabilities
- **Smart Fallback**: Graceful degradation to curated content when AI models unavailable
- **Voice Output**: Text-to-speech integration for AI-generated motivational quotes

### User Interface
- **Dark Theme**: Beautiful dark theme with subtle gradients
- **Material 3 Design**: Modern Android design principles with custom theming
- **Edge-to-Edge Display**: Immersive full-screen experience
- **Responsive Layout**: Optimized for different screen sizes

## Requirements

- **Android SDK**: API level 30 (Android 11) or higher
- **Java**: JDK 17 or higher
- **Build Tools**: Gradle 8.10.2+, Android Gradle Plugin 8.8.2
- **Target SDK**: API level 35 (Android 14)
- **Permissions**: Requires the `SCHEDULE_EXACT_ALARM` and `POST_NOTIFICATIONS` permissions for event alarms and notifications
- **AI Models**: Optional MediaPipe .task format models for enhanced AI functionality
- **Storage**: External storage access for AI model files and voice audio caching

## Build Instructions

### Online Environment

If you're working in an environment with internet access during setup:

1. The `setup.sh` script will automatically download the Gradle distribution and Android Gradle Plugin:
   ```bash
   chmod +x setup.sh gradlew
   ./setup.sh
   ```

2. After setup, build the app:
   ```bash
   ./gradlew assembleDebug
   ```

3. Install on connected device or emulator:
   ```bash
   ./gradlew installDebug
   ```

### Offline Environment

In environments without internet access:

1. Make sure all scripts are executable: 
   ```bash
   chmod +x *.sh gradlew
   ```

2. Extract Gradle from the system: 
   ```bash
   ./extract-gradle.sh
   ```

3. Build offline:
   ```bash
   ./gradlew assembleDebug --offline
   ```

4. Run tests:
   ```bash
   ./gradlew test --offline
   ```

For detailed development instructions, see the [AGENT.md](AGENT.md) file.

## Project Structure

- **`app/`**: Main Android application module
- **`app/src/main/java/com/jahi/pipelinetest/`**: Source code
  - `MainActivity.kt`: Main app entry point with navigation
  - `CountdownScreens.kt`: Countdown timer implementations  
  - `SettingsScreen.kt`: User preferences and event management
  - `MainViewModel.kt`: App state management
  - `*Widget.kt`: Home screen widget implementations
  - **`gallery/`**: AI Gallery integration components
  - **`domain/`**: Use cases including AI text generation
- **`memory-bank/`**: Project intelligence and development guidelines
- **`UI_SPEC.md`**: Detailed visual design specifications
- **`CLAUDE.md`**: Development guide and architecture overview

## Architecture

The app follows modern Android development practices:

- **Clean Architecture**: Strict MVVM with Use Case pattern (View → ViewModel → Use Case → Repository)
- **Jetpack Compose**: Declarative UI framework for modern Android apps
- **Material 3**: Latest Material Design components and theming
- **SharedPreferences**: Simple data persistence for settings and events
- **Android Widgets**: Home screen integration for quick access
- **MediaPipe Integration**: On-device AI inference using Google's MediaPipe framework
- **Encrypted Storage**: Secure API key management with EncryptedSharedPreferences
- **Structured Logging**: Timber-based logging with DEBUG_FLOW tags for debugging

## Usage

1. **Countdowns Tab**: View daily and yearly countdowns, create custom events
2. **Life Tab**: Explore your life timeline with the hourglass visualization
3. **Tasks Tab**: Add, edit, and delete tasks associated with your events
4. **AI Gallery Tab**: Explore on-device AI capabilities and features
5. **Settings**: Configure your birthdate, manage custom countdown events, and set API keys
6. **Widgets**: Add countdown widgets to your home screen for quick access
7. **AI Generation**: Use the Generate button on widgets for AI-powered motivational quotes
8. **Notifications**: Receive alerts when an event alarm triggers

### AI Setup (Optional)

To enable AI-powered features:

1. **Download AI Models**: Place MediaPipe .task files in the app's external storage `__imports` directory
2. **Configure TTS**: Set your ElevenLabs API key in Settings > General for voice output
3. **Test Generation**: Use the Generate button on any widget to create AI motivational content
4. **Explore Gallery**: Visit the AI Gallery tab to see available AI capabilities

## Development

For development commands, testing, and troubleshooting information, refer to:
- [AGENT.md](AGENT.md) - Comprehensive development workflow
- [CLAUDE.md](CLAUDE.md) - Architecture and code guidance  
- [UI_SPEC.md](UI_SPEC.md) - Visual design specifications
- [memory-bank/](memory-bank/) - Project intelligence and development standards
  - `systemPatterns.md` - Architecture patterns and logging standards
  - `aiFeatures.md` - AI integration documentation
  - `activeContext.md` - Current development status

## Contributing

When contributing to this project:

1. **Follow Memory Bank Guidelines**: All development must adhere to patterns in `memory-bank/`
2. **Use Clean Architecture**: Strict View → ViewModel → Use Case → Repository pattern
3. **Implement Proper Logging**: Use Timber with DEBUG_FLOW tags
4. **Test AI Features**: Verify both AI success and fallback scenarios
5. **Update Documentation**: Keep Memory Bank current with changes