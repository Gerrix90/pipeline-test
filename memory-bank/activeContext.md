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
   - Added screen routing for Gallery screen (index 3)
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

## Current Focus
- **LOGGING STANDARDIZATION**: Implement Timber logging with DEBUG_FLOW tag and class prefixes across all components
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
- Similar task UI logic in TaskList and TaskListContent - needs common component
- **CRITICAL**: Hardcoded strings throughout UI - must move to strings.xml
- **CRITICAL**: Hardcoded dimensions (16.dp, 8.dp, etc.) - must move to dimens.xml
- **CRITICAL**: Direct color values in Compose - must use theme colors only
- **CRITICAL**: Magic numbers (50, 30000, etc.) - must be constants
- **CRITICAL**: Hardcoded SharedPreferences keys - must be constants
- **CRITICAL**: Hardcoded Intent/Bundle keys - must be constants

## Security Improvements Completed
- ✅ **API keys now encrypted**: ElevenLabs API key stored using EncryptedSharedPreferences
- ✅ **Fallback mechanism**: Graceful fallback to regular SharedPreferences if encryption fails
- ✅ **Security dependency added**: androidx.security:security-crypto:1.0.0