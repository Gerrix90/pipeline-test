#!/bin/bash

# Set up environment to use local Gradle distribution

echo "Setting up environment to use local Gradle distribution..."

# Create .gradle directory structure
mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin

# Create directory for the specific distribution
HASH_DIR="4dxsxvlz075zraiywjxduzqqf"
mkdir -p $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/$HASH_DIR

# Copy the Gradle zip from our repo to the .gradle directory
cp -v gradle-dist/gradle-8.10.2-bin.zip $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/

# Extract Gradle into the hash directory
echo "Extracting Gradle distribution..."
unzip -q -o gradle-dist/gradle-8.10.2-bin.zip -d $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/$HASH_DIR

# Create the cached metadata file
echo "distributionUrl=https\://services.gradle.org/distributions/gradle-8.10.2-bin.zip" > $HOME/.gradle/wrapper/dists/gradle-8.10.2-bin/$HASH_DIR/gradle-8.10.2-bin.zip.ok

echo "Gradle setup complete. You can now run Gradle commands."