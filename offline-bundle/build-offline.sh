#!/bin/bash

# Script to build app completely offline

# Setup environment
export GRADLE_USER_HOME="$PWD/.gradle"
export ANDROID_HOME="$PWD/android-sdk"
export ANDROID_SDK_ROOT="$PWD/android-sdk"
export PATH="$PATH:$ANDROID_HOME/platform-tools"

# Create local.properties
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Create offline gradle.properties
cat > gradle.properties << 'EOG'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.offline=true
EOG

# Make gradlew executable
chmod +x gradlew

# Build the app
echo "Building app in offline mode..."
./gradlew assembleDebug --offline --no-daemon

echo "Build complete! Check for app-debug.apk in app/build/outputs/apk/debug/"
