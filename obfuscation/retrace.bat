@echo off
cls

echo Please create a file named stacktrace.txt and paste in the stack trace
pause
set /p version=Please enter the Dragonfly version that produced the stack trace (e.g. 1.1.4.6):

proguard-7.0.0/bin/retrace.bat released/dragonfly-client-%version%.map stacktrace.txt