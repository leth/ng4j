
package de.fuberlin.wiwiss.ng4j.swp.impl;

import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.FOAF;

/**
 * 
 * An SWPAuthority represents information about an authorty
 * like id, label, eMail, keys and certificates 
 * 
 * @author chris bizer
 *
 */
public class SWPAuthorityImpl implements SWPAuthority {

    private Node id;
    private String label;
    private String email = null;
    private PublicKey publickey;
    private PrivateKey privatekey;
	private X509Certificate certificate;

     public SWPAuthorityImpl(Node id) {
		setID(id);
	}

    /**
     * 
     * Sets the ID of the authority.
     * Authorities can by identified using a URIref or a bNode
     * 
     */
	public void setID(Node id) {
        this.id = id ;
    }
	public Node getID() {
		return this.id;
    }
	
	/**
     * 
     * Sets the Label / Name of the authority.
     * Will be serialized using rdfs:label
     * 
     */
	public void setLabel(String label) {
        this.label = label ;
    }
	public String getLabel()  {
		return this.label;
    }

    /**
     * 
     * Sets the eMail adress of the authority.
     * Will be serialized using foaf:mbox
     * 
     */
	public void setEmail(String email) {
        this.email = email ;
    }
	public String getEmail() {
		return this.email;
    }
	
    /**
     * 
     * Sets the public key of the authority.
     * Will be serialized using swp:hasKey
     * 
     */
	public void setPublicKey(PublicKey key) {
        this.publickey = key ;
    }
	public PublicKey getPublicKey() {
		return this.publickey;
    }

    /**
     * 
     * Sets the private key of the authority.
     * 
     */
	public void setPrivateKey(PrivateKey key) {
        this.privatekey = key ;
    }
	public PrivateKey getPrivateKey() {
		return this.privatekey;
    }

    /**
     * 
     * Sets the certificate of the authority.
     * 
     */
	public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate ;
    }
	public X509Certificate getCertificate() {
		return this.certificate;
    }
	
    /**
     * 
     * Adds an additional property of the authority.
     * 
     */
	public void addProperty(Node predicate, Node object) {
    }

	/**
     * 
     * Returns an iterator over all property values (nodes) for a given property.
     * 
     */
	public ExtendedIterator getProperty(Node predicate){
		return null;
    }

	/**
     * 
     * Adds a description of the authority to a graph.
     * 
     * The listOfAuthorityProperties determines which information
     * about the authority is added.
     * 
     */
	public boolean addDescriptionToGraph(NamedGraph graph, ArrayList listOfAuthorityProperties) {
		// Add swp:authority
		graph.add(new Triple(graph.getGraphName(), SWP.authorityNode, this.getID()));
        // Check if the eMail adress has to be added.
		if (this.getID().isBlank() && this.getEmail() != null) {
             graph.add(new Triple(this.getID(), FOAF.mboxNode, Node.createURI("mailto:" + this.getEmail())));
		}
		// Add authority description
        if (listOfAuthorityProperties.contains((Object) FOAF.mboxNode)) {
			graph.add(new Triple(this.getID(), FOAF.mboxNode, Node.createURI("mailto:" + this.getEmail())));
        };

		Node rdfsLabel = Node.createURI("http://www.w3.org/2000/01/rdf-schema#label");
        if (listOfAuthorityProperties.contains((Object) rdfsLabel)) {
			graph.add(new Triple(this.getID(), rdfsLabel, Node.createLiteral(this.getLabel(), null, null)));
        };

        if (listOfAuthorityProperties.contains((Object) SWP.RSAKeyNode)) {
			// We need code for publishing information about a RSA key here, using the SWP-2 and the XML-Sig vocabulary
        };

        if (listOfAuthorityProperties.contains((Object) SWP.X509CertificateNode)) {
			// We need code for publishing information about a X509 certificate here, using the SWP-2 and the XML-Sig vocabulary
        };

        return true;
	}

	/**
     * 
     * Returns a graph containing all information about the authorty.
     * Excluding its private key :-)
     * 
     */
	public Graph getGraph() {
		return null;
    }

}

/*
 *  (c)   Copyright 2004 Chris Bizer (chris@bizer.de)
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
