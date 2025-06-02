# Time Fomo

Time Fomo is an Android application built with Kotlin and Jetpack Compose that helps you visualize the passage of time through countdown timers and life visualization. The app provides an intuitive interface to track time across different scales - from daily routines to your entire lifespan.

## Features

### Countdown Timers
- **Daily Countdown**: Track time remaining in the current day
- **Yearly Countdown**: See how much time is left in the current year
- **Custom Events**: Create and track countdowns to important personal events and milestones
- **Event Alarms**: Save events in Settings to automatically schedule alarms with dismissible notifications when your custom events occur

### Life Visualization
- **Life Hourglass**: Visualize your entire lifespan as hourglasses representing past, current, and future years
- **Interactive Timeline**: Explore different life periods with an intuitive hourglass metaphor

### Android Widgets
- **Daily Countdown Widget**: Quick access to daily time remaining from your home screen
- **Circular Progress Widget**: Visual progress indicators for your countdowns
- **Event Countdown Widget**: Track specific custom events directly from your home screen

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
- **Permissions**: Requires the `SCHEDULE_EXACT_ALARM` permission for precise event alarms

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
- **`UI_SPEC.md`**: Detailed visual design specifications
- **`CLAUDE.md`**: Development guide and architecture overview

## Architecture

The app follows modern Android development practices:

- **MVVM Pattern**: Clean separation between UI and business logic
- **Jetpack Compose**: Declarative UI framework for modern Android apps
- **Material 3**: Latest Material Design components and theming
- **SharedPreferences**: Simple data persistence for settings and events
- **Android Widgets**: Home screen integration for quick access

## Usage

1. **Countdowns Tab**: View daily and yearly countdowns, create custom events
2. **Life Tab**: Explore your life timeline with the hourglass visualization  
3. **Settings**: Configure your birthdate and manage custom countdown events
4. **Widgets**: Add countdown widgets to your home screen for quick access
5. **Notifications**: Receive alerts when an event alarm triggers

## Development

For development commands, testing, and troubleshooting information, refer to:
- [AGENT.md](AGENT.md) - Comprehensive development workflow
- [CLAUDE.md](CLAUDE.md) - Architecture and code guidance  
- [UI_SPEC.md](UI_SPEC.md) - Visual design specifications