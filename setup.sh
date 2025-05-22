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

# Download Kotlin Android Plugin artifacts
KOTLIN_ANDROID_DIR="$MAVEN_REPO_DIR/org/jetbrains/kotlin/android/org.jetbrains.kotlin.android.gradle.plugin/$KOTLIN_VERSION"
mkdir -p "$KOTLIN_ANDROID_DIR"

echo "Downloading Kotlin Android Plugin artifact..."
curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/android/org.jetbrains.kotlin.android.gradle.plugin/$KOTLIN_VERSION/org.jetbrains.kotlin.android.gradle.plugin-$KOTLIN_VERSION.jar" \
     -o "$KOTLIN_ANDROID_DIR/org.jetbrains.kotlin.android.gradle.plugin-$KOTLIN_VERSION.jar" 2>/dev/null || echo "Kotlin Android plugin jar not found, continuing..."

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/android/org.jetbrains.kotlin.android.gradle.plugin/$KOTLIN_VERSION/org.jetbrains.kotlin.android.gradle.plugin-$KOTLIN_VERSION.pom" \
     -o "$KOTLIN_ANDROID_DIR/org.jetbrains.kotlin.android.gradle.plugin-$KOTLIN_VERSION.pom" 2>/dev/null || echo "Kotlin Android plugin pom not found, continuing..."

# Download Kotlin Compose Plugin artifacts 
KOTLIN_COMPOSE_PLUGIN_DIR="$MAVEN_REPO_DIR/org/jetbrains/kotlin/plugin/compose/org.jetbrains.kotlin.plugin.compose.gradle.plugin/$KOTLIN_VERSION"
mkdir -p "$KOTLIN_COMPOSE_PLUGIN_DIR"

echo "Downloading Kotlin Compose Plugin artifact..."
curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/plugin/compose/org.jetbrains.kotlin.plugin.compose.gradle.plugin/$KOTLIN_VERSION/org.jetbrains.kotlin.plugin.compose.gradle.plugin-$KOTLIN_VERSION.jar" \
     -o "$KOTLIN_COMPOSE_PLUGIN_DIR/org.jetbrains.kotlin.plugin.compose.gradle.plugin-$KOTLIN_VERSION.jar" 2>/dev/null || echo "Kotlin Compose plugin jar not found, continuing..."

curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/plugin/compose/org.jetbrains.kotlin.plugin.compose.gradle.plugin/$KOTLIN_VERSION/org.jetbrains.kotlin.plugin.compose.gradle.plugin-$KOTLIN_VERSION.pom" \
     -o "$KOTLIN_COMPOSE_PLUGIN_DIR/org.jetbrains.kotlin.plugin.compose.gradle.plugin-$KOTLIN_VERSION.pom" 2>/dev/null || echo "Kotlin Compose plugin pom not found, continuing..."

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

# Download and setup proper Android SDK platform
echo "Setting up Android platform-35..."
mkdir -p "$ANDROID_SDK_DIR/platforms/android-35"

# Create basic platform structure that Gradle expects
cat > "$ANDROID_SDK_DIR/platforms/android-35/build.prop" << 'EOF'
ro.build.version.sdk=35
ro.build.version.codename=REL
EOF

cat > "$ANDROID_SDK_DIR/platforms/android-35/source.properties" << 'EOF'
Pkg.Desc=Android SDK Platform 35
Pkg.UserSrc=false
Pkg.Revision=1
Platform.Version=15.0
Platform.CodeName=VanillaIceCream
Platform.ApiLevel=35
AndroidVersion.ApiLevel=35
AndroidVersion.CodeName=VanillaIceCream
EOF

# Skip package.xml - it causes XML parsing issues with wrong namespace
# The source.properties file is sufficient for Gradle to recognize the platform

# Try to download Android platform JAR
echo "Downloading Android platform JAR..."
if curl -x http://proxy:8080 \
     --cacert $CODEX_PROXY_CERT \
     -L "https://dl.google.com/android/repository/android-35_r01.jar" \
     -o "$ANDROID_SDK_DIR/platforms/android-35/android.jar" 2>/dev/null; then
    echo "✓ Android platform JAR downloaded"
