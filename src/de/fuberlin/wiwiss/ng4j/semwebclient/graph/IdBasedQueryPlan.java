package de.fuberlin.wiwiss.ng4j.semwebclient.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.query.BindingQueryPlan;
import com.hp.hpl.jena.graph.query.Domain;
import com.hp.hpl.jena.graph.query.ExpressionSet;
import com.hp.hpl.jena.graph.query.Query;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.NiceIterator;


/**
 * An iterator based (see {@link IdBasedQueryIterator}) query plan for BGP
 * queries that can be used for RDF graph implementations which use identifiers
 * for RDF nodes (ie for {@link IdBasedGraph} implementations).
 * This plan consist of a pipeline of {@link IdBasedQueryIterator}s where each
 * iterator evaluates one triple pattern from the BGP query.
 *
 * @author Olaf Hartig
 */
public class IdBasedQueryPlan implements BindingQueryPlan
{
	// members

	/** the queried RDF graph */
	final protected IdBasedGraph graph;
	/** the BGP query */
	final protected Query query;

	/**
	 * the root iterator of the iterator pipeline; ie the iterator that provides
	 * the overall result of the BGP query
	 */
	final protected IdBasedQueryIterator rootIterator;


	// initialization

	public IdBasedQueryPlan ( IdBasedGraph graph, Query query, Node[] variables )
	{
		assert graph != null;

		if ( query.getConstraints().isComplex() ) {
			throw new UnsupportedOperationException( "Constraints are not supported at the moment." );
		}

		this.graph = graph;
		this.query = query;

		// create a temporary dictionary of the query variables
		Map<Node,Integer> varsDict = new HashMap<Node,Integer> ();
		for ( int i = 0; i < variables.length; ++i ) {
			varsDict.put( variables[i], Integer.valueOf(i) );
		}

		// create an initial iterator that provides a single empty solution
		IdBasedQueryIterator it = new IdBasedQItSingleton( new EncodedSolution(variables.length) );

		// create a pipeline of iterators (one for each triple pattern in the BGP query)
		for ( Object tp : query.getPattern() )
		{
			EncodedTriplePattern etp = encode( (Triple) tp, varsDict );
			if ( etp == null )
			{
				// etp is null iff at least one of the RDF nodes in the triple
				// pattern are not known by the queried RDF graph (i.e. they are
				// not in the node dictionary of the graph). In this case we won't
				// find matching triples for this pattern. Hence, we append an
				// iterator that provides no solutions to the pipeline.
				it = new IdBasedQItEmpty();
				break;
			}

			it = new IdBasedTriplePatternMatcher( graph, etp, it );
		}
		rootIterator = it;
	}


	// implementation of the BindingQueryPlan interface

	public ExtendedIterator executeBindings ()
	{
		return new ConvertEncodedSolutionToDomainIterator( rootIterator );
	}


	// helpers

	/**
	 * Encodes the given triple pattern by using the node dictionary of the
	 * queried RDF graph and the given dictionary of query variables.
	 * This method return null iff at least one of the RDF nodes in the given
	 * triple pattern are not known by the queried RDF graph (i.e. they are not
	 * not in the node dictionary of the graph).
	 */
	final protected EncodedTriplePattern encode ( Triple tp, Map<Node,Integer> varsDict )
	{
		boolean sIsVar = ( tp.getSubject().isVariable() );
		boolean pIsVar = ( tp.getPredicate().isVariable() );
		boolean oIsVar = ( tp.getObject().isVariable() );

		if ( sIsVar && ! varsDict.containsKey(tp.getSubject()) ) {
			throw new IllegalArgumentException( "Our dictionary of query variables does not contain the variable " + tp.getSubject().toString() + "." );
		}
		if ( pIsVar && ! varsDict.containsKey(tp.getPredicate()) ) {
			throw new IllegalArgumentException( "Our dictionary of query variables does not contain the variable " + tp.getPredicate().toString() + "." );
		}
		if ( oIsVar && ! varsDict.containsKey(tp.getObject()) ) {
			throw new IllegalArgumentException( "Our dictionary of query variables does not contain the variable " + tp.getObject().toString() + "." );
		}

		if (    (!sIsVar && graph.getId(tp.getSubject()) == -1)
		     || (!pIsVar && graph.getId(tp.getPredicate()) == -1)
		     || (!oIsVar && graph.getId(tp.getObject()) == -1) ) {
			return null;
		}

		return new EncodedTriplePattern( sIsVar, (sIsVar) ? varsDict.get(tp.getSubject()).intValue() : graph.getId(tp.getSubject()),
		                                 pIsVar, (pIsVar) ? varsDict.get(tp.getPredicate()).intValue() : graph.getId(tp.getPredicate()),
		                                 oIsVar, (oIsVar) ? varsDict.get(tp.getObject()).intValue() : graph.getId(tp.getObject()) );
	}


