package org.rubypeople.rdt.core.search;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.core.search.indexing.IIndexConstants;
import org.rubypeople.rdt.internal.core.search.matching.ConstructorPattern;
import org.rubypeople.rdt.internal.core.search.matching.FieldPattern;
import org.rubypeople.rdt.internal.core.search.matching.InternalSearchPattern;
import org.rubypeople.rdt.internal.core.search.matching.MethodPattern;
import org.rubypeople.rdt.internal.core.search.matching.OrPattern;
import org.rubypeople.rdt.internal.core.search.matching.QualifiedTypeDeclarationPattern;
import org.rubypeople.rdt.internal.core.search.matching.TypeReferencePattern;
import org.rubypeople.rdt.internal.core.util.CharOperation;

public abstract class SearchPattern extends InternalSearchPattern {
//	 Rules for pattern matching: (exact, prefix, pattern) [ | case sensitive]
	/**
	 * Match rule: The search pattern matches exactly the search result,
	 * that is, the source of the search result equals the search pattern.
	 */
	public static final int R_EXACT_MATCH = 0;

	/**
	 * Match rule: The search pattern is a prefix of the search result.
	 */
	public static final int R_PREFIX_MATCH = 0x0001;

	/**
	 * Match rule: The search pattern contains one or more wild cards ('*' or '?'). 
	 * A '*' wild-card can replace 0 or more characters in the search result.
	 * A '?' wild-card replaces exactly 1 character in the search result.
	 */
	public static final int R_PATTERN_MATCH = 0x0002;

	/**
	 * Match rule: The search pattern contains a regular expression.
	 */
	public static final int R_REGEXP_MATCH = 0x0004;

	/**
	 * Match rule: The search pattern matches the search result only if cases are the same.
	 * Can be combined to previous rules, e.g. {@link #R_EXACT_MATCH} | {@link #R_CASE_SENSITIVE}
	 */
	public static final int R_CASE_SENSITIVE = 0x0008;

	/**
	 * Match rule: The search pattern matches search results as raw/parameterized types/methods with same erasure.
	 * This mode has no effect on other java elements search.<br>
	 * Type search example:
	 * 	<ul>
	 * 	<li>pattern: <code>List&lt;Exception&gt;</code></li>
	 * 	<li>match: <code>List&lt;Object&gt;</code></li>
	 * 	</ul>
	 * Method search example:
	 * 	<ul>
	 * 	<li>declaration: <code>&lt;T&gt;foo(T t)</code></li>
	 * 	<li>pattern: <code>&lt;Exception&gt;foo(new Exception())</code></li>
	 * 	<li>match: <code>&lt;Object&gt;foo(new Object())</code></li>
	 * 	</ul>
	 * Can be combined to all other match rules, e.g. {@link #R_CASE_SENSITIVE} | {@link #R_ERASURE_MATCH}
	 * This rule is not activated by default, so raw types or parameterized types with same erasure will not be found
	 * for pattern List&lt;String&gt;,
	 * Note that with this pattern, the match selection will be only on the erasure even for parameterized types.
	 * @since 3.1
	 */
	public static final int R_ERASURE_MATCH = 0x0010;

