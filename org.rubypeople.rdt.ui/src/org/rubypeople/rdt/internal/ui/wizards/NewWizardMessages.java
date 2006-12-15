/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class NewWizardMessages extends NLS {

	private static final String BUNDLE_NAME= "org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages";//$NON-NLS-1$
	
	private NewWizardMessages() {
		// Do not instantiate
	}
	
	public static String NewContainerWizardPage_container_label;
	public static String NewContainerWizardPage_container_button;
	public static String NewContainerWizardPage_ChooseSourceContainerDialog_title;
	public static String NewContainerWizardPage_ChooseSourceContainerDialog_description;
	public static String NewContainerWizardPage_error_EnterContainerName;
	public static String NewContainerWizardPage_error_ProjectClosed;
	public static String NewContainerWizardPage_warning_NotARubyProject;
	public static String NewContainerWizardPage_warning_NotInARubyProject;
	public static String NewContainerWizardPage_error_NotAFolder;
	public static String NewContainerWizardPage_error_ContainerDoesNotExist;
	public static String AbstractOpenWizardAction_createerror_message;
	public static String AbstractOpenWizardAction_createerror_title;
	public static String AbstractOpenWizardAction_noproject_title;
	public static String AbstractOpenWizardAction_noproject_message;
	
	public static String NewTypeWizardPage_typename_label;
	public static String NewTypeWizardPage_superclass_label;
	public static String NewTypeWizardPage_superclass_button;
	public static String NewTypeWizardPage_interfaces_add;
	public static String NewTypeWizardPage_interfaces_remove;
	public static String NewTypeWizardPage_interfaces_class_label;
	public static String NewTypeWizardPage_interfaces_ifc_label;
	public static String NewTypeWizardPage_configure_templates_title;
	public static String NewTypeWizardPage_configure_templates_message;
	public static String NewTypeWizardPage_InterfacesDialog_message;
	public static String NewTypeWizardPage_InterfacesDialog_interface_title;
	public static String NewTypeWizardPage_InterfacesDialog_class_title;
	public static String NewTypeWizardPage_error_EnterTypeName;
	public static String NewTypeWizardPage_operationdesc;
	public static String NewElementWizard_op_error_title;
	public static String NewElementWizard_op_error_message;
	public static String NewElementWizard_typecomment_deprecated_title;
	public static String NewElementWizard_typecomment_deprecated_message;
	public static String NewClassCreationWizard_title;
	public static String NewClassWizardPage_title;
	public static String NewClassWizardPage_description;
	public static String NewClassWizardPage_methods_main;
	public static String NewClassWizardPage_methods_constructors;
	public static String NewClassWizardPage_methods_label;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, NewWizardMessages.class);
	}

}
