/*
 * Created on 24-Nov-2004
*/
package de.fuberlin.wiwiss.ng4j.swp.impl;

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
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphImpl;
import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import de.fuberlin.wiwiss.ng4j.swp.signature.impl.SWPSignatureUtilitiesImpl;

import com.eaio.uuid.UUID;

/**
 * @author Chris Bizer.
 */
public class SWPNamedGraphSetImpl extends NamedGraphSetImpl implements SWPNamedGraphSet
{

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#swpAssert(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, java.util.ArrayList)
     */
    public boolean swpAssert(SWPAuthority authority, ArrayList listOfAuthorityProperties) {
		// Create a new warrant graph.
		SWPNamedGraph warrantGraph = createNewWarrantGraph();
		// Assert all graph in the graphset.
        Iterator graphIterator = this.listGraphs();
        while (graphIterator.hasNext()) {
            NamedGraph currentGraph = (NamedGraph) graphIterator.next();
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.assertedByNode, warrantGraph.getGraphName()));
        }
        // Add a description of the authorty to the warrant graph
        authority.addDescriptionToGraph(warrantGraph, listOfAuthorityProperties);
		// Add warrant graph to graphset
		this.addGraph(warrantGraph);
        return true;
    }

    public boolean swpAssert(SWPAuthority authority) {
        return swpAssert(authority, new ArrayList());
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#swpQuote(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, java.util.ArrayList)
     */
    public boolean swpQuote(SWPAuthority authority, ArrayList listOfAuthorityProperties) {
        // Create a new warrant graph.
		SWPNamedGraph warrantGraph = createNewWarrantGraph();
		// Assert all graph in the graphset.
        Iterator graphIterator = this.listGraphs();
        while (graphIterator.hasNext()) {
            NamedGraph currentGraph = (NamedGraph) graphIterator.next();
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.quotedByNode, warrantGraph.getGraphName()));
        }
        // Add a description of the authorty to the warrant graph
        authority.addDescriptionToGraph(warrantGraph, listOfAuthorityProperties);

		// Add warrant graph to graphset
		this.addGraph(warrantGraph);
        return true;
    }

   public boolean swpQuote(SWPAuthority authority) {
        return swpQuote(authority, new ArrayList());
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#assertGraphs(java.util.ArrayList, de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, java.util.ArrayList)
     */
    public boolean assertGraphs(ArrayList listOfGraphNames, SWPAuthority authority, ArrayList listOfAuthorityProperties) {
        // Create a new warrant graph.
		SWPNamedGraph warrantGraph = createNewWarrantGraph();
		// Assert all graph in the list.
        Iterator graphNameIterator = listOfGraphNames.iterator();
        while (graphNameIterator.hasNext()) {
			Node currentGraphName = (Node) graphNameIterator.next();
            NamedGraph currentGraph = (NamedGraph) this.getGraph(currentGraphName);
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.assertedByNode, warrantGraph.getGraphName()));
        }
        // Add a description of the authorty to the warrant graph
        authority.addDescriptionToGraph(warrantGraph, listOfAuthorityProperties);

		// Add warrant graph to graphset
		this.addGraph(warrantGraph);
        return true;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#assertGraphs(java.util.ArrayList, de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, java.util.ArrayList)
     */
    public boolean quoteGraphs(ArrayList listOfGraphNames, SWPAuthority authority, ArrayList listOfAuthorityProperties) {
        // Create a new warrant graph.
		SWPNamedGraph warrantGraph = createNewWarrantGraph();
		// Assert all graph in the list.
        Iterator graphNameIterator = listOfGraphNames.iterator();
        while (graphNameIterator.hasNext()) {
			Node currentGraphName = (Node) graphNameIterator.next();
            NamedGraph currentGraph = (NamedGraph) this.getGraph(currentGraphName);
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.quotedByNode, warrantGraph.getGraphName()));
        }
        // Add a description of the authorty to the warrant graph
        authority.addDescriptionToGraph(warrantGraph, listOfAuthorityProperties);

		// Add warrant graph to graphset
		this.addGraph(warrantGraph);
        return true;
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

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#assertWithSignature(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, java.util.ArrayList)
     */
    public boolean assertWithSignature(SWPAuthority authority, Node signatureMethod, Node digestMethod, ArrayList listOfAuthorityProperties) {
		// Create a new warrant graph.
		SWPNamedGraph warrantGraph = createNewWarrantGraph();
		// Assert all graph in the graphset.
        Iterator graphIterator = this.listGraphs();
        while (graphIterator.hasNext()) {
            NamedGraph currentGraph = (NamedGraph) graphIterator.next();
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.assertedByNode, warrantGraph.getGraphName()));
			// Add digest for the current graph
            // Rowland: Can't we make SWPSignatureUtilitiesImpl static ?????
            String currentGraphDigest = new SWPSignatureUtilitiesImpl().calculateDigest( currentGraph, digestMethod );
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.digestNode, Node.createLiteral(currentGraphDigest, null, null)));
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.digestMethodNode, digestMethod));
        }
        // Add a description of the authorty to the warrant graph
        authority.addDescriptionToGraph(warrantGraph, listOfAuthorityProperties);

        // Sign the warrant graph now.

        // Can't do because there are no keys.
		// String warrantGraphSignature = new SWPSignatureUtilitiesImpl().calculateSignature( warrantGraph, signatureMethod, authority.getPrivateKey());
        String warrantGraphSignature = "Dummy Signature";
        warrantGraph.add(new Triple(warrantGraph.getGraphName(), SWP.signatureNode, Node.createLiteral(warrantGraphSignature, null, null)));
		warrantGraph.add(new Triple(warrantGraph.getGraphName(), SWP.signatureMethodNode, signatureMethod));

		// Add warrant graph to graphset
		this.addGraph(warrantGraph);
        return true;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#quoteWithSignature(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, java.util.ArrayList)
     */
    public boolean quoteWithSignature(SWPAuthority authority, Node signatureMethod, Node digestMethod, ArrayList listOfAuthorityProperties) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#assertGraphsWithSignature(java.util.ArrayList, de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, java.util.ArrayList)
     */
    public boolean assertGraphsWithSignature(ArrayList listOfGraphURIs, SWPAuthority authority, Node signatureMethod, Node digestMethod, ArrayList listOfAuthorityProperties) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#getAllWarrants(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority)
     */
    public ExtendedIterator getAllWarrants(SWPAuthority authority) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#getAllAssertedGraphs(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority)
     */
    public ExtendedIterator getAllAssertedGraphs(SWPAuthority authority) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#getAllquotedGraphs(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority)
     */
    public ExtendedIterator getAllquotedGraphs(SWPAuthority authority) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean verifyAllSignatures() {
		return true;
    }

	protected NamedGraph createNamedGraphInstance(Node graphName) {
		if (!graphName.isURI()) {
			throw new IllegalArgumentException("Graph names must be URIs");
		}
		return new SWPNamedGraphImpl(graphName, new GraphMem());
	}

    protected SWPNamedGraph createNewWarrantGraph() {
		Node warrantGraphName = Node.createURI("urn:uuid:" + new UUID());
		SWPNamedGraph warrantGraph = new SWPNamedGraphImpl(warrantGraphName, new GraphMem());
		warrantGraph.add(new Triple(warrantGraphName, SWP.assertedByNode, warrantGraphName));
        return warrantGraph;
    }

}

/*
 *  (c)   Copyright 2004 Chris Bizer (chris@bizer.de) & Rowland Watkins (rowland@grid.cx) 
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