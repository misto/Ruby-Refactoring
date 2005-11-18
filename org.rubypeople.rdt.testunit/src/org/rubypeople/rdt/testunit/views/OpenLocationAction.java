package org.rubypeople.rdt.testunit.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.rubypeople.rdt.internal.core.symbols.Location;
import org.rubypeople.rdt.internal.ui.util.PositionBasedEditorOpener;


public class OpenLocationAction extends Action implements IAction {

    private final Location location;

    public OpenLocationAction(Location location) {
        super(location.getFilename());
        this.location = location;
    }

    public void run() {
        new PositionBasedEditorOpener(location.getFilename(), location.getPosition()).open();
    }
    

}
