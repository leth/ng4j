/*
 * Created on 24-Nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp;

import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import de.fuberlin.wiwiss.ng4j.NamedGraph;

/**
 * 
 * An SWPAuthority represents information about an authority
 * such as id, label, eMail, keys and certificates. 
 * 
 * @author chris bizer
 * @author rowland watkins
 * 
 */
public interface SWPAuthority {

    /**
     * 
     * Sets the ID of the authority.
     * Authorities can by identified using a URIref or a bNode
     * 
     */
	public void setID(Node id);
	public Node getID();
	
	/**
     * 
     * Sets the Label / Name of the authority.
     * Will be serialized using rdfs:label
     * 
     */
	public void setLabel(String label);
	public String getLabel();

    /**
     * 
     * Sets the eMail address of the authority.
     * Will be serialized using foaf:mbox
     * 
     */
	public void setEmail(String email);
	public String getEmail();
	
    /**
     * 
     * Sets the public key of the authority.
     * Will be serialized using swp:hasKey
     * 
     */
	public void setPublicKey(PublicKey key);
	public PublicKey getPublicKey();

    /**
     * 
     * Sets the certificate of the authority.
     * 
     */
	public void setCertificate(X509Certificate certificate);
	public X509Certificate getCertificate();
	
    /**
     * 
     * Adds an additional property of the authority.
     * 
     */
	public void addProperty(Node predicate, Node object);

	/**
     * 
     * Returns an iterator over all property values (nodes) for a given property.
     * 
     */
	public ExtendedIterator getProperty(Node predicate);

    public boolean addDescriptionToGraph( NamedGraph graph, ArrayList<Node> listOfAuthorityProperties );

	/**
     * 
     * Returns a graph containing all information about the authorty.
     * Excluding its private key :-)
     * 
     */
	public Graph getGraph();

}

/*
 *  (c)   Copyright 2004 - 2010 Chris Bizer (chris@bizer.de) & Rowland Watkins (rowland@grid.cx) 
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