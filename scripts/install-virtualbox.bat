@echo off
chcp 65001 >nul
echo ============================================================
echo   VorteXOS — VirtualBox Otomatik Kurulum Aracı
echo ============================================================
echo.
echo VirtualBox 7.x indiriliyor ve kuruluyor...
echo (Yönetici izni penceresi [UAC] çıkarsa 'Evet'e tıklayınız)
echo.

winget install --id Oracle.VirtualBox -e --accept-source-agreements --accept-package-agreements

if errorlevel 1 (
    echo.
    echo [BILGI] winget ile kurulum tamamlanamadıysa resmi web sitesinden indirebilirsiniz:
    echo https://www.virtualbox.org/wiki/Downloads
    echo.
) else (
    echo.
    echo [BAŞARILI] VirtualBox kuruldu!
    echo.
)

pause
