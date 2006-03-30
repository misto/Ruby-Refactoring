/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;

/**
 * <code>RubyElementLabels</code> provides helper methods to render names of
 * Ruby elements.
 * 
 * @since 3.1
 */
public class RubyElementLabels {

    /**
     * Method names contain parameter names. e.g. <code>foo(index)</code>
     */
    public final static long M_PARAMETER_NAMES = 1L << 1;

    /**
     * Method names are fully qualified. e.g. <code>java.util.Vector.size</code>
     */
    public final static long M_FULLY_QUALIFIED = 1L << 7;

    /**
     * Method names are post qualified. e.g.
     * <code>size - java.util.Vector</code>
     */
    public final static long M_POST_QUALIFIED = 1L << 8;

    /**
     * Initializer names are fully qualified. e.g.
     * <code>java.util.Vector.{ ... }</code>
     */
    public final static long I_FULLY_QUALIFIED = 1L << 10;

    /**
     * Type names are post qualified. e.g. <code>{ ... } - java.util.Map</code>
     */
    public final static long I_POST_QUALIFIED = 1L << 11;

    /**
     * Fields names are fully qualified. e.g. <code>java.lang.System.out</code>
     */
    public final static long F_FULLY_QUALIFIED = 1L << 16;

    /**
     * Fields names are post qualified. e.g. <code>out - java.lang.System</code>
     */
    public final static long F_POST_QUALIFIED = 1L << 17;

    /**
     * Type names are fully qualified. e.g. <code>java.util.Map.MapEntry</code>
     */
    public final static long T_FULLY_QUALIFIED = 1L << 18;

    /**
     * Type names are type container qualified. e.g. <code>Map.MapEntry</code>
     */
    public final static long T_CONTAINER_QUALIFIED = 1L << 19;

    /**
     * Type names are post qualified. e.g. <code>MapEntry - java.util.Map</code>
     */
    public final static long T_POST_QUALIFIED = 1L << 20;

    /**
     * Declarations (import container / declaration, package declaration) are
     * qualified. e.g. <code>java.util.Vector.class/import container</code>
     */
    public final static long D_QUALIFIED = 1L << 24;

    /**
     * Declarations (import container / declaration, package declaration) are
     * post qualified. e.g.
     * <code>import container - java.util.Vector.class</code>
     */
    public final static long D_POST_QUALIFIED = 1L << 25;

    /**
     * Class file names are fully qualified. e.g.
     * <code>java.util.Vector.class</code>
     */
    public final static long CF_QUALIFIED = 1L << 27;

    /**
     * Class file names are post qualified. e.g.
     * <code>Vector.class - java.util</code>
     */
    public final static long CF_POST_QUALIFIED = 1L << 28;

    /**
     * Compilation unit names are fully qualified. e.g.
     * <code>java.util.Vector.java</code>
     */
    public final static long CU_QUALIFIED = 1L << 31;

    /**
     * Compilation unit names are post qualified. e.g.
     * <code>Vector.java - java.util</code>
     */
    public final static long CU_POST_QUALIFIED = 1L << 32;

    /**
     * Package names are qualified. e.g. <code>MyProject/src/java.util</code>
     */
    public final static long P_QUALIFIED = 1L << 35;

    /**
     * Package names are post qualified. e.g.
     * <code>java.util - MyProject/src</code>
     */
    public final static long P_POST_QUALIFIED = 1L << 36;

    /**
     * Package names are compressed. e.g. <code>o*.e*.search</code>
     */
    public final static long P_COMPRESSED = 1L << 37;

    /**
     * Package Fragment Roots contain variable name if from a variable. e.g.
     * <code>JRE_LIB - c:\java\lib\rt.jar</code>
     */
    public final static long ROOT_VARIABLE = 1L << 40;

    /**
     * Package Fragment Roots contain the project name if not an archive
     * (prepended). e.g. <code>MyProject/src</code>
     */
    public final static long ROOT_QUALIFIED = 1L << 41;

