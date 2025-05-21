# Pipeline Test

This sample Jetpack Compose app shows a simple greeting wrapped in a
`Scaffold` that includes a top app bar.

## Build Instructions

### Online Environment

If you're working in an environment with internet access during setup:

1. The `setup.sh` script will automatically download the Gradle distribution and Android Gradle Plugin
2. After setup, run:
   ```
   ./gradlew assembleDebug
   ```

### Offline Environment

In environments without internet access:

1. Make sure all scripts are executable: `chmod +x *.sh gradlew`
2. Extract Gradle from the system: 
   ```
   ./extract-gradle.sh
   ```
3. Run tests:
   ```
   ./gradlew test --offline
   ```

For detailed instructions, see the [AGENT.md](AGENT.md) file.