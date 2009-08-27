package de.fuberlin.wiwiss.jenaext.sparql.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.fuberlin.wiwiss.jenaext.IdBasedGraph;
import de.fuberlin.wiwiss.jenaext.IdBasedTriple;
import de.fuberlin.wiwiss.jenaext.impl.IdBasedQueryPlan.IdBasedTriplePattern;
import de.fuberlin.wiwiss.jenaext.sparql.IdBasedBinding;
import de.fuberlin.wiwiss.jenaext.sparql.IdBasedExecutionContext;


/**
 * A query iterator that provides ID-based bindings for an ID-based triple
 * pattern which are compatible with the bindings provided by an input
 * operator.
 *
 * @author Olaf Hartig
 */
public class IdBasedTriplePatternQueryIter implements Iterator<IdBasedBinding>
{
	// members

	final protected IdBasedExecutionContext execCxt;

	/** the input iterator consumed by this one */
	final protected Iterator<IdBasedBinding> input;

	/** the triple pattern matched by this iterator */
	final protected IdBasedTriplePattern tp;

	/** the binding currently consumed from the input iterator */
	protected IdBasedBinding currentInputBinding = null;

	/**
	 * The current query pattern is the triple pattern of this iterator
	 * (see {@link #tp} substituted with the bindings provided by the
	 * current binding consumed from the input iterator (ie by
	 * {@link #currentInputBinding}).
	 */
	protected IdBasedTriplePattern currentQueryPattern = null;

	/**
	 * an iterator over all triples that match the current query pattern
	 * (see {@link #currentQueryPattern}) in the queried RDF graph
	 */
	protected Iterator<IdBasedTriple> currentMatches = null;


	// initialization

	public IdBasedTriplePatternQueryIter ( IdBasedTriplePattern tp, Iterator<IdBasedBinding> input, IdBasedExecutionContext execCxt )
	{
		this.tp = tp;
		this.input = input;
		this.execCxt = execCxt;
	}

	// implementation of the Iterator interface

	public boolean hasNext ()
	{
		while ( currentMatches == null || ! currentMatches.hasNext() )
		{
			if ( ! input.hasNext() ) {
				return false;
			}

			IdBasedGraph queriedGraph = (IdBasedGraph) execCxt.getActiveGraph();

			currentInputBinding = input.next();
			currentQueryPattern = substitute( tp, currentInputBinding );
			currentMatches = queriedGraph.find( (currentQueryPattern.sIsVar) ? -1 : currentQueryPattern.s,
			                                    (currentQueryPattern.pIsVar) ? -1 : currentQueryPattern.p,
			                                    (currentQueryPattern.oIsVar) ? -1 : currentQueryPattern.o );
		}

		return true;
	}

	public IdBasedBinding next ()
	{
		if ( ! hasNext() ) {
			throw new NoSuchElementException();
		}

		// Create the next binding i) by copying the binding currently
		// consumed from the input iterator and ii) by binding the query
		// variables in the copy corresponding to the currently matching
		// triple.
		IdBasedTriple currentMatch = currentMatches.next();
		IdBasedBinding result = new IdBasedBinding( currentInputBinding );

		if ( currentQueryPattern.sIsVar ) {
			result.set( currentQueryPattern.s, currentMatch.s );
		}

		if ( currentQueryPattern.pIsVar ) {
			result.set( currentQueryPattern.p, currentMatch.p );
		}

		if ( currentQueryPattern.oIsVar ) {
			result.set( currentQueryPattern.o, currentMatch.o );
		}

		return result;
	}

	public void remove ()
	{
		throw new UnsupportedOperationException();
	}


	// helper methods

	/**
	 * Replaces each query variable in the given triple pattern that is bound to
	 * a value in the given binding by this value.
	 */
	static public IdBasedTriplePattern substitute ( IdBasedTriplePattern tp, IdBasedBinding b )
	{
		int sNew, pNew, oNew;
		boolean sIsVarNew, pIsVarNew, oIsVarNew;
		boolean isBound;
		if ( tp.sIsVar )
		{
			isBound = b.contains( tp.s );
			sNew = ( isBound ) ? b.get(tp.s) : tp.s;
			sIsVarNew = ( ! isBound );
		}
		else
		{
			sNew = tp.s;
			sIsVarNew = false;
		}

		if ( tp.pIsVar )
		{
			isBound = b.contains( tp.p );
			pNew = ( isBound ) ? b.get(tp.p) : tp.p;
			pIsVarNew = ( ! isBound );
		}
		else
		{
			pNew = tp.p;
			pIsVarNew = false;
		}

		if ( tp.oIsVar )
		{
			isBound = b.contains( tp.o );
			oNew = ( isBound ) ? b.get(tp.o) : tp.o;
			oIsVarNew = ( ! isBound );
		}
		else
		{
			oNew = tp.o;
			oIsVarNew = false;
		}

		return new IdBasedTriplePattern( sIsVarNew, sNew, pIsVarNew, pNew, oIsVarNew, oNew );
	}

}

/*
 * (c) Copyright 2009 Christian Bizer (chris@bizer.de)
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