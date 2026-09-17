# Tubitak Telefon OS Projesi

> Android tabanlı, VirtualBox uyumlu, GitHub Actions ile otomatik derlenen özel mobil işletim sistemi projesi.

## Proje Özeti

Bu proje, **Bliss OS** (Android-x86 tabanlı, en güncel Android sürümlerini destekleyen) hazır ISO’sunu temel alarak **x86 / VirtualBox ortamında çalışabilen** hafif bir telefon işletim sistemi geliştirmeyi amaçlar. Projenin asıl yeniliği, **Tubitak Theme Engine** adlı özel tema motorudur. Bu motor sayesinde kullanıcılar:

- Kişisel fotoğraflarını duvar kağıdı yapabilir.
- Hava durumuna göre otomatik tema kullanabilir.
- Duygu durumuna özel tema seçebilir.
- Anlık konumuna göre (Google Maps) arka plan kullanabilir.
- Cihaz ve konum saatine göre otomatik gündüz/gece teması kullanabilir.

## Neden Bliss OS + ISO Modifikasyon?

| Hedef | Sebep |
|-------|-------|
| En yeni Android | Bliss OS 15, Android 15 x86_64 ISO sunar |
| Güçsüz PC | Hazır ISO üzerinde modifikasyon yapılır, AOSP kaynak derleme yapılmaz |
| VirtualBox uyumluluğu | Bliss OS / Android-x86, standart PC donanımını destekler |
| Telefon OS deneyimi | Android uygulamalarını çalıştırır, telefon benzeri arayüz sunar |
| Otomatik derleme | GitHub Actions ile APK derleme ve ISO modifikasyonu otomatize edilir |

> ⚠️ **Önemli:** AOSP kaynak kodu 100 GB+, derlemesi 8–24 saat ve 32 GB+ RAM ister. Güçlü bir bilgisayarınız veya self-hosted runner’ınız yoksa **AOSP derlemesi yapmak pratik değildir**. Bu yüzden bu projede **hazır Bliss OS ISO’sunu modifiye etme** stratejisi kullanılır.

## Repo Yapısı

```
.
├── .github/workflows/           # CI/CD pipeline'ları
├── docs/                        # Proje dokümantasyonu
│   ├── PROJECT_PLAN.md          # Tubitak proje planı
│   ├── VIRTUALBOX_SETUP.md      # VirtualBox kurulum kılavuzu
│   └── THEME_ENGINE.md          # Tema motoru dokümantasyonu
├── manifest/                    # Android-x86 repo manifest şablonu
├── overlays/                    # OS özelleştirme katmanı
├── scripts/                     # Derleme ve paketleme scriptleri
├── TubitakThemeEngine/          # Android tema uygulaması (Kotlin)
└── README.md                    # Bu dosya
```

## Hızlı Başlangıç

### 1. Tema Uygulamasını Geliştir

```bash
cd TubitakThemeEngine
# Android Studio ile aç veya komut satırından:
./gradlew assembleDebug
```

### 2. API Anahtarlarını Gir

`TubitakThemeEngine/app/src/main/res/values/strings.xml` dosyasına:

- [OpenWeatherMap API Key](https://openweathermap.org/api)
- Konum teması için Google Maps key gerekmez; ücretsiz OpenStreetMap kullanılır.

### 3. Bliss OS ISO’sunu İndir ve VirtualBox’ta Test Et

Bliss OS 15 (Android 15) ISO’sunu indir: https://sourceforge.net/projects/blissos-x86/files/Official/BlissOS15/

VirtualBox kurulumu için: [`docs/VIRTUALBOX_SETUP.md`](docs/VIRTUALBOX_SETUP.md)

### 4. GitHub Actions ile Otomatik İşlemler

| Workflow | Açıklama |
|----------|----------|
| `build-theme-apk.yml` | TubitakThemeEngine APK’sini derler |
| `modify-bliss-os.yml` | Bliss OS ISO’suna APK’yi system app olarak gömer |

## CI/CD

- **APK Workflow:** [`.github/workflows/build-theme-apk.yml`](.github/workflows/build-theme-apk.yml)
- **ISO Modifikasyon Workflow:** [`.github/workflows/modify-bliss-os.yml`](.github/workflows/modify-bliss-os.yml)
- **Çıktılar:** APK, modifiye ISO/OVA

## Tema Motoru

Detaylı tema motoru dokümantasyonu: [`docs/THEME_ENGINE.md`](docs/THEME_ENGINE.md)

## Katkı ve Geliştirme

1. Bu repo'yu forklayın.
2. Yeni bir dal oluşturun: `git checkout -b b/feature-adi`
3. Değişiklikleri yapın ve commit atın.
4. Pull Request açın.

## Lisans

Bu proje Bliss OS, Android-x86 ve AOSP projelerinin lisanslarına tabidir. AOSP kaynak kodu **Apache 2.0** lisansı altında dağıtılmaktadır.

---

**Hazırlayan:** [Adınız Soyadınız]  
**Tubitak Proje Adı:** Özel Android Tabanlı Telefon İşletim Sistemi  
**Tarih:** 2026-09-17
