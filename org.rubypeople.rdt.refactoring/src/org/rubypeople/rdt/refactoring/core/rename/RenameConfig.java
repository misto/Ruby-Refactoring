package org.rubypeople.rdt.refactoring.core.rename;

import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;

public class RenameConfig {

	private DocumentProvider documentProvider;
	private int offset;

	public RenameConfig(DocumentProvider documentProvider, int offset) {
		this.documentProvider = documentProvider;
		this.offset = offset;
	}

	public DocumentProvider getDocumentProvider() {
		return documentProvider;
	}

	public int getOffset() {
		return offset;
	}

}
