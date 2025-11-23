@echo off
REM Compile all Java files in the src directory
echo Compiling Java files...

javac -d bin -sourcepath src src\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Class files are in the bin directory.
) else (
    echo Compilation failed!
    exit /b 1
)
