/*
 * Created on 24-Nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.signature.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.rdf.model.Resource;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet;

/**
 * @author rowland
 *
 * Declarative Systems & Software Engineering Group,
 * School of Electronics & Computer Science,
 * University of Southampton,
 * Southampton,
 * SO17 1BJ
 * 
 * Basd on de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl 
 * by Chris Bizer. 
 * 
 */
public class SWPNamedGraphSetImpl implements SWPNamedGraphSet 
{
    /** Map from names (Node) to NamedGraphs */
	private Map namesToGraphsMap = new HashMap();
	
	/**
	 * List of all NamedGraphs that backs the UnionGraphs handed
	 * out by {@link #asJenaGraph(Node)}.
	 * This whole graphs List affair is probably rather slow.
	 */
	private List graphs = new ArrayList();
    
    
    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.dsig.SWPNamedGraphSet#swpAssert(de.fuberlin.wiwiss.ng4j.dsig.SWPAuthority)
     */
    public boolean swpAssert(SWPAuthority authority) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.dsig.SWPNamedGraphSet#swpQuote(de.fuberlin.wiwiss.ng4j.dsig.SWPAuthority)
     */
    public boolean swpQuote(SWPAuthority authority) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.dsig.SWPNamedGraphSet#assertWithSignature(de.fuberlin.wiwiss.ng4j.dsig.SWPAuthority, com.hp.hpl.jena.graph.Node)
     */
    public boolean assertWithSignature(SWPAuthority authority, Node signatureMethod) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.dsig.SWPNamedGraphSet#quoteWithSignature(de.fuberlin.wiwiss.ng4j.dsig.SWPAuthority, com.hp.hpl.jena.graph.Node)
     */
    public boolean quoteWithSignature(SWPAuthority authority, Node signatureMethod) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.dsig.SWPNamedGraphSet#assertGraphs(java.util.ArrayList, de.fuberlin.wiwiss.ng4j.dsig.SWPAuthority)
     */
    public boolean assertGraphs(ArrayList listOfGraphURIs, SWPAuthority authority) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.dsig.SWPNamedGraphSet#assertGraphsWithSignature(java.util.ArrayList, de.fuberlin.wiwiss.ng4j.dsig.SWPAuthority)
     */
    public boolean assertGraphsWithSignature(ArrayList listOfGraphURIs, SWPAuthority authority) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.dsig.SWPNamedGraphSet#verifyAllSignatures()
     */
    public boolean verifyAllSignatures() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#addGraph(de.fuberlin.wiwiss.ng4j.NamedGraph)
     */
    public void addGraph(NamedGraph arg0) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#removeGraph(com.hp.hpl.jena.graph.Node)
     */
    public void removeGraph(Node arg0) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#removeGraph(java.lang.String)
     */
    public void removeGraph(String arg0) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#containsGraph(com.hp.hpl.jena.graph.Node)
     */
    public boolean containsGraph(Node arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#containsGraph(java.lang.String)
     */
    public boolean containsGraph(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#getGraph(com.hp.hpl.jena.graph.Node)
     */
    public NamedGraph getGraph(Node arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#getGraph(java.lang.String)
     */
    public NamedGraph getGraph(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#createGraph(com.hp.hpl.jena.graph.Node)
     */
    public NamedGraph createGraph(Node arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#createGraph(java.lang.String)
     */
    public NamedGraph createGraph(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#listGraphs()
     */
    public Iterator listGraphs() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#countGraphs()
     */
    public long countGraphs() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#isEmpty()
     */
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#addQuad(de.fuberlin.wiwiss.ng4j.Quad)
     */
    public void addQuad(Quad arg0) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#containsQuad(de.fuberlin.wiwiss.ng4j.Quad)
     */
    public boolean containsQuad(Quad arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#removeQuad(de.fuberlin.wiwiss.ng4j.Quad)
     */
    public void removeQuad(Quad arg0) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#countQuads()
     */
    public int countQuads() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#findQuads(de.fuberlin.wiwiss.ng4j.Quad)
     */
    public Iterator findQuads(Quad arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#findQuads(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
     */
    public Iterator findQuads(Node arg0, Node arg1, Node arg2, Node arg3) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#asJenaGraph(com.hp.hpl.jena.graph.Node)
     */
    public Graph asJenaGraph(Node arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#asJenaModel(java.lang.String)
     */
    public NamedGraphModel asJenaModel(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#close()
     */
    public void close() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#read(java.lang.String, java.lang.String)
     */
    public void read(String arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#read(java.io.InputStream, java.lang.String, java.lang.String)
     */
    public void read(InputStream arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#read(java.io.Reader, java.lang.String, java.lang.String)
     */
    public void read(Reader arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#write(java.io.OutputStream, java.lang.String)
     */
    public void write(OutputStream arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#write(java.io.Writer, java.lang.String)
     */
    public void write(Writer arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }
    
    /**
	 * Subclass of {@link MultiUnion} that allows the list of member
	 * graphs to be directly passed to the constructor. When we later
	 * change the list (add or remove graphs from the NamedGraphSet),
	 * the member list of the MultiUnion is automatically updated.
	 * <p>
	 * Note: This is a hack.
	 */
	private class UnionGraph extends MultiUnion {
		public UnionGraph (List members) {
			super();
			this.m_subGraphs = members;
		}

		/**
		 * MultiUnion deletes from the baseGraph only; we want to
		 * delete from all member graphs
		 */
		public void delete(Triple t) {
			Iterator it = this.m_subGraphs.iterator();
			while (it.hasNext()) {
				Graph member = (Graph) it.next();
				member.delete(t);
			}
		}
	}

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.dsig.SWPNamedGraphSet#assertWithSignature(de.fuberlin.wiwiss.ng4j.dsig.SWPAuthority, com.hp.hpl.jena.rdf.model.Resource)
     */
    public boolean assertWithSignature(SWPAuthority authority, Resource signatureMethod) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.dsig.SWPNamedGraphSet#quoteWithSignature(de.fuberlin.wiwiss.ng4j.dsig.SWPAuthority, com.hp.hpl.jena.rdf.model.Resource)
     */
    public boolean quoteWithSignature(SWPAuthority authority, Resource signatureMethod) {
        // TODO Auto-generated method stub
        return false;
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