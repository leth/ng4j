/*
 * Created on 24-Nov-2004
 */
package de.fuberlin.wiwiss.ng4j.swp.impl;

import java.util.ArrayList;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;

import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.SWPWarrant;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;

/**
 * @author Chris Bizer
 */
public class SWPNamedGraphImpl extends NamedGraphImpl implements SWPNamedGraph {

	public SWPNamedGraphImpl(Node graphName, Graph graph) {
		super(graphName, graph);
	}

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph#swpAssert(de.fuberlin.wiwiss.ng4j.swp.SWPAuthority, java.util.ArrayList)
     */
    public boolean swpAssert(SWPAuthority authority, ArrayList<Node> listOfAuthorityProperties) {

		// Check if the graph is already a warrant graph.
		if (!this.contains(this.getGraphName(), SWP.assertedBy, this.getGraphName()) &&
            !this.contains(this.getGraphName(), SWP.quotedBy, this.getGraphName())) {

			// Graph is no warrant graph => make it a warrant graph
            this.add(new Triple(this.getGraphName(), SWP.assertedBy, this.getGraphName()));
            // Add a description of the authorty to the graph
            authority.addDescriptionToGraph(this, listOfAuthorityProperties);
			return true;
        } else {
            // Graph is already a warrant graph
			return false ;
        }
    }

    public boolean swpAssert(SWPAuthority authority ) {
			return swpAssert(authority, new ArrayList<Node>());
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph#swpQuote(de.fuberlin.wiwiss.ng4j.swp.SWPAuthority, java.util.ArrayList)
     */
    public boolean swpQuote(SWPAuthority authority, ArrayList<Node> listOfAuthorityProperties) {
        // Check if the graph is already a warrant graph.
		if (!this.contains(this.getGraphName(), SWP.assertedBy, this.getGraphName()) &&
            !this.contains(this.getGraphName(), SWP.quotedBy, this.getGraphName())) {

			// Graph is no warrant graph => make it a warrant graph
            this.add(new Triple(this.getGraphName(), SWP.quotedBy, this.getGraphName()));
			// Add a description of the authorty to the graph
            authority.addDescriptionToGraph(this, listOfAuthorityProperties);
			return true;
        } else {
            // Graph is already a warrant graph
			return false ;
        }
    }

    public boolean swpQuote(SWPAuthority authority ) {
			return swpQuote(authority, new ArrayList<Node>());
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph#assertWithSignature(de.fuberlin.wiwiss.ng4j.swp.SWPAuthority, com.hp.hpl.jena.graph.Node, java.util.ArrayList)
     */
    public boolean assertWithSignature(SWPAuthority authority, Node signatureMethod, ArrayList<Node> listOfAuthorityProperties) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph#getWarrants()
     */
    public SWPWarrant[] getWarrants() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph#getWarrantsWithVerifyableSignature()
     */
    public SWPWarrant[] getWarrantsWithVerifyableSignature() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph#getAssertingAuthorities()
     */
    public SWPAuthority[] getAssertingAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph#getQuotingAuthorities()
     */
    public SWPAuthority[] getQuotingAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph#getAssertingAuthoritiesWithVerifyableSignature()
     */
    public SWPAuthority[] getAssertingAuthoritiesWithVerifyableSignature() {
        // TODO Auto-generated method stub
        return null;
    }

}

/*
 *  (c)   Copyright 2004 - 2009 Chris Bizer (chris@bizer.de) & Rowland Watkins (rowland@grid.cx)
 *   	  All rights reserved.
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