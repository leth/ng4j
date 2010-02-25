// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/SingleNamedGraphStatement.java,v 1.2 2010/02/25 14:28:21 hartig Exp $

package de.fuberlin.wiwiss.ng4j;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;

/** A Statement which can provide information about the {@link NamedGraph} 
 * in which it is contained.  SingleNamedGraphStatement's are used in 
 * {@link SingleNamedGraphModel}s.  Those are backed by a NamedGraph.
 * The SingleNamedGraphStatement knows in which graph it is contained. <p>
 * 
 * "Modeled" after NamedGraphStatement.
 * 
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class SingleNamedGraphStatement extends StatementImpl {

	public SingleNamedGraphStatement(Resource subject, Property predicate,
			RDFNode object, SingleNamedGraphModel model) {
		
		super(subject, predicate, object, model);
	}

	/*
	 *  (c)   Copyright 2009 - 2010 Christian Bizer (chris@bizer.de)
	 *   All rights reserved.
	 *
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions
	 * are met:
	 * 1. Redistributions of source code must retain the above copyright
	 *    notice, this list of conditions and the following disclaimer.
	 * 2. Redistributions in binary form must reproduce the above copyright
	 *    notice, this list of conditions and the following disclaimer in the
	 *    documentation and/or other materials provided with the distribution.
	 * 3. The name of the author may not be used to endorse or promote products
	 *    derived from this software without specific prior written permission.
	 *
	 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
	 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
	 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
	 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
	 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
	 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
	 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
	 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
	 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
	 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 *
	 */
}
