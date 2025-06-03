# Architecture Implementation Example

## MANDATORY Pattern for ALL Features

### Example: Adding a new "Archive Task" feature

#### 1. Create Use Case (domain/ArchiveTaskUseCase.kt)
```kotlin
class ArchiveTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Int) {
        // Business logic here
        val task = taskRepository.getTask(taskId)
        val archivedTask = task.copy(isArchived = true)
        taskRepository.updateTask(archivedTask)
    }
}
```

#### 2. Update Repository (repository/TaskRepository.kt)
```kotlin
class TaskRepository(private val prefs: Prefs) {
    suspend fun getTask(taskId: Int): Task {
        // Data access logic here
        return prefs.tasks.find { it.id == taskId } 
            ?: throw TaskNotFoundException()
    }
    
    suspend fun updateTask(task: Task) {
        // Data persistence logic here
        val tasks = prefs.tasks.toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            prefs.tasks = tasks
        }
    }
}
```

#### 3. Update ViewModel (viewmodel/TaskViewModel.kt)
```kotlin
class TaskViewModel(
    private val createTaskUseCase: CreateTaskUseCase,
    private val archiveTaskUseCase: ArchiveTaskUseCase, // Injected via constructor
    // ... other use cases
) : ViewModel() {
    
    fun archiveTask(taskId: Int) {
        viewModelScope.launch {
            archiveTaskUseCase(taskId) // ONLY calls use case, NEVER repository
        }
    }
}
```

#### 4. Update ViewModelFactory
```kotlin
class TaskViewModelFactory(
    private val createTaskUseCase: CreateTaskUseCase,
    private val archiveTaskUseCase: ArchiveTaskUseCase, // Add here
    // ... other use cases
) : ViewModelProvider.Factory {
    // ...
}
```

#### 5. Update View (Compose UI)
```kotlin
@Composable
fun TaskItem(
    task: Task,
    taskViewModel: TaskViewModel
) {
    IconButton(
        onClick = { taskViewModel.archiveTask(task.id) } // ONLY calls ViewModel
    ) {
        Icon(Icons.Default.Archive, contentDescription = "Archive")
    }
}
```

## What NOT to Do ❌

### WRONG - ViewModel calling Repository directly:
```kotlin
class TaskViewModel(
    private val taskRepository: TaskRepository // ❌ NEVER DO THIS
) : ViewModel() {
    fun archiveTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.archiveTask(taskId) // ❌ WRONG
        }
    }
}
```

### WRONG - View calling Use Case or Repository:
```kotlin
@Composable
fun TaskItem(
    task: Task,
    archiveTaskUseCase: ArchiveTaskUseCase // ❌ NEVER DO THIS
) {
    IconButton(
        onClick = { archiveTaskUseCase(task.id) } // ❌ WRONG
    ) {
        Icon(Icons.Default.Archive, contentDescription = "Archive")
    }
}
```

## Checklist for New Features
- [ ] Create Use Case in domain package
- [ ] Inject Repository into Use Case constructor
- [ ] Add Use Case to ViewModel constructor
- [ ] Update ViewModelFactory
- [ ] Update MainActivity to provide Use Case
- [ ] View only calls ViewModel methods
- [ ] ViewModel only calls Use Case methods
- [ ] Use Case only calls Repository methods
- [ ] Repository handles all data source operations