// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/impl/SingleNamedGraphResourceImpl.java,v 1.1 2009/05/27 14:36:53 jenpc Exp $

package de.fuberlin.wiwiss.ng4j.impl;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

import de.fuberlin.wiwiss.ng4j.NamedGraphResource;
import de.fuberlin.wiwiss.ng4j.SingleNamedGraphModel;

/** A resource in a SingleNamedGraphModel. <p>
 * 
 * "Modeled" after NamedGraphResourceImpl but deals with a SingleNamedGraphModel
 * rather than a NamedGraphModel.
 * 
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class SingleNamedGraphResourceImpl extends ResourceImpl implements NamedGraphResource {

	// REVISIT NamedGraphResource currently contains nothing; but if it changed ...
	
	protected SingleNamedGraphModel model;
	
	public SingleNamedGraphResourceImpl(final Resource resource,
			final SingleNamedGraphModel model) {
		
		super(resource, model);
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ResourceImpl#listProperties()
	 */
	@Override
	public StmtIterator listProperties() {
		return new SingleNamedGraphStatementIterator(super.listProperties(),
	            this.model);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ResourceImpl#listProperties(com.hp.hpl.jena.rdf.model.Property)
	 */
	@Override
	public StmtIterator listProperties(Property p) {
		return new SingleNamedGraphStatementIterator(super.listProperties(p),
	            this.model);
	}

	/*
	 *  (c)   Copyright 2009 Christian Bizer (chris@bizer.de)
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
