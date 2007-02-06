/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Mirko Stocker <me@misto.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.rubypeople.rdt.refactoring.core.formatsource;

import org.rubypeople.rdt.refactoring.core.RubyRefactoring;
import org.rubypeople.rdt.refactoring.ui.pages.FormatSourcePage;

public class FormatSourceRefactoring extends RubyRefactoring {

	public static final String NAME = "Format Source";
	
	private static final String source = 
		"class Sample\n" +
		  "def testMethod arg1, arg2\n" +
		    "puts \"Hello world!\"\n" +
		    "hash = {:key => 'value'}\n" +
		  "end\n" +	
		  "def anotherTestMethod arg1, arg2\n" +
		    "[1, 2, 3, 4].each {|e| puts e}\n" +
	      "end\n" +
		"end";
	
	private FormattedSourceEditProvider provider;
	
	public FormatSourceRefactoring() {
		super(NAME);

		FormatSourceConfig config = new FormatSourceConfig(getDocumentProvider());
		FormatSourceConditionChecker checker = new FormatSourceConditionChecker(config);
		setRefactoringConditionChecker(checker);
		if(checker.shouldPerform()) {
			provider = new FormattedSourceEditProvider(config);
			setEditProvider(provider);

			EditableFormatHelper formatHelper = new EditableFormatHelper();

			FormatSourcePage page = new FormatSourcePage(formatHelper, new PreviewGeneratorImpl(source));
			pages.add(page);
		}
	}
}