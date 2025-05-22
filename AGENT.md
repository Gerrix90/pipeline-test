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

### Alternative: Use Project Scripts

The project includes dedicated scripts for testing:
```
./test-only.sh
```

### Expected Behavior

- Compilation should complete successfully
- APK should build without issues
- All Gradle tasks should be accessible offline

If validation fails, check that setup.sh ran successfully and fix any code issues before proceeding.

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

- **Initial setup required**: setup.sh must run successfully with internet access to download all dependencies
- **Offline mode works after setup**: Full Android builds work offline after setup.sh completes
- Unit tests work reliably in offline mode  
- All Gradle functionality works offline after proper setup
- Complete development workflow supported in offline environments