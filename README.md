# Pipeline Test

This sample Jetpack Compose app shows a simple greeting wrapped in a
`Scaffold` that includes a top app bar.

## Offline Build

This project can be built in offline environments. Follow these steps:

1. Make sure all scripts are executable: `chmod +x *.sh gradlew`
2. Use the included Gradle distribution: 
   ```
   ./use-local-gradle.sh
   ```
3. Run the complete offline build script:
   ```
   ./complete-offline-build.sh
   ```

The scripts will:
- Set up the local Gradle distribution
- Configure the environment for offline builds
- Run the tests without requiring internet access

For detailed instructions, see the [AGENT.md](AGENT.md) file.