package org.rubypeople.rdt.core;

public interface ICodeAssist {

	public IRubyElement[] codeSelect(int offset, int length)
			throws RubyModelException;

	public IRubyElement[] codeSelect(int offset, int length,
			WorkingCopyOwner workingCopyOwner) throws RubyModelException;

}
