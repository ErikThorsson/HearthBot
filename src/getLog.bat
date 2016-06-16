CD C:\Users\eorndahl\AppData\Local\Blizzard\Hearthstone\Logs
for /f "delims=" %%x in ('dir /od /b *.*') do set recent=%%x
COPY %recent% C:\Users\eorndahl\Documents\HearthBot\log.txt
