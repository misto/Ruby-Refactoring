package com.aptana.rdt.core.gems;

import java.util.Set;

import org.eclipse.core.runtime.IPath;

public interface IGemManager {

	public abstract boolean update(Gem gem);

	public abstract boolean installGem(Gem gem);

	public abstract boolean removeGem(Gem gem);

	public abstract Set<Gem> getGems();

	public abstract boolean refresh();

	public abstract void addGemListener(GemListener listener);

	public abstract Set<Gem> getRemoteGems();

	public abstract boolean gemInstalled(String gemName);

	public abstract void removeGemListener(GemListener listener);

	public abstract IPath getGemInstallPath();

	public abstract IPath getGemPath(String gemName);
	
	public abstract IPath getGemPath(String gemName, String version);

}