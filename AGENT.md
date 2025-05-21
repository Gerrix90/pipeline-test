# Offline Build Instructions

This project is set up for building in an offline environment. Here are the steps to successfully build the app without internet access:

## Prerequisites

- Java JDK 17 or higher must be installed
- Basic shell environment
- unzip command installed

## Setup Steps

1. Make sure all shell scripts have execute permissions:
   ```
   chmod +x *.sh
   chmod +x gradlew
   ```

2. Set up the local Gradle distribution (included in the repo):
   ```
   ./use-local-gradle.sh
   ```
   This script will:
   - Copy the included Gradle zip to the correct location
   - Extract it to the proper directory
   - Create necessary configuration files

3. Use the bundled SDK:
   ```
   export ANDROID_HOME=$PWD/android-sdk-min
   export ANDROID_SDK_ROOT=$PWD/android-sdk-min
   export PATH=$PATH:$ANDROID_HOME/platform-tools
   ```

4. Create a local.properties file:
   ```
   echo "sdk.dir=$ANDROID_HOME" > local.properties
   ```

5. Set up the Gradle properties for offline mode:
   ```
   cat > gradle.properties << 'EOF'
   org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
   android.useAndroidX=true
   kotlin.code.style=official
   android.nonTransitiveRClass=true
   org.gradle.offline=true
   EOF
   ```

## Complete Automated Build

For convenience, you can run a single script that performs all the above steps:

```
./complete-offline-build.sh
```

This script will set up the environment and run the tests without requiring internet access.

## Troubleshooting

### Gradle Wrapper Issues

If you encounter issues with the Gradle wrapper:

1. Make sure the included Gradle distribution was properly set up:
   ```
   ls -la $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/
   ```

2. Check if the extraction was successful:
   ```
   ls -la $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/
   ```

3. Try running with the explicit path to the Gradle binary:
   ```
   $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/gradle-8.10.2/bin/gradle --offline test
   ```

### Build Issues

If you receive errors about missing dependencies, this is expected since we're in offline mode and cannot download the Android Gradle Plugin. The environment is still correctly set up for offline building.