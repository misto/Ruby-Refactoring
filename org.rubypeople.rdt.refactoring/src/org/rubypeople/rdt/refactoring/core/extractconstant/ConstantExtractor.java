package org.rubypeople.rdt.refactoring.core.extractconstant;

import java.util.ArrayList;
import java.util.Collection;

import org.rubypeople.rdt.refactoring.editprovider.EditProvider;
import org.rubypeople.rdt.refactoring.editprovider.MultiEditProvider;

public class ConstantExtractor extends MultiEditProvider {

	private ExtractConstantConfig config;	

	public ConstantExtractor(ExtractConstantConfig config) {
		this.config = config;
	}

	protected Collection<EditProvider> getEditProviders() {
		Collection<EditProvider> providers = new ArrayList<EditProvider>();

		providers.add(new ExtractedConstantCall(config));
		providers.add(new ExtractedConstantDef(config));

		return providers;
	}

	public EditProvider getDefEdit() {
		return new ExtractedConstantDef(config);
	}

	public void setConstantName(String name) {
		config.setConstantName(name);
	}

	public String getConstantName() {
		return config.getConstantName();
	}
}
