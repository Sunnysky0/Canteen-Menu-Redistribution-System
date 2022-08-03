@echo off

echo Building binaries
.\gradlew ServerCore:jar

echo Server Starting
C:\Users\clbla\.jdks\corretto-11.0.14.1\bin\java.exe -classpath ".\ServerCore\build\libs\ServerCore-1.0-SNAPSHOT.jar;.\ExternalCommandManager\build\libs\ExternalCommandManager-1.0-SNAPSHOT.jar;.\MenuDesigner\build\libs\MenuDesigner-1.0-SNAPSHOT" server.ServerBase

echo Server Terminated