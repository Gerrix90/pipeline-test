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

- Full Android builds will not work in completely offline mode, as the Android Gradle Plugin is required
- Unit tests might work if they don't depend on Android-specific components
- This setup is primarily for development environments with limited connectivity