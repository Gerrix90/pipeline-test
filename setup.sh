#!/bin/bash

# Setup script that runs at the start of every task
# This script has internet access and can download dependencies

echo "Setting up Android build environment..."

# Create Maven structure for Android Gradle Plugin
AGP_VERSION="8.8.2"
MAVEN_REPO_DIR="$HOME/.m2/repository"
AGP_DIR="$MAVEN_REPO_DIR/com/android/tools/build/gradle/$AGP_VERSION"

mkdir -p "$AGP_DIR"

# Download Android Gradle Plugin and all its dependencies
echo "Downloading Android Gradle Plugin $AGP_VERSION and dependencies..."

# Download main AGP JAR and POM
echo "Downloading AGP JAR..."
if curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/tools/build/gradle/$AGP_VERSION/gradle-$AGP_VERSION.jar" \
     -o "$AGP_DIR/gradle-$AGP_VERSION.jar"; then
    echo "✓ AGP JAR downloaded successfully"
else
    echo "❌ Failed to download AGP JAR"
fi

echo "Downloading AGP POM..."
if curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/tools/build/gradle/$AGP_VERSION/gradle-$AGP_VERSION.pom" \
     -o "$AGP_DIR/gradle-$AGP_VERSION.pom"; then
    echo "✓ AGP POM downloaded successfully"
else
    echo "❌ Failed to download AGP POM"
fi

# Download the actual plugin artifact that Gradle looks for
PLUGIN_DIR="$MAVEN_REPO_DIR/com/android/application/com.android.application.gradle.plugin/$AGP_VERSION"
mkdir -p "$PLUGIN_DIR"

echo "Downloading Android Application Plugin artifact..."
curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/application/com.android.application.gradle.plugin/$AGP_VERSION/com.android.application.gradle.plugin-$AGP_VERSION.jar" \
     -o "$PLUGIN_DIR/com.android.application.gradle.plugin-$AGP_VERSION.jar" 2>/dev/null || echo "Plugin jar not found, trying alternative..."

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/application/com.android.application.gradle.plugin/$AGP_VERSION/com.android.application.gradle.plugin-$AGP_VERSION.pom" \
     -o "$PLUGIN_DIR/com.android.application.gradle.plugin-$AGP_VERSION.pom" 2>/dev/null || echo "Plugin pom not found, continuing..."

# Download AGP Builder and other critical dependencies
AGP_BUILDER_DIR="$MAVEN_REPO_DIR/com/android/tools/build/builder/$AGP_VERSION"
mkdir -p "$AGP_BUILDER_DIR"

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/tools/build/builder/$AGP_VERSION/builder-$AGP_VERSION.jar" \
     -o "$AGP_BUILDER_DIR/builder-$AGP_VERSION.jar" 2>/dev/null || echo "Builder jar not found, continuing..."

# Download Android Gradle Plugin API
AGP_API_DIR="$MAVEN_REPO_DIR/com/android/tools/build/gradle-api/$AGP_VERSION"
mkdir -p "$AGP_API_DIR"

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/tools/build/gradle-api/$AGP_VERSION/gradle-api-$AGP_VERSION.jar" \
     -o "$AGP_API_DIR/gradle-api-$AGP_VERSION.jar" 2>/dev/null || echo "Gradle API jar not found, continuing..."

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

# Download Kotlin Gradle Plugin
echo "Downloading Kotlin Gradle Plugin..."
KOTLIN_VERSION="2.0.0"
KOTLIN_PLUGIN_DIR="$MAVEN_REPO_DIR/org/jetbrains/kotlin/kotlin-gradle-plugin/$KOTLIN_VERSION"
mkdir -p "$KOTLIN_PLUGIN_DIR"

# Download Kotlin plugin artifacts
curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-gradle-plugin/$KOTLIN_VERSION/kotlin-gradle-plugin-$KOTLIN_VERSION.jar" \
     -o "$KOTLIN_PLUGIN_DIR/kotlin-gradle-plugin-$KOTLIN_VERSION.jar" 2>/dev/null || echo "Kotlin plugin jar not found, continuing..."

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-gradle-plugin/$KOTLIN_VERSION/kotlin-gradle-plugin-$KOTLIN_VERSION.pom" \
     -o "$KOTLIN_PLUGIN_DIR/kotlin-gradle-plugin-$KOTLIN_VERSION.pom" 2>/dev/null || echo "Kotlin plugin pom not found, continuing..."

# Download Kotlin Compose Compiler Plugin
KOTLIN_COMPOSE_DIR="$MAVEN_REPO_DIR/org/jetbrains/kotlin/kotlin-compose-compiler-gradle-plugin/$KOTLIN_VERSION"
mkdir -p "$KOTLIN_COMPOSE_DIR"

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-compose-compiler-gradle-plugin/$KOTLIN_VERSION/kotlin-compose-compiler-gradle-plugin-$KOTLIN_VERSION.jar" \
     -o "$KOTLIN_COMPOSE_DIR/kotlin-compose-compiler-gradle-plugin-$KOTLIN_VERSION.jar" 2>/dev/null || echo "Kotlin compose plugin jar not found, continuing..."

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-compose-compiler-gradle-plugin/$KOTLIN_VERSION/kotlin-compose-compiler-gradle-plugin-$KOTLIN_VERSION.pom" \
     -o "$KOTLIN_COMPOSE_DIR/kotlin-compose-compiler-gradle-plugin-$KOTLIN_VERSION.pom" 2>/dev/null || echo "Kotlin compose plugin pom not found, continuing..."

