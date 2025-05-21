# Pipeline Test

This sample Jetpack Compose app shows a simple greeting wrapped in a
`Scaffold` that includes a top app bar.

## Offline Build

This project can be built in offline environments. Follow these steps:

1. Make sure all scripts are executable: `chmod +x *.sh gradlew`
2. Set up the gradle wrapper directory: 
   ```
   mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf
   ```
3. Run the all-in-one build script:
   ```
   ./all-in-one-build.sh
   ```

For detailed instructions, see the [AGENT.md](AGENT.md) file.