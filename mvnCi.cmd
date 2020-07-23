@echo off
PUSHD %~dp0
rem call mvn clean compile package 
call mvn clean install
POPD
pause
