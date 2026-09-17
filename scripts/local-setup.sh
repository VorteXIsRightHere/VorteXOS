#!/usr/bin/env bash
set -euo pipefail

# ---------------------------------------------------------------------------
# Tubitak Phone OS - Yerel Geliştirme Ortamı Kurulum Scripti
# ---------------------------------------------------------------------------
# Ubuntu 22.04+ üzerinde Android-x86 derlemesi için gerekli araçları kurar.
# ---------------------------------------------------------------------------

echo "=============================================="
echo " Tubitak Phone OS - Ortam Kurulumu"
echo "=============================================="

if [ "$(id -u)" -ne 0 ]; then
  echo "Bu script sudo ile çalıştırılmalıdır."
  exit 1
fi

# Paket listesini güncelle
echo "[1/5] Paket listesi güncelleniyor..."
apt-get update

# Gerekli paketleri kur
echo "[2/5] Derleme bağımlılıkları kuruluyor..."
apt-get install -y \
  git-core gnupg flex bison build-essential zip curl zlib1g-dev \
  libc6-dev-i386 x11proto-core-dev libx11-dev lib32z1-dev \
  libgl1-mesa-dev libxml2-utils xsltproc unzip fontconfig \
  repo ccache bc libncurses5-dev libssl-dev python3-pip \
  libelf-dev libssl-dev libffi-dev libxml2-dev libxslt1-dev \
  openjdk-11-jdk virtualbox virtualbox-ext-pack

# Repo aracını güncelle (eski sürüm varsa)
echo "[3/5] Repo aracı güncelleniyor..."
mkdir -p /usr/local/bin
curl https://storage.googleapis.com/git-repo-downloads/repo > /usr/local/bin/repo
chmod a+x /usr/local/bin/repo

# ccache yapılandır
echo "[4/5] ccache yapılandırılıyor..."
ccache -M 20G
ccache -o compression=true

# Git yapılandırması
echo "[5/5] Git yapılandırması..."
if [ -z "$(git config --global user.name || true)" ]; then
  git config --global user.name "Tubitak Developer"
fi
if [ -z "$(git config --global user.email || true)" ]; then
  git config --global user.email "developer@tubitak.local"
fi

echo "=============================================="
echo " Kurulum tamamlandı."
echo " Sonraki adım: ./scripts/build.sh"
echo "=============================================="
