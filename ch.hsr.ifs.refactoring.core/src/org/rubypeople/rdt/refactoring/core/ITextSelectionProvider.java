package org.rubypeople.rdt.refactoring.core;

public interface ITextSelectionProvider {

	public abstract SelectionInformation getSelectionInformation();

	public abstract int getCarretPosition();

	public abstract int getStartOffset();

	public abstract int getEndOffset();

	public abstract String getActiveDocument();

}