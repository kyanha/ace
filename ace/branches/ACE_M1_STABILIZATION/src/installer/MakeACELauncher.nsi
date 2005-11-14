; Java Launcher
;--------------
 
Name "ACE - a collaborative editor"
Caption "ACE - a collaborative editor"
Icon "icon_ace.ico"
OutFile "ace.exe"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
!define CLASSPATH ".;lib;HelloWorld.jar;lib\HelloWorldSplash.jar"
!define CLASS "ch.iserver.ace.HelloWorld"
 
Section ""
  Call GetJRE
  Pop $R0
 
  ; change for your purpose (-jar etc.)
  StrCpy $0 '"$R0" -classpath "${CLASSPATH}" ${CLASS}'
 
  SetOutPath $EXEDIR
  Exec $0
SectionEnd
 
Function GetJRE
;
;  Find JRE (Java.exe)
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume java.exe in current dir or PATH
 
  Push $R0
  Push $R1
 
  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\java.exe"
  IfFileExists $R0 JreFound
  StrCpy $R0 ""
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\java.exe"
  IfErrors 0 JreFound
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\java.exe"
 
  IfErrors 0 JreFound
  StrCpy $R0 "java.exe"
        
 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd