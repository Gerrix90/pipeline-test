#!/bin/bash

# This script sets up the environment for building Android projects on servers
# with limited connectivity or without pre-installed Android tools

echo "Setting up Android build environment..."

# Create necessary directories
mkdir -p "$HOME/android-sdk"
export ANDROID_SDK_ROOT="$HOME/android-sdk"
export ANDROID_HOME="$HOME/android-sdk"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

# Check if cmdline tools are available
if [ ! -d "$ANDROID_HOME/cmdline-tools/latest" ]; then
  echo "Android SDK Command-line Tools not found, installing..."
  
  # Create a local.properties file pointing to the SDK location
  echo "sdk.dir=$ANDROID_HOME" > local.properties
  
  # Use offline mode for Gradle to avoid network issues
  echo "org.gradle.offline=true" >> gradle.properties
  
  # Create a simple wrapper script for testing purposes only
  echo "#!/bin/sh" > test-wrapper.sh
  echo "./gradlew test --offline" >> test-wrapper.sh
  chmod +x test-wrapper.sh
  
  echo "Setup complete. For full builds, you'll need to install Android SDK components."
  echo "To run basic tests only: ./test-wrapper.sh"
else
  echo "Android SDK Command-line Tools found."
fi

# Create a simple project test script
cat > run-unit-tests.sh << 'EOF'
#!/bin/bash
echo "Running Android unit tests in offline mode..."
./gradlew test --offline
EOF

chmod +x run-unit-tests.sh

echo "Setup complete. Run ./run-unit-tests.sh to execute unit tests in offline mode."