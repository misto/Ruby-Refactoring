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
 * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
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

package org.rubypeople.rdt.refactoring.util;

import org.rubypeople.rdt.refactoring.RefactoringPlugin;


public class HsrFormatter
{

	public static String format(String document, String replaceText, int replaceStartOffset, int replaceEndOffset)
	{
		StringBuilder docStringBuilder = new StringBuilder(document);
		docStringBuilder.delete(replaceStartOffset, replaceEndOffset);
		return format(docStringBuilder.toString(), replaceText, replaceStartOffset);
	}
	
	public static String format(String document, String insertText, int insertOffset)
	{
		if(insertText.length() == 0)
			return insertText;
		if(document.length() == 0)
			return RefactoringPlugin.getRefactoringObjectFactory().getFormatter().formatString(insertText);
		StringBuilder docStringBuilder = new StringBuilder(document);
		insertText = Constants.NL + insertText + Constants.NL;
		if(insertOffset == document.length()) {
			docStringBuilder.append(insertText);
		}
		else {
			docStringBuilder.insert(insertOffset, insertText);
		}
		int end = insertOffset + insertText.length() - 1;
		insertOffset += Character.toString(Constants.NL).length();
		int linesToSort = getLineCount(docStringBuilder.toString(), insertOffset, end);
		int lnNr = getLnNr(docStringBuilder.toString(), insertOffset - 1);
		document = docStringBuilder.toString();
		String formattedDocument = RefactoringPlugin.getRefactoringObjectFactory().getFormatter().formatString(document);
		String text = getSubstring(formattedDocument, lnNr, linesToSort);
		return text;
	}


	private static String getSubstring(String str, int lnNr, int lnCount)
	{

		int start = -1;
		int stop = 0;
		for (int i = 0; i < lnNr; i++)
		{
			start = str.indexOf(Constants.NL, start + 1);
		}
		start++;
		stop = start - 2;
		for (int i = 0; i < lnCount; i++)
		{
			int tmpStop = str.indexOf(Constants.NL, stop + 1);
			if(tmpStop != -1) {
				stop = tmpStop;
			}
			else {
				 break;
			}
		}
		str = str.substring(start, stop);
		return str;
	}

	private static int getLineCount(String text, int offset, int end)
	{
		int count = 0;
		int pos = offset - 1;
		while ((pos = text.indexOf(Constants.NL, pos + 1)) <= end && pos != -1)
		{
			count++;
		}
		return ++count;
	}

	private static int getLnNr(String text, int offset)
	{
		return getLineCount(text, 0, offset) - 1;
	}
}
