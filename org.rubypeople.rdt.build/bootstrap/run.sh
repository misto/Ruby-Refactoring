#!/bin/sh

#set eclipseDir=D:\eclipse-3.0
#set pdeBuildPluginVersion=3.0.0
#set pluginAutomaticBuildDir=D:\java\rdt-workspace-ssh\org.rubypeople.rdt.build\bootstrap
#set buildDirectory=D:\Temp\RdtBuildDir
#set verboseAnt=-verbose
#REM set verboseAnt=
#set eclipseAutomatedTestHome=D:\eclipse-testing
#set rubyInterpreter=D:\Program Files\ruby-1.8.2\bin\ruby.exe
#set docbook.root=D:\java\docbook
#set vm=java

#REM set tests=
#set installmode=clean

#set os=win32
#set ws=win32
#set arch=x86

#set ANT_CMD_LINE_ARGS=



eclipseDir=/home/markus/eclipse-3.0.1
pdeBuildPluginVersion=3.0.1
pluginAutomaticBuildDir=/tmp/org.rubypeople.rdt.build/bootstrap
buildDirectory=/tmp/PlugInBuildDir
verboseAnt=-verbose
eclipseAutomatedTestHome=/home/markus/eclipse-testing
rubyInterpreter=/usr/local/bin/ruby
docbookRoot=/home/markus/docbook
vm=java
installmode=clean
os=linux
ws=gtk
arch=x86

#REM reset ant command line args
ANT_CMD_LINE_ARGS=






#REM %vm% -cp %eclipseDir%\startup.jar -Dosgi.ws=%ws% -Dosgi.os=%os% -Dosgi.arch=%arch% org.eclipse.core.launcher.Main 
#-application org.eclipse.ant.core.antRunner -buildfile %buildfile% 
#-data %buildDirectory%/workspace-build -Dconfigs="*,*,*" -Dbaseos=win32 -Dbasews=win32 -Dbasearch=x86 -Dbuilder=%pluginAutomaticBuildDir%  
#"-D%installmode%=true" -DjavacFailOnError=true -DbuildDirectory=%buildDirectory% -DbaseLocation=%eclipseDir% %verboseAnt% 
#-DeclipseAutomatedTestHome=%eclipseAutomatedTestHome% -Drdt.rubyInterpreter="%rubyInterpreter%" 
#-Drdt-tests-workspace=%buildDirectory%/workspace-rdt-tests -Ddocbook.root="%docbook.root%"

buildfile=$eclipseDir/plugins/org.eclipse.pde.build_$pdeBuildPluginVersion/scripts/build.xml
echo Starting eclipse in $eclipseDir, $vm
cmd="$vm -cp $eclipseDir/startup.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile $buildfile -data $buildDirectory/workspace -Dbasews=$ws -Dbaseos=$os -Dbasearch=$arch -Dbuilder=$pluginAutomaticBuildDir  -D$installmode=true -DjavacFailOnError=true -DbuildDirectory=/tmp/eclipseBuild -DbaseLocation=$eclipseDir $verboseAnt -DeclipseAutomatedTestHome=$eclipseAutomatedTestHome -Drdt.rubyInterpreter="$rubyInterpreter" -Drdt-tests-workspace=$buildDirectory/workspace-rdt-tests -Ddocbook.root=$docbookRoot"
echo $cmd
`$cmd`