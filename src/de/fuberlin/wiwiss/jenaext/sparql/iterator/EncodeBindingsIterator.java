package de.fuberlin.wiwiss.jenaext.sparql.iterator;

import java.util.Iterator;

import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

import de.fuberlin.wiwiss.jenaext.IdBasedGraph;
import de.fuberlin.wiwiss.jenaext.NodeDictionary;
import de.fuberlin.wiwiss.jenaext.sparql.IdBasedBinding;
import de.fuberlin.wiwiss.jenaext.sparql.IdBasedExecutionContext;
import de.fuberlin.wiwiss.jenaext.sparql.VarDictionary;


/**
 * This iterator converts {@link com.hp.hpl.jena.sparql.engine.binding.Binding}s
 * to {@link IdBasedBinding}s.
 *
 * @author Olaf Hartig
 */
public class EncodeBindingsIterator implements Iterator<IdBasedBinding>
{
	// members

	final protected IdBasedExecutionContext execCxt;

	final protected VarDictionary varDict;

	/** the input iterator consumed by this one */
	final protected QueryIterator input;


	// initialization

	public EncodeBindingsIterator ( QueryIterator input, IdBasedExecutionContext execCxt )
	{
		this.input = input;
		this.execCxt = execCxt;
		this.varDict = execCxt.getVarDictionary();
	}

	// implementation of the Iterator interface

	public boolean hasNext ()
	{
		return input.hasNext();
	}

	public IdBasedBinding next ()
	{
		Binding curInput = input.next();

		NodeDictionary nodeDict = ( (IdBasedGraph) execCxt.getActiveGraph() ).getNodeDictionary();

		IdBasedBinding curOutput = new IdBasedBinding( varDict.size() );
		Iterator<Var> itVar = curInput.vars();
		while ( itVar.hasNext() )
		{
			Var var = itVar.next();
			curOutput.set( varDict.getId(var),
			               nodeDict.getId(curInput.get(var)) );
		}

		return curOutput;
	}

	public void remove ()
	{
		throw new UnsupportedOperationException();
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