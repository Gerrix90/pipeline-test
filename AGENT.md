# Android Development Instructions

## 🚨 MANDATORY: Memory Bank Management

**BEFORE STARTING ANY DEVELOPMENT**:
1. **MUST** read ALL files in `/memory-bank/` directory
2. **MUST** understand architecture patterns from `systemPatterns.md`
3. **MUST** check current status in `activeContext.md` and `progress.md`
4. **NEVER** write code without consulting Memory Bank

**AFTER COMPLETING ANY TASK**:
1. **MUST** update `activeContext.md` with changes made
2. **MUST** update `progress.md` with completed/pending items
3. **MUST** document any new patterns or decisions
4. Memory Bank is LIVING documentation - maintain it!

**Memory Bank Structure**:
- `/memory-bank/memory-bank.md` - Overview and workflow
- `/memory-bank/projectbrief.md` - Core requirements
- `/memory-bank/systemPatterns.md` - MANDATORY architecture rules
- `/memory-bank/activeContext.md` - Current work status
- `/memory-bank/progress.md` - What's done and what's left
- `/memory-bank/architecture-example.md` - Implementation examples
- `/memory-bank/clean-code-examples.md` - Code quality examples

---

This project supports Android development in both online and offline environments.

## Prerequisites

- Java JDK 17 or higher must be installed
- Internet access during initial setup (setup.sh)
- Basic shell environment

## Setup Steps

1. **Initial setup** (requires internet access):
   ```
   chmod +x setup.sh gradlew
   ./setup.sh
   ```
   
   This script will:
   - Automatically detect or install Android SDK
   - Download all necessary build dependencies
   - Configure the development environment
   - Verify that builds work correctly

2. **After setup** (works online or offline):
   ```
   # Online build
   ./gradlew assembleDebug
   
   # Offline build (after initial setup)
   ./gradlew assembleDebug --offline
   ```

## Architecture Guidelines

- Build new features using the **MVVM** pattern so UI logic resides in ViewModel
  classes.
- When possible, encapsulate business logic in **use case** classes and access
  data through dedicated **repository** classes.
  This applies to all features, including task deadlines and alarms. Keep
  ViewModels free of Android framework dependencies and trigger alarm scheduling
  from the Activity or a dedicated service instead.
  - For task management, implement distinct use case classes for Create, Read,
    Update, and Delete operations so that ViewModels interact with a clean API
    and repositories handle persistence.

## ⚠️ MANDATORY Code Validation

**CRITICAL REQUIREMENT**: After making ANY code changes, you MUST validate that the code compiles and builds correctly before considering your work complete.

### Required Validation Command

**ALWAYS use this command after making code changes:**
```
./gradlew assembleDebug --offline
```

### Why This Validation is Required

1. **Ensures buildability**: Verifies your changes don't break compilation
2. **Catches syntax errors**: Finds Kotlin/Java syntax issues immediately  
3. **Validates dependencies**: Confirms all imports and dependencies are correct
4. **APK generation**: Ensures the final Android application can be built
5. **Offline compatibility**: Confirms builds work without internet access

### Validation Workflow

```bash
# 1. Make your code changes
# 2. ALWAYS run validation
./gradlew assembleDebug --offline

# 3. Check the result:
# ✅ BUILD SUCCESSFUL - your changes are valid
# ❌ BUILD FAILED - fix errors before proceeding
```

### Alternative Validation Commands

For specific validation needs:

1. **Quick compilation check**:
   ```
   ./gradlew compileDebugKotlin --offline
   ```

2. **Gradle configuration check**:
   ```
   ./gradlew tasks --offline
   ```

3. **Clean build validation**:
   ```
   ./gradlew clean assembleDebug --offline
   ```

### Expected Behavior

✅ **Successful validation shows:**
- `BUILD SUCCESSFUL` message
- No compilation errors
- APK file generated successfully
- All Gradle tasks complete without issues

❌ **Failed validation shows:**
- `BUILD FAILED` message  
- Specific error details
- Line numbers and file locations of issues

**DO NOT proceed with additional changes if validation fails. Fix all errors first.**

## Troubleshooting

### If Build Validation Fails

1. **Check error messages carefully**:
   - Read the specific error details
   - Note file names and line numbers
   - Fix syntax or dependency issues

2. **Common solutions**:
   ```bash
   # Fix permissions
   chmod +x gradlew
   
   # Clean and rebuild
   ./gradlew clean assembleDebug --offline
   
   # Re-run setup if dependencies missing
   ./setup.sh
   ```

3. **If setup.sh fails**:
   - Ensure internet access during initial setup
   - setup.sh should show "✓ Android build successful!" at the end
   - Re-run setup.sh if build validation consistently fails

### Build Requirements

- **Initial setup required**: setup.sh must run successfully with internet access
- **Validation mandatory**: Every code change must pass `./gradlew assembleDebug --offline`
- **Offline capability**: Full Android builds work offline after setup.sh completes
- **Error-free code**: Build failures must be resolved before proceeding with development

### Development Workflow Summary

1. Run `./setup.sh` once (with internet)
2. Make code changes
3. **ALWAYS validate**: `./gradlew assembleDebug --offline`
4. Fix any build errors before continuing
5. Repeat steps 2-4 for each change

**Remember: A successful build validation is REQUIRED after every code modification.**