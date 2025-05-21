#!/bin/bash

# This script sets up the environment for a complete offline build

echo "Setting up offline build environment..."

# Create SDK directory
SDK_DIR="$PWD/android-sdk"
mkdir -p $SDK_DIR

# Extract SDK components
if [ -d "android-deps/android-sdk" ]; then
  echo "Copying SDK components..."
  cp -R android-deps/android-sdk/* $SDK_DIR/
fi

# Configure local.properties
echo "sdk.dir=$SDK_DIR" > local.properties

# Set up environment variables
export ANDROID_HOME=$SDK_DIR
export ANDROID_SDK_ROOT=$SDK_DIR
export PATH=$PATH:$SDK_DIR/cmdline-tools/latest/bin:$SDK_DIR/platform-tools

# Configure gradle.properties for offline build
if [ ! -f "gradle.properties.original" ]; then
  cp gradle.properties gradle.properties.original
fi

cat > gradle.properties << 'EOF2'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.offline=true
org.gradle.caching=true
android.enableJetifier=true
EOF2

# Ensure gradle wrapper is executable
chmod +x gradlew

echo "Environment setup complete. Run ./build-app-offline.sh to build the app."
