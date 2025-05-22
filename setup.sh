#!/bin/bash

# Setup script for Android builds
echo "Setting up Android build environment..."

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
fi

rm -f "$BUILD_LOG"
echo "Setup finished."