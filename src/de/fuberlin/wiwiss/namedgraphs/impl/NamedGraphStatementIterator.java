// $Id: NamedGraphStatementIterator.java,v 1.1 2004/09/13 14:37:27 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs.impl;

import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl;

import de.fuberlin.wiwiss.namedgraphs.NamedGraphModel;
import de.fuberlin.wiwiss.namedgraphs.NamedGraphStatement;

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
	    
	public Object next() {
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
