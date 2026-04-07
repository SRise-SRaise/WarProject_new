@echo off
setlocal

set "DB_USER=%~1"
set "DB_PASS=%~2"
set "DB_HOST=%~3"
set "DB_PORT=%~4"

if "%DB_USER%"=="" set "DB_USER=root"
if "%DB_HOST%"=="" set "DB_HOST=localhost"
if "%DB_PORT%"=="" set "DB_PORT=3306"
if "%DB_PASS%"=="" (
    set /p "DB_PASS=Please input MySQL password (press Enter for empty): "
)

set "SCRIPT_DIR=%~dp0"

call :run_sql "%SCRIPT_DIR%init-tabel.sql"
if errorlevel 1 exit /b 1

call :run_sql "%SCRIPT_DIR%init_view.sql"
if errorlevel 1 exit /b 1

call :run_sql "%SCRIPT_DIR%init_procedure.sql"
if errorlevel 1 exit /b 1

echo Database initialization completed successfully.
exit /b 0

:run_sql
set "SQL_FILE=%~1"
echo Running %SQL_FILE% ...
if defined DB_PASS set "MYSQL_PWD=%DB_PASS%"
mysql -u"%DB_USER%" -h"%DB_HOST%" -P"%DB_PORT%" < "%SQL_FILE%"
set "MYSQL_PWD="
if errorlevel 1 (
    echo Failed to execute %SQL_FILE%.
    exit /b 1
)
exit /b 0
