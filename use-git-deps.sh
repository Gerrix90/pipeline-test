#!/bin/bash

# Set up build environment using dependencies committed to Git

echo "Setting up build environment with Git dependencies..."

# Create .gradle structure
mkdir -p $HOME/.gradle/caches
mkdir -p $HOME/.gradle/wrapper

# Link committed SDK
SDK_DIR="$PWD/android-sdk-min"
echo "sdk.dir=$SDK_DIR" > local.properties

# Configure for offline build
cat > gradle.properties << 'EOG'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.offline=true
EOG

# Export environment variables
export ANDROID_HOME=$SDK_DIR
export ANDROID_SDK_ROOT=$SDK_DIR
export PATH=$PATH:$SDK_DIR/platform-tools

# Make sure gradlew is executable
chmod +x gradlew

echo "Environment set up. Now run: ./git-build.sh"