# Tubitak Theme Engine Dokümantasyonu

## Genel Bakış

Tubitak Theme Engine, Tubitak Telefon OS projesinin yenilikçi arayüz katmanıdır. Kullanıcılara iOS'taki gibi bir **Ayarlar > Temalar** sekmesi sunar ve 5 farklı akıllı tema modu ile duvar kağıdını otomatik olarak değiştirir.

## Mimari

```
┌─────────────────────────────────────┐
│         Settings / Launcher         │
│   (TubitakThemeEngine Activity)     │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│      ThemeMode (Enum)               │
│  PERSONAL / WEATHER / EMOTION /     │
│  LOCATION / TIME                    │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│   Mod-Specific Implementations      │
│   PersonalTheme, WeatherTheme, ...  │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│   TubitakWallpaperService         │
│   (Live Wallpaper Engine)         │
└─────────────────────────────────────┘
```

## Tema Modları Detayı

### 1. Kişisel Tema (`PersonalTheme`)

- Kullanıcı galeriden fotoğraf seçer.
- `ContentResolver` ile URI üzerinden `Bitmap` yüklenir.
- `WallpaperManager.setBitmap()` ile uygulanır.

### 2. Hava Durumu Teması (`WeatherTheme`)

- OpenWeatherMap API'den konuma göre hava durumu çekilir.
- `weather.main` değeri (Clear, Clouds, Rain, Snow, Thunderstorm) ve konum saati analiz edilir.
- Saat bilgisine göre gece/gündüz varyasyonu seçilir.
- Uygun duvar kağıdı URL'si belirlenir ve indirilir.

### 3. Duygu Durumu Teması (`EmotionTheme` + `FaceEmotionDetector`)

- Kameradan kişinin yüz hatlarına göre duygu tespiti yapılır.
- ML Kit Face Detection ile `smilingProbability`, `leftEyeOpenProbability`, `rightEyeOpenProbability` ve `headEulerAngle` değerleri analiz edilir.
- Duygular: **Mutlu, Sinirli, Üzgün, Sakin**.
- Ayarlardan açılıp kapatılabilir ve hassasiyeti ayarlanabilir.
- Her duygu için arka plan resmi ve sistem renkleri (accent, primary, secondary) atanır.
- Gelecekte: TensorFlow Lite FER modeli ile daha yüksek doğruluk sağlanabilir.

### 4. Konuma Özel Tema (`LocationTheme`)

- Cihaz konumu alınır.
- Google Maps Static API ile konumun uydu görüntüsü oluşturulur.
- Görüntü duvar kağıdı olarak ayarlanır.

### 5. Saate Uygun Tema (`TimeTheme`)

- Cihaz saati ve konumun yaklaşık yerel saati hesaplanır.
- Saat aralığına göre şafak, gündüz, alacakaranlık veya gece teması seçilir.
- Aynı mantık hava durumu temasında da kullanılır.

## API Anahtarları

Aşağıdaki API anahtarı `strings.xml` dosyasına eklenmelidir:

```xml
<string name="openweather_api_key">YOUR_OPENWEATHER_API_KEY</string>
```

Konum teması için Google Maps Static API key isteğe bağlıdır. Key verilmezse ücretsiz Wikimedia Maps / OpenStreetMap görüntüleri kullanılır.

## Sistem Renkleri ve OS Entegrasyonu

TubitakThemeEngine şu an için duvar kağıdını değiştirir. **Sistem renklerinin** (accent, primary, secondary) OS genelinde değişmesi için:

1. `TubitakThemeEngine` APK'sı `system/priv-app/TubitakThemeEngine/` altına kopyalanmalıdır.
2. Uygulamaya `android.permission.WRITE_SECURE_SETTINGS` ve `android.permission.WRITE_SETTINGS` gibi system izinleri verilmelidir.
3. Android 12+ Monet / Material You API veya RRO (Runtime Resource Overlay) kullanılarak sistem renkleri dinamik olarak değiştirilebilir.
4. `Settings` uygulamasının `AndroidManifest.xml` ve kaynaklarına yeni bir "Temalar" tercihi eklenir; tıklama ile `com.tubitak.themeengine.MainActivity` açılır.

## Ayarlar Uygulamasına Entegrasyon

1. `TubitakThemeEngine` APK'sı `system/priv-app/TubitakThemeEngine/` altına kopyalanır.
2. `Settings` uygulamasının `AndroidManifest.xml` ve kaynaklarına yeni bir "Temalar" tercihi eklenir.
3. Tıklama ile `com.tubitak.themeengine.MainActivity` açılır.

## Gelecek Geliştirmeler

- [ ] Duygu durumu otomatik tespiti
- [ ] Kullanıcı profili ve öğrenen tema önerileri
- [ ] Animasyonlu geçişler
- [ ] Sistem geneli tema renkleri (accent color) desteği
- [ ] Widget desteği
