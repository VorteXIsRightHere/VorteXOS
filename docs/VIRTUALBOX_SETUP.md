# VirtualBox Kurulum ve Çalıştırma Kılavuzu

Bu kılavuz, Tubitak Phone OS çıktılarını (ISO veya OVA) Oracle VirtualBox üzerinde çalıştırmak için hazırlanmıştır.

## Gereksinimler

- Oracle VirtualBox 7.0+ ([İndir](https://www.virtualbox.org/wiki/Downloads))
- En az 4 GB boş RAM
- En az 20 GB boş disk alanı
- 64-bit işlemci ve sanallaştırma desteği (Intel VT-x / AMD-V)

## Yöntem 1: ISO Dosyasını Kullanarak Yeni VM Oluşturma

1. VirtualBox'ı açın.
2. **Yeni (New)** butonuna tıklayın.
3. Ad: `Tubitak Phone OS`
4. ISO Image: `out/tubitak-phone-os.iso` dosyasını seçin.
5. Type: `Linux`, Version: `Linux 2.6 / 3.x / 4.x (64-bit)` veya `Other Linux (64-bit)`
6. **Hardware** sekmesinde:
   - Base Memory: **2048 MB** veya daha fazla
   - Processors: **2 CPU**
7. **Virtual Hard Disk** sekmesinde:
   - Create a virtual hard disk now
   - Disk size: **8 GB** veya daha fazla
   - Format: **VDI**, dynamically allocated
8. VM oluşturulduktan sonra **Settings > System > Processor**'dan **Enable PAE/NX** seçeneğini işaretleyin.
9. **Settings > Display > Screen**'den **Video Memory**'i **16 MB** yapın ve **Graphics Controller** olarak **VMSVGA** seçin.
10. VM'i başlatın.

## Yöntem 2: OVA Dosyasını Import Etme

1. VirtualBox'ı açın.
2. **File > Import Appliance** seçeneğine tıklayın.
3. `out/tubitak-phone-os.ova` dosyasını seçin.
4. **Next** ve ardından **Import** butonuna tıklayın.
5. Import tamamlandıktan sonra VM'i seçip **Start**'a basın.

## Android-x86 Kurulumu

ISO veya OVA ile VM başlatıldığında karşınıza boot menüsü gelecektir:

1. **Advanced options** yerine direkt **Installation** seçeneğini seçin.
2. Disk seçim ekranında sanal diski seçin.
3. **ext4** dosya sistemi seçin ve formatlayın.
4. **Yes** ile GRUB bootloader kurulumunu onaylayın.
5. `/system` dizinini okunabilir-yazılabilir (read-write) olarak mount etmeyi seçin.
6. Kurulum tamamlandığında **Reboot** seçeneğini seçin.
7. İlk açılışta Android kurulum sihirbazını tamamlayın.

## Önerilen VirtualBox Ayarları

| Ayar | Değer | Açıklama |
|------|-------|----------|
| RAM | 2048+ MB | Android'in akıcı çalışması için |
| CPU | 2+ | Çoklu görevler için |
| Disk | 8+ GB | Uygulama kurulumu için |
| Ekran | VMSVGA, 16 MB | En iyi Android-x86 uyumluluğu |
| Ağ | NAT | İnternet erişimi için |
| USB | USB 1.1 / 2.0 | Fare ve klavye için |

## Klavye ve Fare Sorunu

Eğer fare imleci kaybolursa:
- VirtualBox menüsünden **Input > Mouse Integration** seçeneğini açıp kapatmayı deneyin.
- Alternatif olarak **Input > Keyboard > Soft Keyboard** kullanabilirsiniz.

## İnternet Bağlantısı

Varsayılan NAT ayarı ile Android-x86 internete çıkabilir. Eğer bağlantı sorunu yaşarsanız:
- **Settings > Network > Adapter 1**'in **NAT** olduğundan emin olun.
- Android içinde **Settings > Network & Internet > Wi-Fi** menüsünden **VirtWifi** ağını kontrol edin.

## Sorun Giderme

| Sorun | Çözüm |
|-------|-------|
| Boot etmiyor | ISO dosyasının bozuk olmadığını kontrol edin; VMSVGA deneyin |
| Yavaş çalışıyor | CPU/RAM artırın; 3D acceleration kapalı kalsın |
| İnternet yok | NAT ayarını ve VirtWifi bağlantısını kontrol edin |
| Ses çalışmıyor | VirtualBox ses aygıtını Intel HD Audio veya ICH AC97 yapın |

## İleri Seviye: OVA Export Etme

Kurulum yaptıktan sonra kendi hazır dağıtımınızı OVA olarak export edebilirsiniz:

```bash
VBoxManage export "Tubitak-Phone-OS" --output tubitak-phone-os-v1.ova
```

veya VirtualBox GUI'den **File > Export Appliance** kullanabilirsiniz.
