#!/usr/bin/env bash
set -euo pipefail

# ---------------------------------------------------------------------------
# Tubitak Phone OS - Yerel Derleme Scripti
# ---------------------------------------------------------------------------
# Bu script, Android-x86 kaynak kodunu senkronize eder ve ISO imajı derler.
# Ubuntu 22.04+ üzerinde test edilmiştir.
# ---------------------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
WORK_DIR="${PROJECT_ROOT}/android-x86"
MANIFEST_URL="https://github.com/android-x86/manifest"
MANIFEST_BRANCH="${ANDROID_VERSION:-android-x86-9.0-r2}"
LUNCH_TARGET="${LUNCH_TARGET:-android_x86_64-userdebug}"
BUILD_TARGET="${BUILD_TARGET:-iso_img}"
JOBS="$(nproc --all)"

echo "=============================================="
echo " Tubitak Phone OS Build Script"
echo "=============================================="
echo "Manifest URL:    ${MANIFEST_URL}"
echo "Manifest Branch: ${MANIFEST_BRANCH}"
echo "Lunch Target:    ${LUNCH_TARGET}"
echo "Build Target:    ${BUILD_TARGET}"
echo "Parallel Jobs:   ${JOBS}"
echo "Work Directory:  ${WORK_DIR}"
echo "=============================================="

# Gerekli araçları kontrol et
for cmd in repo git python3; do
  if ! command -v "${cmd}" &>/dev/null; then
    echo "Hata: ${cmd} bulunamadı. Lütfen önce scripts/local-setup.sh çalıştırın."
    exit 1
  fi
done

# Çalışma dizinini oluştur
mkdir -p "${WORK_DIR}"
cd "${WORK_DIR}"

# Repo init (eğer daha önce init yapılmamışsa)
if [ ! -d ".repo" ]; then
  echo "[1/4] Repo init yapılıyor..."
  repo init -u "${MANIFEST_URL}" -b "${MANIFEST_BRANCH}" \
    --depth=1 --no-tags --groups=all,-notdefault,-darwin,-mips
else
  echo "[1/4] Repo zaten init edilmiş."
fi

# Repo sync
# Başarısız olursa 2 kez daha denensin (ağ sorunları için)
echo "[2/4] Repo sync yapılıyor..."
repo sync -c --no-tags -j"${JOBS}" || repo sync -c --no-tags -j"${JOBS}" || repo sync -c --no-tags -j"${JOBS}"

# Özelleştirmeleri uygula (varsa)
if [ -d "${PROJECT_ROOT}/overlays" ]; then
  echo "[3/4] Proje overlay'leri uygulanıyor..."
  cp -r "${PROJECT_ROOT}/overlays/"* "${WORK_DIR}/" || true
fi

# Derleme
echo "[4/4] Derleme başlıyor..."
source build/envsetup.sh
lunch "${LUNCH_TARGET}"

# ccache aktif et
export USE_CCACHE=1
export CCACHE_EXEC="$(command -v ccache)"
ccache -M 20G

m -j"${JOBS}" "${BUILD_TARGET}"

# Çıktıları kopyala
ISO_PATHS="${WORK_DIR}/out/target/product/x86_64"/*.iso
if ls ${ISO_PATHS} 1>/dev/null 2>&1; then
  mkdir -p "${PROJECT_ROOT}/out"
  cp -v ${ISO_PATHS} "${PROJECT_ROOT}/out/"
  echo "✅ ISO çıktısı: ${PROJECT_ROOT}/out/"
else
  echo "⚠️ ISO dosyası bulunamadı."
fi

echo "=============================================="
echo " Derleme tamamlandı."
echo "=============================================="
