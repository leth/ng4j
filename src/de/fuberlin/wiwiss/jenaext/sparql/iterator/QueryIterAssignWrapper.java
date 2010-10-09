package de.fuberlin.wiwiss.jenaext.sparql.iterator;

import java.util.Iterator;

import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterAssign;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterProcessBinding;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

import de.fuberlin.wiwiss.jenaext.IdBasedGraph;
import de.fuberlin.wiwiss.jenaext.NodeDictionary;
import de.fuberlin.wiwiss.jenaext.sparql.IdBasedExecutionContext;


/**
 * This iterator wraps a {@link com.hp.hpl.jena.sparql.engine.iterator.QueryIterAssign}
 * iterator to ensure the assigned values are in the node dictionary.
 *
 * @author Olaf Hartig
 */
public class QueryIterAssignWrapper extends QueryIterProcessBinding
{
	// initialization

	public QueryIterAssignWrapper( QueryIterAssign input, IdBasedExecutionContext execCxt )
	{
		super( input, execCxt );
	}


	@Override
	public Binding accept ( Binding b )
	{
		NodeDictionary nodeDict = ( (IdBasedGraph) getExecContext().getActiveGraph() ).getNodeDictionary();
		Iterator<Var> itVar = b.vars();
		while ( itVar.hasNext() ) {
			nodeDict.createId( b.get(itVar.next()) );
		}

		return b;
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