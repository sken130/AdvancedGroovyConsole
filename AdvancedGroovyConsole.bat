REM cls

set OLD_JAVA_HOME=%JAVA_HOME%
REM set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_172
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.2.13-hotspot
REM set JAVA_HOME=C:\Program Files\AdoptOpenJDK\jdk-15.0.2.7-hotspot
call SET PATH=%%PATH:%OLD_JAVA_HOME%=%JAVA_HOME%%%
path|find /i "%JAVA_HOME%\bin"    >nul || set path=%path%;%JAVA_HOME%\bin

REM set JAVA_OPTS=

gradlew run
