/*
 * Created on 24-Nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.signature.impl;

import java.util.ArrayList;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.signature.SWPWarrant;

/**
 * @author rowland
 *
 * Declarative Systems & Software Engineering Group,
 * School of Electronics & Computer Science,
 * University of Southampton,
 * Southampton,
 * SO17 1BJ
 */
public class SWPNamedGraphImpl 
implements SWPNamedGraph
{

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraph#swpAssert(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, java.util.ArrayList)
     */
    public boolean swpAssert(SWPAuthority authority, ArrayList listOfAuthorityProperties) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraph#assertWithSignature(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, com.hp.hpl.jena.graph.Node, java.util.ArrayList)
     */
    public boolean assertWithSignature(SWPAuthority authority, Node signatureMethod, ArrayList listOfAuthorityProperties) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraph#getWarrants()
     */
    public SWPWarrant[] getWarrants() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraph#getWarrantsWithVerifyableSignature()
     */
    public SWPWarrant[] getWarrantsWithVerifyableSignature() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraph#getAssertingAuthorities()
     */
    public SWPAuthority[] getAssertingAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraph#getQuotingAuthorities()
     */
    public SWPAuthority[] getQuotingAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraph#getAssertingAuthoritiesWithVerifyableSignature()
     */
    public SWPAuthority[] getAssertingAuthoritiesWithVerifyableSignature() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraph#getGraphName()
     */
    public Node getGraphName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#dependsOn(com.hp.hpl.jena.graph.Graph)
     */
    public boolean dependsOn(Graph arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#queryHandler()
     */
    public QueryHandler queryHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#getTransactionHandler()
     */
    public TransactionHandler getTransactionHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#getBulkUpdateHandler()
     */
    public BulkUpdateHandler getBulkUpdateHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#getCapabilities()
     */
    public Capabilities getCapabilities() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#getEventManager()
     */
    public GraphEventManager getEventManager() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#getReifier()
     */
    public Reifier getReifier() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#getPrefixMapping()
     */
    public PrefixMapping getPrefixMapping() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#delete(com.hp.hpl.jena.graph.Triple)
     */
    public void delete(Triple arg0) throws DeleteDeniedException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#find(com.hp.hpl.jena.graph.TripleMatch)
     */
    public ExtendedIterator find(TripleMatch arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#find(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
     */
    public ExtendedIterator find(Node arg0, Node arg1, Node arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#isIsomorphicWith(com.hp.hpl.jena.graph.Graph)
     */
    public boolean isIsomorphicWith(Graph arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#contains(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
     */
    public boolean contains(Node arg0, Node arg1, Node arg2) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#contains(com.hp.hpl.jena.graph.Triple)
     */
    public boolean contains(Triple arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#close()
     */
    public void close() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#isEmpty()
     */
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.Graph#size()
     */
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.GraphAdd#add(com.hp.hpl.jena.graph.Triple)
     */
    public void add(Triple arg0) throws AddDeniedException {
        // TODO Auto-generated method stub
        
    }

}

/*
 *  (c)   Copyright 2004 Rowland Watkins (rowland@grid.cx) & University of 
 * 		  Southampton, Declarative Systems and Software Engineering Research 
 *        Group, University of Southampton, Highfield, SO17 1BJ
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