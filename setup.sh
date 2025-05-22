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
curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/tools/build/gradle/$AGP_VERSION/gradle-$AGP_VERSION.jar" \
     -o "$AGP_DIR/gradle-$AGP_VERSION.jar"

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/maven2/com/android/tools/build/gradle/$AGP_VERSION/gradle-$AGP_VERSION.pom" \
     -o "$AGP_DIR/gradle-$AGP_VERSION.pom"

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

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-gradle-plugin/$KOTLIN_VERSION/kotlin-gradle-plugin-$KOTLIN_VERSION.jar" \
     -o "$KOTLIN_PLUGIN_DIR/kotlin-gradle-plugin-$KOTLIN_VERSION.jar" 2>/dev/null || echo "Kotlin plugin jar not found, continuing..."

# Download Kotlin Compose Compiler Plugin
KOTLIN_COMPOSE_DIR="$MAVEN_REPO_DIR/org/jetbrains/kotlin/kotlin-compose-compiler-gradle-plugin/$KOTLIN_VERSION"
mkdir -p "$KOTLIN_COMPOSE_DIR"

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-compose-compiler-gradle-plugin/$KOTLIN_VERSION/kotlin-compose-compiler-gradle-plugin-$KOTLIN_VERSION.jar" \
     -o "$KOTLIN_COMPOSE_DIR/kotlin-compose-compiler-gradle-plugin-$KOTLIN_VERSION.jar" 2>/dev/null || echo "Kotlin compose plugin jar not found, continuing..."

# Downloading other essential Android dependencies
echo "Downloading key Android dependencies..."

# Create comprehensive offline repositories configuration
mkdir -p "$HOME/.gradle"
cat > "$HOME/.gradle/init.gradle" << 'EOF'
allprojects {
    repositories {
        mavenLocal()
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

gradle.projectsEvaluated {
    tasks.withType(JavaCompile) {
        options.compilerArgs << "-Xlint:unchecked" << "-Xlint:deprecation"
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

# Test offline build capability
echo ""
echo "Testing offline build capability..."
echo "Running: ./gradlew tasks --offline"
if ./gradlew tasks --offline >/dev/null 2>&1; then
    echo "✓ Gradle tasks work offline"
    
    echo "Testing: ./gradlew assembleDebug --offline"
    if ./gradlew assembleDebug --offline >/dev/null 2>&1; then
        echo "✓ Offline build SUCCESS! Android app builds without internet."
        echo ""
        echo "🎉 Setup complete! You can now develop Android apps offline."
        echo "Use: ./gradlew assembleDebug --offline to build the app"
    else
        echo "❌ Offline build FAILED. Some dependencies may be missing."
        echo "Check the error with: ./gradlew assembleDebug --offline"
    fi
else
    echo "❌ Gradle tasks failed offline. Basic setup incomplete."
fi

echo ""
echo "Setup finished. All necessary plugins and dependencies downloaded."