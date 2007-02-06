package org.rubypeople.rdt.refactoring.core.rename;

import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;

public class RenameConfig {

	private IDocumentProvider documentProvider;
	private int offset;

	public RenameConfig(IDocumentProvider documentProvider, int offset) {
		this.documentProvider = documentProvider;
		this.offset = offset;
	}

	public IDocumentProvider getDocumentProvider() {
		return documentProvider;
	}

	public int getOffset() {
		return offset;
	}

}