	/**
	 * Match rule: The search pattern matches search results as raw/parameterized types/methods with equivalent type parameters.
	 * This mode has no effect on other java elements search.<br>
	 * Type search example:
	 * <ul>
	 * 	<li>pattern: <code>List&lt;Exception&gt;</code></li>
	 * 	<li>match:
	 * 		<ul>
	 * 		<li><code>List&lt;? extends Throwable&gt;</code></li>
	 * 		<li><code>List&lt;? super RuntimeException&gt;</code></li>
	 * 		<li><code>List&lt;?&gt;</code></li>
	 *			</ul>
	 * 	</li>
	 * 	</ul>
	 * Method search example:
	 * 	<ul>
	 * 	<li>declaration: <code>&lt;T&gt;foo(T t)</code></li>
	 * 	<li>pattern: <code>&lt;Exception&gt;foo(new Exception())</code></li>
	 * 	<li>match:
	 * 		<ul>
	 * 		<li><code>&lt;? extends Throwable&gt;foo(new Exception())</code></li>
	 * 		<li><code>&lt;? super RuntimeException&gt;foo(new Exception())</code></li>
	 * 		<li><code>foo(new Exception())</code></li>
	 *			</ul>
	 * 	</ul>
	 * Can be combined to all other match rules, e.g. {@link #R_CASE_SENSITIVE} | {@link #R_EQUIVALENT_MATCH}
	 * This rule is not activated by default, so raw types or equivalent parameterized types will not be found
	 * for pattern List&lt;String&gt;,
	 * This mode is overridden by {@link  #R_ERASURE_MATCH} as erasure matches obviously include equivalent ones.
	 * That means that pattern with rule set to {@link #R_EQUIVALENT_MATCH} | {@link  #R_ERASURE_MATCH}
	 * will return same results than rule only set with {@link  #R_ERASURE_MATCH}.
	 * @since 3.1
	 */
	public static final int R_EQUIVALENT_MATCH = 0x0020;

	/**
	 * Match rule: The search pattern matches exactly the search result,
	 * that is, the source of the search result equals the search pattern.
	 * @since 3.1
	 */
	public static final int R_FULL_MATCH = 0x0040;

	/**
	 * Match rule: The search pattern contains a Camel Case expression.
	 * <br>
	 * Examples:
	 * <ul>
	 * 	<li><code>NPE</code> type string pattern will match
	 * 		<code>NullPointerException</code> and <code>NpPermissionException</code> types,</li>
	 * 	<li><code>NuPoEx</code> type string pattern will only match
	 * 		<code>NullPointerException</code> type.</li>
	 * </ul>
	 * @see CharOperation#camelCaseMatch(char[], char[]) for a detailed explanation
	 * of Camel Case matching.
	 *<br>
	 * Can be combined to {@link #R_PREFIX_MATCH} match rule. For example,
	 * when prefix match rule is combined with Camel Case match rule,
	 * <code>"nPE"</code> pattern will match <code>nPException</code>.
	 *<br>
	 * Match rule {@link #R_PATTERN_MATCH} may also be combined but both rules
	 * will not be used simultaneously as they are mutually exclusive.
	 * Used match rule depends on whether string pattern contains specific pattern 
	 * characters (e.g. '*' or '?') or not. If it does, then only Pattern match rule
	 * will be used, otherwise only Camel Case match will be used.
	 * For example, with <code>"NPE"</code> string pattern, search will only use
	 * Camel Case match rule, but with <code>N*P*E*</code> string pattern, it will 
	 * use only Pattern match rule.
	 * 
	 * @since 3.2
	 */
	public static final int R_CAMELCASE_MATCH = 0x0080;

	private static final int MODE_MASK = R_EXACT_MATCH | R_PREFIX_MATCH | R_PATTERN_MATCH | R_REGEXP_MATCH;
	
	private int matchRule;

	/**
	 * Creates a search pattern with the rule to apply for matching index keys. 
	 * It can be exact match, prefix match, pattern match or regexp match.
	 * Rule can also be combined with a case sensitivity flag.
	 * 
	 * @param matchRule one of {@link #R_EXACT_MATCH}, {@link #R_PREFIX_MATCH}, {@link #R_PATTERN_MATCH},
	 * 	{@link #R_REGEXP_MATCH}, {@link #R_CAMELCASE_MATCH} combined with one of following values:
	 * 	{@link #R_CASE_SENSITIVE}, {@link #R_ERASURE_MATCH} or {@link #R_EQUIVALENT_MATCH}.
	 *		e.g. {@link #R_EXACT_MATCH} | {@link #R_CASE_SENSITIVE} if an exact and case sensitive match is requested, 
	 *		{@link #R_PREFIX_MATCH} if a prefix non case sensitive match is requested or {@link #R_EXACT_MATCH} | {@link #R_ERASURE_MATCH}
	 *		if a non case sensitive and erasure match is requested.<br>
	 * 	Note that {@link #R_ERASURE_MATCH} or {@link #R_EQUIVALENT_MATCH} have no effect
	 * 	on non-generic types/methods search.<br>
	 * 	Note also that default behavior for generic types/methods search is to find exact matches.
	 */
	public SearchPattern(int matchRule) {
		this.matchRule = matchRule;
		// Set full match implicit mode
		if ((matchRule & (R_EQUIVALENT_MATCH | R_ERASURE_MATCH )) == 0) {
			this.matchRule |= R_FULL_MATCH;
		}
	}	
	
