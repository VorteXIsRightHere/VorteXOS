#!/usr/bin/env bash
set -euo pipefail

# ---------------------------------------------------------------------------
# Tubitak Phone OS - VirtualBox OVA Paketleme Scripti
# ---------------------------------------------------------------------------
# Bu script, derlenen ISO dosyasını kullanarak VirtualBox'a import edilebilir
# bir .ova dosyası üretir.
#
# Gereksinim: VirtualBox ve VBoxManage komut satırı aracının kurulu olması
#
# Kullanım:
#   ./scripts/create-ova.sh <iso-dosyasi> <cikti-ova-dosyasi>
#
# Örnek:
#   ./scripts/create-ova.sh out/android-x86_64-9.0-r2.iso out/tubitak-phone-os.ova
# ---------------------------------------------------------------------------

ISO_FILE="${1:-}"
OVA_FILE="${2:-out/tubitak-phone-os.ova}"
VM_NAME="Tubitak-Phone-OS"
VM_CPU=2
VM_RAM=2048
VM_DISK_MB=8192

if [ -z "${ISO_FILE}" ]; then
  echo "Kullanım: $0 <iso-dosyasi> [cikti-ova-dosyasi]"
  exit 1
fi

if [ ! -f "${ISO_FILE}" ]; then
  echo "Hata: ISO dosyası bulunamadı: ${ISO_FILE}"
  exit 1
fi

if ! command -v VBoxManage &>/dev/null; then
  echo "Hata: VBoxManage bulunamadı. Lütfen VirtualBox'ı kurun."
  exit 1
fi

mkdir -p "$(dirname "${OVA_FILE}")"

# Eski VM varsa kaldır
if VBoxManage showvminfo "${VM_NAME}" &>/dev/null; then
  echo "Eski VM kaldırılıyor: ${VM_NAME}"
  VBoxManage unregistervm "${VM_NAME}" --delete || true
fi

echo "VirtualBox VM oluşturuluyor: ${VM_NAME}"
VBoxManage createvm --name "${VM_NAME}" --ostype Linux_64 --register

VBoxManage modifyvm "${VM_NAME}" \
  --memory "${VM_RAM}" \
  --cpus "${VM_CPU}" \
  --boot1 dvd \
  --boot2 disk \
  --boot3 none \
  --boot4 none \
  --acpi on \
  --ioapic on \
  --nic1 nat \
  --nictype1 82540EM \
  --cableconnected1 on \
  --audio none \
  --usb on \
  --mouse ps2 \
  --keyboard ps2 \
  --vram 16 \
  --graphicscontroller vmsvga

# Sanal disk oluştur
VM_DIR="$(VBoxManage list systemproperties | grep "Default machine folder" | sed 's/.*:\s*//')"
DISK_FILE="${VM_DIR}/${VM_NAME}/${VM_NAME}.vdi"

VBoxManage createmedium disk \
  --filename "${DISK_FILE}" \
  --size "${VM_DISK_MB}" \
  --format VDI \
  --variant Standard

# SATA kontrolcüsü ekle
VBoxManage storagectl "${VM_NAME}" --name "SATA Controller" --add sata --controller IntelAhci
VBoxManage storageattach "${VM_NAME}" \
  --storagectl "SATA Controller" \
  --port 0 \
  --device 0 \
  --type hdd \
  --medium "${DISK_FILE}"

# ISO'yu DVD sürücüsüne tak
VBoxManage storageattach "${VM_NAME}" \
  --storagectl "SATA Controller" \
  --port 1 \
  --device 0 \
  --type dvddrive \
  --medium "${ISO_FILE}"

echo "OVA export ediliyor: ${OVA_FILE}"
VBoxManage export "${VM_NAME}" \
  --output "${OVA_FILE}" \
  --vsys 0 \
  --product "Tubitak Phone OS" \
  --vendor "Tubitak Project" \
  --version "0.1.0" \
  --description "Android-x86 tabanlı Tubitak telefon işletim sistemi."

echo "✅ OVA dosyası oluşturuldu: ${OVA_FILE}"
echo ""
echo "Bilgi: OVA dosyası VirtualBox'a import edildikten sonra ISO üzerinden"
echo "Android-x86 kurulumu yapılmalıdır. Kurulum sonrası tekrar OVA export"
echo "edilerek dağıtılabilir bir imaj elde edilebilir."
