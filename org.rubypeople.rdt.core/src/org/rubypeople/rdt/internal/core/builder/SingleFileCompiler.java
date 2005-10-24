/*
?* Author: David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/


package org.rubypeople.rdt.internal.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

interface SingleFileCompiler {
    public void compileFile(IFile file) throws CoreException;
}