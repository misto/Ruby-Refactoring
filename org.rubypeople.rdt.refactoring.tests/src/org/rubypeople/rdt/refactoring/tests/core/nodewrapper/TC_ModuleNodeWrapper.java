package org.rubypeople.rdt.refactoring.tests.core.nodewrapper;

import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.ModuleNodeWrapper;
import org.rubypeople.rdt.refactoring.tests.FileTestCase;

public class TC_ModuleNodeWrapper extends FileTestCase {

	public TC_ModuleNodeWrapper() {
		super("Module Node Wrapper");
	}

	private ModuleNodeWrapper findModule(String file, int position) {
		return SelectionNodeProvider.getSelectedModuleNode(getRootNode(file),position);
	}
	
	public void testSimpleModule() {
		ModuleNodeWrapper moduleNode = findModule("TC_ModuleNodeWrapper_SimpleModule.rb", 8);
		
		assertNull(moduleNode.getParentModule());
		assertNotNull(moduleNode.getWrappedNode());
		assertEquals("Modul", moduleNode.getName());
		assertEquals("Modul", moduleNode.getFullName());
	}

	public void testModuleWithParent() {
		ModuleNodeWrapper moduleNode = findModule("TC_ModuleNodeWrapper_ModuleWithParent.rb", 31);
		
		assertNotNull(moduleNode.getParentModule());
		assertNotNull(moduleNode.getParentModule().getWrappedNode());
		assertNull(moduleNode.getParentModule().getParentModule());
		assertNotNull(moduleNode.getWrappedNode());
		assertEquals("Modul", moduleNode.getName());
		assertEquals("OuterModule::Modul", moduleNode.getFullName());
	}
	
	public void testModuleWithMultipleParents() {
		ModuleNodeWrapper moduleNode = findModule("TC_ModuleNodeWrapper_ModuleWithMultipleParents.rb", 68);

		assertEquals("M5", moduleNode.getName());
		assertEquals("M1::M2::M3::M4::M5", moduleNode.getFullName());
	}
}
