@PUSHD %~dp0
@rem mvnw clean compile package 
@cmd /c mvnw clean install
@POPD
@pause
