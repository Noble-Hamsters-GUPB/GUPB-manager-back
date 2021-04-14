@Rem This file runs gupb module. call params:
:: * path_to_gupb_parent
:: * name_of_gupb_dir
::

@ECHO OFF
:: UTF-8
CHCP 65001 > NUL

CALL :get_formatted_date_and_time current_time_stamp

:: To speed up tests this variable can be empty after successful installation of packages for GUPB version
:: By default it should be defined
::SET SETUP=
SET SETUP=anything could be written here


SET runner_dir=%~1
SET gupb_dir_name=%2
SET gupb_dir_path=%runner_dir%\%gupb_dir_name%
SET venv_name=GUPB-venv
SET setup_logs_dir="%runner_dir%\setup_logs_dir"
SET date_time=%date%-%time%
SET setup_logs_file="%setup_logs_dir:"=%\%current_time_stamp%_setup_log.txt"

IF NOT EXIST %setup_logs_dir% MKDIR %setup_logs_dir%

SETLOCAL
cd "%gupb_dir_path%"

IF DEFINED SETUP (
    py -m pip install --user virtualenv > %setup_logs_file%

    :: create venv
    py -m venv "%venv_name%" >> %setup_logs_file%

    CALL .\%venv_name%\Scripts\activate.bat
    py -m pip install --upgrade pip >> %setup_logs_file%

    py -m pip install -r "%gupb_dir_path%\requirements.txt" >> %setup_logs_file%
) ELSE (
    CALL .\%venv_name%\Scripts\activate.bat
)

ECHO Execution starting >&2
py -m gupb
CALL .\%venv_name%\Scripts\deactivate.bat
ENDLOCAL

IF DEFINED SETUP (
    ECHO Done >> %setup_logs_file%
)

::PAUSE
EXIT /B

::----------------------------------------------------------------------------------------------------------------------

@rem First param is number, second is returned value
:is_numeric

SETLOCAL EnableDelayedExpansion
SET "LOCALVAR="&FOR /F "delims=0123456789" %%i IN ("%1") DO SET LOCALVAR=%%i
IF DEFINED LOCALVAR (SET /A returned=0) else (SET /A returned=1)

ENDLOCAL & SET %2=%returned%
EXIT /B 0

::----------------------------------------------------------------------------------------------------------------------

@rem Get date and time in format DD_MM_YY__HH_MM_SS
:get_formatted_date_and_time

SETLOCAL EnableDelayedExpansion

SET mydate=%DATE:.=_%
SET mydate=%mydate: =%
SET mytime=%TIME::=_%
SET mytime=%mytime: =%

SETLOCAL
FOR /F "tokens=1 delims=," %%A IN ("%mytime%") DO (
    ENDLOCAL
    SET mytime=%%A
)

CALL :is_numeric %mytime:~1,1% returned
IF %returned% EQU 0 SET mytime=0%mytime%

SET mytimestamp=%mydate%__%mytime%

ENDLOCAL & SET %1=%mytimestamp%
EXIT /B 0