	/**
	 * Returns a blank pattern that can be used as a record to decode an index key.
	 * <p>
	 * Implementors of this method should return a new search pattern that is going to be used
	 * to decode index keys.
	 * </p>
	 * 
	 * @return a new blank pattern
	 * @see #decodeIndexKey(char[])
	 */
	public abstract SearchPattern getBlankPattern();
	
	/**
	 * Decode the given index key in this pattern. The decoded index key is used by 
	 * {@link #matchesDecodedKey(SearchPattern)} to find out if the corresponding index entry 
	 * should be considered.
	 * <p>
	 * This method should be re-implemented in subclasses that need to decode an index key.
	 * </p>
	 * 
	 * @param key the given index key
	 */
	public void decodeIndexKey(char[] key) {
		// called from findIndexMatches(), override as necessary
	}
	
	/**
	 * Returns a key to find in relevant index categories, if null then all index entries are matched.
	 * The key will be matched according to some match rule. These potential matches
	 * will be further narrowed by the match locator, but precise match locating can be expensive,
	 * and index query should be as accurate as possible so as to eliminate obvious false hits.
	 * <p>
	 * This method should be re-implemented in subclasses that need to narrow down the
	 * index query.
	 * </p>
	 * 
	 * @return an index key from this pattern, or <code>null</code> if all index entries are matched.
	 */
	public char[] getIndexKey() {
		return null; // called from queryIn(), override as necessary
	}
	/**
	 * Returns an array of index categories to consider for this index query.
	 * These potential matches will be further narrowed by the match locator, but precise
	 * match locating can be expensive, and index query should be as accurate as possible
	 * so as to eliminate obvious false hits.
	 * <p>
	 * This method should be re-implemented in subclasses that need to narrow down the
	 * index query.
	 * </p>
	 * 
	 * @return an array of index categories
	 */
	public char[][] getIndexCategories() {
		return CharOperation.NO_CHAR_CHAR; // called from queryIn(), override as necessary
	}
	
	/**
	 * Returns the rule to apply for matching index keys. Can be exact match, prefix match, pattern match or regexp match.
	 * Rule can also be combined with a case sensitivity flag.
	 * 
	 * @return one of R_EXACT_MATCH, R_PREFIX_MATCH, R_PATTERN_MATCH, R_REGEXP_MATCH combined with R_CASE_SENSITIVE,
	 *   e.g. R_EXACT_MATCH | R_CASE_SENSITIVE if an exact and case sensitive match is requested, 
	 *   or R_PREFIX_MATCH if a prefix non case sensitive match is requested.
	 * [TODO (frederic) I hope R_ERASURE_MATCH doesn't need to be on this list. Because it would be a breaking API change.]
	 */	
	public final int getMatchRule() {
		return this.matchRule;
	}
	/**
	 * Returns whether this pattern matches the given pattern (representing a decoded index key).
	 * <p>
	 * This method should be re-implemented in subclasses that need to narrow down the
	 * index query.
	 * </p>
	 * 
	 * @param decodedPattern a pattern representing a decoded index key
	 * @return whether this pattern matches the given pattern
	 */
	public boolean matchesDecodedKey(SearchPattern decodedPattern) {
		return true; // called from findIndexMatches(), override as necessary if index key is encoded
	}

	public static SearchPattern createPattern(int elementType, String stringPattern, int limitTo, int matchRule) {
		switch (elementType) {
		case IRubyElement.TYPE:
			return createTypePattern(stringPattern, limitTo, matchRule, IIndexConstants.TYPE_SUFFIX);
		case IRubyElement.METHOD:
			return createMethodOrConstructorPattern(stringPattern, limitTo, matchRule, false/*not a constructor*/);
		case IRubyElement.FIELD:
		case IRubyElement.CONSTANT:
		case IRubyElement.GLOBAL:
		case IRubyElement.CLASS_VAR:
		case IRubyElement.INSTANCE_VAR:
			return createFieldPattern(stringPattern, limitTo, matchRule);
		default:
			break;
		}
		return null;
	}
	
