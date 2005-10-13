package org.rubypeople.rdt.testunit.views;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.rubypeople.rdt.internal.core.symbols.ClassSymbol;
import org.rubypeople.rdt.internal.core.symbols.Location;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;
import org.rubypeople.rdt.internal.ui.util.PositionBasedEditorOpener;


public class OpenClassAction extends Action implements IAction {

    private final String className;
    private final SymbolIndex index;

    public OpenClassAction(String className, SymbolIndex index) {
        super(TestUnitMessages.getString("OpenEditor.action.label"));
        this.className = className;
        this.index = index;
    }
    
    public void run() {
        Set locations = index.find(new ClassSymbol(className));
        if (locations.size() == 1) {
            Location location = (Location) locations.iterator().next();
            new PositionBasedEditorOpener(location.getFilename(), location.getPosition()).open();
        } else {
            System.out.println("Too many locations"); // DSC temporary
        }
    }
    

}
