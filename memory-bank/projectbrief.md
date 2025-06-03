# Project Brief - Time Fomo Android App

## Project Overview
Android application called "Time Fomo" that provides countdown timers and life visualization features with home screen widgets.

## Core Requirements

### Countdown Features
- Daily countdown (time remaining in current day)
- Yearly countdown (time remaining in current year)
- Custom event countdowns with user-defined dates
- Task management for each custom event

### Life Visualization
- Visual representation of past, current, and future years as hourglasses
- Based on user's birthdate and target age

### Android Widgets
- Daily countdown widget
- Circular progress widget
- Event countdown widget
- All widgets update regularly and sync with system time

### Task Management
- Create, read, update, delete tasks for each event
- Due date/time support for tasks
- Task completion tracking
- Task notifications with alarms

## Technical Requirements
- Android 11+ (API 30 minimum)
- Kotlin with Jetpack Compose
- Material3 Design
- Offline-first architecture
- Support for restricted/Docker environments
- **MANDATORY Architecture**: View -> ViewModel -> Use Case -> Repository -> Data Source (NO EXCEPTIONS)
- **MANDATORY Code Quality**: CLEAN code principles and DRY - NO duplicate code
- **MANDATORY Resources**: ALL strings, colors, dimensions MUST be in resource files - NO hardcoding
- **MANDATORY Constants**: ALL numeric values, string keys, limits MUST be named constants

## Key Features Implemented
- MVVM architecture pattern
- SharedPreferences for data persistence
- Alarm scheduling for notifications
- Boot persistence for alarms
- Date/time picker integration
- Progress tracking for tasks

## Development Constraints
- Must support offline builds
- Must work in Docker containers
- No external dependencies beyond what's in gradle cache
- Build verification required after all changes