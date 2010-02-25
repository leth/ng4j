// $Id: NamedGraphStatementIterator.java,v 1.5 2010/02/25 14:28:21 hartig Exp $
package de.fuberlin.wiwiss.ng4j.impl;

import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl;

import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.NamedGraphStatement;

/**
 * Helper implementation of {@link StmtIterator} that wraps a
 * StmtIterator and returns all of its elements as
 * {@link NamedGraphStatement}s instead of simple Jena Statements.
 * This is somewhat hackish, but I couldn't figure out another way
 * to make sure that NamedGraphModel always returns NamedGraphStatements.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class NamedGraphStatementIterator extends StmtIteratorImpl {
	private NamedGraphModel model;

	public NamedGraphStatementIterator(StmtIterator source, NamedGraphModel model) {
		super(source);
		this.model = model;
	}
	    
	public Statement next() {
		Statement stmt = (Statement) super.next();
		if (stmt instanceof NamedGraphStatement) {
			return stmt;
		}
		return new NamedGraphStatement(
				stmt.getSubject(),
				stmt.getPredicate(),
				stmt.getObject(),
				this.model);
	}
	
	// We don't have to override nextStatement() because it 
}

/*
 *  (c) Copyright 2004 - 2010 Christian Bizer (chris@bizer.de)
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
