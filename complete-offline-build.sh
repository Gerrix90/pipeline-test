#!/bin/bash

# Complete offline build that includes Gradle distribution

echo "Setting up complete offline build environment..."

# Set up Gradle from local distribution
./use-local-gradle.sh

# Set up SDK variables
SDK_DIR="$PWD/android-sdk-min"
export ANDROID_HOME=$SDK_DIR
export ANDROID_SDK_ROOT=$SDK_DIR
export PATH=$PATH:$SDK_DIR/platform-tools

# Create local.properties
echo "sdk.dir=$SDK_DIR" > local.properties

# Configure Gradle for offline build
cat > gradle.properties << 'EOF'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.offline=true
EOF

# Make gradlew executable
chmod +x gradlew

# Try to run unit tests
echo "Running unit tests..."
./gradlew test --offline --no-daemon || echo "Tests failed but continuing"

echo "Build process completed"