# Firebase App Distribution Setup Guide (Debug Builds Only)

This comprehensive guide explains how to configure GitHub Secrets for automatic Firebase App Distribution deployment of debug builds.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Firebase Project Setup](#firebase-project-setup)
3. [Android App Configuration](#android-app-configuration)
4. [Service Account Creation](#service-account-creation)
5. [GitHub Secrets Configuration](#github-secrets-configuration)
6. [Project Code Setup](#project-code-setup)
7. [Testing the Setup](#testing-the-setup)
8. [Troubleshooting](#troubleshooting)

## Prerequisites

Before starting, ensure you have:
- A Google account
- A GitHub repository with an Android project
- Admin access to both Firebase Console and GitHub repository
- Android Studio or similar development environment

## Firebase Project Setup

### Step 1: Create or Select Firebase Project

1. **Go to Firebase Console**: Open [https://console.firebase.google.com](https://console.firebase.google.com)
2. **Create New Project** (or select existing):
   - Click "Add project"
   - Enter project name (e.g., "My App Distribution")
   - Choose whether to enable Google Analytics (optional)
   - Click "Create project"
3. **Wait for project creation** to complete

### Step 2: Add Android App to Firebase Project

1. **In Firebase Console**, click on your project
2. **Add Android App**:
   - Click the Android icon or "Add app"
   - Enter your **Android package name** (must match your `applicationId` in `build.gradle`)
     - Example: `com.yourcompany.yourapp`
   - Enter **App nickname** (optional, e.g., "Debug App")
   - **Skip SHA-1** for now (not needed for App Distribution)
   - Click "Register app"

3. **Download google-services.json**:
   - Download the `google-services.json` file
   - **Important**: Save this file securely, we'll use its content later
   - Click "Next" → "Next" → "Continue to console"

### Step 3: Enable App Distribution

1. **In Firebase Console**, go to **App Distribution** from the left sidebar
2. **Click "Get Started"** if this is your first time
3. **Your Android app should appear** in the apps list
4. If not visible, refresh the page or ensure your app was properly registered

### Step 4: Create Tester Groups

1. **Go to "Testers & Groups" tab** in App Distribution
2. **Create a new group**:
   - Click "Add group"
   - Group name: `android-testers`
   - Description: "Debug build testers" (optional)
   - Click "Create group"

3. **Add testers to the group**:
   - Click on the `android-testers` group
   - Click "Add testers"
   - Enter email addresses of people who should test the app
   - Click "Add testers"

## Android App Configuration

### Step 5: Update Project Build Files

1. **Add Google Services Plugin** to project-level `build.gradle`:
```gradle
// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id 'com.google.gms.google-services' version '4.4.2' apply false  // Add this line
}
```

2. **Update app-level `build.gradle`**:
```gradle
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id 'com.google.gms.google-services'  // Add this line
}

android {
    namespace 'com.yourpackage.name'
    compileSdk 35

    defaultConfig {
        applicationId "com.yourpackage.name"  // Must match Firebase registration
        minSdk 30
        targetSdk 35
        versionCode project.hasProperty('buildNumber') ? project.buildNumber.toInteger() : 1  // Dynamic version
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    // ... rest of your configuration
}

dependencies {
    // Import the Firebase BoM
    implementation platform('com.google.firebase:firebase-bom:33.14.0')

    // Firebase Analytics
    implementation 'com.google.firebase:firebase-analytics'

    // ... your other dependencies
}
```

3. **Add google-services.json to .gitignore**:
```gitignore
# Firebase
google-services.json
```

4. **Place google-services.json in app/ directory** for local development

## Service Account Creation

### Step 6: Create Service Account for CI/CD

1. **Go to Google Cloud Console**: Open [https://console.cloud.google.com](https://console.cloud.google.com)

2. **Select the correct project**:
   - In the top project selector, choose the same project as your Firebase project
   - The project ID should match your Firebase project

3. **Navigate to Service Accounts**:
   - Go to **IAM & Admin** → **Service Accounts**
   - Or search for "Service Accounts" in the search bar

4. **Create Service Account**:
   - Click "**+ CREATE SERVICE ACCOUNT**"
   - **Service account name**: `firebase-app-distribution` (or any descriptive name)
   - **Service account ID**: Will auto-generate (e.g., `firebase-app-distribution@your-project.iam.gserviceaccount.com`)
   - **Description**: "Service account for Firebase App Distribution CI/CD"
   - Click "**CREATE AND CONTINUE**"

5. **Grant Permissions**:
   - In the "Grant this service account access to project" section
   - Click "**Select a role**"
   - Search for "**Firebase App Distribution Admin**"
   - Select "**Firebase App Distribution Admin**"
   - Click "**CONTINUE**"
   - Skip "Grant users access to this service account" (click "**DONE**")

6. **Generate JSON Key**:
   - Find your newly created service account in the list
   - Click on the **email address** of the service account
   - Go to the "**Keys**" tab
   - Click "**ADD KEY**" → "**Create new key**"
   - Select "**JSON**" format
   - Click "**CREATE**"
   - **Download the JSON file** - this is critical for GitHub Actions
   - **Store this file securely** - you'll need its entire content

## GitHub Secrets Configuration

### Step 7: Add Secrets to GitHub Repository

1. **Navigate to your GitHub repository**

2. **Go to Settings**:
   - Click "**Settings**" tab in your repository
   - Go to "**Secrets and variables**" → "**Actions**"

3. **Add the following secrets** (click "New repository secret" for each):

#### Secret 1: FIREBASE_APP_ID
- **Name**: `FIREBASE_APP_ID`
- **Value**: Your Firebase App ID
- **How to find**:
  1. Go to Firebase Console → Project Settings (gear icon)
  2. Scroll down to "Your apps"
  3. Click on your Android app
  4. Copy the "App ID" (format: `1:123456789:android:abcdef123456`)

#### Secret 2: FIREBASE_SERVICE_ACCOUNT_KEY_JSON_CONTENT
- **Name**: `FIREBASE_SERVICE_ACCOUNT_KEY_JSON_CONTENT`
- **Value**: Complete JSON content from the service account key file
- **How to get the value**:
  1. Open the JSON file you downloaded from Google Cloud Console
  2. Copy the **entire content** of the file (including all curly braces)
  3. The content should start with `{` and end with `}`
  4. Example structure:
  ```json
  {
    "type": "service_account",
    "project_id": "your-project-id",
    "private_key_id": "...",
    "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
    "client_email": "firebase-app-distribution@your-project.iam.gserviceaccount.com",
    ...
  }
  ```

#### Secret 3: GOOGLE_SERVICES_JSON_CONTENT
- **Name**: `GOOGLE_SERVICES_JSON_CONTENT`
- **Value**: Complete JSON content from google-services.json
- **How to get the value**:
  1. Open the `google-services.json` file you downloaded from Firebase
  2. Copy the **entire content** of the file
  3. Example structure:
  ```json
  {
    "project_info": {
      "project_number": "123456789",
      "project_id": "your-project-id",
      ...
    },
    "client": [...],
    ...
  }
  ```

## Project Code Setup

### Step 8: GitHub Actions Workflow

Ensure your `.github/workflows/android-ci.yml` file contains:

```yaml
name: Android CI with Firebase Distribution

on:
  push:
    branches:
      - main
  workflow_dispatch:

jobs:
  build_and_distribute:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout Repository
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        distribution: 'temurin'
        java-version: '17'
        cache: 'gradle'

    - name: Set up Android SDK
      uses: android-actions/setup-android@v3
      with:
        api-level: 34
        build-tools-version: 34.0.0

    - name: Create google-services.json
      if: env.GOOGLE_SERVICES_JSON_CONTENT != ''
      run: echo "${{ secrets.GOOGLE_SERVICES_JSON_CONTENT }}" > app/google-services.json

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Build Debug APK
      run: ./gradlew assembleDebug -PbuildNumber=${{ github.run_number }}

    - name: Distribute Debug APK to Firebase App Distribution
      uses: wzieba/firebase-app-distribution@v1.7.1
      with:
        appId: ${{ secrets.FIREBASE_APP_ID }}
        serviceCredentialsFileContent: ${{ secrets.FIREBASE_SERVICE_ACCOUNT_KEY_JSON_CONTENT }}
        file: app/build/outputs/apk/debug/app-debug.apk
        groups: "android-testers"
        releaseNotes: "Debug build: ${{ github.event.head_commit.message }}"
```

## Testing the Setup

### Step 9: Test the Complete Flow

1. **Commit and Push Changes**:
   ```bash
   git add .
   git commit -m "Add Firebase App Distribution"
   git push origin main
   ```

2. **Monitor GitHub Actions**:
   - Go to your GitHub repository
   - Click "**Actions**" tab
   - Watch the "Android CI with Firebase Distribution" workflow
   - Check for any errors in the logs

3. **Verify in Firebase Console**:
   - Go to Firebase Console → App Distribution
   - You should see a new debug build listed
   - Build should be distributed to the "android-testers" group

4. **Check Notifications**:
   - Testers in the group should receive email notifications
   - They can install the Firebase App Distribution app and download your APK

## Troubleshooting

### Common Issues and Solutions

#### 1. "App not found" Error
**Symptoms**: GitHub Actions fails with app not found
**Solutions**:
- Verify `FIREBASE_APP_ID` is correct in GitHub Secrets
- Ensure you clicked "Get Started" in Firebase App Distribution
- Check that the Android app is properly registered in Firebase

#### 2. Authentication Errors
**Symptoms**: "Permission denied" or authentication failures
**Solutions**:
- Verify the service account has "Firebase App Distribution Admin" role
- Ensure the JSON content in `FIREBASE_SERVICE_ACCOUNT_KEY_JSON_CONTENT` is complete and valid
- Check that you're using the correct Google Cloud project

#### 3. Build Failures
**Symptoms**: Gradle build fails in GitHub Actions
**Solutions**:
- Verify `google-services.json` content is valid in `GOOGLE_SERVICES_JSON_CONTENT`
- Check that your `applicationId` matches the package name in Firebase
- Ensure all Gradle dependencies are correctly configured

#### 4. "File not found" Errors
**Symptoms**: APK file not found during distribution
**Solutions**:
- Verify the APK path: `app/build/outputs/apk/debug/app-debug.apk`
- Check that `assembleDebug` task completed successfully
- Ensure you're building the correct variant

#### 5. Testers Not Receiving Notifications
**Symptoms**: App builds successfully but testers don't get notified
**Solutions**:
- Verify tester group name matches exactly: `android-testers`
- Check that testers are properly added to the group
- Ensure testers have Firebase App Distribution app installed
- Check spam folders for email notifications

### Debug Steps

1. **Check GitHub Actions Logs**:
   - Go to Actions tab → Failed workflow → Click on job → Expand each step
   - Look for specific error messages

2. **Verify All Secrets**:
   - Go to repository Settings → Secrets and variables → Actions
   - Ensure all three secrets are present and not empty

3. **Test Locally**:
   ```bash
   # Test building locally
   ./gradlew assembleDebug
   
   # Check if APK was created
   ls -la app/build/outputs/apk/debug/
   ```

4. **Validate JSON Files**:
   - Use online JSON validators to check your secret contents
   - Ensure no extra characters or formatting issues

## Manual Distribution (Fallback)

If automated distribution fails, you can manually upload:

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Build debug APK
./gradlew assembleDebug

# Distribute manually
firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
  --app YOUR_FIREBASE_APP_ID \
  --groups "android-testers"
```

## Key Benefits

Once set up, this automated system provides:
- **Automatic version incrementing** using GitHub run numbers
- **Instant distribution** to testers on every main branch push
- **Secure credential management** through GitHub Secrets
- **Consistent build environment** via GitHub Actions
- **Easy tester management** through Firebase groups
- **Automated notifications** to testers

## Next Steps

After successful setup, consider:
- Adding release build distribution for production testing
- Implementing additional test automation before distribution
- Setting up different tester groups for different build types
- Adding Slack/Discord notifications for build status
- Implementing rollback strategies for problematic builds