# Android Jetpack Compose Project Structure Guide

This document outlines the organizational structure and architectural patterns for a modern Android project using **Jetpack Compose** with **Clean Architecture** and **MVVM** pattern.

## Project Overview

This project follows **Clean Architecture** principles with **MVVM** (Model-View-ViewModel) pattern, leveraging Jetpack Compose for the UI layer to ensure separation of concerns, testability, and maintainability.

## Root Directory Structure

```
project-root/
├── app/                    # Main Android application module
├── gradle/                 # Gradle wrapper files
├── build/                  # Build outputs (auto-generated)
├── build.gradle.kts       # Root build configuration
├── settings.gradle.kts    # Project settings
├── gradle.properties      # Gradle properties
└── [documentation]/       # Project documentation files
```

## Architecture: Clean Architecture + MVVM

### Feature Module Structure

Each feature follows a consistent three-layer architecture:

```
features/[feature_name]/
├── data/              # DATA LAYER
│   ├── repositories/  # Repository implementations
│   ├── sources/       # Data sources
│   │   ├── remote/   # API calls
│   │   └── local/    # Database/cache
│   ├── models/       # Data Transfer Objects (DTOs)
│   └── mappers/      # DTO to Domain model mappers
│
├── domain/            # DOMAIN LAYER
│   ├── models/        # Business entities
│   ├── repositories/  # Repository interfaces
│   ├── use_cases/     # Business logic/rules
│   └── validators/    # Business validation
│
├── presentation/      # PRESENTATION LAYER
│   ├── screens/       # Compose screens
│   ├── components/    # Reusable Compose components
│   ├── viewmodels/    # ViewModels
│   ├── states/        # UI state models
│   ├── navigation/    # Navigation logic
│   └── theme/         # Compose theme
│
└── di/               # Dependency injection modules
```

### Data Flow

```
Composable Screen 
    ↓↑
ViewModel 
    ↓↑
UseCase 
    ↓↑
Repository Interface
    ↓↑
Repository Implementation
    ↓↑
Data Source (API/Database)
```

## Package Structure

### Base Package Structure

```
com.example.app/
├── common/           # Shared components
├── features/         # Feature modules
├── widget/           # App widgets
└── AppClass.kt      # Application class
```

### Common Module Organization

```
common/
├── data/
│   ├── api/
│   │   ├── interceptors/    # HTTP interceptors
│   │   ├── clients/         # API client setup
│   │   └── services/        # Retrofit/GraphQL services
│   ├── database/            # Room database setup
│   └── preferences/         # SharedPreferences
│
├── domain/
│   ├── models/              # Shared domain models
│   ├── use_cases/           # Common use cases
│   └── interfaces/          # Common interfaces
│
├── presentation/
│   ├── base/                # Base classes (BaseViewModel)
│   ├── components/          # Reusable Compose components
│   ├── modifiers/           # Custom Compose modifiers
│   ├── utils/               # Compose utilities
│   └── theme/               # Material3 theme definition
│       ├── Color.kt         # Color palette
│       ├── Type.kt          # Typography
│       ├── Shape.kt         # Shapes
│       └── Theme.kt         # Theme composition
│
├── di/                      # Dependency injection
│   ├── modules/             # Dagger/Hilt modules
│   └── qualifiers/          # DI qualifiers
│
└── utils/                   # General utilities
    ├── extensions/          # Kotlin extensions
    ├── constants/           # App constants
    └── helpers/             # Helper classes
```

## Compose UI Structure

### Presentation Layer Organization

```
presentation/
├── screens/                 # Screen-level composables
│   ├── login/
│   │   ├── LoginScreen.kt
│   │   └── LoginViewModel.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   └── components/     # Screen-specific components
│   └── profile/
│       ├── ProfileScreen.kt
│       └── ProfileViewModel.kt
│
├── components/              # Reusable components
│   ├── buttons/
│   │   ├── PrimaryButton.kt
│   │   └── SecondaryButton.kt
│   ├── cards/
│   │   └── ContentCard.kt
│   ├── inputs/
│   │   ├── TextField.kt
│   │   └── PasswordField.kt
│   └── layouts/
│       └── ScaffoldLayout.kt
│
├── navigation/
│   ├── NavGraph.kt         # Navigation graph definition
│   ├── NavHost.kt          # Navigation host setup
│   └── Routes.kt           # Route definitions
│
├── theme/
│   ├── Color.kt            # Color definitions
│   ├── Type.kt             # Typography definitions
│   ├── Shape.kt            # Shape definitions
│   ├── Theme.kt            # Theme composition
│   └── Dimensions.kt       # Spacing and sizing
│
└── utils/
    ├── ComposeExtensions.kt
    └── PreviewUtils.kt
```

### Compose Component Structure

