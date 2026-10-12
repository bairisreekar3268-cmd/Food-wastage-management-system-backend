@echo off
rem Minimal Maven wrapper: downloads Apache Maven once into %USERPROFILE%\.m2\wrapper\dists, then runs it.
setlocal
set "MVN_VER=3.9.9"
set "MVN_ROOT=%USERPROFILE%\.m2\wrapper\dists"
set "MVN_HOME=%MVN_ROOT%\apache-maven-%MVN_VER%"
if not exist "%MVN_HOME%\bin\mvn.cmd" (
  echo Downloading Apache Maven %MVN_VER% - first run only...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; New-Item -ItemType Directory -Force -Path '%MVN_ROOT%' | Out-Null; Invoke-WebRequest -UseBasicParsing -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MVN_VER%/apache-maven-%MVN_VER%-bin.zip' -OutFile '%TEMP%\apache-maven.zip'; Expand-Archive -Force -Path '%TEMP%\apache-maven.zip' -DestinationPath '%MVN_ROOT%'"
  if errorlevel 1 (
    echo Could not download Maven. Check your internet connection.
    exit /b 1
  )
)
call "%MVN_HOME%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
