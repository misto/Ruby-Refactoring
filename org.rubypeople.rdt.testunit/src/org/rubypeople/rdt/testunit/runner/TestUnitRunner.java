/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_x.x.x/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.testunit.runner;

import org.rubypeople.rdt.internal.launching.InterpreterRunner;
import org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration;
import org.rubypeople.rdt.testunit.launcher.TestUnitRunnerConfiguration;

/**
 * @author Chris
 *  
 */
public class TestUnitRunner extends InterpreterRunner {

	public TestUnitRunner() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.launching.InterpreterRunner#renderLoadPath(org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration)
	 */
	protected String renderLoadPath(InterpreterRunnerConfiguration configuration) {
		StringBuffer buffer = new StringBuffer(super.renderLoadPath(configuration));
		if (configuration instanceof TestUnitRunnerConfiguration) {
			TestUnitRunnerConfiguration testUnitConfig = (TestUnitRunnerConfiguration) configuration;
			buffer.append(" -I " + osDependentPath(testUnitConfig.getAbsoluteTestFileName()));
		}
		return buffer.toString();
	}

}