#!/bin/bash

# Script to run unit tests in an offline environment
# with limited Android SDK access

# Use offline properties
if [ -f "gradle.properties.offline" ]; then
  cp gradle.properties.offline gradle.properties
  echo "Using offline Gradle properties"
fi

# Ensure we have a local.properties file
if [ ! -f "local.properties" ]; then
  echo "Creating minimal local.properties"
  echo "sdk.dir=$HOME/android-sdk" > local.properties
fi

# Run only unit tests which don't require the full Android SDK
echo "Running unit tests in offline mode..."
./gradlew :app:testDebugUnitTest --offline --no-daemon

echo "Test run complete"