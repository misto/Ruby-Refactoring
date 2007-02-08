package org.rubypeople.rdt.refactoring.tests.core;

import junit.framework.TestCase;

import org.rubypeople.rdt.refactoring.core.IRefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;
import org.rubypeople.rdt.refactoring.documentprovider.StringDocumentProvider;

public class TC_RefactoringConditionChecker extends TestCase {
	private final class TestConditionChecker extends RefactoringConditionChecker {
		private TestConditionChecker(IDocumentProvider provider, Object config) {
			super(provider, config);
		}

		@Override
		protected void init(Object configObj) {				
		}

		@Override
		protected void checkInitialConditions() {
		}
	}

	public void testSyntaxErrors() {
		
		RefactoringConditionChecker checker = new TestConditionChecker(new StringDocumentProvider("class Test; en"), null);
		assertEquals(1, checker.getInitialMessages().get(IRefactoringConditionChecker.ERRORS).size());
		assertEquals(0, checker.getInitialMessages().get(IRefactoringConditionChecker.WARNING).size());
	}
	
	public void testSyntaxErrorsInIncludes() {
		
		StringDocumentProvider stringDocumentProvider = new StringDocumentProvider("class Test; end");
		stringDocumentProvider.addFile("other", "class Test; en");
		
		RefactoringConditionChecker checker = new TestConditionChecker(stringDocumentProvider, null);
		assertEquals(0, checker.getInitialMessages().get(IRefactoringConditionChecker.ERRORS).size());
		assertEquals(0, checker.getInitialMessages().get(IRefactoringConditionChecker.WARNING).size());

		assertEquals(0, checker.getFinalMessages().get(IRefactoringConditionChecker.ERRORS).size());
		assertEquals(1, checker.getFinalMessages().get(IRefactoringConditionChecker.WARNING).size());
	}
}
