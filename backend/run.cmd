@echo off
setlocal

echo ========================================================
echo WorkForceHub Application Starter
echo ========================================================

REM Automatically use the downloaded JDK 21 to avoid Lombok compilation errors on JDK 26
set "JAVA_HOME=%~dp0.jdk\jdk-21.0.3+9"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Using JAVA_HOME: %JAVA_HOME%
echo.

call mvnw.cmd spring-boot:run %*

endlocal
