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


;--------------------------------
;Installer Sections

Section "Install" InstallSection

  SetOutPath "$INSTDIR"
  
  ;copy root
  File "ace.exe"
  File "HelloWorld.jar"
  
  ;copy libs
  CreateDirectory "$INSTDIR\lib"
  SetOutPath "$INSTDIR\lib"
  File "lib\HelloWorldSplash.jar"
  


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
