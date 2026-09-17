# Tubitak Theme Engine

Tubitak Telefon OS projesinin özel tema motoru. Android (Kotlin) ile yazılmıştır ve 5 farklı tema modu sunar.

## Tema Modları

1. **Kişisel Tema** — Kullanıcının galeriden seçtiği fotoğrafı duvar kağıdı yapar.
2. **Hava Durumu Teması** — OpenWeatherMap API ile anlık hava durumuna ve konum saatine göre tema değişir.
3. **Duygu Durumu Teması** — Kullanıcının seçtiği duygu durumuna göre renk ve arka plan değişir.
4. **Konuma Özel Tema** — Google Maps Static API ile cihazın anlık konumunun uydu fotoğrafını gösterir.
5. **Saate Uygun Tema** — Cihaz ve konum saatine göre şafak/gündüz/alacakaranlık/gece temaları seçer.

## Gereksinimler

- Android Studio Hedgehog veya daha yenisi
- Android SDK 34
- JDK 17
- OpenWeatherMap API Key
- Google Maps Static API Key

## Kurulum

1. `TubitakThemeEngine/app/src/main/res/values/strings.xml` dosyasındaki API anahtarlarını kendi anahtarlarınızla değiştirin.
2. Android Studio ile projeyi açın veya komut satırından derleyin:

```bash
./gradlew assembleDebug
```

3. Çıktı: `app/build/outputs/apk/debug/app-debug.apk`

## OS’e Entegrasyon

Bu uygulama daha sonra Bliss OS / Android-x86 system image’ına `priv-app` veya `system/app` olarak gömülecek ve Ayarlar uygulamasına "Temalar" sekmesi olarak entegre edilecektir.

## Duygu Durumu Teması

`EmotionTheme.kt` dosyası şu an için hazır modüldür. Kullanıcıdan gelecek duygu analizi özellikleri (örneğin: yüz ifadesi, ses tonu, günlük not analizi) buraya eklenecektir.
