package org.codehaus.groovy.science;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import groovy.lang.Closure;
import groovy.lang.MissingMethodException;


/**
 * <p>A nullary operator intended for use as a placeholder for a calculated
 * subexpression in the replacement {@code SymbolicExpression} of a pattern
 * search-and-replace.</p>
 * 
 * <p>A replacement expression is an expression that, when applied to a match
 * result using this class's {@code replacementFor} method, produces either
 * a {@code SymbolicExpression} to use as a search-and-replace result or
 * {@code null} to indicate it cannot generate a result expression for that
 * match result. A replacement expression oftentimes includes a
 * {@code ReplacementTermOperator} or a {@code PatternTermOperator}; if it does
 * not, it only produces itself, no matter what the match result was. Any
 * nullary expression with a {@code ReplacementTermOperator} as its operator,
 * when used as a replacement expression, behaves just like that
 * {@code ReplacementTermOperator}'s own replacer {@code Closure}. A nullary
 * expression with a {@code PatternTermOperator} that has a name behaves like a
 * replacer that retrieves the value associated in the match result with that
 * name.</p>
 * 
 * @see org.codehaus.groovy.science.SymbolicExpression
 */
public class ReplacementTermOperator
{
	/**
	 * <p>The replacer this operator simulates.</p>
	 * 
	 * <p>A replacer is a closure that accepts a {@code Map} and either returns
	 * a {@code SymbolicExpression} or returns {@code null} if no appropriate
	 * expression can be built based on that {@code Map}. The {@code Map} is
	 * usually one of the results of matching a pattern against some other
	 * {@code SymbolicExpression}.</p>
	 */
	private Closure replacer;
	
	/**
	 * <p>Creates a {@code ReplacementTermOperator} with the given replacer
	 * behavior.</p>
	 * 
	 * <p>A replacer is a closure that accepts a {@code Map} and either returns
	 * a {@code SymbolicExpression} or returns {@code null} if no appropriate
	 * expression can be built based on that {@code Map}. The {@code Map} is
	 * usually one of the results of matching a pattern against some other
	 * {@code SymbolicExpression}.</p>
	 * 
	 * @param replacer  the replacer this operator should simulate
	 * 
	 * @throws NullPointerException  if {@code replacer} is {@code null}
	 */
	public ReplacementTermOperator( Closure replacer )
	{
		if ( replacer == null )
			throw new NullPointerException();
		
		this.replacer = replacer;
	}
	
	
	/**
	 * <p>Creates a replacement {@code SymbolicExpression} that uses the given
	 * replacer (by putting it in a {@code ReplacementTermOperator}).</p>
	 * 
	 * @param replacer
	 *     the replacer for the {@code SymbolicExpression} to simulate
	 * 
	 * @return  a {@code SymbolicExpression} that simulates the given replacer
	 * 
	 * @throws NullPointerException  if {@code replacer} is {@code null}
	 */
	public static SymbolicExpression rTerm( Closure replacer )
	{
		if ( replacer == null )
			throw new NullPointerException();
		
		return new SymbolicExpression(
			new ReplacementTermOperator( replacer ),
			new ArrayList< SymbolicExpression >()
		);
	}
	
	/**
	 * <p>Creates a replacement {@code SymbolicExpression} that produces a
	 * {@code SymbolicExpression} that is already present in the match result by
	 * dereferencing the match result with the given {@code name}. If the
	 * {@code name} is not associated with a {@code SymbolicExpression} in the
	 * match result, this replacement expression will fail to produce
	 * anything.</p>
	 * 
	 * <p>This is done by using a {@code ReplacementTermOperator}.</p>
	 * 
	 * @param name
	 *     the object the replacement expression should hope to find associated
	 *     with the {@code SymbolicExpression} it needs to produce
	 * 
	 * @return
	 *     a replacement expression that always produces the
	 *     {@code SymbolicExpression} associated with the given {@code name} if
	 *     that expression exists
	 * 
	 * @throws NullPointerException  if {@code name} is {@code null}
	 */
	public static SymbolicExpression rTerm( Object name )
	{
		if ( name == null )
			throw new NullPointerException();
		
		final Object finalName = name;
		
		return rTerm( new Closure( null ) {
			
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("unused")
			public SymbolicExpression doCall( Map< ?, ? > matchInformation )
			{
				if ( matchInformation == null )
					throw new NullPointerException();
				
				try
				{
					if ( !matchInformation.containsKey( finalName ) )
						return null;
				}
				catch ( ClassCastException e )
				{
					return null;
				}
				
				Object result = matchInformation.get( finalName );
				
				if ( result instanceof SymbolicExpression )
					return (SymbolicExpression)result;
				
				return null;
			}
		} );
	}
	
