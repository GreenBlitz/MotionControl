@echo off

echo This file copies all of the intelliJ workspace into eclipse,
echo it will overwrite anything as needed.
echo press 'Y' to preform copy, 'N' to exit the program.
echo ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

:begin
echo Are you sure? this will overwrite the current files in eclipse.
choice
if "%ERRORLEVEL%" == "2" goto exit
for /d %%i in (".\intelliJ\src\*") do (
	robocopy %%i ".\Eclipse_final\MotionTesting\src\%%~nxi" /e /is
)
goto begin
:exit
