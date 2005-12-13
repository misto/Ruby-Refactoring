package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ILoadpathEntry;

public class LoadpathEntry implements ILoadpathEntry {

	private static final String TYPE_PROJECT = "project";

	private String rootID;
	private int entryKind;
	private IPath path;
	/**
	 * Patterns allowing to include/exclude portions of the resource tree
	 * denoted by this entry path.
	 */
	private IPath[] inclusionPatterns;
	private char[][] fullInclusionPatternChars;
	private IPath[] exclusionPatterns;
	private char[][] fullExclusionPatternChars;
	private final static char[][] UNINIT_PATTERNS = new char[][] { "Non-initialized yet".toCharArray()}; //$NON-NLS-1$

	/*
	 * Default inclusion pattern set
	 */
	public final static IPath[] INCLUDE_ALL = {};

	/*
	 * Default exclusion pattern set
	 */
	public final static IPath[] EXCLUDE_NONE = {};

	private IProject project;

	/**
	 * The export flag
	 */
	private boolean isExported;

	public LoadpathEntry(IProject project) {
		this(ILoadpathEntry.CPE_PROJECT, project.getFullPath(), INCLUDE_ALL, EXCLUDE_NONE, true);
		this.project = project;
	}

	public LoadpathEntry(int entryKind, IPath path, IPath[] inclusionPatterns, IPath[] exclusionPatterns, boolean isExported) {
		this.path = path;
		this.entryKind = entryKind;
		this.inclusionPatterns = inclusionPatterns;
		this.exclusionPatterns = exclusionPatterns;

		if (inclusionPatterns != INCLUDE_ALL && inclusionPatterns.length > 0) {
			this.fullInclusionPatternChars = UNINIT_PATTERNS;
		}
		if (exclusionPatterns.length > 0) {
			this.fullExclusionPatternChars = UNINIT_PATTERNS;
		}
		this.isExported = isExported;
	}

	public IPath getPath() {
		return path;
	}

	// FIXME We shouldn't need this!
	public IProject getProject() {
		return this.project;
	}

	public int getEntryKind() {
		return this.entryKind;
	}

	/**
	 * Returns a <code>String</code> for the kind of a class path entry.
	 */
	static String kindToString(int kind) {
		switch (kind) {
		case ILoadpathEntry.CPE_PROJECT:
			return TYPE_PROJECT; //$NON-NLS-1$
		case ILoadpathEntry.CPE_SOURCE:
			return "src"; //$NON-NLS-1$
		case ILoadpathEntry.CPE_LIBRARY:
			return "lib"; //$NON-NLS-1$
		case ILoadpathEntry.CPE_VARIABLE:
			return "var"; //$NON-NLS-1$
		case ILoadpathEntry.CPE_CONTAINER:
			return "con"; //$NON-NLS-1$
		default:
			return "unknown"; //$NON-NLS-1$
		}
	}

	public String toXML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<pathentry type=\"");
		buffer.append(LoadpathEntry.kindToString(entryKind) + "\" ");
		buffer.append("path=\"" + getPath() + "\"/>");
		return buffer.toString();
	}

	/**
	 * Answers an ID which is used to distinguish entries during package
	 * fragment root computations
	 */
	public String rootID() {
		if (this.rootID == null) {
			switch (this.entryKind) {
			case ILoadpathEntry.CPE_LIBRARY:
				this.rootID = "[LIB]" + this.path; //$NON-NLS-1$
				break;
			case ILoadpathEntry.CPE_PROJECT:
				this.rootID = "[PRJ]" + this.path; //$NON-NLS-1$
				break;
			case ILoadpathEntry.CPE_SOURCE:
				this.rootID = "[SRC]" + this.path; //$NON-NLS-1$
				break;
			case ILoadpathEntry.CPE_VARIABLE:
				this.rootID = "[VAR]" + this.path; //$NON-NLS-1$
				break;
			case ILoadpathEntry.CPE_CONTAINER:
				this.rootID = "[CON]" + this.path; //$NON-NLS-1$
				break;
			default:
				this.rootID = ""; //$NON-NLS-1$
				break;
			}
		}
		return this.rootID;
	}

	/*
	 * Returns a char based representation of the exclusions patterns full path.
	 */
	public char[][] fullExclusionPatternChars() {

		if (this.fullExclusionPatternChars == UNINIT_PATTERNS) {
			int length = this.exclusionPatterns.length;
			this.fullExclusionPatternChars = new char[length][];
			IPath prefixPath = this.path.removeTrailingSeparator();
			for (int i = 0; i < length; i++) {
				this.fullExclusionPatternChars[i] = prefixPath.append(this.exclusionPatterns[i]).toString().toCharArray();
			}
		}
		return this.fullExclusionPatternChars;
	}

	/*
	 * Returns a char based representation of the exclusions patterns full path.
	 */
	public char[][] fullInclusionPatternChars() {

		if (this.fullInclusionPatternChars == UNINIT_PATTERNS) {
			int length = this.inclusionPatterns.length;
			this.fullInclusionPatternChars = new char[length][];
			IPath prefixPath = this.path.removeTrailingSeparator();
			for (int i = 0; i < length; i++) {
				this.fullInclusionPatternChars[i] = prefixPath.append(this.inclusionPatterns[i]).toString().toCharArray();
			}
		}
		return this.fullInclusionPatternChars;
	}

	/**
	 * @see ILoadpathEntry#isExported()
	 */
	public boolean isExported() {
		return this.isExported;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.core.ILoadpathEntry#getExclusionPatterns()
	 */
	public IPath[] getExclusionPatterns() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.core.ILoadpathEntry#getInclusionPatterns()
	 */
	public IPath[] getInclusionPatterns() {
		// TODO Auto-generated method stub
		return null;
	}
}
