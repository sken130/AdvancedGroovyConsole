REM cls

REM set OLD_JAVA_HOME=%JAVA_HOME%
REM set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_172
REM set JAVA_HOME=C:\Program Files\AdoptOpenJDK\jdk-15.0.2.7-hotspot
REM call SET PATH=%%PATH:%OLD_JAVA_HOME%=%JAVA_HOME%%%

REM set OLD_GROOVY_HOME=%GROOVY_HOME%
REM set GROOVY_HOME=C:\groovy\groovy-2.4.15
REM set GROOVY_HOME=C:\groovy\groovy-3.0.7
REM call SET PATH=%%PATH:%OLD_GROOVY_HOME%=%GROOVY_HOME%%%

set CLASSPATH=src/;lib/*

groovy src/com/kenlam/groovyconsole/AdvancedGroovyConsole.groovy