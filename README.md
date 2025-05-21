# Pipeline Test

This sample Jetpack Compose app shows a simple greeting wrapped in a
`Scaffold` that includes a top app bar.

## Offline Build

This project can be built in offline environments. Follow these steps:

1. Make sure all scripts are executable: `chmod +x *.sh gradlew`
2. Extract Gradle from the system: 
   ```
   ./extract-gradle.sh
   ```
3. Run the tests:
   ```
   ./gradlew test --offline
   ```

The scripts will:
- Try to find Gradle on the system and use it
- Configure the environment for offline builds
- Run the tests without requiring internet access

For detailed instructions, see the [AGENT.md](AGENT.md) file.