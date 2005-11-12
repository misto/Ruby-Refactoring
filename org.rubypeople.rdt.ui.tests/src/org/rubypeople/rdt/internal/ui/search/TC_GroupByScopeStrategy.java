package org.rubypeople.rdt.internal.ui.search;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;
import org.rubypeople.rdt.internal.core.symbols.Location;
import org.rubypeople.rdt.internal.core.symbols.MethodSymbol;
import org.rubypeople.rdt.internal.core.symbols.SearchResult;


public class TC_GroupByScopeStrategy extends TestCase {
	private GroupByScopeStrategy groupByScopeStrategy ;
	
	public void setUp() {
		groupByScopeStrategy = new GroupByScopeStrategy() ;
	}
	
	public void testNoQualifiedName() {
		Assert.assertEquals(null, groupByScopeStrategy.getParent(new Scope("name"))) ;
	}
	
	public void testQualifiedName1() {
		Assert.assertEquals(new Scope("pckg"), groupByScopeStrategy.getParent(new Scope("pckg::name"))) ;
	}	
	
	public void testQualifiedName2() {
		Assert.assertEquals(new Scope("pckg::class"), groupByScopeStrategy.getParent(new Scope("pckg::class::name"))) ;
	}
	
	public void testSearchResult() {
		MethodSymbol methodSymbol = new MethodSymbol("clazz::myMethod") ;
		SearchResult searchResult = new SearchResult(methodSymbol, new Location(new ShamFile("test"), new RdtPosition(0,0,0,0))) ;
		Assert.assertEquals(new Scope("clazz"), groupByScopeStrategy.getParent(searchResult)) ;
	}
	
	public void testOtherClass() {
		Assert.assertEquals(null, groupByScopeStrategy.getParent(this)) ;
	}
	
	
}
