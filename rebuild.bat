@echo off
setlocal

rem ============================================================
rem  FishBook-X build script
rem
rem  NOTE: Keep this file ASCII-only. cmd.exe reads .bat using the
rem  OEM code page (936/GBK on zh-CN). Non-ASCII bytes in comments or
rem  paths break parsing badly and silently.
rem
rem  The project directory is derived from %~dp0 so that no non-ASCII
rem  path ever has to be hardcoded here.
rem ============================================================

rem === Project directory (derived from script location, no trailing backslash) ===
for %%I in ("%~dp0.") do set "PROJECT=%%~fI"

rem === IntelliJ IDEA location (edit this one line for your machine) ===
set "IDEA_HOME=D:\Develop\software\JetBrains\IntelliJ IDEA 2025.3.4"
set "IDEA_LIB=%IDEA_HOME%\lib"
set "JBR_BIN=%IDEA_HOME%\jbr\bin"

rem === Classpath: IDEA platform jars + third-party deps ===
set "CP=%IDEA_LIB%\app.jar;%IDEA_LIB%\util-8.jar;%IDEA_LIB%\util.jar;%IDEA_LIB%\forms_rt.jar;%IDEA_LIB%\annotations.jar;%IDEA_LIB%\lib.jar;%IDEA_LIB%\util_rt.jar;%IDEA_LIB%\jps-model.jar;%IDEA_LIB%\platform-loader.jar;%IDEA_LIB%\idea_rt.jar"

rem === Toolchain: javac from IDEA's bundled JBR 21 ===
set "JAVAC=%JBR_BIN%\javac.exe"
if not exist "%JAVAC%" (
    echo *** javac not found: %JAVAC%
    echo *** Check IDEA_HOME above.
    exit /b 1
)

rem === jar.exe: IDEA's JBR is a slim runtime and does NOT ship jar.exe ===
set "JAR="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\jar.exe" set "JAR=%JAVA_HOME%\bin\jar.exe"
if not defined JAR for %%J in (jar.exe) do if not "%%~$PATH:J"=="" set "JAR=%%~$PATH:J"
if not defined JAR if exist "D:\language\Java\jdk1.8.0_202\bin\jar.exe" set "JAR=D:\language\Java\jdk1.8.0_202\bin\jar.exe"
if not defined JAR (
    echo *** jar.exe not found. Tried: %%JAVA_HOME%%\bin, PATH, D:\language\Java\jdk1.8.0_202\bin
    echo *** Install a full JDK or set JAVA_HOME.
    exit /b 1
)
echo Using jar: %JAR%
echo Project  : %PROJECT%
echo.

set "VER=2.0.2"

cd /d "%PROJECT%"

rem === 1/5. Clean previous output ===
echo [1/5] Cleaning build dir...
if exist build\classes rmdir /s /q build\classes
if exist build\FishBook-X-%VER%.jar del /f /q build\FishBook-X-%VER%.jar
mkdir build\classes

rem === 2/5. Copy resources to build\classes (jar root) ===
echo [2/5] Copying resources...
xcopy /Y /E /I /Q resources\* build\classes\ >nul
if errorlevel 1 (
    echo *** COPY FAILED ***
    exit /b 1
)
echo Resources copied.

rem === 3/5. Compile Java sources ===
echo [3/5] Compiling Java sources...
"%JAVAC%" -encoding UTF-8 -d "build\classes" -cp "%CP%" ^
    src\icu\jogeen\fishbook\factory\ReadWindowFactory.java ^
    src\icu\jogeen\fishbook\factory\SettingConfigFactory.java ^
    src\icu\jogeen\fishbook\service\BookScanner.java ^
    src\icu\jogeen\fishbook\service\BookScannerBuilder.java ^
    src\icu\jogeen\fishbook\service\Chapter.java ^
    src\icu\jogeen\fishbook\service\PersistentState.java ^
    src\icu\jogeen\fishbook\service\TxtBookScanner.java ^
    src\icu\jogeen\fishbook\ui\ReadUI.java ^
    src\icu\jogeen\fishbook\ui\SettingUI.java

if errorlevel 1 (
    echo.
    echo *** COMPILE FAILED ***
    exit /b 1
)
echo Compile successful.

rem === 4/5. Package the plugin jar ===
echo [4/5] Packaging jar...
pushd build\classes
"%JAR%" cf ..\FishBook-X-%VER%.jar META-INF icon.png icon_150.png icu
set "PACKRC=%errorlevel%"
popd

if not "%PACKRC%"=="0" (
    echo.
    echo *** JAR PACKAGING FAILED ***
    exit /b 1
)
echo Jar created.

rem === 5/5. Mirror a copy at the project root for easy access ===
copy /Y build\FishBook-X-%VER%.jar FishBook-X-%VER%.jar >nul

echo.
echo === DONE ===
echo Plugin jar: build\FishBook-X-%VER%.jar
echo Mirror    : FishBook-X-%VER%.jar
echo.
dir build\FishBook-X-%VER%.jar

endlocal
