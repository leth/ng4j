// $Id: GraphPattern.java,v 1.3 2004/11/26 02:42:55 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdql.QueryException;

/**
 * A graph pattern in a TriQL query, consisting of a graph name pattern and
 * a collection of triple patterns.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphPattern {
	private Node name;
	private List triples = new ArrayList();

	public GraphPattern(Node name) {
		this.name = name;
	}
	
	public void addTriplePattern(Node s, Node p, Node o) {
		if (areSameVariable(s, p) || areSameVariable(s, o) || areSameVariable(p, o)) {
			throw new QueryException("Currently, multiple occurences of the same " +
					"variable in the same TriQL triple pattern are not supported. " +
					"If you need this, please tell us.");
		}
		this.triples.add(new Triple(s, p, o));
	}
	
	public Node getName() {
		return this.name;
	}
	
	public int getTripleCount() {
		return this.triples.size();
	}
	
	public Triple getTriple(int index) {
		return (Triple) this.triples.get(index);
	}
	
	private boolean areSameVariable(Node v1, Node v2) {
		return v1.isVariable() && v2.isVariable() && v1.getName().equals(v2.getName());
	}
}

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
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