	/**
	 * <p>Creates a replacement {@code SymbolicExpression} that complements a
	 * pattern expression produced by
	 * {@code PatternTermOperator.pJump( Object, SymbolicExpression )}.
	 * expects an expression segment {@code Closure} to be associated with
	 * {@code name} in the match result, and it applies {@code innerReplacement}
	 * to the match result, returning the expression obtained by plugging the
	 * resulting expression into that expression segment. If
	 * {@code innerReplacement} fails to produce a result, or if there is no
	 * {@code Closure} associated with {@code name} in the match result that can
	 * be used to create a new expression from that result expression, this
	 * replacement expression fails.</p>
	 * 
	 * @see PatternTermOperator#pJump( Object, SymbolicExpression )
	 * 
	 * @param name
	 *     the object the replacement expression should hope to find associated
	 *     with the expression segment {@code Closure} it needs to "jump" with
	 * 
	 * @param rJump
	 *     the replacement expression whose result to insert at the other end of
	 *     the "jump"
	 * 
	 * @return
	 *     a replacement expression that produces, if possible the result of
	 *     {@code innerReplacement}, as modified by a call to a {@code Closure}
	 *     associated with the given {@code name}
	 * 
	 * @throws NullPointerException  if {@code name} is {@code null}
	 */
	public static SymbolicExpression rJump(
		Object name,
		SymbolicExpression innerReplacement
	)
	{
		if (
			(name == null)
			||
			(innerReplacement == null)
		)
			throw new NullPointerException();
		
		final Object finalName = name;
		final SymbolicExpression finalInnerReplacement = innerReplacement;
		
		return rTerm( new Closure( null ) {
			
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("unused")
			public SymbolicExpression doCall( Map< ?, ? > matchInformation )
			{
				if ( matchInformation == null )
					throw new NullPointerException();
				
				try
				{
					if ( !matchInformation.containsKey( finalName ) )
						return null;
				}
				catch ( ClassCastException e )
				{
					return null;
				}
				
				Object jumpSegment = matchInformation.get( finalName );
				
				if ( !(jumpSegment instanceof Closure) )
					return null;
				
				SymbolicExpression innerResult =
					replacementFor( matchInformation, finalInnerReplacement );
				
				if ( innerResult == null )
					return null;
				
				try
				{
					return (SymbolicExpression)((Closure)jumpSegment).call(
						new Object[]{ innerResult }
					);
				}
				catch ( ClassCastException e )
				{
					return null;
				}
				catch ( MissingMethodException e )
				{
					return null;
				}
			}
		} );
	}
	
	/**
	 * <p>Generates the result of using the {@code replacement}
	 * {@code SymbolicExpression} given the specified {@code matchInformation}.
	 * If the replacement expression fails to produce a result expression, this
	 * method returns {@code null}.</p>
	 * 
	 * @param matchInformation
	 *     the result of applying a pattern expression to a subject expression
	 * 
	 * @param replacement
	 *     the replacement expression associated with the presumed pattern
	 *     expression
	 * 
	 * @return  the result expression, if that exists; {@code null} otherwise
	 * 
	 * @throws NullPointerException
	 *     if {@code matchInformation} or {@code replacement} is {@code null}
	 */
	public static SymbolicExpression replacementFor(
		Map< ?, ? > matchInformation,
		SymbolicExpression replacement
	)
	{
		if (
			(matchInformation == null)
			||
			(replacement == null)
		)
			throw new NullPointerException();
		
		Object replacementOperator = replacement.getOperator();
		
		List< SymbolicExpression > subReplacements =
			replacement.getArgumentList();
		
		int numberOfArguments = subReplacements.size();
		
		if (
			(replacementOperator instanceof ReplacementTermOperator)
			&&
			(numberOfArguments == 0)
		)
		{
			return (
				(SymbolicExpression)
				((ReplacementTermOperator)replacementOperator).getReplacer()
					.call( new Object[]{ matchInformation } )
			);
		}
		
		if (
			(replacementOperator instanceof PatternTermOperator)
			&&
			(numberOfArguments == 0)
		)
		{
			Object name = ((PatternTermOperator)replacementOperator).getName();
			
			if ( name != null )
				return replacementFor( matchInformation, rTerm( name ) );
		}
		
		
		List< SymbolicExpression > newArgumentList =
			new ArrayList< SymbolicExpression >();
		
		for ( SymbolicExpression oldArgument: replacement.getArgumentList() )
		{
			SymbolicExpression newArgument =
				replacementFor( matchInformation, oldArgument );
			
			if ( newArgument == null )
				return null;
			
			newArgumentList.add( newArgument );
		}
		
		
		return new SymbolicExpression( replacementOperator, newArgumentList );
	}
	
