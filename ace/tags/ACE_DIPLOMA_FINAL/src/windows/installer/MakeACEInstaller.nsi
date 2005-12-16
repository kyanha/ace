;ACE Installer
;Written by Mark Bigler


;--------------------------------
;Include Modern UI
  !include "MUI.nsh"

;--------------------------------
;Define Constants
  !define ACE_PRODUCT_NAME "ACE"
  !define ACE_PRODUCT_DESC "ACE - a collaborative editor"
  !define ACE_URL_LINK "ace.lnk"
  !define ACE_URL_EXEC "ace.exe"
  !define BONJOUR_URL "http://wsidecar.apple.com/cgi-bin/nph-reg3rdpty1.pl/product=05945&platform=osx&method=sa/BonjourSetup.exe"
  !define BONJOUR_TEMP_URL "$TEMP\BonjourSetup.exe"


;--------------------------------
;General
  ;Name and file
  Name "${ACE_PRODUCT_DESC}" "${ACE_PRODUCT_NAME}"
  OutFile "ACEInstaller.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\${ACE_PRODUCT_NAME}"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\${ACE_PRODUCT_NAME}" ""


;--------------------------------
;Interface Settings
  !define MUI_ABORTWARNING
  !define MUI_FINISHPAGE_TEXT "ACE was successfully installed."
  !define MUI_FINISHPAGE_LINK "ACE Homepage"
  !define MUI_FINISHPAGE_LINK_COLOR "000080"
  !define MUI_FINISHPAGE_LINK_LOCATION "http://ace.iserver.ch/"
  !define MUI_FINISHPAGE_RUN
  !define MUI_FINISHPAGE_RUN_TEXT "Launch ACE"
  !define MUI_FINISHPAGE_RUN_FUNCTION "LaunchACE"
  

;--------------------------------
;Pages
  !insertmacro MUI_PAGE_LICENSE "doc_gpl.txt"
  Page custom DetectBonjour
  !insertmacro MUI_PAGE_DIRECTORY
  Page custom CustomPageOptions
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH


;--------------------------------
;Languages
  !insertmacro MUI_LANGUAGE "English"


;--------------------------------
;Reserve Files
  ReserveFile "page_opt.ini"
  !insertmacro MUI_RESERVEFILE_INSTALLOPTIONS


;--------------------------------
;Variables
  Var OPT_DESC_VALUE
  Var OPT_MENU_VALUE
  ;Var OPT_BONJOUR_INSTALLED


;--------------------------------
;Installer Sections

Section "Install" InstallSection

  SetOutPath "$INSTDIR"
  
  ;copy root
  File "ace.exe"
    
  ;copy libs
  CreateDirectory "$INSTDIR\lib"
  SetOutPath "$INSTDIR\lib"
  
  File "lib\ace.jar"
  File "lib\commons-beanutils-1.7.0.jar"
  File "lib\commons-logging-1.0.4.jar"
  File "lib\glazedlists-1.0.0.jar"
  File "lib\log4j-1.2.12.jar"
  File "lib\spring-aop-1.2.6.jar"
  File "lib\spring-core-1.2.6.jar"
  File "lib\ace-windows.jar"
  File "lib\commons-collections-3.1.jar"
  File "lib\concurrent-1.3.4.jar"
  File "lib\jdom-1.0.jar"
  File "lib\looks-1.3.2.jar"
  File "lib\spring-beans-1.2.6.jar"
  File "lib\xercesImpl-2.6.2.jar"
  File "lib\backport-util-concurrent-1.1_01.jar"
  File "lib\commons-io-1.1.jar"
  File "lib\dnssd-107.1.jar"
  File "lib\spin-1.4.jar"
  File "lib\spring-context-1.2.6.jar"
  File "lib\xml-apis-2.0.2.jar"


  ;Store installation folder
  WriteRegStr HKCU "Software\${ACE_PRODUCT_NAME}" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  
  ;Read values from an InstallOptions
  !insertmacro MUI_INSTALLOPTIONS_READ $OPT_DESC_VALUE "page_opt.ini" "Field 2" "State"
  !insertmacro MUI_INSTALLOPTIONS_READ $OPT_MENU_VALUE "page_opt.ini" "Field 3" "State"
  
  ;Create Desktop Icon if check box was checked
  StrCmp $OPT_DESC_VALUE "1" "" +2
    CreateShortCut "$DESKTOP\${ACE_URL_LINK}" "$INSTDIR\${ACE_URL_EXEC}" ""

  ;Create Start Menu Icon if check box was checked
  StrCmp $OPT_MENU_VALUE "1" "" +4
    CreateDirectory "$SMPROGRAMS\${ACE_PRODUCT_NAME}"
    CreateShortCut "$SMPROGRAMS\${ACE_PRODUCT_NAME}\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
    CreateShortCut "$SMPROGRAMS\${ACE_PRODUCT_NAME}\${ACE_URL_LINK}" "$INSTDIR\${ACE_URL_EXEC}" "" "$INSTDIR\${ACE_URL_EXEC}" 0

  ;write uninstall information
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ACE_PRODUCT_NAME}" "DisplayName" "${ACE_PRODUCT_DESC}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ACE_PRODUCT_NAME}" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd





