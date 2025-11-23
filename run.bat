@echo off
echo Running Library application...

if not exist bin (
    echo Error: bin directory not found, run compile.bat first
    exit /b 1
)

if not exist bin\LibraryDriver.class (
    echo Error: Compiled classes not found, run compile.bat first
    exit /b 1
)

java -cp bin LibraryDriver