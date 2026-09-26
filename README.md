<div align="center">

<img src="branding/icons/vortexos_logo.png" alt="VorteXOS Logo" width="120" height="120"/>

# VorteXOS

**Duygu-Uyumlu, Hava-Uyumlu Android İşletim Sistemi**

[![Build & Release VorteXOS](https://github.com/VorteX/VorteXOS/actions/workflows/build-vortexos.yml/badge.svg)](https://github.com/VorteX/VorteXOS/actions/workflows/build-vortexos.yml)
![Android 11](https://img.shields.io/badge/Android-11%20(x86)-3DDC84?logo=android&logoColor=white)
![VirtualBox](https://img.shields.io/badge/VirtualBox-Uyumlu-183A61?logo=virtualbox&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?logo=kotlin&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue)
![Teknofest 2026](https://img.shields.io/badge/Teknofest-2026-red)

> Türkiye'nin ilk duygu ve hava durumu uyumlu Android tabanlı işletim sistemi.

</div>

---

## 🌟 Benzersiz Özellikler

### 🧠 VorteX Emotion Engine
Dünyada bir ilk — ön kamerayı kullanarak kullanıcının **yüz ifadesini gerçek zamanlı analiz eder** ve tüm işletim sistemi arayüzünü ile AI asistanın yanıt tonunu otomatik olarak duygusal duruma göre adapte eder.

| Duygu | UI Rengi | AI Asistan Tonu |
|-------|---------|-----------------|
| 😊 Mutlu | Sıcak sarı `#FFB300` | Enerjik, esprili |
| 😢 Üzgün | Yumuşak indigo `#5C6BC0` | Empatik, destekleyici |
| 😠 Kızgın | Sakinleştirici teal `#00897B` | Sakin, anlayışlı |
| 😴 Yorgun | Koyu gri-mavi `#546E7A` | Kısa, öz |
| 😊 Rahat | Doğal yeşil `#66BB6A` | Arkadaşça |

> ⚠️ **Gizlilik**: Tüm analiz cihaz üzerinde gerçekleşir. Hiçbir görüntü kaydedilmez veya sunucuya gönderilmez.

---

### 🌦️ VorteX WeatherSync
Gerçek zamanlı hava durumuna göre tüm OS temasını — duvar kağıdı efektleri, sistem renkleri ve animasyonlar dahil — değiştirir.

| Hava | Ekran Efekti | Accent Renk |
|------|-------------|-------------|
| ☀️ Güneşli | Altın ışık hüzmeleri | `#FFB300` |
| 🌧️ Yağmurlu | Yağmur damlaları akıyor | `#42A5F5` |
| ❄️ Karlı | Kar taneleri düşüyor | `#B3E5FC` |
| ⛈️ Fırtınalı | Şimşek + yağmur | `#4A148C` |
| 🌙 Gece | Yıldızlı gökyüzü | `#1A237E` |

---

## 📱 Dahil Olan Uygulamalar

| Uygulama | Açıklama |
|----------|---------|
| **VorteX Launcher** | Özel ana ekran (dock, app drawer, widget grid) |
| **VorteX AI Assistant** | Gemini API destekli, Türkçe, duygu-uyumlu asistan |
| **WeatherSync** | Canlı hava wallpaper + tema motoru |
| **Emotion Engine** | Kamera tabanlı duygu algılama servisi |
| **VorteX Calculator** | Bilimsel hesap makinesi |
| **VorteX Notepad** | Markdown not defteri |
| **VorteX File Manager** | Dosya yöneticisi |
| **VorteXOS Settings** | Sistem ayarları |
| **VorteX Guard** | Güvenlik: uygulama kilidi, gizlilik modu |

---

## 🏗️ Mimari

```
┌─────────────────────────────────────────────────┐
│  VorteX Launcher   │  Sistem Uygulamaları        │
├─────────────────────────────────────────────────┤
│  🧠 Emotion Engine │  🌦️ WeatherSync             │
│  (CameraX + ML Kit)│  (OpenWeatherMap + Canvas)  │
├─────────────────────────────────────────────────┤
│  VorteX Broadcast Bus (Intent-based Event Bus)  │
├─────────────────────────────────────────────────┤
│            Android 11 (x86_64)                  │
│           Linux Kernel 5.x                      │
└─────────────────────────────────────────────────┘
```

---

## 🚀 Kurulum & Build

### Önkoşullar
- JDK 17+
- Android SDK (API 33)
- VirtualBox 7.x

### 1. Kaynak Koddan Derleme

```bash
git clone https://github.com/YourUsername/VorteXOS.git
cd VorteXOS
./gradlew assembleRelease
```

### 2. ISO Oluşturma (Linux / GitHub Actions)

```bash
# Base ISO indir, uygulamaları enjekte et, yeni ISO üret
sudo ./scripts/remaster-iso.sh base-android-x86.iso ./apks VorteXOS.iso
```

### 3. VirtualBox'ta Çalıştırma

**Windows:**
```bat
scripts\setup-virtualbox.bat VorteXOS.iso
```

**Linux/macOS:**
```bash
chmod +x scripts/setup-virtualbox.sh
./scripts/setup-virtualbox.sh VorteXOS.iso
```

**Manuel Ayarlar (VirtualBox GUI):**

| Ayar | Değer |
|------|-------|
| Tip | Linux (64-bit) |
| RAM | 4096 MB |
| CPU | 2 çekirdek |
| Chipset | ICH9 |
| Fare | PS/2 Mouse |
| Video | 128 MB, VBoxSVGA |
| Disk | 16 GB VDI |

### 4. Boot Parametreleri (Sorun giderme)

VirtualBox GRUB ekranında `e` tuşuna basın ve şunu ekleyin:
```
nomodeset xforcevesa androidboot.selinux=permissive
```

---

## 🤖 GitHub Actions CI/CD

Her `v*` etiketi push'unda otomatik olarak:
1. Tüm Kotlin uygulamaları derlenir
2. Android-x86 base ISO indirilir
3. Özel bileşenler enjekte edilir
4. Bootable VorteXOS ISO üretilir
5. GitHub Releases'a yayınlanır

---

## 🔑 API Anahtarları Yapılandırması

`local.properties` dosyasına ekleyin (git'e commit etmeyin!):
```properties
GEMINI_API_KEY=your_gemini_api_key_here
OPENWEATHER_API_KEY=your_openweathermap_api_key_here
```

---

## 📂 Proje Yapısı

```
VorteXOS/
├── .github/workflows/     # GitHub Actions CI/CD
├── apps/
│   ├── common/            # Paylaşılan kütüphane (VorteXThemeManager)
│   ├── launcher/          # VorteX Launcher
│   ├── emotion-engine/    # 🧠 Duygu Motoru
│   ├── weather-sync/      # 🌦️ Hava Senkronizasyonu
│   ├── assistant/         # 🤖 AI Asistan
│   ├── calculator/        # Hesap Makinesi
│   ├── notepad/           # Not Defteri
│   ├── filemanager/       # Dosya Yöneticisi
│   ├── settings/          # Sistem Ayarları
│   └── security/          # VorteX Guard
├── branding/
│   ├── bootanimation/     # Boot animasyonu kareleri + generator
│   └── wallpapers/
├── scripts/               # ISO remastering & VirtualBox scriptleri
├── system/                # build.prop, overlay
├── virtualbox/            # VM şablonu & kurulum kılavuzu
└── docs/                  # Teknik dokümantasyon
```

---

## 📄 Lisans

MIT License — Ayrıntılar için [LICENSE](LICENSE) dosyasına bakın.

---

<div align="center">

**VorteXOS — Teknofest 2026**  
*Türkiye'nin Duygu-Uyumlu İşletim Sistemi*

</div>
