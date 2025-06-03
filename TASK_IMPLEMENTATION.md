# Task/To-Do List Integration

This document describes the implementation of the Task/To-Do List functionality integrated with custom events in the pipeline-test Android app.

## Architecture Overview

The implementation follows **MVVM architecture** with **Repository pattern** and **Use Cases** for clean separation of concerns:

```
UI Layer (Compose) → ViewModel → Use Cases → Repository → Data Layer (SharedPreferences)
```

## Data Models

### Task Entity
```kotlin
data class Task(
    val id: Int,                    // Auto-generated unique identifier
    val eventId: Int,              // Foreign key to CustomEvent
    var description: String,        // Task description
    var isCompleted: Boolean,       // Completion status
    val createdAt: String,         // Creation timestamp
    var dueDate: String?           // Optional due date
)
```

### Relationship
- **One-to-Many**: One event can have many tasks
- **Always Attached**: Tasks must belong to an event (no standalone tasks)

## Data Persistence

Uses **SharedPreferences** (same pattern as existing CustomEvent storage):
- JSON serialization for task storage
- Reactive updates via StateFlow
- Consistent with existing app architecture

## Repository Pattern

### TaskRepository
- Abstracts data operations from ViewModels
- Provides reactive data via Flow
- Handles CRUD operations
- Manages one-to-many relationship

Key methods:
- `addTask(task: Task)`
- `updateTask(task: Task)`
- `deleteTask(taskId: Int)`
- `getTasksForEvent(eventId: Int): List<Task>`
- `toggleTaskCompletion(taskId: Int)`

## Use Cases (Business Logic)

### CreateTaskUseCase
Creates new task attached to specific event

### GetTasksUseCase
Retrieves all tasks for a given event

### UpdateTaskUseCase
Updates existing task properties

### DeleteTaskUseCase
Removes task by ID

### ToggleTaskCompletionUseCase
Toggles task completion status

## ViewModels

### TaskViewModel
- Manages UI state for task operations
- Uses use cases for business logic
- Provides reactive data to UI
- Handles user interactions

Key features:
- Task list state management
- Add task UI state
- Task operations (CRUD)

## UI Components

### TaskList
Main UI component showing:
- Task progress indicator (n/m completed)
- Add task functionality
- Task checklist with completion toggle
- Delete task functionality

### CountdownCard (Enhanced)
Event cards now show:
- Task progress bar
- Expandable task management
- Click to show/hide tasks
- Visual task completion status

## Integration Points

### MainActivity
- Initializes dependency injection
- Creates TaskRepository and Use Cases
- Provides TaskViewModel to UI

### CountdownsScreen
- Integrates TaskList into event display
- Shows task progress for each event
- Allows task management per event

## User Experience

1. **View Tasks**: Click on any event card to expand tasks
2. **Add Task**: Click "Add Task" button, enter description, confirm
3. **Complete Task**: Check/uncheck task checkbox
4. **Delete Task**: Click delete icon next to task
5. **Progress**: Visual progress bar shows completion status

## Benefits

✅ **Clean Architecture**: MVVM + Repository + Use Cases  
✅ **Reactive UI**: StateFlow provides automatic UI updates  
✅ **Type Safety**: Kotlin data classes with proper typing  
✅ **Testable**: Use cases can be unit tested independently  
✅ **Consistent**: Follows same patterns as existing app code  
✅ **Persistent**: Tasks saved across app restarts  
✅ **User Friendly**: Intuitive task management UI  

## Future Enhancements

When Android build environment is fixed, can easily migrate to:
- **Room Database**: Replace SharedPreferences with proper SQLite
- **Due Date UI**: Add date picker for task due dates
- **Task Categories**: Add task types or priorities
- **Sync**: Add cloud synchronization
- **Notifications**: Task deadline reminders

## File Structure

```
app/src/main/java/com/jahi/pipelinetest/
├── model/
│   └── Task.kt                          # Task data model
├── repository/
│   └── TaskRepository.kt                # Data access layer
├── domain/
│   ├── CreateTaskUseCase.kt             # Business logic
│   ├── GetTasksUseCase.kt
│   ├── UpdateTaskUseCase.kt
│   ├── DeleteTaskUseCase.kt
│   └── ToggleTaskCompletionUseCase.kt
├── viewmodel/
│   └── TaskViewModel.kt                 # UI state management
├── ui/components/
│   └── TaskList.kt                      # Task UI components
├── Prefs.kt                            # Updated with task storage
├── MainActivity.kt                      # Updated with task dependencies
└── CountdownScreens.kt                  # Updated with task integration
```