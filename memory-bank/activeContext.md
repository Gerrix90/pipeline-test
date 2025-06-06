# Active Context - Current Work Status

## 🚨 THIS FILE MUST BE UPDATED AFTER EVERY TASK

## Recent Changes (June 5, 2025)

### Completed Today
1. **Codebase Decoupling Analysis**:
   - Generated `CodeDecouplingAnalysis.md` summarizing modularity and merge conflict risk.
   - Build verified with `./gradlew assembleDebug --offline`.
2. **Removed Hardcoded UI Constants**:
- Moved strings and dp values from `ChatScreen` and `CountdownScreens` to `strings.xml` and `dimens.xml`.
- Replaced constants with `stringResource()` and `dimensionResource()` calls.
- Build verified with `./gradlew assembleDebug --offline`.

## Recent Changes (June 6, 2025)

### Completed Today
1. **Removed More Hardcoded UI Values**:
   - Moved remaining strings and dimensions from `SettingsScreen`, `TaskOverviewScreen`, and gallery dialogs into resources.
   - Added new dimension constants (`padding_xsmall`, `padding_medium`, `padding_tiny`, `spacer_height_large`, `dialog_corner_radius`).
   - Added new strings (`back_button_desc`, `placeholder_enter_content`, `label_preview_prompt`, `close`, `error_title`, `action_import`, `dialog_import_model`).
   - Updated Compose code to use `stringResource()` and `dimensionResource()`.
   - Build verified with `./gradlew assembleDebug --offline`.

## Recent Changes (June 7, 2025)

### Completed Today
1. **Replaced Remaining Hardcoded Constants**:
   - Moved strings and dp values from `TaskList` and gallery chat components into resources.
   - Added new dimensions (`padding_xxsmall`, `padding_regular`, `task_progress_width`, `task_progress_height`, `icon_size_small`, `padding_chat_*`, `slider_height_small`, `text_field_width_small`, `border_width_*`).
   - Added new strings (`title_tasks`, `text_copied`, `copy_text`, `action_take_picture`, `action_pick_from_album`, `action_prompt_templates`, `action_input_history`, `download_try_it`, `live_camera`).
   - Updated Compose files to use resource references.
   - Build verified with `./gradlew assembleDebug --offline`.

## Recent Changes (June 4, 2025)

### Completed Today
1. **Implemented AI Gallery Back Navigation**:
   - **BACK BUTTON HANDLING**: Added BackHandler to GalleryScreen to return to Time Fomo when system back button is pressed
   - **NAVIGATION FLOW**: AI Gallery now properly returns to previous screen (Countdowns) instead of exiting app
   - **SOLUTION APPLIED**:
     * Added BackHandler composable to GalleryScreen.kt:169-172
     * Connected to onBackPressed callback from MainActivity
     * System back button now calls `viewModel.selectScreen(SCREEN_COUNTDOWNS)`
   - **BUILD VERIFIED**: All builds successful, navigation working correctly
   - **USER EXPERIENCE**: Resolves issue where back button would exit entire app from AI Gallery

2. **Reordered Navigation Tabs**:
   - Life tab moved to last position
   - New order: Countdowns, Tasks, AI Gallery, Life

3. **Implemented Complete AI Gallery Fullscreen Experience**:
   - **FULLSCREEN MODE**: AI Gallery now displays without Time Fomo app bars when selected
   - **ORIGINAL UI RESTORED**: Completely replaced GalleryScreen with original Google AI Edge Gallery HomeScreen
   - **SOLUTION APPLIED**:
     * Modified MainActivity.kt to show AI Gallery fullscreen (`else if (viewModel.screen == SCREEN_GALLERY)`)
     * Copied complete HomeScreen.kt from `/gallery/Android/src/app/src/main/java/com/google/ai/edge/gallery/ui/home/HomeScreen.kt`
     * Updated all package names from `com.google.ai.edge.gallery` to `com.jahi.pipelinetest`
     * Created GalleryTopAppBar component with logo and "AI Gallery" title
     * Fixed gradient and color issues using Material3 standard colors
   - **COMPONENTS IMPLEMENTED**:
     * GalleryTopAppBar with logo icon and "AI Gallery" title
     * Settings icon functionality
     * NewReleaseNotification banner
     * TaskCard components with animated model count
     * Import model FloatingActionButton and dialogs
     * Complete navigation system: TaskList → ModelManager → Chat screens
   - **LOCATIONS**: 
     * `/app/src/main/java/com/jahi/pipelinetest/MainActivity.kt:112-114` (fullscreen mode)
     * `/app/src/main/java/com/jahi/pipelinetest/GalleryScreen.kt` (complete replacement)
     * `/app/src/main/java/com/jahi/pipelinetest/gallery/ui/common/GalleryTopAppBar.kt` (new component)
   - **BUILD VERIFIED**: All builds successful, navigation working
   - **UI RESULT**: AI Gallery now identical to original Google AI Edge Gallery with full functionality
