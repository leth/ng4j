package de.fuberlin.wiwiss.jenaext.sparql.iterator;

import java.util.Iterator;

import org.openjena.atlas.lib.Closeable;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIter;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;

import de.fuberlin.wiwiss.jenaext.IdBasedGraph;
import de.fuberlin.wiwiss.jenaext.NodeDictionary;
import de.fuberlin.wiwiss.jenaext.sparql.IdBasedBinding;
import de.fuberlin.wiwiss.jenaext.sparql.IdBasedExecutionContext;
import de.fuberlin.wiwiss.jenaext.sparql.VarDictionary;


/**
 * This iterator converts {@link IdBasedBinding}s to
 * {@link com.hp.hpl.jena.sparql.engine.binding.Binding}s.
 *
 * @author Olaf Hartig
 */
public class DecodeBindingsIterator extends QueryIter
{
	// members

	final protected VarDictionary varDict;

	/** the input iterator consumed by this one */
	final protected Iterator<IdBasedBinding> input;


	// initialization

	public DecodeBindingsIterator ( Iterator<IdBasedBinding> input, IdBasedExecutionContext execCxt )
	{
		super( execCxt );

		this.input = input;
		this.varDict = execCxt.getVarDictionary();
	}

	// implementation of the QueryIteratorBase abstract methods

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorBase#hasNextBinding()
	 */
	@Override
	protected boolean hasNextBinding ()
	{
		return input.hasNext();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorBase#moveToNextBinding()
	 */
	@Override
	protected Binding moveToNextBinding ()
	{
		IdBasedBinding curInput = input.next();

		NodeDictionary nodeDict = ( (IdBasedGraph) getExecContext().getActiveGraph() ).getNodeDictionary();

		BindingMap curOutput = new BindingHashMap();
		for ( int i = curInput.size() - 1; i >= 0; i-- )
		{
			if ( curInput.contains(i) ) {
				curOutput.add( varDict.getVar(i),
				               nodeDict.getNode(curInput.get(i)) );
			}
		}

		return curOutput;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorBase#requestCancel()
	 */
	@Override
	protected void requestCancel ()
	{
		// do nothing
		// May be we have to cancel the (chain of) input iterator(s) ?
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorBase#closeIterator()
	 */
	@Override
	protected void closeIterator ()
	{
		if ( input instanceof Closeable ) {
			( (Closeable) input ).close();
		}
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