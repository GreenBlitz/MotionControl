@echo off

:begin
echo Are you sure? this will override the current files in eclipse.
choice
if "%ERRORLEVEL%" == "2" goto exit
robocopy ".\intelliJ\src" ".\Eclipse_final\MotionTesting\src"
for /d %%i in (".\intelliJ\src\*") do (
	set "firstVar=%%i"
	echo %%i
	echo "%firstVar%"
	robocopy %%i ".\Eclipse_final\MotionTesting\src\%%~nxi" /e /is
)
goto begin
:exit
