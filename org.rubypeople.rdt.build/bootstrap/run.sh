#!/bin/sh
#REM The eclipse directory from which eclipse will be started for building and testing.
#REM It is run with the AntRunner
eclipseDir=/home/markus/eclipse-3.0-gtk

#REM The version of the PDE Build plug-in is needed to determine the script directory
#REM The version will probably be equal to the eclipse version
pdeBuildPluginVersion=3.0.0

#REM The pluginAutomaticBuildDir contains the necessary files provided by the plug-in
#REM itself for the build-process, e.g. build.properties, customTargets.xml.
#REM It is not bound to reside in the workspace of the eclipse and can reside on any
#REM accessible location
#REM This is also the location of the workspace: a subdirectory workspace will be
# createad in $pluginAutomaticBuildDir
pluginAutomaticBuildDir=/laptop/java/rdt-workspace/org.rubypeople.rdt.build

#REM 
buildTargetDirectory=/tmp/PlugInBuildDir

#REM default java executable
vm=java

#REM reset list of ant targets in test.xml to execute
#REM set tests=
#REM default switch to determine if eclipse should be reinstalled between running of tests
installmode=clean

#REM property file to pass to Ant scripts

#REM default values for os, ws and arch
# Must correspond to the builder-eclipse instance
os=linux
ws=gtk
arch=x86

#REM reset ant command line args
ANT_CMD_LINE_ARGS=



#:processcmdlineargs

#REM ****************************************************************
#REM
#REM Process command line arguments
#REM
#REM ****************************************************************

 
#if x%1==x goto run
#if x%1==x-ws set ws=%2 && shift && shift && goto processcmdlineargs
#if x%1==x-os set os =%2 && shift && shift && goto processcmdlineargs
#if x%1==x-arch set arch=%2 && shift && shift && goto processcmdlineargs
#if x%1==x-noclean set installmode=noclean && shift && goto processcmdlineargs
#if x%1==x-properties set properties=-propertyfile %2 && shift && shift && goto processcmdlineargs
#if x%1==x-vm set vm=%2 && shift && shift && goto processcmdlineargs



#:run
##REM ***************************************************************************
##REM	Run tests by running Ant in Eclipse on the test.xml script
##REM ***************************************************************************
buildfile=$eclipseDir/plugins/org.eclipse.pde.build_$pdeBuildPluginVersion/scripts/build.xml
echo Starting eclipse in $eclipseDir, $vm
$vm -cp $eclipseDir/startup.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile $buildfile -data /tmp/workspace -Dbasews=$ws -Dbaseos=$os -Dbasearch=$arch -Dbuilder=$buildfile  -D$installmode=true -DbuildTargetDirectory=$buildTargetDirectory -verbose  -Dbuilder=$pluginAutomaticBuildDir -DbuildDirectory=/tmp/eclipseBuild -DbaseLocation=$eclipseDir
echo $cmd
`$cmd`

#:end
