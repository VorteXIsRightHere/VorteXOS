@echo off
REM ---------------------------------------------------------
REM Script to create and configure a VirtualBox VM for VorteXOS
REM ---------------------------------------------------------

set VM_NAME=VorteXOS
set VM_RAM=4096
set VM_VRAM=128
set VM_CPU=2
set VM_HDD_SIZE=16384

if "%~1"=="" (
    set ISO_PATH=vortexos.iso
) else (
    set ISO_PATH=%~1
)

echo ==> Creating VirtualBox VM: %VM_NAME%

"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" createvm --name "%VM_NAME%" --ostype "Linux26_64" --register
"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" modifyvm "%VM_NAME%" --memory "%VM_RAM%" --vram "%VM_VRAM%" --cpus "%VM_CPU%"
"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" modifyvm "%VM_NAME%" --chipset ich9
"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" modifyvm "%VM_NAME%" --mouse ps2
"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" modifyvm "%VM_NAME%" --graphicscontroller vboxsvga

echo ==> Creating Virtual Disk
"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" createmedium disk --filename "%VM_NAME%.vdi" --size "%VM_HDD_SIZE%" --format VDI

echo ==> Attaching Storage Controllers
"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" storagectl "%VM_NAME%" --name "SATA Controller" --add sata --controller IntelAhci
"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" storageattach "%VM_NAME%" --storagectl "SATA Controller" --port 0 --device 0 --type hdd --medium "%VM_NAME%.vdi"

"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" storagectl "%VM_NAME%" --name "IDE Controller" --add ide
"C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" storageattach "%VM_NAME%" --storagectl "IDE Controller" --port 0 --device 0 --type dvddrive --medium "%ISO_PATH%"

echo ==> VM Configuration Complete. You can now start the VM.
pause
