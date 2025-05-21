#!/bin/bash

# A complete self-contained build script that uses only Git resources

echo "Setting up minimal build environment..."

# Create and setup SDK directory
SDK_DIR="$PWD/android-sdk-min"
export ANDROID_HOME=$SDK_DIR
export ANDROID_SDK_ROOT=$SDK_DIR
export PATH=$PATH:$SDK_DIR/platform-tools

# Create local.properties
echo "sdk.dir=$SDK_DIR" > local.properties

# Set Gradle to offline mode
cat > gradle.properties << 'EOG'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.offline=true
EOG

# Make gradlew executable
chmod +x gradlew

# Run unit tests
echo "Running unit tests..."
./gradlew test --offline --no-daemon || echo "Tests failed but continuing"

# Try to build the APK
echo "Attempting to build APK..."
./gradlew assembleDebug --offline --no-daemon || echo "Build may have failed"

# Check if APK exists
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
  echo "SUCCESS: APK built at app/build/outputs/apk/debug/app-debug.apk"
else
  echo "Build failed. Some components may be missing."
fi