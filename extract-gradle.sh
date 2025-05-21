#!/bin/bash

# Script to extract and use Gradle from the system

echo "Extracting Gradle from system..."

# Create necessary directories
mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf

# Look for the gradle binary in path
GRADLE_BIN=$(which gradle 2>/dev/null || echo "")

if [ -z "$GRADLE_BIN" ]; then
  echo "Gradle not found in PATH, checking common locations..."
  for dir in /usr/local/bin /usr/bin /opt/gradle/*/bin /opt/gradle/bin; do
    if [ -x "$dir/gradle" ]; then
      GRADLE_BIN="$dir/gradle"
      break
    fi
  done
fi

if [ -z "$GRADLE_BIN" ]; then
  echo "Gradle not found. Will try to use gradlew as is."
  # Create placeholder for gradle wrapper to avoid download
  mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/gradle-8.10.2
  mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/gradle-8.10.2/bin
  echo "#!/bin/sh" > $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/gradle-8.10.2/bin/gradle
  chmod +x $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/gradle-8.10.2/bin/gradle
  touch $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/gradle-8.10.2-bin.zip.ok
else
  echo "Found Gradle at: $GRADLE_BIN"
  GRADLE_HOME=$(dirname $(dirname $GRADLE_BIN))
  echo "Gradle home: $GRADLE_HOME"
  
  # Create a symbolic link to the system Gradle
  ln -sf $GRADLE_HOME $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/gradle-8.10.2
  touch $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/4dxsxvlz075zraiywjxduzqqf/gradle-8.10.2-bin.zip.ok
fi

echo "Setting up build environment..."

# Set up SDK variables
SDK_DIR="$PWD/android-sdk-min"
export ANDROID_HOME=$SDK_DIR
export ANDROID_SDK_ROOT=$SDK_DIR
export PATH=$PATH:$SDK_DIR/platform-tools

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

# Make scripts executable
chmod +x gradlew

echo "Environment setup complete!"