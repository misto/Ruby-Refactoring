package org.rubypeople.rdt.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.util.RubyElementVisitor;
import org.rubypeople.rdt.ui.RubyElementLabelProvider;

public class RubyTypeSelectionDialog extends ElementListSelectionDialog {

    public RubyTypeSelectionDialog(Shell parent) {
        super(parent, new RubyElementLabelProvider());
        setElements(getElements());
        setMultipleSelection(false);
    }
    
    /**
     * Meant for subclasses to override what elements to show
     * @return
     */
    protected Object[] getElements() {
        return getAllTypes();
    }

    /**
     * @return
     */
    private IRubyElement[] getAllTypes() {
        List typeList = new ArrayList();
        IProject[] projects = RubyCore.getRubyProjects();
        for (int i = 0; i < projects.length; i++) {
            IRubyElement[] types = findElements(projects[i], IRubyElement.TYPE);
            typeList.addAll(Arrays.asList(types));
        }       
        IRubyElement[] allTypes = new IRubyElement[typeList.size()];
        System.arraycopy(typeList.toArray(), 0, allTypes, 0, allTypes.length);
        return allTypes;
    }

    public static IRubyElement[] findElements(final IFile file, int elementType) {
        IRubyScript script = RubyCore.create(file);
        try {
            script.reconcile();
            IRubyElement[] children = script.getChildren();
            List types = new ArrayList();
            for (int i = 0; i < children.length; i++) {
                if (children[i].isType(elementType)) types.add(children[i]);
            }
            IRubyElement[] array = new IRubyElement[types.size()];
            System.arraycopy(types.toArray(), 0, array, 0, types.size());
            return array;
        } catch (RubyModelException e) {
            RubyPlugin.log(e);
        }
        return new IRubyElement[0];
    }

    /**
     * @param rubyProject
     * @param elementType
     */
    public static IRubyElement[] findElements(IProject rubyProject, int elementType) {
        if (rubyProject == null) { return new IRubyElement[0]; }
        try {
            List allElements = new ArrayList();
            RubyElementVisitor visitor = new RubyElementVisitor();
            rubyProject.accept(visitor);
            Object[] rubyFiles = visitor.getCollectedRubyFiles();
            for (int i = 0; i < rubyFiles.length; i++) {
                IFile rubyFile = (IFile) rubyFiles[i];
                IRubyElement[] fileElements = findElements(rubyFile, elementType);
                for (int j = 0; j < fileElements.length; j++) {
                    allElements.add(fileElements[j]);
                }
            }
            Object[] listArray = allElements.toArray();
            IRubyElement[] array = new IRubyElement[allElements.size()];
            System.arraycopy(listArray, 0, array, 0, listArray.length);
            return array;
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return new RubyElement[0];
    }

}
