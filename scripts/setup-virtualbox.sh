#!/bin/bash
# ---------------------------------------------------------
# Script to create and configure a VirtualBox VM for VorteXOS
# ---------------------------------------------------------
set -euo pipefail

VM_NAME="VorteXOS"
VM_RAM="4096"
VM_VRAM="128"
VM_CPU="2"
VM_HDD_SIZE="16384"
ISO_PATH=$(realpath "${1:-vortexos.iso}")

echo "==> Creating VirtualBox VM: $VM_NAME"

VBoxManage createvm --name "$VM_NAME" --ostype "Linux26_64" --register
VBoxManage modifyvm "$VM_NAME" --memory "$VM_RAM" --vram "$VM_VRAM" --cpus "$VM_CPU"
VBoxManage modifyvm "$VM_NAME" --chipset ich9
VBoxManage modifyvm "$VM_NAME" --mouse ps2
VBoxManage modifyvm "$VM_NAME" --graphicscontroller vboxsvga

echo "==> Creating Virtual Disk"
VBoxManage createmedium disk --filename "$VM_NAME.vdi" --size "$VM_HDD_SIZE" --format VDI

echo "==> Attaching Storage Controllers"
VBoxManage storagectl "$VM_NAME" --name "SATA Controller" --add sata --controller IntelAhci
VBoxManage storageattach "$VM_NAME" --storagectl "SATA Controller" --port 0 --device 0 --type hdd --medium "$VM_NAME.vdi"

VBoxManage storagectl "$VM_NAME" --name "IDE Controller" --add ide
VBoxManage storageattach "$VM_NAME" --storagectl "IDE Controller" --port 0 --device 0 --type dvddrive --medium "$ISO_PATH"

echo "==> VM Configuration Complete. You can now start the VM."
