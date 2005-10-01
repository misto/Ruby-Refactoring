/**
 * 
 */
package org.rubypeople.rdt.internal.core.builder;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

interface IFileFinder {
    public List findFiles() throws CoreException;
}