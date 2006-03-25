package org.rubypeople.rdt.internal.ui.browsing;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.part.ViewPart;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyModel;
import org.rubypeople.rdt.internal.core.RubyModelManager;

public class SimpleView extends ViewPart {
	
	public SimpleView() {
		super();
	}

	public void createPartControl(Composite parent) {
		Group methodsGroup = new Group(parent, 8);
		methodsGroup.setText("Methods");
		methodsGroup.setLayout(new FillLayout());

		List methodsList = new List(methodsGroup, 0);
		methodsList.add("Methods");

		Group typesGroup = new Group(parent, 8);
		typesGroup.setText("Types");
		typesGroup.setLayout(new FillLayout());

		List typeList = new List(typesGroup, 0);
		typeList.add("Types");
		typeList.addSelectionListener(new TypeListener(typeList, methodsList));		

		java.util.List projects = new ArrayList();
		try {
			RubyModel model = RubyModelManager.getRubyModelManager()
					.getRubyModel();
			projects = model.getChildrenOfType(IRubyElement.RUBY_PROJECT);
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Group projectsGroup = new Group(parent, 8);
		projectsGroup.setText("Projects");
		projectsGroup.setLayout(new FillLayout());

		List projectList = new List(projectsGroup, 0);
		for (Iterator iter = projects.iterator(); iter.hasNext();) {
			IRubyElement project = (IRubyElement) iter.next();
			projectList.add(project.getElementName());
		}

		projectList.addSelectionListener(new ProjectListener(projectList,
				typeList));
	}

	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private static class ProjectListener implements SelectionListener {

		private List projectList;
		private List typeList;

		private ProjectListener(List project, List types) {
			this.projectList = project;
			this.typeList = types;
		}

		public void widgetSelected(SelectionEvent e) {
			String[] selectedProjectNames = projectList.getSelection();
			if (selectedProjectNames == null
					|| selectedProjectNames.length == 0)
				return;
			typeList.removeAll();
			for (int i = 0; i < selectedProjectNames.length; i++) {
				IRubyModel model = RubyModelManager.getRubyModelManager()
						.getRubyModel();
				IRubyProject rubyProject = model
						.getRubyProject(selectedProjectNames[i]);
				java.util.List types = getTypes(rubyProject);
				for (Iterator iter = types.iterator(); iter.hasNext();) {
					typeList.add(((IType) iter.next()).getElementName());
				}

			}
		}

		private java.util.List getTypes(IRubyElement element) {
			java.util.List types = new ArrayList();
			if (element.isType(IRubyElement.TYPE))
				types.add(element);
			if (element instanceof IParent) {
				IParent parent = (IParent) element;
				try {
					IRubyElement[] children = parent.getChildren();
					for (int i = 0; i < children.length; i++) {
						types.addAll(getTypes(children[i]));
					}
				} catch (RubyModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return types;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub

		}

	}
	
	private static class TypeListener implements SelectionListener {

		private List methodList;
		private List typeList;

		private TypeListener(List types, List methods) {
			this.methodList = methods;
			this.typeList = types;
		}

		public void widgetSelected(SelectionEvent e) {
			String[] selectedTypes = typeList.getSelection();
			if (selectedTypes == null
					|| selectedTypes.length == 0)
				return;
			methodList.removeAll();
			for (int i = 0; i < selectedTypes.length; i++) {
				IRubyModel model = RubyModelManager.getRubyModelManager()
						.getRubyModel();
				IRubyProject rubyProject = model
						.getRubyProject(selectedTypes[i]);
				// TODO Get all the methods for the seleted Types and add them to the
				// method list
				java.util.List methods = getMethods(rubyProject);
				for (Iterator iter = methods.iterator(); iter.hasNext();) {
					methodList.add(((IMethod) iter.next()).getElementName());
				}

			}
		}

		private java.util.List getMethods(IRubyElement element) {
			java.util.List methods = new ArrayList();
			if (element.isType(IRubyElement.METHOD))
				methods.add(element);
			if (element instanceof IParent) {
				IParent parent = (IParent) element;
				try {
					IRubyElement[] children = parent.getChildren();
					for (int i = 0; i < children.length; i++) {
						methods.addAll(getMethods(children[i]));
					}
				} catch (RubyModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return methods;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub

		}

	}

}