	/**
	 * Field pattern are formed by [declaringType.]name[ type]
	 * e.g. java.lang.String.serialVersionUID long
	 *		field*
	 */
	private static SearchPattern createFieldPattern(String patternString, int limitTo, int matchRule) {
		String fieldName = patternString;
		if (fieldName == null) return null;

		char[] fieldNameChars = fieldName.toCharArray();
		if (fieldNameChars.length == 1 && fieldNameChars[0] == '*') fieldNameChars = null;
			
		char[] declaringTypeQualification = null, declaringTypeSimpleName = null;
		char[] typeQualification = null, typeSimpleName = null;

		// Create field pattern
		boolean findDeclarations = false;
		boolean readAccess = false;
		boolean writeAccess = false;
		switch (limitTo) {
			case IRubySearchConstants.DECLARATIONS :
				findDeclarations = true;
				break;
			case IRubySearchConstants.REFERENCES :
				readAccess = true;
				writeAccess = true;
				break;
			case IRubySearchConstants.READ_ACCESSES :
				readAccess = true;
				break;
			case IRubySearchConstants.WRITE_ACCESSES :
				writeAccess = true;
				break;
			case IRubySearchConstants.ALL_OCCURRENCES :
				findDeclarations = true;
				readAccess = true;
				writeAccess = true;
				break;
		}
		return new FieldPattern(
				findDeclarations,
				readAccess,
				writeAccess,
				fieldNameChars,
				declaringTypeQualification,
				declaringTypeSimpleName,
				matchRule);
	}
	
