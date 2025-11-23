@echo off
REM Run the Library application
echo Running Library application...
echo.

if not exist bin\LibraryDriver.class (
    echo Error: Compiled classes not found. Please run compile.bat first.
    exit /b 1
)

java -cp bin LibraryDriver
