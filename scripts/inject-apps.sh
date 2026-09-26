#!/bin/bash
# ---------------------------------------------------------
# VorteXOS App Injection Script
# Copies compiled APKs into the mounted system image
# ---------------------------------------------------------
set -euo pipefail

SYS_MOUNT=$1
APKS_DIR=$2

PRIV_APP_DIR="$SYS_MOUNT/priv-app"

echo "Injecting apps from $APKS_DIR into $PRIV_APP_DIR..."

find "$APKS_DIR" -name "*.apk" -type f | while read -r apk_path; do
    apk_name=$(basename "$apk_path" .apk)
    target_dir="$PRIV_APP_DIR/$apk_name"
    
    echo " -> Installing $apk_name..."
    mkdir -p "$target_dir"
    cp "$apk_path" "$target_dir/${apk_name}.apk"
    
    # Set permissions
    chmod 755 "$target_dir"
    chmod 644 "$target_dir/${apk_name}.apk"
    # Note: Android typically expects specific chown, but 0:0 (root:root) works for priv-app
    chown -R root:root "$target_dir"
done

echo "App injection complete."
