; Java Launcher
;--------------
 
;You want to change the next four lines
Name "ACE - a collaborative editor"
Caption "ACE - a collaborative editor"
Icon "icon_ace.ico"
OutFile "ace.exe"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
;You want to change the next two lines too
!define CLASSPATH "lib\aopalliance-1.0.jar;lib\backport-util-concurrent-1.1_01.jar;lib\xercesImpl-2.6.2.jar;lib\spring-core-1.2.5.jar;lib\spring-beans-1.2.5.jar;lib\spin-1.4.jar;lib\oro-2.0.8.jar;lib\log4j-1.2.12.jar;lib\glazedlists-1.0.0.jar;lib\concurrent-1.3.4.jar;lib\commons-logging-1.0.4.jar;lib\commons-collections-3.1.jar;lib\beepcore-0.9.08.jar;lib\commons-beanutils-1.7.0.jar;lib\commons-io-1.1.jar;lib\commons-pool-1.2.jar;lib\dnssd-107.1.jar;lib\jdom-1.0.jar;lib\looks-1.3.2.jar;lib\spring-aop-1.2.5.jar;lib\spring-context-1.2.5.jar;lib\xml-apis-2.0.2.jar;lib\ace.jar;lib\ace-windows.jar;lib\asm-1.5.3.jar"
!define CLASS "ch.iserver.ace.application.Main"
 
Section ""
  Call GetJRE
  Pop $R0
 
  ; change for your purpose (-jar etc.)
  StrCpy $0 '"$R0" -Dch.iserver.ace.customizer=ch.iserver.ace.application.WindowsCustomizer -classpath "${CLASSPATH}" ${CLASS}'
  
 
  SetOutPath $EXEDIR
  Exec $0
SectionEnd
 
Function GetJRE
;
;  Find JRE (javaw.exe)
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume javaw.exe in current dir or PATH
 
  Push $R0
  Push $R1
 
  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\javaw.exe"
  IfFileExists $R0 JreFound
  StrCpy $R0 ""
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfErrors 0 JreFound
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe"
 
  IfErrors 0 JreFound
  StrCpy $R0 "javaw.exe"
        
 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd