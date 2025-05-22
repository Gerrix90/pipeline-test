# Android Development Instructions

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