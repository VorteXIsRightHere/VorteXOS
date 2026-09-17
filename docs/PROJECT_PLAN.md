# Tubitak Telefon OS Projesi - Proje Planı

## 1. Proje Başlığı

**Android Tabanlı, VirtualBox Uyumlu ve GitHub Actions ile Otomatik Derlenen Özel Telefon İşletim Sistemi**

## 2. Proje Özeti

Bu projede, açık kaynak Android AOSP kaynak kodu temel alınarak **x86 / x86_64 PC ve sanal makine ortamlarında çalışabilen** hafif bir telefon işletim sistemi geliştirilecektir. Projenin tüm derleme ve paketleme süreçleri **GitHub Actions CI/CD** ile otomatikleştirilecek; çıktı olarak **ISO** ve **VirtualBox OVA** dosyaları üretilecektir.

## 3. Problemin Tanımı

Mevcut Android dağıtımları genellikle ARM tabanlı telefonlar için optimize edilmiştir. Geliştiriciler ve araştırmacılar, Android'i PC ve sanal makine ortamında test etmek istediklerinde uyum sorunlarıyla karşılaşır. Ayrıca, özel bir Android dağıtımı geliştirmek isteyenler için derleme süreci karmaşık ve tekrarlanabilir değildir.

## 4. Proje Hedefleri

1. Android-x86 tabanlı minimal ve özelleştirilebilir bir işletim sistemi oluşturmak.
2. Sanal makine ortamında (VirtualBox) sorunsuz çalışan bir ISO/OVA çıktısı üretmek.
3. Derleme sürecini GitHub Actions ile tam otomatik hale getirmek.
4. Gereksiz sistem uygulamalarını kaldırarak hafif ve hızlı bir kullanıcı deneyimi sunmak.
5. Tubitak sunum ve raporlama için profesyonel dokümantasyon oluşturmak.

## 5. Teknik Altyapı

| Bileşen | Açıklama |
|---------|----------|
| İşletim Sistemi Tabanı | Bliss OS 15 (Android 15, Android-x86 tabanlı, x86/x86_64 destekli) |
| Derleme Ortamı | Ubuntu 22.04 LTS |
| Repo Aracı | Google `repo` |
| CI/CD | GitHub Actions |
| Sanallaştırma | Oracle VirtualBox |
| Çıktı Formatları | ISO (Live CD / kurulum), OVA (VirtualBox import) |
| Versiyon Kontrolü | Git + GitHub |

## 6. Yöntem ve Aşamalar

### Aşama 1: Hazırlık ve Ortam Kurulumu
- Ubuntu geliştirme ortamının kurulması
- Gerekli derleme bağımlılıklarının yüklenmesi
- Android-x86 manifest dosyasının incelenmesi

### Aşama 2: Kaynak Kod Senkronizasyonu
- `repo init` ve `repo sync` ile Android-x86 kaynak kodunun indirilmesi
- GitHub Actions üzerinde cache stratejisinin oluşturulması

### Aşama 3: Özelleştirme
- Tubitak Theme Engine entegrasyonu
- Duygu durumu, hava durumu, konum ve saat tabanlı akıllı temalar
- Gereksiz uygulamaların kaldırılması
- Türkçe dil desteğinin varsayılan yapılması

### Aşama 4: Derleme ve Test
- Yerel ortamda `m iso_img` ile ISO derleme
- VirtualBox'da ISO test edilmesi
- Hata kayıtlarının analizi

### Aşama 5: CI/CD Otomasyonu
- GitHub Actions workflow dosyasının yazılması
- Self-hosted runner veya larger runner yapılandırması
- Otomatik artifact ve release oluşturma

### Aşama 6: Paketleme ve Dağıtım
- ISO'dan OVA paketleme scriptinin yazılması
- GitHub Releases üzerinden dağıtım
- Kullanım kılavuzunun hazırlanması

## 7. Zaman Planı (Öneri)

| Hafta | Görev |
|-------|-------|
| 1-2 | Ortam kurulumu, Android-x86 kaynak kodunun indirilmesi |
| 3-4 | İlk başarılı ISO derlemesi ve VirtualBox testi |
| 5-6 | Arayüz özelleştirmeleri ve gereksiz uygulamaların kaldırılması |
| 7-8 | GitHub Actions pipeline'ının kurulması ve test edilmesi |
| 9-10 | OVA paketleme, dokümantasyon ve sunum hazırlığı |

## 8. Beklenen Çıktılar

1. **Kaynak Kod:** GitHub üzerinde açık kaynak repo.
2. **ISO Dosyası:** VirtualBox ve fiziksel PC'lerde çalıştırılabilir Android-x86 imajı.
3. **OVA Dosyası:** Tek tıkla VirtualBox'a import edilebilir sanal makine.
4. **CI/CD Pipeline:** Her push'ta otomatik derleyen GitHub Actions workflow'u.
5. **Dokümantasyon:** Kurulum, derleme ve kullanım kılavuzları.
6. **Tubitak Raporu:** Proje planı, yöntem, sonuçlar ve değerlendirme.

## 9. Değerlendirme Kriterleri

- Derlemenin başarıyla tamamlanması
- VirtualBox'da boot edebilmesi
- Temel Android uygulamalarının çalışması
- CI/CD pipeline'ının düzgün çalışması
- Projenin açık kaynak ve yeniden üretilebilir olması

## 10. Kaynaklar

- [Android Open Source Project](https://source.android.com/)
- [Android-x86 Project](http://www.android-x86.org/)
- [Android-x86 GitHub](https://github.com/android-x86)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [VirtualBox Manual](https://www.virtualbox.org/manual/)

---

**Proje Sahibi:** [Adınız Soyadınız]  
**Danışman Öğretmen:** [Danışman Adı]  
**Okul:** [Okul Adı]  
**Tarih:** 2026-09-17
