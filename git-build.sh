#!/bin/bash

# Script to build Android app using only resources committed to Git
# For use on remote servers with ONLY Git access

echo "Building Android app using Git resources..."

# Set up Android SDK paths
SDK_DIR="$PWD/android-sdk-min"
mkdir -p $SDK_DIR/platforms
mkdir -p $SDK_DIR/build-tools
mkdir -p $SDK_DIR/platform-tools

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

# Create dummy platform file for test compilation
mkdir -p $SDK_DIR/platforms/android-35
touch $SDK_DIR/platforms/android-35/android.jar

# Make gradlew executable
chmod +x gradlew

# Run only unit tests which don't need Android SDK
echo "Running unit tests (should work on most environments)..."
./gradlew test --offline --no-daemon || echo "Tests might have failed, continuing with build attempt"

echo "Attempting to build (this may or may not work depending on environment)..."
./gradlew assembleDebug --offline --no-daemon --stacktrace || echo "Build failed but continuing"

echo "Build process complete. Check logs for success/failure details."

# Check if APK was built
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
  echo "SUCCESS: APK built successfully at app/build/outputs/apk/debug/app-debug.apk"
else
  echo "NOTE: APK build failed. This is expected in environments without full Android SDK."
fi