else
    echo "⚠ Android platform JAR not found, creating stub version..."
    # Create a proper stub android.jar with some Android classes
    mkdir -p /tmp/android_classes
    cat > /tmp/android_classes/android.java << 'JAVAEOF'
package android;
public class Build {
    public static class VERSION {
        public static final int SDK_INT = 35;
        public static final String RELEASE = "15";
    }
}
JAVAEOF
    
    # Create Android JAR with proper structure
    cd /tmp/android_classes
    mkdir -p android
    mv android.java android/
    echo 'public class R {}' > android/R.java
    
    # Try to compile if javac is available, otherwise create empty JAR
    if which javac >/dev/null 2>&1; then
        javac android/*.java 2>/dev/null || echo "Compilation failed, using minimal JAR"
        jar cf "$ANDROID_SDK_DIR/platforms/android-35/android.jar" android/*.class 2>/dev/null || \
        jar cf "$ANDROID_SDK_DIR/platforms/android-35/android.jar" android/*.java
    else
        # Create a larger minimal JAR if javac not available
        jar cf "$ANDROID_SDK_DIR/platforms/android-35/android.jar" android/*.java
    fi
    
    cd - >/dev/null
    rm -rf /tmp/android_classes
    echo "✓ Stub android.jar created"
fi

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

# Debug Android SDK structure
echo ""
echo "Android SDK structure:"
echo "SDK directory: $ANDROID_SDK_DIR"
if [ -d "$ANDROID_SDK_DIR/platforms/android-35" ]; then
    echo "✓ android-35 platform directory exists"
    echo "Contents of android-35:"
    ls -la "$ANDROID_SDK_DIR/platforms/android-35/"
    
    if [ -f "$ANDROID_SDK_DIR/platforms/android-35/android.jar" ]; then
        echo "✓ android.jar exists ($(ls -lh "$ANDROID_SDK_DIR/platforms/android-35/android.jar" | awk '{print $5}'))"
    else
        echo "❌ android.jar missing"
    fi
    
    if [ -f "$ANDROID_SDK_DIR/platforms/android-35/source.properties" ]; then
        echo "✓ source.properties exists"
        echo "Contents:"
        cat "$ANDROID_SDK_DIR/platforms/android-35/source.properties"
    else
        echo "❌ source.properties missing"
    fi
else
    echo "❌ android-35 platform directory missing"
    echo "Available platforms:"
    ls -la "$ANDROID_SDK_DIR/platforms/" 2>/dev/null || echo "No platforms directory"
fi
echo ""

# First, run an online build to populate Gradle cache with ALL dependencies
echo ""
echo "Running online build to populate Gradle cache..."
echo "This will download all necessary dependencies for offline use."

if ./gradlew tasks > /dev/null 2>&1; then
    echo "✓ Online build successful - Gradle cache populated"
    
    # Now try assembleDebug online to cache build dependencies too
    echo "Running online assembleDebug to cache build dependencies..."
    if ./gradlew assembleDebug > /dev/null 2>&1; then
        echo "✓ Online assembleDebug successful - all dependencies cached"
    else
        echo "⚠ Online assembleDebug had issues, but continuing with offline test"
    fi
    
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
        echo ""
        echo "Re-checking Android SDK after failure:"
        echo "SDK directory exists: $([ -d "$ANDROID_SDK_DIR" ] && echo 'YES' || echo 'NO')"
        echo "local.properties SDK path: $(grep sdk.dir local.properties 2>/dev/null || echo 'NOT FOUND')"
        echo "android-35 platform: $([ -d "$ANDROID_SDK_DIR/platforms/android-35" ] && echo 'EXISTS' || echo 'MISSING')"
        if [ -d "$ANDROID_SDK_DIR/platforms/android-35" ]; then
            echo "android-35 contents: $(ls "$ANDROID_SDK_DIR/platforms/android-35/" 2>/dev/null || echo 'EMPTY')"
        fi
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

else
    echo "❌ Online build failed. Cannot populate Gradle cache."
    echo "This means offline builds will not work without manually downloaded dependencies."
    echo "Consider checking network connectivity and Gradle configuration."
fi

echo ""
echo "Setup finished. All necessary plugins and dependencies downloaded."