package org.rubypeople.rdt.internal.ui.search;

import junit.framework.Assert;
import junit.framework.TestCase;


public class TC_Scope extends TestCase {
	public void testNameSimple() {
		Assert.assertEquals("b", new Scope("b").getName()) ;
	}
	
	public void testNameQualified() {
		Assert.assertEquals("b", new Scope("a::b").getName()) ;
	}	
}
