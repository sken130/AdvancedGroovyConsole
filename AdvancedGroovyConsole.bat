REM cls

set OLD_JAVA_HOME=%JAVA_HOME%
REM set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_172
set JAVA_HOME=C:\Program Files\AdoptOpenJDK\jdk-11.0.10.9-hotspot
REM set JAVA_HOME=C:\Program Files\AdoptOpenJDK\jdk-15.0.2.7-hotspot
call SET PATH=%%PATH:%OLD_JAVA_HOME%=%JAVA_HOME%%%
path|find /i "%JAVA_HOME%\bin"    >nul || set path=%path%;%JAVA_HOME%\bin

set OLD_GROOVY_HOME=%GROOVY_HOME%
REM set GROOVY_HOME=C:\groovy\groovy-2.4.15
REM set GROOVY_HOME=C:\groovy\groovy-2.5.14
set GROOVY_HOME=C:\groovy\groovy-3.0.7
call SET PATH=%%PATH:%OLD_GROOVY_HOME%=%GROOVY_HOME%%%
path|find /i "%GROOVY_HOME%\bin"    >nul || set path=%path%;%GROOVY_HOME%\bin

REM set JAVA_OPTS=

REM groovy src/com/kenlam/groovyconsole/AdvancedGroovyConsole.groovy

gradlew run