	/**
	 * <p>Performs a pattern search-and-replace on the given subject expression,
	 * and returns an {@code Iterable} of the possible result expressions.</p>
	 * 
	 * @param pattern
	 *     the pattern expression to match against {@code subject}
	 * 
	 * @param replacement
	 *     the replacement expression to apply to each of the results of
	 *     matching {@code pattern} against {@code subject}
	 * 
	 * @param subject
	 *     the expression to perform the pattern search-and-replace on
	 * 
	 * @return
	 *     an {@code Iterable} over the possible results of the pattern search-
	 *     and-replace
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern}, {@code replacement}, or {@code subject} is
	 *     {@code null}
	 */
	public static Iterable< SymbolicExpression > replacementsFor(
		SymbolicExpression pattern,
		SymbolicExpression replacement,
		SymbolicExpression subject
	)
	{
		if (
			(pattern == null)
			||
			(replacement == null)
			||
			(subject == null)
		)
			throw new NullPointerException();
		
		final SymbolicExpression finalReplacement = replacement;
		
		final Iterable< Map< ?, ? > > matches =
			PatternTermOperator.matchesFor( pattern, subject );
		
		return new Iterable< SymbolicExpression >()
		{
			/* (non-Javadoc)
			 * @see java.lang.Iterable#iterator()
			 */
			@Override
			public Iterator< SymbolicExpression > iterator()
			{
				return new Iterator< SymbolicExpression >()
				{
					/**
					 * <p>An {@code Iterator} over all of the possible results
					 * of matching {@code pattern} against
					 * {@code expression}.</p>
					 */
					private Iterator< Map< ?, ? > > matchIterator =
						matches.iterator();
					
					/**
					 * <p>A flag that is {@code true} only when
					 * {@code nextValue} actually represents either the next
					 * value this {@code Iterator} will generate or {@code null}
					 * if this {@code Iterator} has no more values.</p>
					 */
					private boolean nextIsCalculated = false;
					
					/**
					 * <p>The value that this {@code Iterator} is about to
					 * generate via its {@code next} method. This value is
					 * {@code null} if there are no more elements for this
					 * {@code Iterator} to generate (in which case
					 * {@code nextIsCalculated} is also {@code true}) or if the
					 * next value has not been calculated yet (in which case
					 * {@code nextIsCalculated} is also {@code false}).</p>
					 */
					private SymbolicExpression nextValue;
					
					
					/**
					 * Calculates the next value to return from this
					 * {@code Iterator}, if necessary. 
					 */
					private void calculateNext()
					{
						if ( nextIsCalculated )
							return;
						
						while ( matchIterator.hasNext() )
						{
							SymbolicExpression result = replacementFor(
								matchIterator.next(),
								finalReplacement
							);
							
							if ( result != null )
							{
								nextValue = result;
								nextIsCalculated = true;
								
								return;
							}
						}
						
						nextValue = null;
						nextIsCalculated = true;
					}
					
					/* (non-Javadoc)
					 * @see java.util.Iterator#hasNext()
					 */
					@Override
					public boolean hasNext()
					{
						calculateNext();
						
						return (nextValue != null);
					}
					
					/* (non-Javadoc)
					 * @see java.util.Iterator#next()
					 */
					@Override
					public SymbolicExpression next()
					{
						calculateNext();
						
						if ( nextValue == null )
							throw new NoSuchElementException();
						
						nextIsCalculated = false;
						
						return nextValue;
					}
					
					/* (non-Javadoc)
					 * @see java.util.Iterator#remove()
					 */
					@Override
					public void remove()
					{
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
	
	/**
	 * <p>Performs a pattern search-and-replace on the given subject expression.
	 * If a replacement can be made, the first one is chosen (as determined by
	 * the {@code Iterator} produced by matching {@code pattern} against
	 * {@code subject}). If no replacement can be made, the original expression
	 * is returned.</p>
	 * 
	 * @param pattern
	 *     the pattern expression to match against {@code subject}
	 * 
	 * @param replacement
	 *     the replacement expression to apply to the first result of matching
	 *     {@code pattern} against {@code subject}
	 * 
	 * @param subject
	 *     the expression to perform the pattern search-and-replace on
	 * 
	 * @return
	 *     the {@code SymbolicExpression} formed by making the replacement
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern}, {@code replacement}, or {@code subject} is
	 *     {@code null}
	 */
	public static SymbolicExpression firstReplacementFor(
		SymbolicExpression pattern,
		SymbolicExpression replacement,
		SymbolicExpression subject
	)
	{
		if (
			(pattern == null)
			||
			(replacement == null)
			||
			(subject == null)
		)
			throw new NullPointerException();
		
		
		Iterator< SymbolicExpression > resultIterator = replacementsFor(
			pattern,
			replacement,
			subject
		).iterator();
		
		if ( resultIterator.hasNext() )
			return resultIterator.next();
		
		return subject;
	}
	
	/**
	 * <p>Performs pattern search-and-replaces on the given subject expression
	 * until no further changes are made, and returns the final expression.</p>
	 * 
	 * <p>The first replacement, as determined by the {@code Iterator} given
	 * when applying {@code pattern}, is always taken.</p>
	 * 
	 * <p>If a search-and-replace operation results in one or more new ways for
	 * the search-and-replaces to match, and this happens repeatedly, this
	 * method can wind up in an infinite loop.</p>
	 * 
	 * @param pattern
	 *     the pattern expression to match against {@code subject}
	 * 
	 * @param replacement
	 *     the replacement expression to apply to the first result of matching
	 *     {@code pattern} against {@code subject}
	 * 
	 * @param subject
	 *     the expression to perform the pattern search-and-replace on
	 * 
	 * @return
	 *     the {@code SymbolicExpression} formed by making the replacements
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern}, {@code replacement}, or {@code subject} is
	 *     {@code null}
	 */
	public static SymbolicExpression replaceRepeatedly(
		SymbolicExpression pattern,
		SymbolicExpression replacement,
		SymbolicExpression subject
	)
	{
		if (
			(pattern == null)
			||
			(replacement == null)
			||
			(subject == null)
		)
			throw new NullPointerException();
		
		
		SymbolicExpression result = subject;
		
		while ( true )
		{
			SymbolicExpression newResult =
				firstReplacementFor( pattern, replacement, result );
			
			if ( newResult.equals( result ) )
				return result;
			
			result = newResult;
		}
	}
	
	/**
	 * <p>Performs a pattern search-and-replace on each subexpression of the
	 * given subject expression, and returns an {@code Iterable} of the possible
	 * result expressions.</p>
	 * 
	 * <p>The subexpressions are traversed starting with the root subexpression
	 * (the {@code subject} itself) and going inward, with each argument of an
	 * expression explored in the order specified by that expression's argument
	 * list.</p>
	 * 
	 * <p>During the replacement, the expression segment {@code Closure} that
	 * "jumps" to the subexpression currently being operated on will be
	 * available in the match result. It is guaranteed to be associated with a
	 * key that does not conflict with any of the keys used in
	 * {@code pattern}.</p>
	 * 
	 * @param pattern
	 *     the pattern expression to match against each subexpression of
	 *     {@code subject}
	 * 
	 * @param replacement
	 *     the replacement expression to apply to each of the results of
	 *     matching {@code pattern} against each subexpression of
	 *     {@code subject}
	 * 
	 * @param subject
	 *     the expression to perform the pattern search-and-replace on
	 * 
	 * @return
	 *     an {@code Iterable} over the possible results of the pattern search-
	 *     and-replace
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern}, {@code replacement}, or {@code subject} is
	 *     {@code null}
	 */
	public static Iterable< SymbolicExpression > replacementsAnywhereFor(
		SymbolicExpression pattern,
		SymbolicExpression replacement,
		SymbolicExpression subject
	)
	{
		if (
			(pattern == null)
			||
			(replacement == null)
			||
			(subject == null)
		)
			throw new NullPointerException();
		
		Object name = new Object();
		
		return replacementsFor(
			PatternTermOperator.pJump( name, pattern ),
			rJump( name, replacement ),
			subject
		);
	}
	
	/**
	 * <p>Performs a pattern search-and-replace on each subexpression of the
	 * given subject expression. If a replacement can be made, the first one is
	 * chosen (as determined by the first nonempty {@code Iterator} produced by
	 * matching {@code pattern} against {@code subject}). If no replacement can
	 * be made, the original expression is returned.</p>
	 * 
	 * @param pattern
	 *     the pattern expression to match against the subexpressions of
	 *     {@code subject}
	 * 
	 * @param replacement
	 *     the replacement expression to apply to the first result of matching
	 *     {@code pattern} against the subexpressions of {@code subject}
	 * 
	 * @param subject
	 *     the expression to perform the pattern search-and-replace on
	 * 
	 * @return
	 *     the {@code SymbolicExpression} formed by making the replacement
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern}, {@code replacement}, or {@code subject} is
	 *     {@code null}
	 */
	public static SymbolicExpression firstReplacementAnywhereFor(
		SymbolicExpression pattern,
		SymbolicExpression replacement,
		SymbolicExpression subject
	)
	{
		if (
			(pattern == null)
			||
			(replacement == null)
			||
			(subject == null)
		)
			throw new NullPointerException();
		
		
		Iterator< SymbolicExpression > resultIterator = replacementsAnywhereFor(
			pattern,
			replacement,
			subject
		).iterator();
		
		if ( resultIterator.hasNext() )
			return resultIterator.next();
		
		return subject;
	}
	
	/**
	 * <p>Performs pattern search-and-replaces on the subexpressions of the
	 * given subject expression until no further changes are made, and returns
	 * the final expression.</p>
	 * 
	 * <p>The first replacement, as determined by the first nonempty
	 * {@code Iterator} given when applying {@code pattern}, is always
	 * taken.</p>
	 * 
	 * <p>If a search-and-replace operation results in one or more new ways for
	 * the search-and-replaces to match, and this happens repeatedly, this
	 * method can wind up in an infinite loop.</p>
	 * 
	 * @param pattern
	 *     the pattern expression to match against the subexpressions of
	 *     {@code subject}
	 * 
	 * @param replacement
	 *     the replacement expression to apply to the first result of matching
	 *     {@code pattern} against the subexpressions of {@code subject}
	 * 
	 * @param subject
	 *     the expression to perform the pattern search-and-replace on
	 * 
	 * @return
	 *     the {@code SymbolicExpression} formed by making the replacements
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern}, {@code replacement}, or {@code subject} is
	 *     {@code null}
	 */
	public static SymbolicExpression replaceAnywhereRepeatedly(
		SymbolicExpression pattern,
		SymbolicExpression replacement,
		SymbolicExpression subject
	)
	{
		if (
			(pattern == null)
			||
			(replacement == null)
			||
			(subject == null)
		)
			throw new NullPointerException();
		
		
		SymbolicExpression result = subject;
		
		while ( true )
		{
			SymbolicExpression newResult =
				firstReplacementAnywhereFor( pattern, replacement, result );
			
			if ( newResult.equals( result ) )
				return result;
			
			result = newResult;
		}
	}
	
	/**
	 * <p>Finds all pattern matches in the subexpressions of the given subject
	 * expression, and returns the result of performing search-and-replaces on
	 * all of those matches.</p>
	 * 
	 * <p>The first replacement, as determined by the {@code Iterator} given
	 * when applying {@code pattern}, is always taken, unless that replacement
	 * would result from or overwrite some previously made replacement.</p>
	 * 
	 * <p>If a search-and-replace operation results in one or more new ways for
	 * the search-and-replaces to match, the new potential replacements will not
	 * be attempted. Unlike {@code replaceAnywhereRepeatedly}, this method
	 * should never wind up in an infinite loop because of a situation like
	 * this.</p>
	 * 
	 * @see replaceAnywhereRepeatedly
	 * 
	 * @param pattern
	 *     the pattern expression to match against {@code subject}
	 * 
	 * @param replacement
	 *     the replacement expression to apply to each result of matching
	 *     {@code pattern} against {@code subject}
	 * 
	 * @param subject
	 *     the expression to perform the pattern search-and-replace on
	 * 
	 * @return
	 *     the {@code SymbolicExpression} formed by making the replacements
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern}, {@code replacement}, or {@code subject} is
	 *     {@code null}
	 */
	public static SymbolicExpression replaceAll(
		SymbolicExpression pattern,
		SymbolicExpression replacement,
		SymbolicExpression subject
	)
	{
		if (
			(pattern == null)
			||
			(replacement == null)
			||
			(subject == null)
		)
			throw new NullPointerException();
		
		
		final SymbolicExpression finalPattern = pattern;
		final SymbolicExpression finalReplacement = replacement;
		
		final Object placeholderOperator = new Object();
		
		SymbolicExpression temporaryResult = replaceAnywhereRepeatedly(
			PatternTermOperator.pTerm( new Closure( null ) {
				
				private static final long serialVersionUID = 1L;
				
				private boolean containsPlaceholder(
					SymbolicExpression expression
				)
				{
					if ( expression == null )
						throw new NullPointerException();
					
					Object operator = expression.getOperator();
					if ( operator instanceof ConstantOperator )
					{
						Object value =
							((ConstantOperator< ? >)operator).getValue();
						
						if ( value instanceof SymbolicExpression )
						{
							if (
								((SymbolicExpression)value).getOperator()
									== placeholderOperator
							)
								return true;
						}
					}
					
					for (
						SymbolicExpression argument:
							expression.getArgumentList()
					)
					{
						if ( containsPlaceholder( argument ) )
							return true;
					}
					
					return false;
				}
				
				@SuppressWarnings("unused")
                public Iterable< Map< ?, ? > > doCall(
					SymbolicExpression thisSubject
				)
				{
					if ( thisSubject == null )
						throw new NullPointerException();
					
					if ( containsPlaceholder( thisSubject ) )
						return new ArrayList< Map< ?, ? > >();
					
					return PatternTermOperator.matchesFor(
						finalPattern,
						thisSubject
					);
				}
			} ),
			rTerm( new Closure( null ) {
				
				private static final long serialVersionUID = 1L;
				
				@SuppressWarnings("unused")
                public SymbolicExpression doCall( Map< ?, ? > matchResult )
				{
					SymbolicExpression result =
						replacementFor( matchResult, finalReplacement );
					
					if ( result == null )
						return null;
					
					return ConstantOperator.con( new SymbolicExpression(
						placeholderOperator,
						Arrays.asList( result )
					) );
				}
			} ),
			subject
		);
		
		final Object key = new Object();
		
		return replaceAnywhereRepeatedly(
			PatternTermOperator.pTerm( new Closure( null ) {
				
				private static final long serialVersionUID = 1L;
				
				@SuppressWarnings("unused")
                public Iterable< Map< ?, ? > > doCall(
					SymbolicExpression thisSubject
				)
				{
					if ( thisSubject == null )
						throw new NullPointerException();
					
					Object operator = thisSubject.getOperator();
					if ( !(operator instanceof ConstantOperator) )
						return new ArrayList< Map< ?, ? > >();
					
					Object value = ((ConstantOperator< ? >)operator).getValue();
					
					if ( !(value instanceof SymbolicExpression) )
						return new ArrayList< Map< ?, ? > >();
					
					SymbolicExpression innerExpression =
						(SymbolicExpression)value;
					
					if ( innerExpression.getOperator() != placeholderOperator )
						return new ArrayList< Map< ?, ? > >();
					
					Map< Object, Object > result =
						new HashMap< Object, Object >();
					
					result.put(
						key,
						innerExpression.getArgumentList().get( 0 )
					);
					
					return Arrays.asList( new Map< ?, ? >[]{ result } );
				}
			} ),
			rTerm( key ),
			temporaryResult
		);
	}
	
	
	/**
	 * @see ReplacementTermOperator#matcher
	 * 
	 * @return  the replacer this operator should simulate
	 */
	public Closure getReplacer()
	{
		return replacer;
	}
}