@echo off

REM The eclipse directory from which eclipse will be started for building and testing. 
REM (the eclipse build host)
REM the AntRunner application is started within this eclipse installation
set eclipseDir=D:\eclipse-3.0

REM The version of the PDE Build plug-in is needed to determine the script directory
REM The version will probably be equal to the eclipse version
set pdeBuildPluginVersion=3.0.0

REM The pluginAutomaticBuildDir contains the necessary files provided by the plug-in
REM itself for the build-process, e.g. build.properties, customTargets.xml.
REM It is not bound to reside in the workspace of the eclipse and can reside on any
REM accessible location
set pluginAutomaticBuildDir=D:\java\rdt-workspace-ssh\org.rubypeople.rdt.build\bootstrap

REM Directory where the build takes place. The workspace will also be created in this directory
set buildDirectory=D:\Temp\RdtBuildDir

REM Verbose is a switch for the AntRunner application
set verboseAnt=-verbose
REM set verboseAnt=

REM Variables for configuration of the testing process 
set eclipseAutomatedTestHome=D:\eclipse-testing

REM The Ruby interpreter used from the Tests in org.rubypeople.rdt.debug.core.tests
set rubyInterpreter=D:\Program Files\ruby-1.8.2\bin\ruby.exe
set docbook.root=D:\java\docbook

 
REM default java executable
set vm=java

REM reset list of ant targets in test.xml to execute
REM set tests=
REM default switch to determine if eclipse should be reinstalled between running of tests
set installmode=clean

REM property file to pass to Ant scripts

REM default values for os, ws and arch
set os=win32
set ws=win32
set arch=x86

REM reset ant command line args
set ANT_CMD_LINE_ARGS=



:processcmdlineargs

REM ****************************************************************
REM
REM Process command line arguments
REM
REM ****************************************************************

if x%1==x goto run
if x%1==x-ws set ws=%2 && shift && shift && goto processcmdlineargs
if x%1==x-os set os =%2 && shift && shift && goto processcmdlineargs
if x%1==x-arch set arch=%2 && shift && shift && goto processcmdlineargs
if x%1==x-noclean set installmode=noclean && shift && goto processcmdlineargs
if x%1==x-properties set properties=-propertyfile %2 && shift && shift && goto processcmdlineargs
if x%1==x-vm set vm=%2 && shift && shift && goto processcmdlineargs



:run
REM ***************************************************************************
REM	Run tests by running Ant in Eclipse on the test.xml script
REM ***************************************************************************
set buildfile=%eclipseDir%/plugins/org.eclipse.pde.build_%pdeBuildPluginVersion%/scripts/build.xml
echo Starting eclipse in %eclipseDir%
REM -Dws=%ws% -Dos=%os% -Darch=%arch% wenn gesetzt, dann funktioniert der Build nicht, da ein
REM build target names rootFileswin32_win32_x36 in features/org.rubypeople.rdt/build.xml
REM gesucht wird.
REM wenn ws, os und arch nicht gesetzt werden, dann fehlt in den generierten plugin-build-Dateien
REM der richtige Classpath-Eintrag für die swt-Bibliothek
REM Wenn man keine Fehler erlauben möchte: Wird im generierten File <buildDir>/<pluginDir>/build.xml benutzt
REM -DjavacFailOnError=true 
REM <property name="eclipseAutomatedTestHome" value="D:/eclipse-testing" />
REM Nützliche Switches: -verbose
%vm% -cp %eclipseDir%\startup.jar -Dosgi.ws=%ws% -Dosgi.os=%os% -Dosgi.arch=%arch% org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile %buildfile% -data %buildDirectory%/workspace-build -Dconfigs="*,*,*" -Dbaseos=win32 -Dbasews=win32 -Dbasearch=x86 -Dbuilder=%pluginAutomaticBuildDir%  "-D%installmode%=true" -DjavacFailOnError=true -DbuildDirectory=%buildDirectory% -DbaseLocation=%eclipseDir% %verboseAnt% -DeclipseAutomatedTestHome=%eclipseAutomatedTestHome% -Drdt.rubyInterpreter="%rubyInterpreter%" -Drdt-tests-workspace=%buildDirectory%/workspace-rdt-tests -Ddocbook.root="%docbook.root%"
goto end

:end
