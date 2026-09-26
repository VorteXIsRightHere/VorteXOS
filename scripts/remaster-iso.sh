#!/bin/bash
# ---------------------------------------------------------
# VorteXOS ISO Remastering Script
# Requirements: squashfs-tools, xorriso, isolinux, simg2img, img2simg, e2fsprogs
# ---------------------------------------------------------
set -euo pipefail

if [ "$#" -lt 3 ]; then
    echo "Usage: $0 <base-iso> <apks-dir> <output-iso>"
    exit 1
fi

BASE_ISO=$1
APKS_DIR=$2
OUTPUT_ISO=$3

WORK_DIR=$(mktemp -d)
ISO_EXTRACT="$WORK_DIR/iso_extract"
SQUASH_EXTRACT="$WORK_DIR/squash_extract"
SYS_MOUNT="$WORK_DIR/sys_mount"

echo "==> Setting up workspace in $WORK_DIR"
mkdir -p "$ISO_EXTRACT" "$SQUASH_EXTRACT" "$SYS_MOUNT"

echo "==> Extracting Base ISO..."
osirrox -indev "$BASE_ISO" -extract / "$ISO_EXTRACT" > /dev/null 2>&1

echo "==> Unpacking system.sfs..."
if [ -f "$ISO_EXTRACT/system.sfs" ]; then
    unsquashfs -d "$SQUASH_EXTRACT" "$ISO_EXTRACT/system.sfs"
    SYS_IMG="$SQUASH_EXTRACT/system.img"
else
    # Some Android-x86 variants might have system.img directly
    SYS_IMG="$ISO_EXTRACT/system.img"
fi

echo "==> Resizing and mounting system.img..."
# If system.img is sparse, convert it
if file "$SYS_IMG" | grep -q "Android sparse image"; then
    simg2img "$SYS_IMG" "$WORK_DIR/system_raw.img"
    SYS_IMG="$WORK_DIR/system_raw.img"
fi

# Expand by 500MB for our modifications
e2fsck -yf "$SYS_IMG"
resize2fs "$SYS_IMG" +500M
sudo mount -o loop "$SYS_IMG" "$SYS_MOUNT"

echo "==> Injecting Apps and Modifying System..."
# Call helper script
sudo ./scripts/inject-apps.sh "$SYS_MOUNT" "$APKS_DIR"

echo "==> Removing Stock Launcher3..."
sudo rm -rf "$SYS_MOUNT/priv-app/Launcher3"

echo "==> Applying VorteXOS build.prop..."
if [ -f "./system/build.prop" ]; then
    sudo cp ./system/build.prop "$SYS_MOUNT/build.prop"
    sudo chmod 644 "$SYS_MOUNT/build.prop"
fi

echo "==> Applying Boot Animation..."
if [ -d "./branding/bootanimation" ]; then
    cd ./branding/bootanimation
    zip -r0 "$WORK_DIR/bootanimation.zip" ./*
    cd -
    sudo mkdir -p "$SYS_MOUNT/media"
    sudo cp "$WORK_DIR/bootanimation.zip" "$SYS_MOUNT/media/bootanimation.zip"
    sudo chmod 644 "$SYS_MOUNT/media/bootanimation.zip"
fi

echo "==> Unmounting system.img..."
sudo umount "$SYS_MOUNT"

echo "==> Re-packing system.sfs..."
if [ -d "$SQUASH_EXTRACT" ]; then
    rm "$ISO_EXTRACT/system.sfs"
    mksquashfs "$SQUASH_EXTRACT" "$ISO_EXTRACT/system.sfs" -comp xz -b 1M
fi

echo "==> Building Final Bootable ISO..."
# Generate ISO using xorriso
cd "$ISO_EXTRACT"
xorriso -as mkisofs \
    -r -V "VorteXOS" \
    -cache-inodes -J -l \
    -b isolinux/isolinux.bin \
    -c isolinux/boot.cat \
    -no-emul-boot -boot-load-size 4 -boot-info-table \
    -eltorito-alt-boot \
    -e efi/boot/efiboot.img \
    -no-emul-boot \
    -isohybrid-gpt-basdat \
    -o "../$OUTPUT_ISO" .
cd -

mv "$WORK_DIR/$OUTPUT_ISO" "$OUTPUT_ISO"

echo "==> Cleanup..."
sudo rm -rf "$WORK_DIR"

echo "==> Remastering Complete! Output: $OUTPUT_ISO"