	/**
	 * Decodes the given encoded solution by using the node dictionary of the
	 * queried RDF graph.
	 */
	final protected Domain decode ( EncodedSolution es )
	{
		int size = es.size();
		Domain d = new Domain( size );
		for ( int i = 0; i < size; ++i ) {
			d.setElement( i, graph.getNode(es.getBoundId(i)) );
		}
		return d;
	}


	// helper classes

	/**
	 * Converts the encoded solutions provided by the input iterator to Domain
	 * objects as required by the Jena.
	 */
	class ConvertEncodedSolutionToDomainIterator extends NiceIterator
	{
		final protected Iterator<EncodedSolution> base;
		public ConvertEncodedSolutionToDomainIterator ( Iterator<EncodedSolution> base ) { this.base = base; }
		public boolean hasNext () { return base.hasNext(); }
		public Domain next () { return decode( base.next() ); }
	}


	/**
	 * Represents a triple pattern by identifiers for the RDF nodes as used by
	 * the queried graph and by identifiers for the query variables as provided
	 * to the query plan.
	 */
	static public class EncodedTriplePattern
	{
		/** designates whether the identifier of the subject represents an RDF node or a query variable */
		final public boolean sIsVar;
		/** designates whether the identifier of the predicate represents an RDF node or a query variable */
		final public boolean pIsVar;
		/** designates whether the identifier of the object represents an RDF node or a query variable */
		final public boolean oIsVar;
		/** the identifier for the subject */
		final public int s;
		/** the identifier for the predicate */
		final public int p;
		/** the identifier for the object */
		final public int o;

		public EncodedTriplePattern ( boolean sIsVar, int s, boolean pIsVar, int p, boolean oIsVar, int o ) {
			this.sIsVar = sIsVar; this.s = s;
			this.pIsVar = pIsVar; this.p = p;
			this.oIsVar = oIsVar; this.o = o;
		}

		@Override
		public String toString () {
			return "EncodedTriplePattern(" + (sIsVar?"v":"n") + String.valueOf(s) + "," + (pIsVar?"v":"n") + String.valueOf(p) + "," + (oIsVar?"v":"n") + String.valueOf(o) + ")";
		}
	}


	/**
	 * Represents a solution mapping with identifiers for the RDF nodes bound to
	 * the query variables.
	 */
	static public class EncodedSolution
	{
		final static protected int UNBOUND = -1;
		final protected int[] bindings;

		/** Creates an empty solution mapping for the given number of query variables. */
		public EncodedSolution ( int size )
		{
			bindings = new int[size];
			for ( int i = 0; i < size; ++i ) {
				bindings[i] = UNBOUND;
			}
		}

		/** Creates a solution mapping that is a copy of the given mapping. */
		public EncodedSolution ( EncodedSolution template )
		{
			int size = template.bindings.length;
			bindings = new int[size];
			for ( int i = 0; i < size; ++i ) {
				bindings[i] = template.bindings[i];
			}
		}

		/**
		 * Returns the size of this solution mapping (ie the number of query
		 * variables this mapping has been created for).
		 */
		public int size ()
		{
			return bindings.length;
		}

		/**
		 * Returns true if the query variable identified by the given ID is bound
		 * to an RDF node in this solution mapping.
		 */
		public boolean isBound ( int varId )
		{
			return bindings[varId] != UNBOUND;
		}

		/**
		 * Returns the identifier of the RDF node to which the query variable
		 * identified by the given ID is bound in this solution mapping.
		 */
		public int getBoundId ( int varId )
		{
			return bindings[varId];
		}

		/**
		 * Binds the query variable identified by the given ID to the RDF node
		 * identified by the second ID.
		 */
		public void setBoundId ( int varId, int nodeId )
		{
			assert bindings[varId] == UNBOUND || bindings[varId] == nodeId;
			bindings[varId] = nodeId;
		}

		@Override
		public String toString ()
		{
			String result = "EncodedSolution(";
			for ( int i = 0; i < bindings.length; ++i ) {
				result += " " + String.valueOf(i) + " => " + ( bindings[i]==UNBOUND ? "unbound" : String.valueOf(bindings[i]) );
			}
			result += " )";
			return result;
		}
	}


	/**
	 * Base class for all query iterators that provide encoded solutions
	 * (see {@link EncodedSolution}).
	 */
	static abstract class IdBasedQueryIterator implements Iterator<EncodedSolution>
	{
		public void remove () { throw new UnsupportedOperationException(); }
	}

	/**
	 * A query iterator that provides exactly one encoded solution.
	 */
	static class IdBasedQItSingleton extends IdBasedQueryIterator
	{
		final protected EncodedSolution singleSolution;
		protected boolean delivered = false;
		public IdBasedQItSingleton ( EncodedSolution singleSolution ) { this.singleSolution = singleSolution; }
		public boolean hasNext () { return ! delivered; }
		public EncodedSolution next () { delivered = true; return singleSolution; }
	}