1. **Fixed Widget Model Implementation Issues**:
   - **CRITICAL FIX**: Removed session reset before each generation in GenerateMotivationalTextUseCase
   - **MODEL INITIALIZATION**: Added proper model initialization waiting pattern from LlmChatViewModel
   - **SESSION MANAGEMENT**: Fixed session management to prevent repetitive responses
   - **CONTEXT PRESERVATION**: Widgets now maintain model context between generations
   - **FOLLOWING PATTERN**: Applied LlmChatViewModel patterns to widget model usage
   - **BUILD VERIFIED**: All changes compile successfully

2. **Resolved Widget Text Length and Cleaning Issues**:
   - **SAFETY LIMIT FIXED**: Increased timeout from 10s to 30s and length limit from 500 to 1000 chars
   - **FULL TEXT PRESERVATION**: Fixed cleanUpResponse() to preserve complete AI-generated content
   - **TTS INTEGRATION**: Complete AI responses now properly sent to TTS instead of truncated versions
   - **MULTIPLE QUOTES**: AI now generates multiple motivational quotes in single response
   - **DETAILED LOGGING**: Added comprehensive logging for debugging text cleaning process
   - **WORKING PERFECTLY**: Widget generates and plays complete AI motivational content via TTS

3. **Enhanced AI Content Variety and Uniqueness**:
   - **EXPANDED PROMPTS**: Increased from 5 to 20 diverse motivational prompts covering various themes
   - **RANDOMNESS INJECTION**: Added random prompt modifiers (70% chance) with creativity instructions
   - **SESSION MANAGEMENT**: Implemented periodic session reset every 5 generations to prevent repetition
   - **GENERATION TRACKING**: Added counter to track generations and optimize session management
   - **CONTENT DIVERSITY**: Significantly improved variety in AI-generated motivational quotes
   - **ANTI-REPETITION**: Systematic approach to prevent model from falling into repetitive patterns

4. **Fixed Settings Back Navigation**:
   - **BACKHANDLER ADDED**: Implemented BackHandler in SettingsScreen
   - **GESTURE NAVIGATION**: System back gesture now returns to home instead of closing app
   - **BUILD VERIFIED**: `./gradlew assembleDebug --offline` successful

### Completed June 3, 2024
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
5. **Fixed widget generate button functionality**:
   - Added ElevenLabs API key setting in Settings screen General tab
   - Updated Prefs class to store API key securely using EncryptedSharedPreferences
   - Modified GenerateAudioUseCase to use stored API key instead of build config
   - Generate button now works when API key is provided
   - Updated MainViewModel to expose API key setting
   - Added androidx.security:security-crypto dependency for encrypted storage
   - API key is now stored encrypted for security compliance
6. **Completed AI Gallery integration**:
   - Created functional GalleryScreen.kt with feature showcase UI
   - Added new navigation item "AI Gallery" to bottom navigation bar  
   - Updated MainActivity.kt to use constants for all screen indices and labels
   - Replaced hardcoded strings with constants following Memory Bank standards
   - Added screen routing for Gallery screen (index 2)
   - Built dependency structure for Gallery app (navigation, serialization, icons, etc.)
   - Created interactive feature cards showing AI capabilities:
     * LLM Chat (marked as available)
     * Image Classification (coming soon)
     * Text Classification (coming soon) 
     * Image Generation (coming soon)
   - Gallery resources (drawables, fonts, strings, dimensions) integrated
   - Built and tested - complete navigation working
