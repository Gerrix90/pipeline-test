#!/bin/bash

# This script creates a minimal fake Android SDK structure
# for unit tests that don't require the actual SDK

SDK_DIR="android-sdk-minimal"
mkdir -p $SDK_DIR

# Create basic SDK structure
mkdir -p $SDK_DIR/platforms/android-35
mkdir -p $SDK_DIR/build-tools/34.0.0
mkdir -p $SDK_DIR/platform-tools

# Create dummy platform file
touch $SDK_DIR/platforms/android-35/android.jar

# Create dummy build tools
touch $SDK_DIR/build-tools/34.0.0/aapt
chmod +x $SDK_DIR/build-tools/34.0.0/aapt

# Create dummy platform tools
touch $SDK_DIR/platform-tools/adb
chmod +x $SDK_DIR/platform-tools/adb

echo "Created minimal mock Android SDK at $SDK_DIR"
echo "This is only for running unit tests that don't depend on real SDK"