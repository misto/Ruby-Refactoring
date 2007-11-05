package org.rubypeople.rdt.refactoring.editprovider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileNameChangeProvider {
	
	public Map<String, String> getFilesToRename(Collection<String> collection) {
		return new HashMap<String, String>(); 
	}	
	
	public Map<String, String> getFilesToRename() {
		return getFilesToRename(null); 
	}
}
