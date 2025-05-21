#!/bin/bash

# Script to build Android app on a restricted remote server
# This avoids UI saving issues by creating a standalone solution

echo "Setting up minimal build environment..."

# Set SDK path to current directory
SDK_DIR="$PWD/android-sdk-minimal"
mkdir -p $SDK_DIR

# Create required SDK structure
mkdir -p $SDK_DIR/platforms
mkdir -p $SDK_DIR/build-tools
mkdir -p $SDK_DIR/platform-tools

# Create local.properties
echo "sdk.dir=$SDK_DIR" > local.properties

# Set up gradle wrapper directory
mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf

# Modify gradle.properties for offline use
cat > gradle.properties << 'EOF'
# Project-wide Gradle settings for offline builds
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true

# Offline mode settings
org.gradle.offline=true
org.gradle.daemon=false

# Skip non-essential tasks for testing
android.optional.compilation=all
kotlin.incremental=false
android.sdk.path=$SDK_DIR
EOF

# Verify gradle wrapper exists and is executable
chmod +x gradlew

# Run tests only
echo "Running unit tests in offline mode..."
./gradlew :app:testDebugUnitTest --offline --no-daemon --info

echo "Test run complete"