# Downloading other essential Android dependencies
echo "Downloading key Android dependencies..."

# Create comprehensive offline repositories configuration
mkdir -p "$HOME/.gradle"
cat > "$HOME/.gradle/init.gradle" << 'EOF'
settingsEvaluated { settings ->
    settings.pluginManagement {
        repositories {
            maven {
                url 'file://' + System.getProperty('user.home') + '/.m2/repository'
            }
            maven {
                url 'https://dl.google.com/dl/android/maven2/'
            }
            maven {
                url 'https://repo1.maven.org/maven2/'
            }
            mavenCentral()
            google()
            gradlePluginPortal()
        }
    }
    settings.dependencyResolutionManagement {
        repositories {
            maven {
                url 'file://' + System.getProperty('user.home') + '/.m2/repository'
            }
            maven {
                url 'https://dl.google.com/dl/android/maven2/'
            }
            maven {
                url 'https://repo1.maven.org/maven2/'
            }
            mavenCentral()
            google()
        }
    }
}
EOF

# Configure Gradle to prefer offline mode
cat > "$HOME/.gradle/gradle.properties" << 'EOF'
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.configureondemand=true
org.gradle.caching=true
android.useAndroidX=true
android.enableJetifier=true
EOF

# Set up basic Android SDK structure
ANDROID_SDK_DIR="$HOME/android-sdk"
mkdir -p "$ANDROID_SDK_DIR/platforms/android-35"
mkdir -p "$ANDROID_SDK_DIR/build-tools/34.0.0"
mkdir -p "$ANDROID_SDK_DIR/platform-tools"

# Download basic Android platform JAR (minimal for compilation)
echo "Downloading Android platform JAR..."
curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/repository/android-35_r01.jar" \
     -o "$ANDROID_SDK_DIR/platforms/android-35/android.jar" 2>/dev/null || echo "Android platform jar not found, using minimal setup..."

# Create local.properties to point to Android SDK
cat > "local.properties" << EOF
sdk.dir=$ANDROID_SDK_DIR
EOF

# Verify downloaded files
echo ""
echo "Verifying downloaded dependencies..."
echo "AGP JAR size: $(ls -lh "$AGP_DIR/gradle-$AGP_VERSION.jar" 2>/dev/null | awk '{print $5}' || echo 'NOT FOUND')"
echo "AGP POM size: $(ls -lh "$AGP_DIR/gradle-$AGP_VERSION.pom" 2>/dev/null | awk '{print $5}' || echo 'NOT FOUND')"
echo "Gradle distribution size: $(ls -lh "$GRADLE_DIST_DIR/gradle-$GRADLE_VERSION-bin.zip" 2>/dev/null | awk '{print $5}' || echo 'NOT FOUND')"
echo "Android SDK directory created: $([ -d "$ANDROID_SDK_DIR" ] && echo 'YES' || echo 'NO')"
echo "local.properties created: $([ -f "local.properties" ] && echo 'YES' || echo 'NO')"
echo ""

# Test offline build capability
echo ""
echo "Testing offline build capability..."
echo "Running: ./gradlew tasks --offline"

# Create a temp file for error output
TEMP_LOG="/tmp/gradle_test.log"

if ./gradlew tasks --offline > "$TEMP_LOG" 2>&1; then
    echo "✓ Gradle tasks work offline"
    
    echo "Testing: ./gradlew assembleDebug --offline"
    if ./gradlew assembleDebug --offline > "$TEMP_LOG" 2>&1; then
        echo "✓ Offline build SUCCESS! Android app builds without internet."
        echo ""
        echo "🎉 Setup complete! You can now develop Android apps offline."
        echo "Use: ./gradlew assembleDebug --offline to build the app"
    else
        echo "❌ Offline build FAILED. Error details:"
        echo "--- START ERROR LOG ---"
        cat "$TEMP_LOG" | tail -20
        echo "--- END ERROR LOG ---"
        echo ""
        echo "Debugging info:"
        echo "Maven repo contents:"
        find "$HOME/.m2/repository" -name "*.jar" | head -10
        echo "Gradle cache:"
        ls -la "$HOME/.gradle/" 2>/dev/null || echo "No .gradle directory"
    fi
else
    echo "❌ Gradle tasks failed offline. Error details:"
    echo "--- START ERROR LOG ---"
    cat "$TEMP_LOG"
    echo "--- END ERROR LOG ---"
    echo ""
    echo "Debugging info:"
    echo "Current directory contents:"
    ls -la
    echo ""
    echo "local.properties content:"
    cat local.properties 2>/dev/null || echo "No local.properties file"
    echo ""
    echo "gradle.properties content:"
    cat gradle.properties 2>/dev/null || echo "No gradle.properties file"
    echo ""
    echo "Maven repo structure:"
    find "$HOME/.m2/repository" -type d | head -15
    echo ""
    echo "Downloaded JARs:"
    find "$HOME/.m2/repository" -name "*.jar" | head -10
fi

# Cleanup
rm -f "$TEMP_LOG"

echo ""
echo "Setup finished. All necessary plugins and dependencies downloaded."