7. **Copied Gallery UI Common Components**:
   - Copied 8 essential UI components from gallery/Android/src/app/src/main/java/com/google/ai/edge/gallery/ui/common/ 
   - Updated all package names from "com.google.ai.edge.gallery" to "com.jahi.pipelinetest.gallery"
   - Updated imports and R references to use pipeline-test package structure
   - Components include: AuthConfig, DownloadAndTryButton, ErrorDialog, ModelPageAppBar, ModelPicker, ModelPickerChipsPager, TaskIcon, Utils
   - All files successfully created in app/src/main/java/com/jahi/pipelinetest/gallery/ui/common/
8. **Fixed Gallery R Import References**:
   - Fixed R import in MessageInputText.kt (com.jahi.pipelinetest.gallery.R → com.jahi.pipelinetest.R)
   - Fixed R import in MessageSender.kt (com.jahi.pipelinetest.gallery.R → com.jahi.pipelinetest.R)
   - Fixed R import in ModelDownloadingAnimation.kt (com.jahi.pipelinetest.gallery.R → com.jahi.pipelinetest.R)
   - Fixed R import in ModelInitializationStatus.kt (com.jahi.pipelinetest.gallery.R → com.jahi.pipelinetest.R)
   - Fixed R import in TextInputHistorySheet.kt (com.jahi.pipelinetest.gallery.R → com.jahi.pipelinetest.R)
   - Fixed R reference in PromptTemplatesPanel.kt (com.jahi.pipelinetest.gallery.R.dimen → R.dimen)
   - All gallery files now correctly reference the main package R class for strings and resources
8. **Implemented Real AI Text Generation for Widgets**:
   - **COMPLETE**: Replaced simulation with actual MediaPipe LLM inference for dynamic text generation
   - **COMPLETE**: Added automatic model discovery in __imports directory for imported LLM models  
   - **COMPLETE**: Implemented robust response cleaning to extract clean motivational quotes from AI output
   - **COMPLETE**: Added comprehensive error handling with graceful fallback to hardcoded sentences
   - **COMPLETE**: Fixed JSON escaping in TTS requests to handle quotes in AI-generated text properly
   - **COMPLETE**: Added detailed logging for debugging AI initialization and generation process
   - **COMPLETE**: Used few-shot prompting with examples to guide AI toward concise responses
   - **COMPLETE**: Support for both imported models and downloaded models from Gallery allowlist
   - **COMMITTED**: Feature committed to feature/ai-gallery-integration branch (commit 495e34e)
    - **TESTED**: AI model initialization, text generation, and TTS working with downloaded models
9. **Added LLM model repository with caching**:
   - Created `LlmModelRepository` for shared model initialization
   - Added `GetInitializedLlmModelUseCase` and integrated into widgets
   - Preloaded model in `GalleryApplication` to avoid delay

## Current Focus
- ✅ **LOGGING STANDARDIZATION COMPLETE**: Implemented Timber logging with DEBUG_FLOW tag and class prefixes across all AI components
- AI Gallery integration (AI text generation complete, other AI features in progress)
- Widget functionality improvements (AI-powered generate button working)
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
- Task list UI consolidated into single TaskList component
- **IN PROGRESS**: Hardcoded strings - partially moved to strings.xml (ChatScreen, CountdownScreens)
- **IN PROGRESS**: Hardcoded dimensions - partially moved to dimens.xml (ChatScreen, CountdownScreens)
- **CRITICAL**: Direct color values in Compose - must use theme colors only
- **CRITICAL**: Magic numbers (50, 30000, etc.) - must be constants
- **CRITICAL**: Hardcoded SharedPreferences keys - must be constants
- **CRITICAL**: Hardcoded Intent/Bundle keys - must be constants

## Security Improvements Completed
- ✅ **API keys now encrypted**: ElevenLabs API key stored using EncryptedSharedPreferences
- ✅ **Fallback mechanism**: Graceful fallback to regular SharedPreferences if encryption fails
- ✅ **Security dependency added**: androidx.security:security-crypto:1.0.0