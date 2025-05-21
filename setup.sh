#!/bin/bash

# Setup script that runs at the start of every task
# This script has internet access and can download dependencies

echo "Setting up Android build environment..."

# Create Maven structure for Android Gradle Plugin
AGP_VERSION="8.8.2"
MAVEN_REPO_DIR="$HOME/.m2/repository"
AGP_DIR="$MAVEN_REPO_DIR/com/android/tools/build/gradle/$AGP_VERSION"

mkdir -p "$AGP_DIR"

# Download Android Gradle Plugin
echo "Downloading Android Gradle Plugin $AGP_VERSION..."
curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/tools/build/gradle/$AGP_VERSION/gradle-$AGP_VERSION.jar" \
     -o "$AGP_DIR/gradle-$AGP_VERSION.jar"

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/tools/build/gradle/$AGP_VERSION/gradle-$AGP_VERSION.pom" \
     -o "$AGP_DIR/gradle-$AGP_VERSION.pom"

# Download Gradle distribution
GRADLE_VERSION="8.10.2"
GRADLE_DIST_DIR="$HOME/.gradle/wrapper/dists/gradle-$GRADLE_VERSION-bin"
GRADLE_HASH_DIR="4dxsxvlz075zraiywjxduzqqf"

mkdir -p "$GRADLE_DIST_DIR/$GRADLE_HASH_DIR"

echo "Downloading Gradle $GRADLE_VERSION distribution..."
curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" \
     -o "$GRADLE_DIST_DIR/gradle-$GRADLE_VERSION-bin.zip"

# Create .ok file to mark the zip as successfully downloaded
touch "$GRADLE_DIST_DIR/$GRADLE_HASH_DIR/gradle-$GRADLE_VERSION-bin.zip.ok"

# Extract Gradle distribution
echo "Extracting Gradle distribution..."
unzip -q "$GRADLE_DIST_DIR/gradle-$GRADLE_VERSION-bin.zip" -d "$GRADLE_DIST_DIR/$GRADLE_HASH_DIR"

# Downloading other essential Android dependencies
echo "Downloading key Android dependencies..."

# Add repositories to gradle init script
mkdir -p "$HOME/.gradle"
cat > "$HOME/.gradle/init.gradle" << 'EOF'
allprojects {
    repositories {
        maven {
            url 'https://dl.google.com/dl/android/maven2/'
        }
        mavenCentral()
        google()
    }
}
EOF

# Install Android SDK components if needed
ANDROID_SDK_DIR="$HOME/android-sdk"
mkdir -p "$ANDROID_SDK_DIR"

echo "Setup complete!"
echo "Agent will now be able to build Android projects offline using pre-downloaded dependencies."