;--------------------------------
;Installer Functions
Function .onInit
  ;Extract InstallOptions INI files
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "page_opt.ini"
FunctionEnd

LangString TEXT_IO_TITLE ${LANG_ENGLISH} "Install Option Page"
LangString TEXT_IO_SUBTITLE ${LANG_ENGLISH} "Choose the options with which you want to install ACE."

Function CustomPageOptions
  !insertmacro MUI_HEADER_TEXT "$(TEXT_IO_TITLE)" "$(TEXT_IO_SUBTITLE)"
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY "page_opt.ini"
FunctionEnd

Function LaunchACE
  ExecShell "" "$INSTDIR\${ACE_URL_EXEC}"
FunctionEnd

Function DetectBonjour
;  ClearErrors
;  EnumRegValue $OPT_BONJOUR_INSTALLED HKLM "SOFTWARE\Apple Computer, Inc.\Bonjour" 0
;  IfErrors 0 +2
;    InetLoad::load /BANNER "asdf" "asdf" "${BONJOUR_URL}" "${BONJOUR_TEMP_URL}"



    ;MessageBox MB_OK|MB_ICONINFORMATION "${ACE_PRODUCT_NAME} uses Bonjour, please download and install it from $\"http://www.apple.com/$\" (see readme file for detailed informations)."
    ;Quit
    ;MessageBox MB_OK "${ACE_PRODUCT_NAME} uses Bonjour, it will now be downloaded and installed. This procedure can take several Minutes."
    ;Call InstallBonjour
FunctionEnd

;Function InstallBonjour
;  nsisdl::download /TIMEOUT=30000 "${BONJOUR_URL}" "${BONJOUR_TEMP_URL}"
;  Pop $R0 ;Get the return value
;    StrCmp $R0 "success" +3
;    MessageBox MB_OK "Bonjour downloa failed. $R0"
;    Quit
;  ExecWait "${BONJOUR_TEMP_URL}"
;  Pop $R0 ;Get the return value
;  MessageBox MB_OK "Bonjour downloa failed. $R0"
;    StrCmp $R0 "success" +3
;    MessageBox MB_OK "Bonjour installation failed. $R0"
;    Quit
;  Delete "${BONJOUR_TEMP_URL}"
;FunctionEnd



;--------------------------------
;Descriptions


;--------------------------------
;Uninstaller Section

Section "Uninstall"
  ;delete files
  RmDir /r "$INSTDIR\*.*"

  ;remove installation directory
  RmDir "$INSTDIR"
  
  ;delete desktop icon
  Delete "$DESKTOP\${ACE_PRODUCT_NAME}.lnk"

  ;delete start menu shortcuts
  Delete "$SMPROGRAMS\${ACE_PRODUCT_NAME}\*.*"
  RmDir  "$SMPROGRAMS\${ACE_PRODUCT_NAME}"  

  DeleteRegKey /ifempty HKCU "Software\${ACE_PRODUCT_NAME}"
  ;DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\${ACE_PRODUCT_NAME}"
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${ACE_PRODUCT_NAME}"  

SectionEnd
