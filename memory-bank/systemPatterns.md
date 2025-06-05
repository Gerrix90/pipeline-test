# System Patterns - Architecture & Design

## Architecture Overview

### STRICT MVVM + Clean Architecture Pattern
**CRITICAL**: ALL new features MUST follow this exact flow:
```
View (Compose UI) -> ViewModel -> Use Case -> Repository -> Data Source
```

**NO EXCEPTIONS**: 
- View NEVER directly calls Repository
- ViewModel NEVER directly calls Repository  
- ViewModel MUST call Use Case
- Use Case MUST call Repository
- Repository handles ALL data source operations

This is NOT optional - this is the REQUIRED architecture for ALL features.

**SEE**: `architecture-example.md` for concrete implementation examples

### Key Components

#### ViewModels
- `MainViewModel`: Central app state management
- `TaskViewModel`: Task-specific operations
- `WidgetViewModel`: Widget state management

#### Use Cases (Domain Layer)
- `AddTaskToEventUseCase` / `CreateTaskUseCase`
- `GetTasksForEventUseCase` / `GetTasksUseCase` 
- `UpdateTaskUseCase`
- `DeleteTaskUseCase`
- `ToggleTaskCompletionUseCase`

#### Data Layer
- `TaskRepository`: Task data operations
- `Prefs`: SharedPreferences wrapper
- Models: `Task`, `CustomEvent`

## Design Patterns

### State Management
- Compose State for UI
- StateFlow for ViewModel data
- SharedPreferences for persistence

### Navigation
- Bottom navigation with 4 tabs (Countdowns, Tasks, AI Gallery, Life)
- Single Activity architecture
- Screen state managed by MainViewModel

### Widget Architecture
- AppWidgetProvider implementations
- RemoteViews for UI
- Alarm-based updates
- System broadcast receivers

### Notification System
- AlarmManager for scheduling
- BroadcastReceivers for handling
- Full-screen intent support
- Wake lock management

## Code Organization

### Package Structure
```
com.jahi.pipelinetest/
├── domain/          # Use cases
├── model/           # Data models
├── repository/      # Data access
├── ui/
│   ├── components/  # Reusable UI
│   └── theme/       # Material3 theme
├── util/            # Utilities
└── viewmodel/       # ViewModels
```

### Key Patterns
1. **MANDATORY Clean Architecture**: View -> ViewModel -> Use Case -> Repository -> Data Source
2. **Dependency Injection**: Manual DI through ViewModelFactory
3. **Single Source of Truth**: Repository manages all data access
4. **Reactive Updates**: Flow-based data streams
5. **Defensive Programming**: Null safety, try-catch blocks
6. **Modular Components**: Reusable UI components

### Architecture Rules
1. **EVERY** business operation MUST have a Use Case
2. **EVERY** Use Case MUST be injected into ViewModel via constructor
3. **EVERY** Repository MUST be injected into Use Case via constructor
4. **NO** direct Repository calls from ViewModel
5. **NO** business logic in ViewModels - only in Use Cases
6. **NO** data access logic in Use Cases - only in Repositories

## Logging Standards (MANDATORY)

### Timber Logging Requirements
**ALL logging MUST use Timber with specific patterns:**

```kotlin
// Required import
import timber.log.Timber

// Required setup (Application class or initialization)
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
}

// MANDATORY: Use DEBUG_FLOW tag with class prefix
companion object {
    private const val TAG = "DEBUG_FLOW"
    private const val CLASS_PREFIX = "MyClassName"
}

// Required logging format
Timber.tag("$TAG:$CLASS_PREFIX").d("Method started: methodName()")
Timber.tag("$TAG:$CLASS_PREFIX").i("Important state change: %s", stateInfo)
Timber.tag("$TAG:$CLASS_PREFIX").e(exception, "Error occurred in: methodName")
```

### Logging Rules
1. **ALWAYS** use Timber.tag() with DEBUG_FLOW prefix
2. **ALWAYS** include class name in tag for identification
3. **NEVER** use Android Log.d/Log.i directly - Timber ONLY
4. **ALWAYS** log method entry/exit for debugging flows
5. **ALWAYS** log important state changes and decisions
6. **ALWAYS** log errors with exception details

### Log Message Standards
- Start with action: "Starting...", "Processing...", "Completed..."
- Include parameter values when relevant
- Use string formatting for better performance: `"Value: %s"` not string concatenation
- Keep messages concise but descriptive

## Code Quality Standards

### CLEAN Code Principles (MANDATORY)
1. **Single Responsibility**: Each class/function does ONE thing well
2. **Open/Closed**: Open for extension, closed for modification
3. **Liskov Substitution**: Derived classes must be substitutable
4. **Interface Segregation**: Many specific interfaces over general ones
5. **Dependency Inversion**: Depend on abstractions, not concretions