	/**
	 * Returns whether the given name matches the given pattern.
	 * <p>
	 * This method should be re-implemented in subclasses that need to define how
	 * a name matches a pattern.
	 * </p>
	 * 
	 * @param pattern the given pattern, or <code>null</code> to represent "*"
	 * @param name the given name
	 * @return whether the given name matches the given pattern
	 */
	public boolean matchesName(char[] pattern, char[] name) {
		if (pattern == null) return true; // null is as if it was "*"
		if (name != null) {
			boolean isCaseSensitive = (this.matchRule & R_CASE_SENSITIVE) != 0;
			boolean isCamelCase = (this.matchRule & R_CAMELCASE_MATCH) != 0;
			int matchMode = this.matchRule & MODE_MASK;
			boolean sameLength = pattern.length == name.length;
			boolean canBePrefix = name.length >= pattern.length;
			boolean matchFirstChar = !isCaseSensitive || pattern.length == 0 || (name.length > 0 &&  pattern[0] == name[0]);
			if (isCamelCase && matchFirstChar && CharOperation.camelCaseMatch(pattern, name)) {
				return true;
			}
			switch (matchMode) {
				case R_EXACT_MATCH :
				case R_FULL_MATCH :
					if (!isCamelCase) {
						if (sameLength && matchFirstChar) {
							return CharOperation.equals(pattern, name, isCaseSensitive);
						}
						break;
					}
					// fall through next case to match as prefix if camel case failed
				case R_PREFIX_MATCH :
					if (canBePrefix && matchFirstChar) {
						return CharOperation.prefixEquals(pattern, name, isCaseSensitive);
					}
					break;

				case R_PATTERN_MATCH :
					if (!isCaseSensitive)
						pattern = CharOperation.toLowerCase(pattern);
					return CharOperation.match(pattern, name, isCaseSensitive);

				case R_REGEXP_MATCH :
					// TODO (frederic) implement regular expression match
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Type pattern are formed by [qualification '.']type [typeArguments].
	 * e.g. java.lang.Object
	 *		Runnable
	 *		List&lt;String&gt;
	 *
	 * @since 3.1
	 *		Type arguments can be specified to search references to parameterized types.
	 * 	and look as follow: '&lt;' { [ '?' {'extends'|'super'} ] type ( ',' [ '?' {'extends'|'super'} ] type )* | '?' } '&gt;'
	 * 	Please note that:
	 * 		- '*' is not valid inside type arguments definition &lt;&gt;
	 * 		- '?' is treated as a wildcard when it is inside &lt;&gt; (ie. it must be put on first position of the type argument)
	 */
	private static SearchPattern createTypePattern(String patternString, int limitTo, int matchRule, char indexSuffix) {
		char[] typePart = patternString.toCharArray();
		char[] typeChars = null;
		char[] qualificationChars = null;
		// get qualification name
		int lastDotPosition = CharOperation.lastIndexOf("::", typePart);
		if (lastDotPosition >= 0) {
			qualificationChars = CharOperation.subarray(typePart, 0, lastDotPosition);
			if (qualificationChars.length == 1 && qualificationChars[0] == '*')
				qualificationChars = null;
			typeChars = CharOperation.subarray(typePart, lastDotPosition+2, typePart.length);
		} else {
			typeChars = typePart;
		}
		if (typeChars.length == 1 && typeChars[0] == '*') {
			typeChars = null;
		}
		switch (limitTo) {
			case IRubySearchConstants.DECLARATIONS : // cannot search for explicit member types
				return new QualifiedTypeDeclarationPattern(qualificationChars, typeChars, indexSuffix, matchRule);
			case IRubySearchConstants.REFERENCES :
				return new TypeReferencePattern(qualificationChars, typeChars, matchRule);
//			case IRubySearchConstants.IMPLEMENTORS : 
//				return new SuperTypeReferencePattern(qualificationChars, typeChars, SuperTypeReferencePattern.ONLY_SUPER_INTERFACES, indexSuffix, matchRule);
			case IRubySearchConstants.ALL_OCCURRENCES :
				return new OrPattern(
					new QualifiedTypeDeclarationPattern(qualificationChars, typeChars, indexSuffix, matchRule),// cannot search for explicit member types
					new TypeReferencePattern(qualificationChars, typeChars, matchRule));
		}
		return null;
	}

	/**
	 * Method pattern are formed by:<br>
	 * 	[declaringType '.'] selector ['(' parameterTypes ')']
	 *		<br>e.g.<ul>
	 *			<li>java.lang.Runnable.run() void</li>
	 *			<li>main(*)</li>
	 *			<li>&lt;String&gt;toArray(String[])</li>
	 *		</ul>
	 * Constructor pattern are formed by:<br>
	 *		[declaringQualification '.'] type ['(' parameterTypes ')']
	 *		<br>e.g.<ul>
	 *			<li>java.lang.Object()</li>
	 *			<li>Main(*)</li>
	 *			<li>&lt;Exception&gt;Sample(Exception)</li>
	 *		</ul>
	 * Type arguments have the same pattern that for type patterns
	 * @see #createTypePattern(String,int,int,char)
	 */
	private static SearchPattern createMethodOrConstructorPattern(String patternString, int limitTo, int matchRule, boolean isConstructor) {
		char[] selectorChars = patternString.toCharArray();
		// TODO Break up the patternString into declaring type, method name, etc
		char[][] parameterNames = new char[0][];
		char[] declaringTypeSimpleName = null;
		char[] declaringTypeQualification = null;
		// Create method/constructor pattern
		boolean findDeclarations = true;
		boolean findReferences = true;
		switch (limitTo) {
			case IRubySearchConstants.DECLARATIONS :
				findReferences = false;
				break;
			case IRubySearchConstants.REFERENCES :
				findDeclarations = false;
				break;
			case IRubySearchConstants.ALL_OCCURRENCES :
				break;
		}
		if (isConstructor) {
			return new ConstructorPattern(
					findDeclarations,
					findReferences,
					declaringTypeSimpleName, 
					declaringTypeQualification,
					parameterNames,
					matchRule);
		} else {
			return new MethodPattern(
					findDeclarations,
					findReferences,
					selectorChars,
					declaringTypeQualification,
					declaringTypeSimpleName,
					parameterNames,
					matchRule);
		}
	}
	
}
