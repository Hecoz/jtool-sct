@echo off
rem
rem Script to compute Raja's gallery
rem
rem Usage: compute resolution
rem
rem Example: compute "256x192"
rem
rem On the contrary to the Unix version of this script, no option is
rem available since at most 9 options can be given to ..\bin\raja.bat


rem set image files extension (codec)
set img_ext="png"


set resolution=%1

if "%resolution%"=="" goto usage

set compute=..\bin\raja.bat -v -p txt -r %resolution% -d20

echo *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
echo              Computing Raja's gallery (%resolution%).
echo *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
echo.
echo.

set DATA_FILES=(AdvancedCristal AdvancedCristalIor Clown Cone Couloir Cristal Disco Extraction Lens1 Lens2 Mickey Moon OverlappingSpheres PenInWater Phong Priority PriorityIor SnowMan Transparency Tunnel)

FOR %%i IN %DATA_FILES% DO call %compute% -o %%i-%resolution%.%img_ext% %%i.raj

echo *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
echo       Computation of Raja's gallery (%resolution%) finished.
echo *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

goto end

:usage
echo Usage: compute.bat resolution

:end
