/*
 *(c) Copyright QNX Software Systems Ltd. 2002.
 * All Rights Reserved.
 * 
 */

package org.rubypeople.rdt.internal.debug.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.ui.IRubyConstants;

public class ManageBreakpointRulerActionDelegate extends AbstractRulerActionDelegate
{

	public void setActiveEditor( IAction callerAction, IEditorPart targetEditor )
	{		
		if ( targetEditor != null )
		{
			String id = targetEditor.getSite().getId();
			if ( !(id.equals( IRubyConstants.EDITOR_ID) || id.equals(IRubyConstants.EXTERNAL_FILES_EDITOR_ID))) {
				targetEditor = null;
			}
		}
		super.setActiveEditor( callerAction, targetEditor );
	}

	public IAction createAction( ITextEditor editor, IVerticalRulerInfo rulerInfo )
	{
		return new ManageBreakpointRulerAction( rulerInfo, editor );
	}
}