### DRY (Don't Repeat Yourself) - STRICTLY ENFORCED
1. **NO** duplicate code - extract to functions/classes
2. **NO** copy-paste programming - create reusable components
3. **NO** similar logic in multiple places - centralize it
4. **ALWAYS** look for existing solutions before creating new ones
5. **ALWAYS** refactor when you see duplication

### Code Quality Rules
- Functions: Maximum 20 lines (prefer under 10)
- Classes: Single responsibility only
- Names: Self-documenting, no comments needed
- Parameters: Maximum 3 (use data classes for more)
- Nesting: Maximum 2 levels deep
- No magic numbers/strings - use constants
- Early returns over nested if statements
- Composition over inheritance

## Constants Management (MANDATORY)

### NO HARDCODED VALUES - Use Constants
**ALL** non-resource values must be defined as constants:

1. **Numeric Values**
   - Timeouts, delays, limits
   - Array sizes, max values
   - Animation durations
   - Any numeric literal

2. **String Keys**
   - SharedPreferences keys
   - Bundle/Intent keys
   - Database column names
   - API parameter names

3. **Configuration Values**
   - Feature flags
   - Build variants
   - API endpoints
   - Version codes

### Examples of VIOLATIONS ❌
```kotlin
// WRONG - Magic numbers
if (tasks.size > 50) { ... }
delay(3000)
wakeLock.acquire(30000)

// WRONG - Magic strings
prefs.getBoolean("showYearCountdown", true)
intent.putExtra("event_id", eventId)
```

### Correct Implementation ✅
```kotlin
companion object {
    // Numeric constants
    private const val MAX_TASKS_PER_EVENT = 50
    private const val NOTIFICATION_DELAY_MS = 3000L
    private const val WAKE_LOCK_TIMEOUT_MS = 30_000L
    
    // String constants
    private const val PREF_SHOW_YEAR_COUNTDOWN = "showYearCountdown"
    private const val EXTRA_EVENT_ID = "event_id"
    
    // Configuration
    private const val DATABASE_VERSION = 1
    private const val MAX_ALARM_COUNT = 500
}

// Usage
if (tasks.size > MAX_TASKS_PER_EVENT) { ... }
delay(NOTIFICATION_DELAY_MS)
prefs.getBoolean(PREF_SHOW_YEAR_COUNTDOWN, true)
```

### Constant Organization Rules
1. Group related constants together
2. Use UPPER_SNAKE_CASE for true constants
3. Prefix with context (PREF_, EXTRA_, KEY_, etc.)
4. Document units in name (_MS, _SECONDS, _DP)
5. Place in companion object or object class
6. Consider extracting to separate Constants file for shared values

**SEE**: `clean-code-examples.md` for examples of good vs bad code

## Resource Management (MANDATORY)

### ALL UI Values MUST Go in Resources
**NO HARDCODED VALUES IN CODE** - Everything must be in appropriate resource files:

1. **Strings** → `res/values/strings.xml`
   - ALL user-facing text
   - Error messages
   - Button labels
   - Descriptions
   
2. **Colors** → `res/values/colors.xml` or Theme
   - ALL color values
   - Use semantic names (e.g., `error_color`, not `red`)
   - Reference from theme when possible

3. **Dimensions** → `res/values/dimens.xml`
   - ALL spacing values (padding, margin)
   - Text sizes
   - Component sizes
   - Use dp/sp appropriately

4. **Styles** → `res/values/styles.xml` or Theme
   - Reusable component styles
   - Text appearances
   - Shape definitions

### Examples of VIOLATIONS ❌
```kotlin
// WRONG - Hardcoded string
Text("Add Task")

// WRONG - Hardcoded color
color = Color(0xFF6366F1)

// WRONG - Hardcoded dimension
padding(16.dp)

// WRONG - Hardcoded text size
fontSize = 18.sp
```

### Correct Implementation ✅
```kotlin
// CORRECT - String from resources
Text(stringResource(R.string.add_task))

// CORRECT - Color from theme
color = MaterialTheme.colorScheme.primary

// CORRECT - Dimension from resources
padding(dimensionResource(R.dimen.standard_padding))

// CORRECT - Text style from theme
style = MaterialTheme.typography.titleMedium
```

### Resource Organization Rules
1. Group related strings with comments
2. Use descriptive IDs: `task_delete_confirmation` not `msg1`
3. Provide translatable="false" for non-translatable strings
4. Use string formatting for dynamic content
5. Define color palettes in theme, not individual colors
6. Create dimension constants for consistent spacing