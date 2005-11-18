package org.rubypeople.rdt.testunit.views;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.internal.core.symbols.ClassSymbol;
import org.rubypeople.rdt.internal.core.symbols.Location;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.dialogs.ElementListSelectionDialog;
import org.rubypeople.rdt.internal.ui.util.PositionBasedEditorOpener;


public class OpenClassAction extends Action implements IAction {


    private final String className;
    private final SymbolIndex index;
    private final Shell shell;

    public OpenClassAction(Shell shell, String className, SymbolIndex index) {
        super(TestUnitMessages.getString("OpenEditor.action.label"));
        this.shell = shell;
        this.className = className;
        this.index = index;
    }
    
    public void run() {
        Set locations = index.find(new ClassSymbol(className));
        Location location;
        if (locations.size() == 1) {
            location = (Location) locations.iterator().next();
        } else {
            ElementListSelectionDialog selectionDialog 
                = new ElementListSelectionDialog(shell, new LocationLabel());
            selectionDialog.setElements(locations.toArray());
            selectionDialog.setMessage("Select a location");
            selectionDialog.setTitle("Open Class");
            selectionDialog.open();
            if (selectionDialog.getReturnCode() == SWT.CANCEL)
                return;
            location= (Location) selectionDialog.getResult()[0];
        }
        PositionBasedEditorOpener editorOpener 
                = new PositionBasedEditorOpener(location.getFilename(), 
                        location.getPosition());
        editorOpener.open();
    }
    

    private final class LocationLabel implements ILabelProvider {
        public Image getImage(Object element) {
            return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_PAGE);
        }

        public String getText(Object element) {
            Location location = (Location) element;
            IFile sourceFile = location.getSourceFile();
            return sourceFile.getName() + " - " + sourceFile.getFullPath().removeLastSegments(1);
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            // TODO Auto-generated method stub
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub
            
        }
    }
}
