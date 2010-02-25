package de.fuberlin.wiwiss.jenaext.sparql;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Triple;

import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.main.OpExecutor;
import com.hp.hpl.jena.sparql.engine.main.OpExecutorFactory;

import de.fuberlin.wiwiss.jenaext.IdBasedGraph;
import de.fuberlin.wiwiss.jenaext.NodeDictionary;
import de.fuberlin.wiwiss.jenaext.impl.IdBasedQueryPlan.IdBasedTriplePattern;
import de.fuberlin.wiwiss.jenaext.sparql.iterator.DecodeBindingsIterator;
import de.fuberlin.wiwiss.jenaext.sparql.iterator.EncodeBindingsIterator;
import de.fuberlin.wiwiss.jenaext.sparql.iterator.IdBasedTriplePatternQueryIter;


/**
 * A {@link com.hp.hpl.jena.sparql.engine.main.OpExecutor} implementation for
 * RDF graph implementations which use identifiers for RDF nodes (i.e. for
 * {@link IdBasedGraph} implementations).
 *
 * @author Olaf Hartig
 */
public class IdBasedOpExecutor extends OpExecutor
{
	/**
	 * The factory object that creates this OpExecutor implementation.
	 */
	static final public OpExecutorFactory factory = new OpExecutorFactory()
	{
		public OpExecutor create( ExecutionContext execCxt )
		{
			return new IdBasedOpExecutor( (IdBasedExecutionContext) execCxt );
		}
	};


	/**
	 * Creates an operator compiler.
	 */
	public IdBasedOpExecutor ( IdBasedExecutionContext execCxt )
	{
		super( execCxt );
	}


	// operations

	@Override
	public QueryIterator execute ( OpBGP opBGP, QueryIterator input )
	{
		if (    opBGP.getPattern().isEmpty()
		     || ! (execCxt.getDataset().getDefaultGraph() instanceof IdBasedGraph) )
		{
			return super.execute( opBGP, input );
		}

		VarDictionary varDict = ( (IdBasedExecutionContext) execCxt ).getVarDictionary();
		NodeDictionary nodeDict = ( (IdBasedGraph) execCxt.getDataset().getDefaultGraph() ).getNodeDictionary();

		Iterator<IdBasedBinding> qIt = new EncodeBindingsIterator( input, (IdBasedExecutionContext) execCxt );
		for ( Triple t : opBGP.getPattern().getList() ) {
			qIt = new IdBasedTriplePatternQueryIter( encode(t,varDict,nodeDict),
			                                         qIt,
			                                         (IdBasedExecutionContext) execCxt );
		}

		return new DecodeBindingsIterator( qIt, (IdBasedExecutionContext) execCxt );
	}


	// helper methods

	final protected IdBasedTriplePattern encode ( Triple tp, VarDictionary varDict, NodeDictionary nodeDict )
	{
		boolean sIsVar = Var.isVar( tp.getSubject() );
		boolean pIsVar = Var.isVar( tp.getPredicate() );
		boolean oIsVar = Var.isVar( tp.getObject() );

		return new IdBasedTriplePattern( sIsVar, (sIsVar) ? varDict.getId((Var)tp.getSubject()) : nodeDict.createId(tp.getSubject()),
		                                 pIsVar, (pIsVar) ? varDict.getId((Var)tp.getPredicate()) : nodeDict.createId(tp.getPredicate()),
		                                 oIsVar, (oIsVar) ? varDict.getId((Var)tp.getObject()) : nodeDict.createId(tp.getObject()) );
	}
}

/*
 * (c) Copyright 2009 - 2010 Christian Bizer (chris@bizer.de)
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
