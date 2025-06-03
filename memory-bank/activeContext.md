# Active Context - Current Work Status

## 🚨 THIS FILE MUST BE UPDATED AFTER EVERY TASK

## Recent Changes (June 3, 2024)

### Completed Today
1. **Fixed floating action button removal** - Removed FAB from task screen per user request
2. **Fixed PR #83 issues**:
   - Added BootReceiver for alarm persistence across reboots
   - Updated notifications to navigate to Tasks screen
   - Implemented proper intent handling
3. **Added due date picker to tasks**:
   - Tasks can now have optional due dates
   - Date picker opens on field click or calendar icon
   - Clear button to remove dates
   - User-friendly date formatting
4. **Fixed task due date editing**:
   - Existing tasks can now edit due dates
   - Improved interaction handling
   - Better visual indicators

## Current Focus
- Task management functionality enhancements
- Ensuring alarm persistence and reliability
- UI/UX improvements for date selection

## Known Issues
- Some deprecated API warnings (LinearProgressIndicator, etc.)
- Need to update to newer Material3 APIs

## Next Steps
1. Test alarm functionality after reboot
2. Verify task notifications work correctly
3. Consider adding task priority levels
4. Improve task sorting/filtering options
5. Add task completion statistics

## Active Decisions
- Using ISO date format for internal storage
- Showing date/time in user-friendly format for display
- Maintaining backward compatibility with existing tasks
- Keeping UI consistent across all date pickers
- **STRICT ENFORCEMENT**: All new features MUST follow View -> ViewModel -> Use Case -> Repository -> Data Source pattern
- **STRICT ENFORCEMENT**: CLEAN code and DRY principles - NO duplicate code, small focused functions

## Code Quality Debt to Address
- Date formatting logic duplicated in multiple places - needs DateFormatter utility
- Long functions in some ViewModels - need refactoring
- Magic numbers for alarm timeouts - need constants
- Similar task UI logic in TaskList and TaskListContent - needs common component
- **CRITICAL**: Hardcoded strings throughout UI - must move to strings.xml
- **CRITICAL**: Hardcoded dimensions (16.dp, 8.dp, etc.) - must move to dimens.xml
- **CRITICAL**: Direct color values in Compose - must use theme colors only
- **CRITICAL**: Magic numbers (50, 30000, etc.) - must be constants
- **CRITICAL**: Hardcoded SharedPreferences keys - must be constants
- **CRITICAL**: Hardcoded Intent/Bundle keys - must be constants