	/**
	 * A query iterator that provides no encoded solution.
	 */
	static class IdBasedQItEmpty extends IdBasedQueryIterator
	{
		public boolean hasNext () { return false; }
		public EncodedSolution next () { throw new NoSuchElementException(); }
	}

	/**
	 * Abstract base class for all query iterators the consume encoded solutions
	 * from an input interator.
	 */
	static abstract class IdBasedQItInput1 extends IdBasedQueryIterator
	{
		final protected IdBasedQueryIterator input;
		protected IdBasedQItInput1 ( IdBasedQueryIterator input ) { this.input = input; }
	}

	/**
	 * A query iterator that provides encoded solutions for a triple pattern and
	 * that are compatible with the solutions provided by an input operator.
	 * This iterator can be used for a pipelined evaluation of a set of triple
	 * patterns.
	 */
	static class IdBasedTriplePatternMatcher extends IdBasedQItInput1
	{
		/** the triple pattern matched by this iterator */
		final protected EncodedTriplePattern tp;
		/** the queried RDF graph */
		final protected IdBasedGraph graph;

		/** the solution currently consumed from the input iterator */
		protected EncodedSolution currentInputSolution = null;

		/**
		 * The current query pattern is the triple pattern of this iterator
		 * (see {@link #tp} substituted with the bindings provided by the
		 * current solution consumed from the input iterator (ie by
		 * {@link #currentInputSolution}).
		 */
		protected EncodedTriplePattern currentQueryPattern = null;

		/**
		 * an iterator over all triples that match the current query pattern
		 * (see {@link #currentQueryPattern}) in the queried RDF graph
		 */
		protected Iterator<EncodedTriple> currentMatches = null;

		public IdBasedTriplePatternMatcher ( IdBasedGraph graph, EncodedTriplePattern tp, IdBasedQueryIterator input )
		{
			super( input );
			this.graph = graph;
			this.tp = tp;
		}

		public boolean hasNext ()
		{
			while ( currentMatches == null || ! currentMatches.hasNext() )
			{
				if ( ! input.hasNext() ) {
					return false;
				}

				currentInputSolution = input.next();
				currentQueryPattern = substitute( tp, currentInputSolution );
				currentMatches = graph.find( (currentQueryPattern.sIsVar) ? -1 : currentQueryPattern.s,
				                             (currentQueryPattern.pIsVar) ? -1 : currentQueryPattern.p,
				                             (currentQueryPattern.oIsVar) ? -1 : currentQueryPattern.o );
			}

			return true;
		}

		public EncodedSolution next ()
		{
			if ( ! hasNext() ) {
				throw new NoSuchElementException();
			}

			// Create the next solution i) by copying the solution currently
			// consumed from the input iterator and ii) by binding the query
			// variables in the copy corresponding to the currently matching
			// triple.
			EncodedTriple currentMatch = currentMatches.next();
			EncodedSolution result = new EncodedSolution( currentInputSolution );

			if ( currentQueryPattern.sIsVar ) {
				result.setBoundId( currentQueryPattern.s, currentMatch.s );
			}

			if ( currentQueryPattern.pIsVar ) {
				result.setBoundId( currentQueryPattern.p, currentMatch.p );
			}

			if ( currentQueryPattern.oIsVar ) {
				result.setBoundId( currentQueryPattern.o, currentMatch.o );
			}

			return result;
		}

		/**
		 * Replaces all query variables in the given triple pattern that have
		 * bindings in the given solution mapping by these bindings.
		 */
		static public EncodedTriplePattern substitute ( EncodedTriplePattern tp, EncodedSolution solution )
		{
			int sNew, pNew, oNew;
			boolean sIsVarNew, pIsVarNew, oIsVarNew;
			boolean isBound;
			if ( tp.sIsVar )
			{
				isBound = solution.isBound( tp.s );
				sNew = ( isBound ) ? solution.getBoundId(tp.s) : tp.s;
				sIsVarNew = ( ! isBound );
			}
			else
			{
				sNew = tp.s;
				sIsVarNew = false;
			}

			if ( tp.pIsVar )
			{
				isBound = solution.isBound( tp.p );
				pNew = ( isBound ) ? solution.getBoundId(tp.p) : tp.p;
				pIsVarNew = ( ! isBound );
			}
			else
			{
				pNew = tp.p;
				pIsVarNew = false;
			}

			if ( tp.oIsVar )
			{
				isBound = solution.isBound( tp.o );
				oNew = ( isBound ) ? solution.getBoundId(tp.o) : tp.o;
				oIsVarNew = ( ! isBound );
			}
			else
			{
				oNew = tp.o;
				oIsVarNew = false;
			}

			return new EncodedTriplePattern( sIsVarNew, sNew, pIsVarNew, pNew, oIsVarNew, oNew );
		}
	}

}

/*
 * (c) Copyright 2006 - 2009 Christian Bizer (chris@bizer.de)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */