# Offline Build Instructions

This project is set up for building in an offline environment. Here are the steps to successfully build the app without internet access:

## Prerequisites

- Java JDK 17 or higher must be installed
- Basic shell environment

## Setup Steps

1. Make sure all shell scripts have execute permissions:
   ```
   chmod +x *.sh
   chmod +x gradlew
   ```

2. Copy the Gradle distribution zip file to the correct location:
   ```
   # First create the gradle-wrapper directory if it doesn't exist
   mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/
   
   # Now if you have the gradle-8.10.2-bin.zip file, copy it to the directory
   # cp /path/to/gradle-8.10.2-bin.zip $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/
   
   # Or create an empty directory where Gradle would expect the zip to be extracted
   mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf
   ```

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

## Building the Project

After completing the setup steps above, run the following command to build the project:

```
./gradlew assembleDebug --offline
```

If successful, the APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Running Tests

To run the unit tests only:

```
./gradlew test --offline
```

## Troubleshooting

### Gradle Wrapper Issues

If you encounter issues with the Gradle wrapper, you can use the system Gradle if available:

```
gradle assembleDebug --offline
```

### Build Tool Issues

If you receive errors about missing build tools, ensure the Android SDK path is correctly set and that the minimal mock SDK files have execute permissions.

### Other Issues

For other issues, check the logs for specific error messages. The most common problems in offline builds are related to missing dependencies or SDK components.