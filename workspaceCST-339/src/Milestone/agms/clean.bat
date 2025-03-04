@echo off
echo Cleaning project...
taskkill /F /IM java.exe /T
timeout /t 2
rmdir /s /q target
echo Done!
