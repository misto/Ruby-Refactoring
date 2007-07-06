/**
 * 
 */
package org.rubypeople.rdt.internal.corext.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * @author Chris
 * 
 */
public final class RubyModelUtil {

	public static final String DEFAULT_SCRIPT_SUFFIX = ".rb";
	private static boolean PRIMARY_ONLY = false;

	/**
	 * Returns the original cu if the given cu is a working copy. If the cu is
	 * already an original the input cu is returned. The returned cu might not
	 * exist
	 */
	public static IRubyScript toOriginal(IRubyScript cu) {
		if (PRIMARY_ONLY) {
			testRubyScriptOwner("toOriginal", cu); //$NON-NLS-1$
		}
		// To stay compatible with old version returned null
		// if cu is null
		if (cu == null) return cu;
		return cu.getPrimary();
	}

	private static void testRubyScriptOwner(String methodName, IRubyScript cu) {
		if (cu == null) { return; }
		if (!isPrimary(cu)) {
			RubyPlugin.logErrorMessage(methodName + ": operating with non-primary cu"); //$NON-NLS-1$
		}
	}

	/**
	 * Returns true if a cu is a primary cu (original or shared working copy)
	 */
	public static boolean isPrimary(IRubyScript cu) {
		return cu.getOwner() == null;
	}

    public static void reconcile(IRubyScript unit) throws RubyModelException {    
        unit.reconcile(
                false /* don't force problem detection */, 
                null /* use primary owner */, 
                null /* no progress monitor */);
    }

	/**
	 * Returns the original element if the given element is a working copy. If the cu is already
	 * an original the input element is returned. The returned element might not exist
	 */
	public static IRubyElement toOriginal(IRubyElement element) {
		return element.getPrimaryElement();
	}

	/**
	 * Returns the package fragment root of <code>IRubyElement</code>. If the given
	 * element is already a package fragment root, the element itself is returned.
	 */
	public static ISourceFolderRoot getSourceFolderRoot(IRubyElement element) {
		return (ISourceFolderRoot) element.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
	}

	public static boolean isExcludedPath(IPath resourcePath, IPath[] exclusionPatterns) {
		char[] path = resourcePath.toString().toCharArray();
		for (int i = 0, length = exclusionPatterns.length; i < length; i++) {
			char[] pattern= exclusionPatterns[i].toString().toCharArray();
			if (CharOperation.pathMatch(pattern, path, true, '/')) {
				return true;
			}
		}
		return false;	
	}

	/**
	 * Concatenates two names. Uses a '/' for separation.
	 * Both strings can be empty or <code>null</code>.
	 */
	public static String concatenateName(char[] name1, char[] name2) {
		StringBuffer buf= new StringBuffer();
		if (name1 != null && name1.length > 0) {
			buf.append(name1);
		}
		if (name2 != null && name2.length > 0) {
			if (buf.length() > 0) {
				buf.append("/");
			}
			buf.append(name2);
		}		
		return buf.toString();
	}

	/**
	 * Returns the fully qualified name of the given type using '::' as separators.
	 * This is a replace for IType.getFullyQualifiedTypeName
	 * which uses '$' as separators. As '$' is also a valid character in an id
	 * this is ambiguous.
	 */
	public static String getFullyQualifiedName(IType type) {
		return type.getFullyQualifiedName();
	}

	/** 
	 * Finds a type in a ruby script. Typical usage is to find the corresponding
	 * type in a working copy.
	 * @param script the compilation unit to search in
	 * @param typeQualifiedName the type qualified name (type name with enclosing type names (separated by dots))
	 * @return the type found, or null if not existing
	 */		
	public static IType findTypeInRubyScript(IRubyScript script, String typeQualifiedName) throws RubyModelException {
			IType[] types= script.getAllTypes();
			for (int i= 0; i < types.length; i++) {
				String currName= getTypeQualifiedName(types[i]);
				if (typeQualifiedName.equals(currName)) {
					return types[i];
				}
			}
			return null;
	}
	
	/**
	 * Returns the qualified type name of the given type using '.' as separators.
	 * This is a replace for IType.getTypeQualifiedName()
	 * which uses '$' as separators. As '$' is also a valid character in an id
	 * this is ambiguous. JavaCore PR: 1GCFUNT
	 */
	public static String getTypeQualifiedName(IType type) {
		return type.getTypeQualifiedName("::");
	}

	/*
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=19253
	 * 
	 * Reconciling happens in a separate thread. This can cause a situation where the
	 * Java element gets disposed after an exists test has been done. So we should not
	 * log not present exceptions when they happen in working copies.
	 */
	public static boolean isExceptionToBeLogged(CoreException exception) {
		if (!(exception instanceof RubyModelException))
			return true;
		RubyModelException je= (RubyModelException)exception;
		if (!je.isDoesNotExist())
			return true;
		IRubyElement[] elements= je.getRubyModelStatus().getElements();
		for (int i= 0; i < elements.length; i++) {
			IRubyElement element= elements[i];
			// if the element is already a compilation unit don't log
			// does not exist exceptions. See bug 
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=75894
			// for more details
			if (element.getElementType() == IRubyElement.SCRIPT)
				continue;
			IRubyScript unit= (IRubyScript)element.getAncestor(IRubyElement.SCRIPT);
			if (unit == null)
				return true;
			if (!unit.isWorkingCopy())
				return true;
		}
		return false;		
	}

	public static boolean isSuperType(ITypeHierarchy hierarchy, IType possibleSuperType, IType type) {
		// filed bug 112635 to add this method to ITypeHierarchy
		IType superClass= hierarchy.getSuperclass(type);
		if (superClass != null && (possibleSuperType.equals(superClass) || isSuperType(hierarchy, possibleSuperType, superClass))) {
			return true;
		}
		if (Flags.isModule(hierarchy.getCachedFlags(possibleSuperType))) {
			IType[] superInterfaces= hierarchy.getSuperInterfaces(type);
			for (int i= 0; i < superInterfaces.length; i++) {
				IType curr= superInterfaces[i];
				if (possibleSuperType.equals(curr) || isSuperType(hierarchy, possibleSuperType, curr)) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Evaluates if a member in the focus' element hierarchy is visible from
	 * elements in a package.
	 * @param member The member to test the visibility for
	 * @param pack The package of the focus element focus
	 */
	public static boolean isVisibleInHierarchy(IMember member, ISourceFolder pack) throws RubyModelException {
		if (member.isType(IRubyElement.GLOBAL))
			return true;
		if (!member.isType(IRubyElement.METHOD))
			return false;
		
		IMethod method = (IMethod) member;
		
		IType declaringType= member.getDeclaringType();
		if (method.getVisibility() == IMethod.PUBLIC || method.getVisibility() == IMethod.PROTECTED || (declaringType != null && declaringType.isModule())) {
			return true;
		} else if (method.getVisibility() == IMethod.PRIVATE) {
			return false;
		}		
		
		ISourceFolder otherpack= (ISourceFolder) member.getAncestor(IRubyElement.SOURCE_FOLDER);
		return (pack != null && pack.equals(otherpack));
	}
}
