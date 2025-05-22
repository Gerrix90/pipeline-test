#!/bin/bash

# Setup script for Android builds
echo "Setting up Android build environment..."

# Set up Android SDK location
# Check common Android SDK locations
if [ -d "/opt/android-sdk" ]; then
    ANDROID_SDK_ROOT="/opt/android-sdk"
elif [ -d "/usr/lib/android-sdk" ]; then
    ANDROID_SDK_ROOT="/usr/lib/android-sdk"
elif [ -d "$HOME/Android/Sdk" ]; then
    ANDROID_SDK_ROOT="$HOME/Android/Sdk"
elif [ -d "/android-sdk" ]; then
    ANDROID_SDK_ROOT="/android-sdk"
else
    echo "⚠ Android SDK not found in common locations, trying to continue anyway..."
    ANDROID_SDK_ROOT=""
fi

if [ -n "$ANDROID_SDK_ROOT" ]; then
    echo "Found Android SDK at: $ANDROID_SDK_ROOT"
    export ANDROID_HOME="$ANDROID_SDK_ROOT"
    export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
    
    # Create local.properties
    echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties
    echo "✓ Android SDK configured"
else
    echo "⚠ No Android SDK found, build may fail"
fi

# Test Android build
echo "Testing Android build..."
BUILD_LOG="/tmp/build.log"
if ./gradlew assembleDebug > "$BUILD_LOG" 2>&1; then
    echo "✓ Android build successful!"
    echo "🎉 Setup complete!"
else
    echo "❌ Android build failed. Error details:"
    echo "--- BUILD ERROR LOG ---"
    tail -30 "$BUILD_LOG"
    echo "--- END ERROR LOG ---"
    echo ""
    echo "Environment debug info:"
    echo "ANDROID_HOME: ${ANDROID_HOME:-not set}"
    echo "ANDROID_SDK_ROOT: ${ANDROID_SDK_ROOT:-not set}"
    echo "local.properties exists: $([ -f local.properties ] && echo 'YES' || echo 'NO')"
    if [ -f local.properties ]; then
        echo "local.properties content: $(cat local.properties)"
    fi
    echo "Available directories in /:"
    ls -la / | grep -E "(android|sdk)" || echo "No android/sdk directories found"
fi

rm -f "$BUILD_LOG"
echo "Setup finished."