// $Id: GraphPattern.java,v 1.7 2005/10/11 20:56:16 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * <p>A graph pattern in a TriQL query, consisting of a graph name pattern and
 * a collection of triple patterns.</p>
 *
 * <p>The graph pattern can be viewed as a list of quad patterns
 * which all have the same graph name. (See {@link #getQuads}.)
 * If the graph name is Node.ANY, then, in the quad view, an
 * anonymous variable (_graphXXXX) will be used as the graph name.
 * This helps distinguishing between quad patterns that are part
 * of different graph pattern.</p>
 * 
 * TODO: Test getAllVariables(), getQuad(...)
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphPattern {
	private Node name;
	private Node anonymouseName = null;
	private static long nextID = 0;
	private List triples = new ArrayList();
	private List quads = null;
	
	public GraphPattern(Node name) {
		this.name = name;
		if (needsAnonymousName()) {
		    assignAnonymousName();
		}
	}
	
	public void addTriplePattern(Triple pattern) {
		this.triples.add(pattern);
		this.quads = null;
	}
	
	public Node getName() {
		return this.name;
	}
	
	public int getTripleCount() {
		return this.triples.size();
	}
	
	public List getTriples() {
	    return this.triples;
	}
	
	public Triple getTriple(int index) {
		return (Triple) this.triples.get(index);
	}
	
	public List getQuads() {
	    ensureQuadsInitialized();
	    return this.quads;
	}
	
	public Quad getQuad(int index) {
	    ensureQuadsInitialized();
	    return (Quad) this.quads.get(index);
	}
	
	public Set getAllVariables() {
	    Set result = new HashSet();
	    if (this.name.isVariable()) {
	        result.add(this.name);
	    }
	    Iterator it = this.triples.iterator();
	    while (it.hasNext()) {
            Triple triple = (Triple) it.next();
            if (triple.getSubject().isVariable()) {
                result.add(triple.getSubject());
            }
            if (triple.getPredicate().isVariable()) {
                result.add(triple.getPredicate());
            }
            if (triple.getObject().isVariable()) {
                result.add(triple.getObject());
            }
        }
	    return result;
	}
	
	private void ensureQuadsInitialized() {
	    if (this.quads != null) {
	        return;
	    }
	    this.quads = new ArrayList(this.triples.size());
	    Iterator it = this.triples.iterator();
	    while (it.hasNext()) {
            Triple triple = (Triple) it.next();
            if (needsAnonymousName()) {
                this.quads.add(new Quad(this.anonymouseName, triple));
            } else {
                this.quads.add(new Quad(this.name, triple));
            }
        }
	}

	private void assignAnonymousName() {
	    this.anonymouseName = Node.createVariable("_graph" + nextID);
	    if (nextID == Long.MAX_VALUE) {
	        nextID = Long.MIN_VALUE;
	    } else {
	        nextID++;
	    }
	}
	
	private boolean needsAnonymousName() {
	    return Node.ANY.equals(this.name);
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