```kotlin
// Example of a well-structured Compose component

@Composable
fun UserProfileCard(
    user: User,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        // Component implementation
    ) {
        // Content
    }
}

// Preview
@Preview(showBackground = true)
@Composable
private fun UserProfileCardPreview() {
    AppTheme {
        UserProfileCard(
            user = User.mock(),
            onEditClick = {}
        )
    }
}
```

### Resource Management in Compose

```
res/
├── drawable/                # Vector drawables and images
│   └── ic_*.xml            # Icons
│
├── values/
│   ├── strings.xml         # String resources
│   └── themes.xml          # Android theme (for activities)
│
├── font/                    # Font files
│   └── *.ttf
│
└── raw/                     # Raw resources (JSON, etc.)
```

### Naming Conventions

- **Screens**: `<Feature>Screen.kt`
  - `LoginScreen.kt`
  - `ProfileScreen.kt`
  - `SettingsScreen.kt`

- **Components**: `<Type><Description>.kt`
  - `PrimaryButton.kt`
  - `UserCard.kt`
  - `LoadingIndicator.kt`

- **ViewModels**: `<Feature>ViewModel.kt`
  - `LoginViewModel.kt`
  - `ProfileViewModel.kt`

- **States**: `<Feature>UiState.kt`
  - `LoginUiState.kt`
  - `ProfileUiState.kt`

## Dependency Injection Structure

Using Hilt/Dagger for dependency injection:

```
di/
├── modules/
│   ├── AppModule.kt         # App-level dependencies
│   ├── NetworkModule.kt     # Network dependencies
│   ├── DatabaseModule.kt    # Database dependencies
│   └── RepositoryModule.kt  # Repository bindings
│
├── qualifiers/
│   ├── ApiUrl.kt           # API URL qualifier
│   └── IoDispatcher.kt     # Coroutine dispatcher
│
└── scopes/
    └── ActivityScope.kt     # Custom scopes
```

## Testing Structure

```
app/
├── src/test/java/          # Unit tests
│   ├── [package]/
│   │   ├── domain/         # Domain layer tests
│   │   ├── data/           # Data layer tests
│   │   └── presentation/   # ViewModel tests
│   └── utils/              # Test utilities
│
└── src/androidTest/java/   # Instrumentation tests
    ├── [package]/
    │   ├── compose/        # Compose UI tests
    │   └── integration/    # Integration tests
    └── utils/              # Android test utilities
```

## Key Architectural Principles

### 1. **Separation of Concerns**
- Each layer has a specific responsibility
- Dependencies flow inward (Presentation → Domain → Data)
- Domain layer contains pure business logic (no Android dependencies)

### 2. **Single Responsibility**
- Each class has one reason to change
- Use cases handle one specific business operation
- ViewModels manage UI state for one screen

### 3. **Dependency Inversion**
- High-level modules don't depend on low-level modules
- Both depend on abstractions (interfaces)
- Repository pattern for data access abstraction

### 4. **Feature Isolation**
- Features are self-contained modules
- Minimal dependencies between features
- Shared functionality in common module

### 5. **Testability**
- Each layer can be tested independently
- Use interfaces for mocking dependencies
- ViewModels are tested with unit tests
- UI is tested with instrumentation tests

## Best Practices

### Code Organization
1. Keep related files together (feature-based organization)
2. Use consistent naming conventions
3. Minimize cross-feature dependencies
4. Extract common functionality to shared modules

### Architecture Guidelines
1. ViewModels should not reference Android framework classes (except AndroidViewModel)
2. Use cases should be single-purpose
3. Repository interfaces belong in domain layer
4. Data models (DTOs) should be separate from domain models
5. UI models should be separate from domain models when needed
6. Compose UI should be stateless with state hoisted to ViewModels
7. Use `remember` and `rememberSaveable` appropriately
8. Prefer smaller, focused composables over large ones

### Compose Best Practices
1. Use `stringResource()` for all user-facing text
2. Define dimensions in theme for consistent spacing
3. Use Material3 theme for consistent styling
4. Implement proper preview annotations for development
5. Handle configuration changes with `rememberSaveable`
6. Use `LaunchedEffect` and `DisposableEffect` correctly

### Testing Strategy
1. Unit test domain layer extensively
2. Test ViewModels with mock repositories and test dispatchers
3. Use Compose testing APIs for UI tests
4. Test composables in isolation with `createComposeRule()`
5. Use semantics for accessible and testable UI
6. Write integration tests for critical user flows

## Module Dependencies

```
app (module)
  └── common
      └── features/
          ├── feature_a
          ├── feature_b
          └── feature_c
```

Features can depend on common module but not on each other, ensuring loose coupling and high cohesion.

---

This structure promotes:
- **Scalability**: Easy to add new features
- **Maintainability**: Clear separation of concerns
- **Testability**: Each component can be tested in isolation
- **Team Collaboration**: Multiple developers can work on different features simultaneously
- **Code Reusability**: Common components can be shared across features