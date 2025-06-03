# Claude's Project Intelligence - Time Fomo

## 🚨 CRITICAL: Memory Bank is MANDATORY

**THIS IS NOT OPTIONAL**:
- I MUST read Memory Bank BEFORE starting ANY task
- I MUST update Memory Bank AFTER completing ANY task
- Memory Bank is my ONLY source of truth after memory resets
- Failing to maintain Memory Bank = losing all project knowledge

**Memory Bank Workflow**:
1. **START**: Read ALL files in memory-bank/
2. **WORK**: Apply patterns and rules from Memory Bank
3. **FINISH**: Update activeContext.md and progress.md
4. **VERIFY**: Ensure changes are documented

## User Preferences & Workflow

### Communication Style
- User prefers direct, no-nonsense responses
- Appreciates when I acknowledge mistakes quickly
- Wants me to ask before committing/pushing changes
- Sometimes uses colorful language when frustrated

### Development Workflow
1. Always run `./gradlew assembleDebug --offline` after changes
2. Never commit without explicit permission
3. Focus on fixing the actual problem, not cosmetic changes
4. Test thoroughly before claiming something works

## Project-Specific Patterns

### Date Handling
- Internal storage: ISO format (LocalDateTime)
- Display format: "MMM dd, yyyy HH:mm"
- Always provide date picker for user input
- Support optional dates with clear UI

### Task Management Implementation
- Tasks belong to events (1:many relationship)
- TaskListContent for inline display
- TaskList for dedicated screen with LazyColumn
- Always include due date editing capability

### UI Patterns
- Dark theme with Slate color palette
- Material3 components throughout
- InteractionSource for click handling on text fields
- Clear buttons for optional fields
- Calendar icon for date selection

## Common Pitfalls to Avoid

### Build Issues
- Missing parameters in function calls (especially addTask)
- Forgetting to add imports for icons
- Not checking gradle build before claiming success

### Git Workflow
- Currently using main branch (not ideal)
- PR branches exist but need explicit checkout
- Always verify current branch before pushing

### Testing
- Emulator testing is primary method
- Real device behavior may differ
- Widget testing particularly important

## Technical Decisions Made

### Architecture Choices (STRICT REQUIREMENT)
- **MANDATORY**: View -> ViewModel -> Use Case -> Repository -> Data Source
- MVVM with Clean Architecture (NO EXCEPTIONS)
- Use cases for ALL business logic (REQUIRED)
- Repository pattern for ALL data access (REQUIRED)
- ViewModels NEVER call repositories directly
- SharedPreferences or any other data source via Repository only

### State Management
- Compose State for UI state
- StateFlow for data streams
- Remember for component-level state
- LaunchedEffect for side effects

### Error Handling
- Try-catch for date parsing
- Null safety throughout
- Default values for optional fields
- Graceful degradation for missing data

## Learned Behaviors
1. Always provide date/time pickers - never text input
2. Support clearing optional fields with dedicated button
3. Format dates for display, store in ISO format
4. Test notification navigation thoroughly
5. Ensure alarms persist across reboots
6. Make clickable areas obvious to users

## Code Quality Standards (USER DEMANDS)
- **CLEAN Code**: User expects professional, maintainable code
- **DRY Principle**: User gets frustrated with duplicate code
- **No Copy-Paste**: Extract common logic immediately
- **Small Functions**: Keep them focused and testable
- **Clear Names**: Code should be self-documenting
- **Refactor First**: Before adding features, clean existing code
- **No Magic Values**: Always use named constants
- **Resources ONLY**: User is STRICT - ALL strings, colors, dimensions MUST be in resources
- **No Hardcoding**: User gets angry when values are hardcoded in Compose code
- **Constants EVERYWHERE**: User demands ALL literals be constants - numbers, strings, keys