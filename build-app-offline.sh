#!/bin/bash

# Build the app in offline mode

echo "Building app in offline mode..."

# Set up environment if not done already
if [ ! -f "local.properties" ]; then
  ./setup-offline-build.sh
fi

# Make sure Gradle knows where the SDK is
SDK_DIR="$PWD/android-sdk"
export ANDROID_HOME=$SDK_DIR
export ANDROID_SDK_ROOT=$SDK_DIR
export PATH=$PATH:$SDK_DIR/cmdline-tools/latest/bin:$SDK_DIR/platform-tools

# Build debug APK
echo "Building debug APK..."
./gradlew assembleDebug --offline --stacktrace

# Check if build succeeded
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
  echo "Build successful! APK is at: app/build/outputs/apk/debug/app-debug.apk"
else
  echo "Build failed. Check the logs for details."
fi
