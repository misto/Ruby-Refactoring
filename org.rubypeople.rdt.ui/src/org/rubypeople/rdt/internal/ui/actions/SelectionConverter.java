package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.ui.IEditorInput;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.ui.IWorkingCopyManager;

public class SelectionConverter {

    public static IRubyScript getInputAsRubyScript(RubyEditor editor) {
        Object editorInput = SelectionConverter.getInput(editor);
        if (editorInput instanceof IRubyScript)
            return (IRubyScript) editorInput;
        else
            return null;
    }
    
    public static IRubyElement getInput(RubyEditor editor) {
        if (editor == null)
            return null;
        IEditorInput input= editor.getEditorInput();
        IWorkingCopyManager manager= RubyPlugin.getDefault().getWorkingCopyManager();               
        return manager.getWorkingCopy(input);           
    }

}