    /**
     * Package Fragment Roots contain the project name if not an archive
     * (appended). e.g. <code>src - MyProject</code>
     */
    public final static long ROOT_POST_QUALIFIED = 1L << 42;

    /**
     * Add root path to all elements except Package Fragment Roots and Ruby
     * projects. e.g. <code>java.lang.Vector - c:\java\lib\rt.jar</code>
     * Option only applies to getElementLabel
     */
    public final static long APPEND_ROOT_PATH = 1L << 43;

    /**
     * Add root path to all elements except Package Fragment Roots and Ruby
     * projects. e.g. <code>java.lang.Vector - c:\java\lib\rt.jar</code>
     * Option only applies to getElementLabel
     */
    public final static long PREPEND_ROOT_PATH = 1L << 44;

    /**
     * Post qualify referenced package fragment roots. For example
     * <code>jdt.jar - org.eclipse.jdt.ui</code> if the jar is referenced from
     * another project.
     */
    public final static long REFERENCED_ROOT_POST_QUALIFIED = 1L << 45;

    /**
     * Specified to use the resolved information of a IType, IMethod or IField.
     * See {@link IType#isResolved()}. If resolved information is available,
     * types will be rendered with type parameters of the instantiated type.
     * Resolved method render with the parameter types of the method instance.
     * <code>Vector<String>.get(String)</code>
     */
    public final static long USE_RESOLVED = 1L << 48;

    /**
     * Qualify all elements
     */
    public final static long ALL_FULLY_QUALIFIED = new Long(F_FULLY_QUALIFIED | M_FULLY_QUALIFIED
            | I_FULLY_QUALIFIED | T_FULLY_QUALIFIED | D_QUALIFIED | CF_QUALIFIED | CU_QUALIFIED
            | P_QUALIFIED | ROOT_QUALIFIED).longValue();

    /**
     * Post qualify all elements
     */
    public final static long ALL_POST_QUALIFIED = new Long(F_POST_QUALIFIED | M_POST_QUALIFIED
            | I_POST_QUALIFIED | T_POST_QUALIFIED | D_POST_QUALIFIED | CF_POST_QUALIFIED
            | CU_POST_QUALIFIED | P_POST_QUALIFIED | ROOT_POST_QUALIFIED).longValue();

    /**
     * Default options (M_PARAMETER_NAMES enabled)
     */
    public final static long ALL_DEFAULT = new Long(M_PARAMETER_NAMES).longValue();

    /**
     * Default qualify options (All except Root and Package)
     */
    public final static long DEFAULT_QUALIFIED = new Long(F_FULLY_QUALIFIED | M_FULLY_QUALIFIED
            | I_FULLY_QUALIFIED | T_FULLY_QUALIFIED | D_QUALIFIED | CF_QUALIFIED | CU_QUALIFIED)
            .longValue();

    /**
     * Default post qualify options (All except Root and Package)
     */
    public final static long DEFAULT_POST_QUALIFIED = new Long(F_POST_QUALIFIED | M_POST_QUALIFIED
            | I_POST_QUALIFIED | T_POST_QUALIFIED | D_POST_QUALIFIED | CF_POST_QUALIFIED
            | CU_POST_QUALIFIED).longValue();

    /**
     * User-readable string for separating post qualified names (e.g. " - ").
     */
    public final static String CONCAT_STRING = RubyUIMessages.RubyElementLabels_concat_string;
    /**
     * User-readable string for separating list items (e.g. ", ").
     */
    public final static String COMMA_STRING = RubyUIMessages.RubyElementLabels_comma_string;
    /**
     * User-readable string for separating the return type (e.g. " : ").
     */
    public final static String DECL_STRING = RubyUIMessages.RubyElementLabels_declseparator_string;
    /**
     * User-readable string for ellipsis ("...").
     */
    public final static String ELLIPSIS_STRING = "..."; //$NON-NLS-1$

    private final static long QUALIFIER_FLAGS = P_COMPRESSED | USE_RESOLVED;


    private RubyElementLabels() {
    }

