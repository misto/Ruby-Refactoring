package org.rubypeople.rdt.internal.ti;

import java.util.List;

import junit.framework.TestCase;

/**
 * @author Jason
 *
 */
/**
 * @author Jason
 *
 */
public class TypeInferrerTest extends TestCase {
	
	private ITypeInferrer inferrer;
	public void setUp() {
		inferrer = createTypeInferrer();
	}
	
	/**
	 * Shortcut for testing that a particular type is the only one inferred, 
	 * and is inferred with 100% confidence
	 * @param guesses
	 * @param type
	 */
	private void assertInfersTypeWithoutDoubt(List<ITypeGuess> guesses, String type) {
		assertEquals(1, guesses.size());
		ITypeGuess guess = guesses.get(0);
		assertEquals(type, guess.getType());
		assertEquals(100, guess.getConfidence());		
	}
	
	public void testFixnum() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("5", 0), "Fixnum");
	}
	
	public void testString() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("'string'", 3), "String");
	}
	
	public void testFixnumAssignment() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("var=5", 1), "Fixnum");
	}
	
	public void testLocalVariableAfterAssignment() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("x=5;x", 4), "Fixnum");
	}

	public void testLocalVariableAfterAssignmentInsideScope() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("module M;x=5;x;end", 13), "Fixnum");
	}

	public void testLocalVariableAfterAssignmentWithOverwrite() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("x=5;x='foo';x", 12), "String");
		assertInfersTypeWithoutDoubt(inferrer.infer("x=5;y='foo';x", 12), "Fixnum");
	}
	
	public void testLocalVariableAssignmentToLocalVariable() throws Exception {
		String script = "x=5;y=x;x;y";	
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 8), "Fixnum");  // "x"
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 10), "Fixnum");  // "y"
	}
	
	public void testLocalVariableAssignmentToLocalVariableTwice() throws Exception {
		String script = "x=5;y=x;z=y;z;y;x";
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 12), "Fixnum");  // "z"
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 14), "Fixnum");  // "y"
		assertInfersTypeWithoutDoubt(inferrer.infer(script, 16), "Fixnum");  // "x"
	}
	public void testLocalVariableAssignmentToWellKnownMethodCall() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("x=5.to_s;x", 9), "String");
	}

	public void testLocalVariableAssignmentToClassInstantiation() throws Exception {
		assertInfersTypeWithoutDoubt(inferrer.infer("x=Regexp.new;x", 13), "Regexp");
	}
	
	
	

	
	
//todo: at a later date, make sure this is handled:
/*
 * def foo
 *   x = 5
 *   puts x
 * end
 * 
 * def bar(x)
 *   do_stuff_with(x)
 * end
 * 
 * param to do_stuff_with should not be affected by the assignment to x in foo.
 */
//	public void testLocalVariableAssignmentWithSameNameAsInAnotherScope() throws Exception {
//		System.out.println("booga");
//		// Note that the N::x may be preceded by another operations that affect its type, such as
//		// x.to_s!.  Or it may be a parameter.  The search for a preceding LocalAsgnNode should
//		// respect scopes and not override these, say, with the assignment to local x in M.
//		String script = "module M;x=5;x;end;module N;x=6;x;end";
//		
//		// Test first scope
//		List<ITypeGuess> guesses = inferrer.infer(script, 13);
//		assertEquals(1, guesses.size());
//		ITypeGuess guess = guesses.get(0);
//		assertEquals("Fixnum", guess.getType());
//
//		System.out.println("wooga");		
//		// Test second scope
//		guesses = inferrer.infer(script, 32);
//		assertEquals(0, guesses.size());
////		guess = guesses.get(0);
////		assertEquals("String", guess.getType());
//	}

	/**
	 * Override this method in subclasses so that we can test any 
	 * implementation of ITypeInferrer the same way.
	 * @return an implementation of ITypeInferrer
	 */
	protected ITypeInferrer createTypeInferrer() {
		return new DefaultTypeInferrer();
	}

}
