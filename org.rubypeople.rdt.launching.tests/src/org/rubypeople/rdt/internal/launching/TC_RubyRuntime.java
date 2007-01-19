package org.rubypeople.rdt.internal.launching;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMStandin;

public class TC_RubyRuntime extends TestCase {

	private static final String VM_TYPE_ID = "org.rubypeople.rdt.launching.StandardVMType";

	private IVMInstallType vmType;

	public TC_RubyRuntime(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vmType = RubyRuntime.getVMInstallType(VM_TYPE_ID);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
//		RubyRuntime.setDefaultVMInstall(null, true);
		RubyRuntime.getPreferences().setValue(RubyRuntime.PREF_VM_XML, "");
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
			standin.setInstallLocation(new File("C:\\RubyInstallRootOne"));
			standin.setName("InterpreterOne");
			standin.convertToRealVM();
			RubyRuntime.saveVMConfiguration();
			assertEquals(
					"XML should indicate only one interpreter with it being the selected.",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<vmSettings defaultVM=\"\" defaultVMConnector=\"\">\r\n<vmType id=\"org.rubypeople.rdt.launching.StandardVMType\">\r\n<vm id=\"InterpreterOne\" name=\"InterpreterOne\" path=\"C:\\RubyInstallRootOne\"/>\r\n</vmType>\r\n</vmSettings>\r\n",
					getVMsXML());

			VMStandin standin2 = new VMStandin(vmType, "InterpreterTwo");
			standin2.setInstallLocation(new File("C:\\RubyInstallRootTwo"));
			standin2.setName("InterpreterTwo");
			standin2.convertToRealVM();
			RubyRuntime.saveVMConfiguration();
			assertEquals(
					"XML should indicate both interpreters with the first one being selected.",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<vmSettings defaultVM=\"\" defaultVMConnector=\"\">\r\n<vmType id=\"org.rubypeople.rdt.launching.StandardVMType\">\r\n<vm id=\"InterpreterOne\" name=\"InterpreterOne\" path=\"C:\\RubyInstallRootOne\"/>\r\n<vm id=\"InterpreterTwo\" name=\"InterpreterTwo\" path=\"C:\\RubyInstallRootTwo\"/>\r\n</vmType>\r\n</vmSettings>\r\n",
					getVMsXML());

			RubyRuntime.setSelectedInterpreter(standin2);
			assertEquals(
					"XML should indicate both interpreters with the first one being selected.",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<vmSettings defaultVM=\"" + RubyRuntime.getCompositeIdFromVM(standin2) + "\" defaultVMConnector=\"\">\r\n<vmType id=\"org.rubypeople.rdt.launching.StandardVMType\">\r\n<vm id=\"InterpreterOne\" name=\"InterpreterOne\" path=\"C:\\RubyInstallRootOne\"/>\r\n<vm id=\"InterpreterTwo\" name=\"InterpreterTwo\" path=\"C:\\RubyInstallRootTwo\"/>\r\n</vmType>\r\n</vmSettings>\r\n",
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
