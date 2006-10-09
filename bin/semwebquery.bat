@echo off
@REM $Id: semwebquery.bat,v 1.2 2006/10/09 15:29:32 tgauss Exp $

set NG4J_ROOT=%0\..\..
set CP=%NG4J_ROOT%\build;%NG4J_ROOT%\ng4j.jar
pushd %NG4J_ROOT%
for %%f in (%NG4J_ROOT%\lib\*.jar) do call :oneStep %%f
popd
goto noMore

:oneStep
set CP=%CP%;%NG4J_ROOT%\%1
exit /B

:noMore
java -cp "%CP%" semweb.query %1 %2 %3 %4 %5 %6 %7 %8 %9
