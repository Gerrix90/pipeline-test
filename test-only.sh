#!/bin/bash

# Most minimal script to run unit tests without dependency on Android SDK
# Designed for restricted environments

echo "Running unit tests in minimal environment..."

# Create mock SDK if it doesn't exist
if [ ! -d "android-sdk-minimal" ]; then
  ./mock-android-sdk.sh
fi

# Set SDK path
SDK_DIR="$PWD/android-sdk-minimal"
echo "sdk.dir=$SDK_DIR" > local.properties

# Temporarily modify gradle.properties
if [ ! -f "gradle.properties.original" ]; then
  cp gradle.properties gradle.properties.original
fi

# Create offline gradle properties
cat > gradle.properties << 'EOF'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.offline=true
org.gradle.daemon=false
android.optional.compilation=all
kotlin.incremental=false
EOF

# Make sure gradlew is executable
chmod +x gradlew

# Run unit tests only in offline mode with detailed output
./gradlew :app:testDebugUnitTest --offline --no-daemon --info

# Restore original gradle.properties
if [ -f "gradle.properties.original" ]; then
  mv gradle.properties.original gradle.properties
fi

echo "Test run complete"