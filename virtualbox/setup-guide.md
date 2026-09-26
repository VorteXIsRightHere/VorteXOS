# VorteXOS - VirtualBox Setup Guide

## English
1. Create a new VirtualBox VM.
2. Select **Type**: Linux, **Version**: Linux 2.6/3.x/4.x/5.x (64-bit).
3. Allocate **4096 MB** of RAM and **2 CPUs**.
4. Set Chipset to **ICH9** and Pointing Device to **PS/2 Mouse**.
5. Set Video Memory to **128 MB** and Graphics Controller to **VBoxSVGA**.
6. Create a dynamically allocated **16 GB VDI** virtual hard disk.
7. Attach the `vortexos.iso` to the virtual optical drive.
8. Boot the VM and add the following boot flags to the GRUB menu if necessary:
   `nomodeset xforcevesa androidboot.selinux=permissive`

## Türkçe
1. Yeni bir VirtualBox Sanal Makinesi oluşturun.
2. **Tür**: Linux, **Sürüm**: Linux 2.6/3.x/4.x/5.x (64-bit) seçin.
3. **4096 MB** RAM ve **2 İşlemci Çekirdeği** atayın.
4. Yonga Setini **ICH9** ve İşaretleme Aygıtını **PS/2 Fare** olarak ayarlayın.
5. Görüntü Belleğini **128 MB** ve Grafik Denetleyicisini **VBoxSVGA** yapın.
6. Dinamik olarak ayrılan **16 GB VDI** sanal sabit disk oluşturun.
7. `vortexos.iso` dosyasını sanal optik sürücüye ekleyin.
8. Sanal makineyi başlatın ve gerekirse GRUB menüsüne şu boot parametrelerini ekleyin:
   `nomodeset xforcevesa androidboot.selinux=permissive`
