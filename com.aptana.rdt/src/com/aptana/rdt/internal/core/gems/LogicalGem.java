package com.aptana.rdt.internal.core.gems;

import java.util.Collection;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.aptana.rdt.core.gems.Gem;

public class LogicalGem extends Gem {

	private LogicalGem(String name, String version, String description) {
		super(name, version, description);
	}
	
	public static LogicalGem create(Collection<Gem> gems) {
		if (gems == null || gems.isEmpty()) throw new IllegalArgumentException("Need a non-null, non-empty Collection of Gems");
		String name = null;
		String description = null;
		String version = "(";
		for (Gem gem : gems) {
			if (name == null) name = gem.getName();
			if (description == null) description = gem.getDescription();
			version += gem.getVersion() + ", ";
		}
		version = version.substring(0, version.length() - 2);
		version += ')';
		// XXX Need to take platform into account!!!!!!!!
		return new LogicalGem(name, version, description);
	}

	public SortedSet<String> getVersions() {
		String raw = getVersion().substring(1, getVersion().length() - 1);
		SortedSet<String> version = new TreeSet<String>();
		StringTokenizer tokenizer = new StringTokenizer(raw, ",");
		while (tokenizer.hasMoreTokens()) {
			version.add(tokenizer.nextToken().trim());
		}
		return version;
	}

}
