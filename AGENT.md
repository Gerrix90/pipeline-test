# Offline Build Instructions

This project is set up for building in an offline environment after initial setup.

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
   - Download Android Gradle Plugin and all dependencies
   - Download Kotlin Gradle Plugin and Compose Plugin
   - Set up basic Android SDK structure
   - Configure Gradle for offline development
   - Test that offline builds work

2. **After setup** (works offline):
   ```
   ./gradlew assembleDebug --offline
   ```

## Code Validation After Changes

**IMPORTANT**: After making any code changes, you MUST validate that the code compiles and builds correctly. The setup.sh script downloads all necessary dependencies for offline development.

### Validation Steps for Offline Environment

1. **Full build validation** (recommended):
   ```
   ./gradlew assembleDebug --offline
   ```

2. **Quick compilation check**:
   ```
   ./gradlew compileDebugKotlin --offline
   ```

3. **Gradle configuration check**:
   ```
   ./gradlew tasks --offline
   ```

### Simple Workflow

After setup.sh completes successfully, all validation can be done with:
```
./gradlew assembleDebug --offline
```

### Expected Behavior

- Compilation should complete successfully
- APK should build without issues
- All Gradle tasks should be accessible offline

If validation fails, check that setup.sh ran successfully and fix any code issues before proceeding.

## Troubleshooting

### If Offline Build Fails

1. **Make sure setup.sh ran successfully**:
   - setup.sh should show "✓ Offline build SUCCESS!" at the end
   - If not, re-run setup.sh with internet access

2. **Check for permission issues**:
   ```
   chmod +x gradlew
   ```

3. **Clear Gradle cache if needed**:
   ```
   rm -rf ~/.gradle/caches
   ./setup.sh
   ```

### Known Limitations

- **Initial setup required**: setup.sh must run successfully with internet access to download all dependencies
- **Offline mode works after setup**: Full Android builds work offline after setup.sh completes
- Unit tests work reliably in offline mode  
- All Gradle functionality works offline after proper setup
- Complete development workflow supported in offline environments