    private static final boolean getFlag(long flags, long flag) {
        return (flags & flag) != 0;
    }

    /**
     * Returns the label of the given object. The object must be of type
     * {@link IRubyElement} or adapt to {@link IWorkbenchAdapter}. The empty
     * string is returned if the element type is not known.
     * 
     * @param obj
     *            Object to get the label from.
     * @param flags
     *            The rendering flags
     * @return Returns the label or the empty string if the object type is not
     *         supported.
     */
    public static String getTextLabel(Object obj, long flags) {
        if (obj instanceof IRubyElement) {
            return getElementLabel((IRubyElement) obj, flags);
        } else if (obj instanceof IAdaptable) {
            IWorkbenchAdapter wbadapter = (IWorkbenchAdapter) ((IAdaptable) obj)
                    .getAdapter(IWorkbenchAdapter.class);
            if (wbadapter != null) { return wbadapter.getLabel(obj); }
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Returns the label for a Ruby element with the flags as defined by this
     * class.
     * 
     * @param element
     *            The element to render.
     * @param flags
     *            The rendering flags.
     * @return the label of the Ruby element
     */
    public static String getElementLabel(IRubyElement element, long flags) {
        StringBuffer buf = new StringBuffer(60);
        getElementLabel(element, flags, buf);
        return buf.toString();
    }

    /**
     * Returns the label for a Ruby element with the flags as defined by this
     * class.
     * 
     * @param element
     *            The element to render.
     * @param flags
     *            The rendering flags.
     * @param buf
     *            The buffer to append the resulting label to.
     */
    public static void getElementLabel(IRubyElement element, long flags, StringBuffer buf) {
        int type = element.getElementType();

        switch (type) {
        case IRubyElement.METHOD:
            getMethodLabel((IMethod) element, flags, buf);
            break;
        case IRubyElement.FIELD:
            getFieldLabel((IField) element, flags, buf);
            break;
        case IRubyElement.LOCAL_VARIABLE:
            getLocalVariableLabel((IField) element, flags, buf);
            break;
        case IRubyElement.TYPE:
            getTypeLabel((IType) element, flags, buf);
            break;
        case IRubyElement.SCRIPT:
            getCompilationUnitLabel((IRubyScript) element, flags, buf);
            break;
        case IRubyElement.IMPORT_CONTAINER:
        case IRubyElement.IMPORT_DECLARATION:
            getDeclarationLabel(element, flags, buf);
            break;
        case IRubyElement.RUBY_PROJECT:
        case IRubyElement.RUBY_MODEL:
            buf.append(element.getElementName());
            break;
        default:
            buf.append(element.getElementName());
        }
    }

    /**
     * Appends the label for a method to a {@link StringBuffer}. Considers the
     * M_* flags.
     * 
     * @param method
     *            The element to render.
     * @param flags
     *            The rendering flags. Flags with names starting with 'M_' are
     *            considered.
     * @param buf
     *            The buffer to append the resulting label to.
     */
    public static void getMethodLabel(IMethod method, long flags, StringBuffer buf) {
        try {
            // qualification
            if (getFlag(flags, M_FULLY_QUALIFIED)) {
                if (method.getDeclaringType() != null) {
                getTypeLabel(method.getDeclaringType(), T_FULLY_QUALIFIED
                        | (flags & QUALIFIER_FLAGS), buf);
                buf.append('.');
                }
            }

            buf.append(method.getElementName());

            // parameters
            buf.append('(');
            if (getFlag(flags, M_PARAMETER_NAMES)) {
                String[] types = null;
                int nParams = 0;
                boolean renderVarargs = false;
                String[] names = null;
                if (getFlag(flags, M_PARAMETER_NAMES) && method.exists()) {
                    names = method.getParameterNames();
                    if (types == null) {
                        nParams = names.length;
                    } else {
                        if (nParams != names.length) {
                            // see
                            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=99137
                            // and
                            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=101029
                            // RubyPlugin.logErrorMessage("RubyElementLabels:
                            // Number of param types(" + nParams + ") != number
                            // of names(" + names.length + "): " +
                            // method.getElementName());
                            // //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                            names = null; // no names rendered
                        }
                    }
                }

                for (int i = 0; i < nParams; i++) {
                    if (i > 0) {
                        buf.append(COMMA_STRING);
                    }
                    if (names != null) {
                        buf.append(names[i]);
                    }
                }
            }
            buf.append(')');

            // post qualification
            if (getFlag(flags, M_POST_QUALIFIED)) {
                if (method.getDeclaringType() != null) {
                buf.append(CONCAT_STRING);
                getTypeLabel(method.getDeclaringType(), T_FULLY_QUALIFIED
                        | (flags & QUALIFIER_FLAGS), buf);
                }
            }

        } catch (RubyModelException e) {
            RubyPlugin.log(e); // NotExistsException will not reach this point
        }
    }

    /**
     * Appends the label for a field to a {@link StringBuffer}. Considers the
     * F_* flags.
     * 
     * @param field
     *            The element to render.
     * @param flags
     *            The rendering flags. Flags with names starting with 'F_' are
     *            considered.
     * @param buf
     *            The buffer to append the resulting label to.
     */
    public static void getFieldLabel(IField field, long flags, StringBuffer buf) {
        // qualification
        if (getFlag(flags, F_FULLY_QUALIFIED)) {
            getTypeLabel(field.getDeclaringType(), T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS),
                    buf);
            buf.append('.');
        }
        buf.append(field.getElementName());

        // post qualification
        if (getFlag(flags, F_POST_QUALIFIED)) {
            buf.append(CONCAT_STRING);
            getTypeLabel(field.getDeclaringType(), T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS),
                    buf);
        }
    }

