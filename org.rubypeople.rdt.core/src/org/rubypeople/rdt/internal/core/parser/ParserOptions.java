package org.rubypeople.rdt.internal.core.parser;

import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.util.CharOperation;

public class ParserOptions {
	
	public static final long EnsureBlockNotCompleting = 0x1;
	public static final long UnusedLocalVariable = 0x2;
	public static final long UnusedPrivateMember = 0x4;
	public static final long MaskedRescueBlock = 0x8;
	public static final long UnusedArgument = 0x10;
	public static final long EmptyStatement = 0x20;	
	public static final long UnnecessaryElse = 0x40;
	public static final long NullReference = 0x80;
	public static final long FallthroughCase = 0x100;
	
	public static final String ERROR = RubyCore.ERROR;
	public static final String WARNING = RubyCore.WARNING;
	public static final String IGNORE = RubyCore.IGNORE;
	public static final String ENABLED = RubyCore.ENABLED;
	public static final String DISABLED = RubyCore.DISABLED;
	
	// Default severity level for handlers
	public long errorThreshold = 0;
	
	public long warningThreshold = 
		EnsureBlockNotCompleting
		| UnusedLocalVariable
		| UnusedPrivateMember
		/*| NullReference -- keep RubyCore#getDefaultOptions comment in sync */;
	
	// source encoding format
	public String defaultEncoding = null; // will use the platform default encoding
	
	// tags used to recognize tasks in comments
	public char[][] taskTags = null;
	public char[][] taskPriorites = null;
	public boolean isTaskCaseSensitive = true;
	
	public Map getMap() {
		Map optionsMap = new HashMap(30);
		optionsMap.put(RubyCore.COMPILER_PB_HIDDEN_RESCUE_BLOCK, getSeverityString(MaskedRescueBlock)); 
		optionsMap.put(RubyCore.COMPILER_PB_UNUSED_LOCAL, getSeverityString(UnusedLocalVariable)); 
		optionsMap.put(RubyCore.COMPILER_PB_UNUSED_PARAMETER, getSeverityString(UnusedArgument)); 
		optionsMap.put(RubyCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, getSeverityString(UnusedPrivateMember)); 
		optionsMap.put(RubyCore.COMPILER_PB_EMPTY_STATEMENT, getSeverityString(EmptyStatement)); 
		optionsMap.put(RubyCore.COMPILER_PB_UNNECESSARY_ELSE, getSeverityString(UnnecessaryElse)); 
		optionsMap.put(RubyCore.COMPILER_PB_ENSURE_BLOCK_NOT_COMPLETING, getSeverityString(EnsureBlockNotCompleting));
		if (this.defaultEncoding != null) {
			optionsMap.put(RubyCore.CORE_ENCODING, this.defaultEncoding); 
		}
		optionsMap.put(RubyCore.COMPILER_TASK_TAGS, this.taskTags == null ? "" : new String(CharOperation.concatWith(this.taskTags,','))); //$NON-NLS-1$
		optionsMap.put(RubyCore.COMPILER_TASK_PRIORITIES, this.taskPriorites == null ? "" : new String(CharOperation.concatWith(this.taskPriorites,','))); //$NON-NLS-1$
		optionsMap.put(RubyCore.COMPILER_TASK_CASE_SENSITIVE, this.isTaskCaseSensitive ? ENABLED : DISABLED);
		optionsMap.put(RubyCore.COMPILER_PB_NULL_REFERENCE, getSeverityString(NullReference));
		optionsMap.put(RubyCore.COMPILER_PB_FALLTHROUGH_CASE, getSeverityString(FallthroughCase));
		return optionsMap;		
	}
	
	public String getSeverityString(long irritant) {
		if((this.warningThreshold & irritant) != 0)
			return WARNING;
		if((this.errorThreshold & irritant) != 0)
			return ERROR;
		return IGNORE;
	}

}
