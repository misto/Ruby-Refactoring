A Ruby refactoring plug-in for Eclipse DLTK and the original Ruby Development Tools.

You can downloaded [our report here](http://misto.ch/res/rubyrefactoring.pdf).

The repository is organized as follows:

* ch.hsr.ifs.refactoring.core - all the refactoring implementations
* ch.hsr.ifs.refactoring.dltk - the DLKT specific hooks and integration
* ch.hsr.ifs.refactoring.help - the help for the plug-in
* ch.hsr.ifs.refactoring.rdt-build - buildsystem using Ant and Pluginbuilder
* ch.hsr.ifs.refactoring.rdt-feature
* ch.hsr.ifs.refactoring.rdt-tests-feature
* ch.hsr.ifs.refactoring.updatesite
* org.rubypeople.rdt.astviewer - a helper view that shows the AST for some source code
* org.rubypeople.rdt.refactoring - the RDT specific hooks and integration
* org.rubypeople.rdt.refactoring.tests - the unit tests for the refactorings

Unfortunately, we had to stop the development quite suddenly, so we couldn't even complete a large renaming and refactoring that was going on (thus the different naming schemes of the plugins).
