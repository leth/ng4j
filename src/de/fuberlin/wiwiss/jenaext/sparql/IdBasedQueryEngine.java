package de.fuberlin.wiwiss.jenaext.sparql;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVars;
import com.hp.hpl.jena.sparql.algebra.op.OpModifier;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Substitute;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.Plan;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.QueryEngineFactory;
import com.hp.hpl.jena.sparql.engine.QueryEngineRegistry;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterRoot;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorCheck;
import com.hp.hpl.jena.sparql.engine.main.QueryEngineMain;
import com.hp.hpl.jena.sparql.engine.main.QC;
import com.hp.hpl.jena.sparql.util.Context;

import de.fuberlin.wiwiss.jenaext.IdBasedGraph;


/**
 * A SPARQL query engine for RDF graph implementations which use identifiers
 * for RDF nodes (i.e. for {@link IdBasedGraph} implementations).
 * To use this engine you simply have to register it by calling its
 * {@link #register} method.
 *
 * @author Olaf Hartig
 */
public class IdBasedQueryEngine extends QueryEngineMain
{
	/**
	 * The factory object that creates an {@link IdBasedQueryEngine}.
	 */
	static final private QueryEngineFactory factory = new QueryEngineFactory()
	{
		public boolean accept ( Query query, DatasetGraph ds, Context cxt ) { return isIdBased( ds ); }

		public boolean accept ( Op op, DatasetGraph ds, Context cxt ) { return isIdBased( ds ); }

		public Plan create ( Query query, DatasetGraph dataset, Binding initialBinding, Context context ) {
			IdBasedQueryEngine engine = new IdBasedQueryEngine( query, dataset, initialBinding, context );
			return engine.getPlan();
		}

		public Plan create ( Op op, DatasetGraph dataset, Binding initialBinding, Context context ) {
			IdBasedQueryEngine engine = new IdBasedQueryEngine( op, dataset, initialBinding, context );
			return engine.getPlan();
		}

		private boolean isIdBased ( DatasetGraph ds ) { return ( ds.getDefaultGraph() instanceof IdBasedGraph ); }
	};

	/**
	 * Returns a factory that creates an {@link IdBasedQueryEngine}.
	 */
	static public QueryEngineFactory getFactory () { return factory; }

	/**
	 * Registers this engine so that it can be selected for query execution.
	 */
	static public void register () { QueryEngineRegistry.addFactory( factory ); }

	/**
	 * Unregisters this engine.
	 */
	static public void unregister () { QueryEngineRegistry.removeFactory( factory ); }


	// initialization methods

	public IdBasedQueryEngine ( Op op, DatasetGraph dataset, Binding input, Context context )
	{
		super( op, dataset, input, context );
		registerOpExecutor();
	}

	public IdBasedQueryEngine( Query query, DatasetGraph dataset, Binding input, Context context )
	{
		super( query, dataset, input, context );
		registerOpExecutor();
	}

	private void registerOpExecutor ()
	{
		QC.setFactory( context, IdBasedOpExecutor.factory );
	}


	// operations

	@Override
	public QueryIterator eval ( Op op, DatasetGraph dsg, Binding input, Context context )
	{
		if ( SUBSTITUE && ! input.isEmpty() ) {
			op = Substitute.substitute( op, input );
		}

		VarDictionary varDict = initializeVarDictionary( op );

		ExecutionContext execCxt = new IdBasedExecutionContext( varDict,
		                                                        context,
		                                                        dsg.getDefaultGraph(),
		                                                        dsg,
		                                                        QC.getFactory(context) ) ;
		QueryIterator qIter1 = QueryIterRoot.create( input, execCxt );
		QueryIterator qIter = QC.execute( op, qIter1, execCxt );
		qIter = QueryIteratorCheck.check(qIter, execCxt); // check for closed iterators
		return qIter;
	}

	/**
	 * Creates a dictionary of query variables that knows all variables in the
	 * operator tree of which the given operator is root.
	 */
	final protected VarDictionary initializeVarDictionary ( Op op )
	{
		// We cannot call OpVars.allVars(op) directly because it does not
		// consider all variables in sub-operators of OpProject. Hence,
		// we simply strip the solution modifiers and, thus, call the
		// method for the first operator that is not a solution modifier.
		Op tmp = op;
		while ( tmp instanceof OpModifier ) {
			tmp = ( (OpModifier) tmp ).getSubOp();
		}

		VarDictionary varDict = new VarDictionary();
		for ( Var v : OpVars.allVars(tmp) ) {
			varDict.createId( v );
		}
		return varDict;
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