    /**
     * Appends the label for a local variable to a {@link StringBuffer}.
     * 
     * @param localVariable
     *            The element to render.
     * @param flags
     *            The rendering flags. Flags with names starting with 'F_' are
     *            considered.
     * @param buf
     *            The buffer to append the resulting label to.
     */
    public static void getLocalVariableLabel(IField localVariable, long flags, StringBuffer buf) {

        if (getFlag(flags, F_FULLY_QUALIFIED)) {
            getElementLabel(localVariable.getParent(), M_FULLY_QUALIFIED | T_FULLY_QUALIFIED
                    | (flags & QUALIFIER_FLAGS), buf);
            buf.append('.');
        }

        buf.append(localVariable.getElementName());

        // post qualification
        if (getFlag(flags, F_POST_QUALIFIED)) {
            buf.append(CONCAT_STRING);
            getElementLabel(localVariable.getParent(), M_FULLY_QUALIFIED | T_FULLY_QUALIFIED
                    | (flags & QUALIFIER_FLAGS), buf);
        }
    }

    /**
     * Appends the label for a type to a {@link StringBuffer}. Considers the
     * T_* flags.
     * 
     * @param type
     *            The element to render.
     * @param flags
     *            The rendering flags. Flags with names starting with 'T_' are
     *            considered.
     * @param buf
     *            The buffer to append the resulting label to.
     */
    public static void getTypeLabel(IType type, long flags, StringBuffer buf) {
        if (getFlag(flags, T_FULLY_QUALIFIED)) {
//            IPackageFragment pack = type.getPackageFragment();
//            if (!pack.isDefaultPackage()) {
//                getPackageFragmentLabel(pack, (flags & QUALIFIER_FLAGS), buf);
//                buf.append('.');
//            }
        }
        if (getFlag(flags, T_FULLY_QUALIFIED | T_CONTAINER_QUALIFIED)) {
            IType declaringType = type.getDeclaringType();
            if (declaringType != null) {
                getTypeLabel(declaringType, T_CONTAINER_QUALIFIED | (flags & QUALIFIER_FLAGS), buf);
                buf.append('.');
            }
            int parentType = type.getParent().getElementType();
            if (parentType == IRubyElement.METHOD || parentType == IRubyElement.FIELD) { // anonymous
                                                                    // or local
                getElementLabel(type.getParent(), 0, buf);
                buf.append('.');
            }
        }

        String typeName = type.getElementName();
        if (typeName.length() == 0) { // anonymous
            try {
                
                    String supertypeName = type.getSuperclassName();
//                    String[] superInterfaceNames = type.getSuperInterfaceNames();
//                    if (superInterfaceNames.length > 0) {
//                        supertypeName = Signature.getSimpleName(superInterfaceNames[0]);
//                    } else {
//                        supertypeName = Signature.getSimpleName(type.getSuperclassName());
//                    }
                    typeName = Messages.format(RubyUIMessages.RubyElementLabels_anonym_type,
                            supertypeName);
                
            } catch (RubyModelException e) {
                // ignore
                typeName = RubyUIMessages.RubyElementLabels_anonym;
            }
        }
        buf.append(typeName);

        // post qualification
        if (getFlag(flags, T_POST_QUALIFIED)) {
            buf.append(CONCAT_STRING);
            IType declaringType = type.getDeclaringType();
            if (declaringType != null) {
                getTypeLabel(declaringType, T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS), buf);
                int parentType = type.getParent().getElementType();
                if (parentType == IRubyElement.METHOD || parentType == IRubyElement.FIELD) { // anonymous
                                                                        // or
                                                                        // local
                    buf.append('.');
                    getElementLabel(type.getParent(), 0, buf);
                }
            } else {
                //getPackageFragmentLabel(type.getPackageFragment(), flags & QUALIFIER_FLAGS, buf);
            }
        }
    }

    /**
     * Appends the label for a import container, import or package declaration
     * to a {@link StringBuffer}. Considers the D_* flags.
     * 
     * @param declaration
     *            The element to render.
     * @param flags
     *            The rendering flags. Flags with names starting with 'D_' are
     *            considered.
     * @param buf
     *            The buffer to append the resulting label to.
     */
    public static void getDeclarationLabel(IRubyElement declaration, long flags, StringBuffer buf) {
        if (getFlag(flags, D_QUALIFIED)) {
            IRubyElement openable = (IRubyElement) declaration.getOpenable();
            if (openable != null) {
                buf.append(getElementLabel(openable, CF_QUALIFIED | CU_QUALIFIED
                        | (flags & QUALIFIER_FLAGS)));
                buf.append('/');
            }
        }
        if (declaration.getElementType() == IRubyElement.IMPORT_CONTAINER) {
            buf.append(RubyUIMessages.RubyElementLabels_import_container);
        } else {
            buf.append(declaration.getElementName());
        }
        // post qualification
        if (getFlag(flags, D_POST_QUALIFIED)) {
            IRubyElement openable = (IRubyElement) declaration.getOpenable();
            if (openable != null) {
                buf.append(CONCAT_STRING);
                buf.append(getElementLabel(openable, CF_QUALIFIED | CU_QUALIFIED
                        | (flags & QUALIFIER_FLAGS)));
            }
        }
    }

    /**
     * Appends the label for a compilation unit to a {@link StringBuffer}.
     * Considers the CU_* flags.
     * 
     * @param cu
     *            The element to render.
     * @param flags
     *            The rendering flags. Flags with names starting with 'CU_' are
     *            considered.
     * @param buf
     *            The buffer to append the resulting label to.
     */
    public static void getCompilationUnitLabel(IRubyScript cu, long flags, StringBuffer buf) {
        if (getFlag(flags, CU_QUALIFIED)) {
//            IPackageFragment pack = (IPackageFragment) cu.getParent();
//            if (!pack.isDefaultPackage()) {
//                getPackageFragmentLabel(pack, (flags & QUALIFIER_FLAGS), buf);
//                buf.append('.');
//            }
        }
        buf.append(cu.getElementName());

        if (getFlag(flags, CU_POST_QUALIFIED)) {
            buf.append(CONCAT_STRING);
           // getPackageFragmentLabel((IPackageFragment) cu.getParent(), flags & QUALIFIER_FLAGS, buf);
        }
    }

}
