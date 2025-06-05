# Code Decoupling Analysis

This report summarizes the current decoupling level of the `pipeline-test` Android project and how suitable it is for parallel development.

## Overall Architecture
- The project follows MVVM + Clean Architecture as described in the memory bank. Use cases sit between ViewModels and repositories. Example implementation can be seen in `CreateTaskUseCase.kt` and `TaskViewModel.kt`.
- Manual dependency injection is used in `MainActivity`. There is only a single Gradle module, so features share the same source set.

## Positive Examples
- **Use Case Separation** – business logic resides in domain classes like `GenerateMotivationalTextUseCase` and `ToggleTaskCompletionUseCase`. ViewModels depend on these use cases instead of repositories directly.
- **Repository Layer** – `TaskRepository` encapsulates `Prefs` persistence and exposes a Flow for tasks. This decouples data access from domain logic.
- **ViewModel Isolation** – screens such as `TaskOverviewScreen` retrieve state from `TaskViewModel` rather than touching repositories.

## Coupling Issues
- **Monolithic Activity** – `MainActivity.kt` handles navigation, initializes view models, and defines screen constants in one file. Any feature change requires editing this large file, increasing merge‑conflict risk. Example constants appear around lines 226‑242【F:app/src/main/java/com/jahi/pipelinetest/MainActivity.kt†L226-L242】.
- **Long Use Case Functions** – `GenerateMotivationalTextUseCase` is 264 lines long【1f7c95†L1-L4】. Large functions are hard to maintain and invite conflicts when multiple developers modify them.
- **UI Logic Duplication** – `TaskList.kt` and `TaskListContent.kt` contain nearly identical implementations of task entry forms and date pickers. Hardcoded strings such as `"Due date (optional)"` appear around lines 126‑150 in `TaskListContent.kt`【F:app/src/main/java/com/jahi/pipelinetest/ui/components/TaskListContent.kt†L126-L150】.
- **Hardcoded Constants** – many strings and dp values are directly embedded in Compose code rather than resources. This violates memory‑bank guidelines and ties UI code to presentation details.
- **Prefs Coupling** – `TaskRepository` directly manipulates `SharedPreferences` keys and JSON parsing. Other components might share these keys, so edits could affect multiple features.

## Merge‑Conflict Considerations
- Centralized files (`MainActivity.kt`, `Prefs.kt`, `GenerateMotivationalTextUseCase.kt`) will often require edits for new features. Concurrent changes to these files are likely to collide.
- Duplicate code (Task list UIs) means bug fixes must be applied twice, which can diverge in different branches.
- Lack of modularization (single module) forces all teams to work in the same codebase without isolation.
- On the plus side, the use‑case architecture provides clear seams where new logic can be added without touching existing code.

## Recommendations
1. **Extract Constants** – move navigation labels and strings to `strings.xml` and numeric values to a `Constants` object.
2. **Reduce Large Functions** – break `GenerateMotivationalTextUseCase` into smaller helpers.
3. **Consolidate Task UI Components** – merge `TaskList.kt` and `TaskListContent.kt` to a single reusable component.
4. **Consider Modularization** – creating feature modules would isolate code and minimize conflicts.
5. **Introduce Dependency Injection Framework** – a DI tool could provide decoupling and reduce the need to modify `MainActivity.kt`.

## Conclusion
While the project follows MVVM and use‑case patterns, several areas remain tightly coupled. Large files, duplicated UI logic and hardcoded constants reduce maintainability and make parallel development prone to conflicts. Addressing these issues will improve decoupling and support smoother collaboration.

