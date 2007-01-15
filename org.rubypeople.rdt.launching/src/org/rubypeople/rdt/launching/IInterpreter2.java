package org.rubypeople.rdt.launching;

public interface IInterpreter2 {

	public String getVMArgs();
	
	public String getRubyVersion();
	
	public void setInterpreterArgs(String vmArgs);
	
}
