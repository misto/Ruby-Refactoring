package org.rubypeople.rdt.internal.launching;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.tests.ModifyingResourceTest;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMStandin;

public class TC_RubyRuntime extends ModifyingResourceTest {

	private static final String VM_TYPE_ID = "org.rubypeople.rdt.launching.StandardVMType";

	private IVMInstallType vmType;
	private IFolder folderOne;
	private IFolder folderTwo;
	
	public TC_RubyRuntime(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vmType = RubyRuntime.getVMInstallType(VM_TYPE_ID);
		RubyRuntime.setDefaultVMInstall(null, null, true);
		LaunchingPlugin.getDefault().setIgnoreVMDefPropertyChangeEvents(true);
		createProject("/rubyRuntime");
		folderOne = createFolder("/rubyRuntime/interpreterOne");
		createFolder("/rubyRuntime/interpreterOne/lib");
		createFolder("/rubyRuntime/interpreterOne/bin");
		createFile("/rubyRuntime/interpreterOne/bin/ruby", "");
		folderTwo = createFolder("/rubyRuntime/interpreterTwo");
		createFolder("/rubyRuntime/interpreterTwo/lib");
		createFolder("/rubyRuntime/interpreterTwo/bin");
		createFile("/rubyRuntime/interpreterTwo/bin/ruby", "");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		vmType = null;
		RubyRuntime.setDefaultVMInstall(null, null, true);
		RubyRuntime.getPreferences().setValue(RubyRuntime.PREF_VM_XML, "");
		deleteProject("/rubyRuntime");
	}

	public void testGetInstalledInterpreters() {
		try {
			VMStandin standin = new VMStandin(vmType, "InterpreterOne");
			standin.setInstallLocation(new File("C:/RubyInstallRootOne"));
			standin.setName("InterpreterOne");
			standin.convertToRealVM();

			VMStandin standin2 = new VMStandin(vmType, "InterpreterTwo");
			standin2.setInstallLocation(new File("C:/RubyInstallRootTwo"));
			standin2.setName("InterpreterTwo");
			standin2.convertToRealVM();

			IVMInstallType myType = RubyRuntime.getVMInstallType(VM_TYPE_ID);
			IVMInstall[] installs = myType.getVMInstalls();
			assertEquals(2, installs.length);
			assertEquals("InterpreterOne", installs[0].getName());
			assertEquals("InterpreterTwo", installs[1].getName());
		} finally {
			vmType.disposeVMInstall("InterpreterOne");
			vmType.disposeVMInstall("InterpreterTwo");
		}
	}

	public void testSetInstalledInterpreters() throws CoreException {
		try {
			VMStandin standin = new VMStandin(vmType, "InterpreterOne");
			standin.setInstallLocation(folderOne.getLocation().toFile());
			standin.setName("InterpreterOne");
			IVMInstall one = standin.convertToRealVM();
			RubyRuntime.setDefaultVMInstall(one, null,true);
			assertEquals(
					"XML should indicate only one interpreter with it being the selected.",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<vmSettings defaultVM=\"43,org.rubypeople.rdt.launching.StandardVMType14,InterpreterOne\">\r\n<vmType id=\"org.rubypeople.rdt.launching.StandardVMType\">\r\n<vm id=\"InterpreterOne\" name=\"InterpreterOne\" path=\"" + folderOne.getLocation().toOSString() +  "\"/>\r\n</vmType>\r\n</vmSettings>\r\n",
					getVMsXML());

			VMStandin standin2 = new VMStandin(vmType, "InterpreterTwo");
			standin2.setInstallLocation(folderTwo.getLocation().toFile());
			standin2.setName("InterpreterTwo");
			IVMInstall two = standin2.convertToRealVM();
			RubyRuntime.saveVMConfiguration();
			assertEquals(
					"XML should indicate both interpreters with the first one being selected.",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<vmSettings defaultVM=\"43,org.rubypeople.rdt.launching.StandardVMType14,InterpreterOne\">\r\n<vmType id=\"org.rubypeople.rdt.launching.StandardVMType\">\r\n<vm id=\"InterpreterOne\" name=\"InterpreterOne\" path=\"" + folderOne.getLocation().toOSString() +  "\"/>\r\n<vm id=\"InterpreterTwo\" name=\"InterpreterTwo\" path=\"" + folderTwo.getLocation().toOSString() +  "\"/>\r\n</vmType>\r\n</vmSettings>\r\n",
					getVMsXML());

			RubyRuntime.setDefaultVMInstall(two, null,true);
			assertEquals(
					"XML should indicate both interpreters with the first one being selected.",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<vmSettings defaultVM=\"" + RubyRuntime.getCompositeIdFromVM(standin2) + "\">\r\n<vmType id=\"org.rubypeople.rdt.launching.StandardVMType\">\r\n<vm id=\"InterpreterOne\" name=\"InterpreterOne\" path=\"" + folderOne.getLocation().toOSString() +  "\"/>\r\n<vm id=\"InterpreterTwo\" name=\"InterpreterTwo\" path=\"" + folderTwo.getLocation().toOSString() +  "\"/>\r\n</vmType>\r\n</vmSettings>\r\n",
					getVMsXML());
		} finally {
			vmType.disposeVMInstall("InterpreterOne");
			vmType.disposeVMInstall("InterpreterTwo");
		}
	}

	private String getVMsXML() {
		return RubyRuntime.getPreferences().getString(RubyRuntime.PREF_VM_XML);
	}
}
