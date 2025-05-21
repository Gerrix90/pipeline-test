#!/bin/bash

# This script creates a complete bundle with all dependencies
# needed for offline building

echo "Creating complete offline bundle..."

# Create bundle directory
BUNDLE_DIR="offline-bundle"
rm -rf $BUNDLE_DIR
mkdir -p $BUNDLE_DIR

# Copy project files
echo "Copying project files..."
cp -R app $BUNDLE_DIR/
cp -R gradle $BUNDLE_DIR/
cp build.gradle settings.gradle gradlew gradlew.bat $BUNDLE_DIR/

# Copy pre-built APK for reference
mkdir -p $BUNDLE_DIR/pre-built
cp app/build/outputs/apk/debug/app-debug.apk $BUNDLE_DIR/pre-built/ || echo "No pre-built APK found"

# Create .gradle directory with cache
echo "Copying Gradle cache..."
mkdir -p $BUNDLE_DIR/.gradle
cp -R $HOME/.gradle/wrapper $BUNDLE_DIR/.gradle/
cp -R $HOME/.gradle/caches $BUNDLE_DIR/.gradle/

# Create SDK directory with essential components
echo "Creating minimal SDK..."
mkdir -p $BUNDLE_DIR/android-sdk/platforms
mkdir -p $BUNDLE_DIR/android-sdk/build-tools
mkdir -p $BUNDLE_DIR/android-sdk/platform-tools

# Create build script
cat > $BUNDLE_DIR/build-offline.sh << 'EOF'
#!/bin/bash

# Script to build app completely offline

# Setup environment
export GRADLE_USER_HOME="$PWD/.gradle"
export ANDROID_HOME="$PWD/android-sdk"
export ANDROID_SDK_ROOT="$PWD/android-sdk"
export PATH="$PATH:$ANDROID_HOME/platform-tools"

# Create local.properties
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Create offline gradle.properties
cat > gradle.properties << 'EOG'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.offline=true
EOG

# Make gradlew executable
chmod +x gradlew

# Build the app
echo "Building app in offline mode..."
./gradlew assembleDebug --offline --no-daemon

echo "Build complete! Check for app-debug.apk in app/build/outputs/apk/debug/"
EOF

chmod +x $BUNDLE_DIR/build-offline.sh

# Create README
cat > $BUNDLE_DIR/README.md << 'EOF'
# Offline Build Bundle

This bundle contains everything needed to build the app without internet access.

## Instructions

1. On the target machine, run:
   ```
   ./build-offline.sh
   ```

2. If successful, the APK will be at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

## Pre-built Version

For reference, a pre-built version is included in the `pre-built` directory.
EOF

# Create archive
echo "Creating archive..."
tar -czf offline-bundle.tar.gz $BUNDLE_DIR

echo "Offline bundle created: offline-bundle.tar.gz"
echo "Transfer this file to your target machine and extract it."
echo "Then run ./build-offline.sh inside the extracted directory."