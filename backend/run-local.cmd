@echo off
setlocal

echo ========================================================
echo WorkForceHub Application Starter (LOCAL IN-MEMORY DB)
echo ========================================================

set "JAVA_HOME=%~dp0.jdk\jdk-21.0.3+9"
set "PATH=%JAVA_HOME%\bin;%PATH%"

call mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"

endlocal
