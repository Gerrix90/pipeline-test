#!/bin/bash

# Script to prepare a bundle for offline builds
# Run this on a machine with internet connection before transferring to restricted environment

echo "Preparing offline build bundle..."

# Refresh all dependencies
./gradlew build --refresh-dependencies

# Create the bundle directory
BUNDLE_DIR="offline-bundle"
mkdir -p $BUNDLE_DIR

# Copy Gradle cache
echo "Copying Gradle cache..."
mkdir -p $BUNDLE_DIR/.gradle
cp -R $HOME/.gradle/caches $BUNDLE_DIR/.gradle/
cp -R $HOME/.gradle/wrapper $BUNDLE_DIR/.gradle/

# Copy local.properties with relative paths
echo "Creating portable local.properties..."
echo "sdk.dir=./android-sdk" > $BUNDLE_DIR/local.properties

# Copy offline gradle properties
cp gradle.properties.offline $BUNDLE_DIR/gradle.properties

# Copy the main project files
echo "Copying project files..."
cp -R app $BUNDLE_DIR/
cp -R gradle $BUNDLE_DIR/
cp build.gradle settings.gradle gradlew gradlew.bat $BUNDLE_DIR/
cp -R .github $BUNDLE_DIR/

# Copy offline scripts
cp remote-setup.sh run-tests-offline.sh $BUNDLE_DIR/

# Make sure scripts are executable
chmod +x $BUNDLE_DIR/gradlew $BUNDLE_DIR/remote-setup.sh $BUNDLE_DIR/run-tests-offline.sh

echo "Creating README for offline build..."
cat > $BUNDLE_DIR/README.md << 'EOF'
# Offline Build Instructions

This bundle contains everything needed to build the app without internet access.

## Setup:

1. Unzip/extract this bundle on the target machine
2. Set the Gradle home:
   ```
   export GRADLE_USER_HOME=$PWD/.gradle
   ```
3. Run the build:
   ```
   ./gradlew build --offline
   ```

## Testing:

To run tests only:
```
./run-tests-offline.sh
```
EOF

echo "Creating a compressed archive..."
tar -czf offline-build-bundle.tar.gz $BUNDLE_DIR

echo "Bundle created: offline-build-bundle.tar.gz"
echo "Transfer this file to the restricted environment and extract it."