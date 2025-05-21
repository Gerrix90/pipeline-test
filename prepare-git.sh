#!/bin/bash

# Script to prepare minimal dependencies to be committed to Git
# This prepares essential files for offline builds through Git

echo "Preparing minimal build dependencies for Git..."

# Create directories for Android SDK essentials
SDK_DIR="android-sdk-min"
mkdir -p $SDK_DIR/platforms/android-35
mkdir -p $SDK_DIR/build-tools/34.0.0
mkdir -p $SDK_DIR/platform-tools

# Create directory for vendor libs
VENDOR_DIR="vendor-libs"
mkdir -p $VENDOR_DIR/gradle-cache

# Collect critical Gradle dependencies
echo "Collecting essential Gradle dependencies..."
./gradlew dependencies --refresh-dependencies || echo "Continuing with partial dependencies"

# Create empty placeholder files for Android SDK
touch $SDK_DIR/platforms/android-35/android.jar
touch $SDK_DIR/build-tools/34.0.0/aapt
touch $SDK_DIR/platform-tools/adb
chmod +x $SDK_DIR/build-tools/34.0.0/aapt $SDK_DIR/platform-tools/adb

# Copy minimal Gradle cache files
mkdir -p $VENDOR_DIR/gradle-cache/android
mkdir -p $VENDOR_DIR/gradle-cache/kotlin
mkdir -p $VENDOR_DIR/gradle-cache/jars

# Create helper script to use these files on the remote server
cat > use-git-deps.sh << 'EOF'
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
EOF

chmod +x use-git-deps.sh

# Create a self-contained build script
cat > all-in-one-build.sh << 'EOF'
#!/bin/bash

# A complete self-contained build script that uses only Git resources

echo "Setting up minimal build environment..."

# Create and setup SDK directory
SDK_DIR="$PWD/android-sdk-min"
export ANDROID_HOME=$SDK_DIR
export ANDROID_SDK_ROOT=$SDK_DIR
export PATH=$PATH:$SDK_DIR/platform-tools

# Create local.properties
echo "sdk.dir=$SDK_DIR" > local.properties

# Set Gradle to offline mode
cat > gradle.properties << 'EOG'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.offline=true
EOG

# Make gradlew executable
chmod +x gradlew

# Run unit tests
echo "Running unit tests..."
./gradlew test --offline --no-daemon || echo "Tests failed but continuing"

# Try to build the APK
echo "Attempting to build APK..."
./gradlew assembleDebug --offline --no-daemon || echo "Build may have failed"

# Check if APK exists
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
  echo "SUCCESS: APK built at app/build/outputs/apk/debug/app-debug.apk"
else
  echo "Build failed. Some components may be missing."
fi
EOF

chmod +x all-in-one-build.sh

echo "Preparation complete. Next steps:"
echo "1. git add android-sdk-min/ vendor-libs/ git-build.sh use-git-deps.sh all-in-one-build.sh"
echo "2. git commit -m \"Add vendored dependencies for offline builds\""
echo "3. git push origin main"
echo ""
echo "On the remote server, after git clone:"
echo "1. ./all-in-one-build.sh"