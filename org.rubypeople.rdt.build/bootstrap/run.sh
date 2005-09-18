echo using RDT_BUILD_HOME: ${RDT_BUILD_HOME:?must be set}
eclipseDir=${RDT_BUILD_HOME}/eclipse-3.1
pdeBuildPluginVersion=3.1.0
buildDirectory=${RDT_BUILD_TARGET_DIR:-${RDT_BUILD_HOME}/tmp/PlugInBuildDir}
bootstrapDir=${buildDirectory}/org.rubypeople.rdt.build/bootstrap
verboseAnt=${RDT_ANT_VERBOSE:+-verbose}
eclipseAutomatedTestHome=${RDT_BUILD_HOME}/eclipse-testing
rubyInterpreter=${RDT_RUBY_INTERPRETER:-/usr/bin/ruby}
docbookRoot=${RDT_BUILD_HOME}/docbook
vm=${RDT_JAVA_INTERPRETER:-java}

os=${RDT_OS:-linux}
ws=${RDT_WS:-motif}
arch=${RDT_ARCH:-x86}
usePserver=${RDT_USE_PSERVER:+-DusePserver=true}
# the default is to clean up before testing, it can be avoided by defining noclean
testNoclean=${RDT_TEST_NOCLEAN:+-Dnoclean=true}
dontRunTests=${RDT_DONT_RUN_TESTS:+-DdontRunTests=true}

#REM reset ant command line args
ANT_CMD_LINE_ARGS=


buildfile=$eclipseDir/plugins/org.eclipse.pde.build_$pdeBuildPluginVersion/scripts/build.xml
# buildTarget can be one of the build targets defined in $buildfile: 
# main, preBuild, fetch, generate, process, assemble, package, postBuild, clean
buildTarget=${RDT_BUILD_TARGET:+main}

echo Starting eclipse in $eclipseDir, $vm
cmd="$vm -cp $eclipseDir/startup.jar org.eclipse.core.launcher.Main -ws $ws -os $os -application org.eclipse.ant.core.antRunner -buildfile $buildfile $buildTarget -data $buildDirectory/workspace $verboseAnt $usePserver $dontRunTests -Dbasews=$ws -Dbaseos=$os -Dbasearch=$arch -Dbuilder=$bootstrapDir  $testNoclean  -DjavacFailOnError=true -DbuildDirectory=$buildDirectory -DbaseLocation=$eclipseDir  -DeclipseAutomatedTestHome=$eclipseAutomatedTestHome -Drdt.rubyInterpreter="$rubyInterpreter" -Drdt-tests-workspace=$buildDirectory/workspace-rdt-tests -Ddocbook.root=$docbookRoot"
echo $cmd
exec $cmd