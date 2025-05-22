# Offline Build Instructions

This project is set up for building in an offline environment. Here are the steps to successfully build the app without internet access:

## Prerequisites

- Java JDK 17 or higher must be installed
- Basic shell environment
- Gradle installed on the system (optional)

## Setup Steps

1. Make sure all shell scripts have execute permissions:
   ```
   chmod +x *.sh
   chmod +x gradlew
   ```

2. Run the extract-gradle script to set up the environment:
   ```
   ./extract-gradle.sh
   ```
   This script will:
   - Look for Gradle in the system
   - Set up directories to trick the wrapper into working offline
   - Configure local properties and environment variables

3. After running the script, try running the tests:
   ```
   ./gradlew test --offline
   ```

## Code Validation After Changes

**IMPORTANT**: After making any code changes, you MUST validate that the code compiles correctly. Since the environment is set up with pre-downloaded dependencies, you can validate offline using these commands:

### Recommended Validation Steps (in order):

1. **Run unit tests** (fastest validation):
   ```
   ./gradlew test --offline
   ```

2. **Compile the project** (checks for compilation errors):
   ```
   ./gradlew compileDebugKotlin --offline
   ```

3. **Build the debug APK** (full build validation):
   ```
   ./gradlew assembleDebug --offline
   ```

4. **Run lint checks** (code quality validation):
   ```
   ./gradlew lintDebug --offline
   ```

### Quick Validation Script

For convenience, you can run all validations at once:
```
./gradlew test compileDebugKotlin assembleDebug lintDebug --offline
```

### Expected Behavior

- Tests should pass without errors
- Compilation should complete successfully 
- APK should build without issues
- Lint should report minimal warnings

If any of these steps fail, the code changes need to be fixed before proceeding.

## Troubleshooting

### Gradle Wrapper Issues

If you encounter issues with the Gradle wrapper:

1. Check if Gradle is available on your system:
   ```
   which gradle
   ```

2. If Gradle is available, you can run it directly:
   ```
   gradle test --offline
   ```

3. Create the directory structure manually:
   ```
   mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf
   touch $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/gradle-8.10.2-bin.zip.ok
   ```

### Build Issues

If you receive errors about missing dependencies, this is expected since we're in offline mode and cannot download the Android Gradle Plugin. The environment is still correctly set up for offline building.

### Known Limitations

- ~~Full Android builds will not work in completely offline mode, as the Android Gradle Plugin is required~~ **UPDATED**: Full Android builds now work offline after setup.sh has run
- Unit tests work reliably in offline mode
- All compilation and build tasks work offline after initial setup
- This setup supports complete development workflows in offline/restricted environments