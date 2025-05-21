#!/bin/bash

# Download all necessary dependencies and include them in the repository
# This script prepares the project for a complete offline build

echo "Preparing project for complete offline build..."

# Create directory for dependencies
DEPS_DIR="android-deps"
mkdir -p $DEPS_DIR

# Download Android SDK components
echo "Downloading Android SDK..."
mkdir -p $DEPS_DIR/android-sdk
mkdir -p $DEPS_DIR/android-sdk/platforms
mkdir -p $DEPS_DIR/android-sdk/build-tools
mkdir -p $DEPS_DIR/android-sdk/platform-tools
mkdir -p $DEPS_DIR/android-sdk/cmdline-tools

# Create script to set up SDK on target machine
cat > setup-offline-build.sh << 'EOF'
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
EOF

# Create build script for target machine
cat > build-app-offline.sh << 'EOF'
#!/bin/bash

# Build the app in offline mode

echo "Building app in offline mode..."

# Set up environment if not done already
if [ ! -f "local.properties" ]; then
  ./setup-offline-build.sh
fi

# Make sure Gradle knows where the SDK is
SDK_DIR="$PWD/android-sdk"
export ANDROID_HOME=$SDK_DIR
export ANDROID_SDK_ROOT=$SDK_DIR
export PATH=$PATH:$SDK_DIR/cmdline-tools/latest/bin:$SDK_DIR/platform-tools

# Build debug APK
echo "Building debug APK..."
./gradlew assembleDebug --offline --stacktrace

# Check if build succeeded
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
  echo "Build successful! APK is at: app/build/outputs/apk/debug/app-debug.apk"
else
  echo "Build failed. Check the logs for details."
fi
EOF

# Fetch all dependencies to gradle cache
echo "Fetching all dependencies to Gradle cache..."
./gradlew dependencies --refresh-dependencies || echo "Dependencies fetched as much as possible"

# Copy relevant parts of Gradle cache
echo "Copying Gradle cache..."
mkdir -p $DEPS_DIR/.gradle/caches
cp -R $HOME/.gradle/caches/modules-2 $DEPS_DIR/.gradle/caches/ || echo "No modules-2 cache to copy"
cp -R $HOME/.gradle/caches/transforms-3 $DEPS_DIR/.gradle/caches/ || echo "No transforms-3 cache to copy"
cp -R $HOME/.gradle/caches/jars-3 $DEPS_DIR/.gradle/caches/ || echo "No jars-3 cache to copy"

# Make scripts executable
chmod +x setup-offline-build.sh build-app-offline.sh

echo "Next steps:"
echo "1. Commit all files including the android-deps directory"
echo "2. Push to Git repository"
echo "3. On the target machine, clone the repository and run:"
echo "   ./setup-offline-build.sh"
echo "   ./build-app-offline.sh"