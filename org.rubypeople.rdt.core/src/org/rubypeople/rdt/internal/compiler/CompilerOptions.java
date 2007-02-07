package org.rubypeople.rdt.internal.compiler;

import java.util.HashMap;
import java.util.Map;

public class CompilerOptions {
	
	public static final String OPTION_ReportEmptyStatement = "org.rubypeople.rdt.core.compiler.problem.emptyStatement"; //$NON-NLS-1$
	
	public static final long EmptyStatement = 0x80000;
	
	public static final String ERROR = "error"; //$NON-NLS-1$
	public static final String WARNING = "warning"; //$NON-NLS-1$
	public static final String IGNORE = "ignore"; //$NON-NLS-1$
	
//	 Default severity level for handlers
	public long errorThreshold = 0;
	public long warningThreshold = 0;
	
	public Map getMap() {
		Map optionsMap = new HashMap(30);
		optionsMap.put(OPTION_ReportEmptyStatement, getSeverityString(EmptyStatement));
		return optionsMap;
	}
	
	public String getSeverityString(long irritant) {
		if((this.warningThreshold & irritant) != 0)
			return WARNING;
		if((this.errorThreshold & irritant) != 0)
			return ERROR;
		return IGNORE;
	}
	
	public void set(Map optionsMap) {
		Object optionValue;
		if ((optionValue = optionsMap.get(OPTION_ReportEmptyStatement)) != null) updateSeverity(EmptyStatement, optionValue);
	}
	
	void updateSeverity(long irritant, Object severityString) {
		if (ERROR.equals(severityString)) {
			this.errorThreshold |= irritant;
			this.warningThreshold &= ~irritant;
		} else if (WARNING.equals(severityString)) {
			this.errorThreshold &= ~irritant;
			this.warningThreshold |= irritant;
		} else if (IGNORE.equals(severityString)) {
			this.errorThreshold &= ~irritant;
			this.warningThreshold &= ~irritant;
		}
	}	

}
