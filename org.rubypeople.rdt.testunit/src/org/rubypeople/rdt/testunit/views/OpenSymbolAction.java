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
import org.rubypeople.rdt.internal.core.symbols.ISymbolFinder;
import org.rubypeople.rdt.internal.core.symbols.Location;
import org.rubypeople.rdt.internal.core.symbols.MethodSymbol;
import org.rubypeople.rdt.internal.core.symbols.Symbol;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.dialogs.ElementListSelectionDialog;
import org.rubypeople.rdt.internal.ui.util.PositionBasedEditorOpener;

public class OpenSymbolAction extends Action implements IAction {

    private final Symbol symbol;
    private final ISymbolFinder finder;
    private final Shell shell;
    private String dialogTitle;

    public static IAction forClass(String className, ISymbolFinder finder, Shell shell) {
        return new OpenSymbolAction(new ClassSymbol(className), finder, shell, "Open Class");
    }
    
    public static IAction forMethod(String className, String testMethod, 
            ISymbolFinder finder, Shell shell) {
        return new OpenSymbolAction(new MethodSymbol(className, testMethod), finder, shell, "Open Method");
    }
    
    public OpenSymbolAction(Symbol symbol, ISymbolFinder finder, Shell shell, String title) {
        super(TestUnitMessages.getString("OpenEditor.action.label"));
        this.shell = shell;
        this.symbol = symbol;
        this.finder = finder;
        this.dialogTitle = title;
    }
    

    public void run() {
        Set locations = finder.find(symbol);
        if (locations.size() == 0)
            return;
        
        Location location;
        if (locations.size() == 1) {
            location = (Location) locations.iterator().next();
        } else {
            ElementListSelectionDialog selectionDialog 
                = new ElementListSelectionDialog(shell, new LocationLabel());
            selectionDialog.setElements(locations.toArray());
            selectionDialog.setMessage("Select a location");
            selectionDialog.setTitle(dialogTitle);
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
            return sourceFile.getName() + ":" + location.getPosition().getStartLine() + " - " + sourceFile.getFullPath().removeLastSegments(1);
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }
    }


}
