package org.rubypeople.rdt.internal.ui.search;


public class Scope {
	private String name ;
	private String qualifiedName ;
	public Scope(String qualifiedName) {
		this.qualifiedName = qualifiedName ;
	}
	
	public String getName() {
		if (name == null) {
			int index = qualifiedName.lastIndexOf("::") ;
			if (index == -1) {
				name = qualifiedName ;
			}
			else {
				name = qualifiedName.substring(index + 2) ;
			}
		}
		return name;
	}

	public boolean equals(Object other) {
		if (!(other instanceof Scope)) {
			return false ;
		}
		return this.qualifiedName.equals(((Scope) other).qualifiedName) ;
	}

	
	public String getQualifiedName() {
		return qualifiedName;
	}

	public String toString() {
		return qualifiedName;
	}

	public int hashCode() {
		return qualifiedName.hashCode();
	}

}
