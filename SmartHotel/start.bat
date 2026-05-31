@echo off
echo Starting Docker...
start cmd /k "cd /d D:\Download\Smart-Hotel\SmartHotel && docker compose up"

echo Waiting for containers to start...
timeout /t 30

echo Importing database...
docker cp D:\Download\Smart-Hotel\SmartHotel\smarthoteldb.sql smarthotel-db:/smarthoteldb.sql
docker exec -i smarthotel-db mysql -u root -pAbcd123 smarthoteldb -e "source /smarthoteldb.sql"

echo Starting ngrok...
start cmd /k "C:\Users\03358\Downloads\ngrok-v3-stable-windows-amd64\ngrok.exe http 8080"

echo Done! Access http://localhost:8080
pause