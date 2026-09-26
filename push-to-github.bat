@echo off
setlocal

echo ============================================================
echo   VorteXOS - GitHub Dagitim ve Yayinlama Araci
echo ============================================================
echo.

:: Git ve GitHub CLI PATH ayarlarini ekle
set "PATH=C:\Users\VorteX\.gemini\antigravity\scratch\tools\git\cmd;C:\Program Files\GitHub CLI;%PATH%"

:: Proje kok dizinine gec
cd /d "C:\Users\VorteX\.gemini\antigravity\scratch\VorteXOS"

echo [1/4] Git ve GitHub CLI kontrol ediliyor...
git.exe --version
if errorlevel 1 goto err_git

gh.exe --version
if errorlevel 1 goto err_gh

echo.
echo [2/4] GitHub giris kontrolu yapiliyor...
gh.exe auth status
if errorlevel 1 goto do_login
goto check_repo

:do_login
echo.
echo [BILGI] GitHub hesabinizla oturum acilacak.
echo Tarayici acilacak ve tek kullanimlik kod girmeniz istenecek.
echo Lutfen acilan sayfada onay verin.
echo.
gh.exe auth login -w -p https
if errorlevel 1 goto err_login

:check_repo
echo.
echo [3/4] GitHub deposu kontrol ediliyor...
git.exe remote get-url origin >nul 2>&1
if errorlevel 1 goto create_repo
goto do_push

:create_repo
echo GitHub uzerinde VorteXOS deposu olusturuluyor...
gh.exe repo create VorteXOS --public --source=. --remote=origin
if errorlevel 1 goto err_create

:do_push
echo.
echo [4/4] Kodlar ve v1.0.0 surumu GitHub'a gonderiliyor...
git.exe push -u origin main
git.exe push origin v1.0.0

echo.
echo ============================================================
echo   Tebrikler! VorteXOS basariyla GitHub'a yuklendi!
echo   GitHub Actions otomatik olarak ISO'yu derlemeye basladi.
echo ============================================================
echo.
goto end

:err_git
echo [HATA] Git programi bulunamadi.
goto end

:err_gh
echo [HATA] GitHub CLI programi bulunamadi.
goto end

:err_login
echo [HATA] GitHub giris islemi tamamlanamadi.
goto end

:err_create
echo [HATA] GitHub deposu olusturulamadi.
goto end

:end
echo.
echo Cikmak icin bir tusa basin...
pause >nul
