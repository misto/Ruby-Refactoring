package org.rubypeople.rdt.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;

public class RubyModuleSelectionDialog extends RubyTypeSelectionDialog {

    public RubyModuleSelectionDialog(Shell parent) {
        super(parent);
    }

    protected Object[] getElements() {
        Object[] types = super.getElements();
        List classes = new ArrayList();
        for (int i = 0; i < types.length; i++) {
            IType type = (IType) types[i];
            if (type.isModule()) classes.add(types[i]);
        }
        IRubyElement[] elements = new IRubyElement[classes.size()];
        System.arraycopy(classes.toArray(), 0, elements, 0, elements.length);
        return elements;
    }

}
