@echo off
echo Compiling Java files...

if not exist bin mkdir bin

javac -d bin -sourcepath src src\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation success
    echo Class files are in the bin directory
) else (
    echo Compilation failed
    exit /b 1
)