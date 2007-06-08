package org.rubypeople.rdt.refactoring.core.renameclass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.refactoring.core.RenameTypeConfig;
import org.rubypeople.rdt.refactoring.editprovider.FileNameChangeProvider;
import org.rubypeople.rdt.refactoring.util.NameHelper;

public class RenameClassFileNameChangeProvider extends FileNameChangeProvider {

	private final RenameTypeConfig config;

	public RenameClassFileNameChangeProvider(RenameTypeConfig renameClassConfig) {
		this.config = renameClassConfig;
	}

	@Override
	public Map<String, String> getFilesToRename(Collection<String> affectedFiles) {
		HashMap<String, String> filesToRename = new HashMap<String, String>();
		for (String file : affectedFiles) {
			if(NameHelper.fileNameEqualsClassName(file, config.getSelectedName())) {
				filesToRename.put(file, NameHelper.fileNameFromClassName(config.getNewName()));
			}
		}
		return filesToRename;
	}
}
