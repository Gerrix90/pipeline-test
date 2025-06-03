# CLEAN Code & DRY Examples

## ❌ BAD Code (What NOT to do)

### Duplicate Code
```kotlin
// In TaskListContent.kt
val displayDate = selectedDate?.let {
    try {
        val dateTime = LocalDateTime.parse(it)
        dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    } catch (e: Exception) {
        it
    }
} ?: ""

// In TaskItem.kt - DUPLICATE!
val displayDate = task.dueDate?.let {
    try {
        val dateTime = LocalDateTime.parse(it)
        dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    } catch (e: Exception) {
        it
    }
} ?: ""
```

### Long Functions
```kotlin
fun processTask(task: Task) {
    // 50+ lines of code doing multiple things
    // Validating
    // Formatting  
    // Saving
    // Notifying
    // Updating UI
    // etc...
}
```

### Magic Values
```kotlin
if (tasks.size > 50) { // What is 50?
    showWarning()
}

val delay = 30000 // What is this number?

// WRONG - Hardcoded strings
prefs.getString("customEvents", "[]")
intent.putExtra("event_name", eventName)
```

## ✅ GOOD Code (What TO do)

### DRY - Extract Common Logic
```kotlin
// In DateFormatter.kt
object DateFormatter {
    fun formatForDisplay(isoDate: String?): String {
        return isoDate?.let {
            try {
                LocalDateTime.parse(it)
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
            } catch (e: Exception) {
                it
            }
        } ?: ""
    }
}

// Usage everywhere
val displayDate = DateFormatter.formatForDisplay(selectedDate)
```

### Small, Focused Functions
```kotlin
fun processTask(task: Task) {
    validateTask(task)
    val formattedTask = formatTask(task)
    saveTask(formattedTask)
    notifyTaskUpdate(formattedTask)
}

private fun validateTask(task: Task) {
    // Only validation logic
}

private fun formatTask(task: Task): Task {
    // Only formatting logic
}

private fun saveTask(task: Task) {
    // Only saving logic
}
```

### Named Constants
```kotlin
companion object {
    private const val MAX_TASKS_PER_EVENT = 50
    private const val WAKE_LOCK_TIMEOUT_MS = 30_000
    private const val DATE_DISPLAY_FORMAT = "MMM dd, yyyy HH:mm"
}

if (tasks.size > MAX_TASKS_PER_EVENT) {
    showWarning()
}
```

## Refactoring Checklist

Before committing ANY code:
- [ ] No duplicate code blocks
- [ ] All functions under 20 lines
- [ ] No magic numbers or strings
- [ ] Clear, self-documenting names
- [ ] Single responsibility per class/function
- [ ] Common logic extracted to utilities
- [ ] Constants defined for all fixed values
- [ ] Early returns instead of nested ifs

## Common Patterns to Extract

### Date Handling
```kotlin
object DateUtils {
    fun formatForDisplay(date: String?): String
    fun formatForStorage(date: LocalDateTime): String
    fun parseOrNull(date: String): LocalDateTime?
    fun isValidDate(date: String): Boolean
}
```

### UI State Management
```kotlin
sealed class UiState<T> {
    class Loading<T> : UiState<T>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String) : UiState<T>()
}
```

### Common Extensions
```kotlin
fun String?.orEmpty(): String = this ?: ""
fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
```

Remember: If you write something twice, it should be a function. If you write it three times, it should be a utility class.

## Constants Examples

### ❌ BAD - Hardcoded Values Throughout Code
```kotlin
class TaskViewModel {
    fun scheduleAlarm() {
        // WRONG - What is 50?
        if (alarms.size >= 50) return
        
        // WRONG - What is 30000?
        wakeLock.acquire(30000)
    }
}

class Prefs(context: Context) {
    var customEvents: List<CustomEvent>
        get() {
            // WRONG - Hardcoded key
            val json = prefs.getString("customEvents", "[]")
            // ...
        }
}

class MainActivity {
    fun handleIntent(intent: Intent) {
        // WRONG - Hardcoded extra key
        val eventId = intent.getIntExtra("event_id", -1)
    }
}
```

### ✅ GOOD - Using Constants
```kotlin
class TaskViewModel {
    companion object {
        private const val MAX_ALARMS = 50
        private const val WAKE_LOCK_TIMEOUT_MS = 30_000L
    }
    
    fun scheduleAlarm() {
        if (alarms.size >= MAX_ALARMS) return
        wakeLock.acquire(WAKE_LOCK_TIMEOUT_MS)
    }
}

class Prefs(context: Context) {
    companion object {
        private const val KEY_CUSTOM_EVENTS = "customEvents"
        private const val DEFAULT_EVENTS_JSON = "[]"
    }
    
    var customEvents: List<CustomEvent>
        get() {
            val json = prefs.getString(KEY_CUSTOM_EVENTS, DEFAULT_EVENTS_JSON)
            // ...
        }
}

object IntentKeys {
    const val EXTRA_EVENT_ID = "event_id"
    const val EXTRA_EVENT_NAME = "event_name"
    const val EXTRA_TASK_ID = "task_id"
}

class MainActivity {
    fun handleIntent(intent: Intent) {
        val eventId = intent.getIntExtra(IntentKeys.EXTRA_EVENT_ID, -1)
    }
}
```

### Constants Organization Pattern
```kotlin
// For class-specific constants
class MyClass {
    companion object {
        private const val MAX_ITEMS = 100
        private const val TIMEOUT_MS = 5000L
    }
}

// For shared constants across features
object Constants {
    object Prefs {
        const val KEY_USER_NAME = "user_name"
        const val KEY_SETTINGS = "settings"
    }
    
    object Intents {
        const val ACTION_REFRESH = "com.app.ACTION_REFRESH"
        const val EXTRA_DATA = "extra_data"
    }
    
    object Limits {
        const val MAX_TASKS_PER_EVENT = 50
        const val MAX_EVENTS = 100
    }
}
```

## Resource Management Examples

### ❌ VIOLATIONS - Hardcoded Values
```kotlin
@Composable
fun TaskItem() {
    Card(
        modifier = Modifier.padding(16.dp), // WRONG - hardcoded
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B) // WRONG - hardcoded
        )
    ) {
        Text(
            text = "Task Description", // WRONG - hardcoded
            fontSize = 16.sp, // WRONG - hardcoded
            color = Color.White // WRONG - hardcoded
        )
        Button(onClick = {}) {
            Text("Delete") // WRONG - hardcoded
        }
    }
}
```

### ✅ CORRECT - Using Resources
```kotlin
@Composable
fun TaskItem() {
    Card(
        modifier = Modifier.padding(dimensionResource(R.dimen.card_padding)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = stringResource(R.string.task_description_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Button(onClick = {}) {
            Text(stringResource(R.string.delete_action))
        }
    }
}
```

### Resource Files Structure

**strings.xml**
```xml
<resources>
    <!-- Task Management -->
    <string name="task_description_label">Task Description</string>
    <string name="delete_action">Delete</string>
    <string name="add_task_button">Add Task</string>
    <string name="task_due_date_format">Due: %1$s</string>
</resources>
```

**dimens.xml**
```xml
<resources>
    <!-- Standard Spacing -->
    <dimen name="spacing_small">4dp</dimen>
    <dimen name="spacing_medium">8dp</dimen>
    <dimen name="spacing_large">16dp</dimen>
    <dimen name="spacing_xlarge">24dp</dimen>
    
    <!-- Component Specific -->
    <dimen name="card_padding">16dp</dimen>
    <dimen name="button_height">48dp</dimen>
    <dimen name="icon_size">24dp</dimen>
</resources>
```