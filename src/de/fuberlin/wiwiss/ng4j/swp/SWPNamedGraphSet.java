package de.fuberlin.wiwiss.ng4j.swp;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadDigestException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadSignatureException;

import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * 
 * @author chris bizer
 * @author rowland watkins
 * @since 24-Nov-2004 
 */
public interface SWPNamedGraphSet extends NamedGraphSet
{
    /**
     * 
     * <p>Given an SWP Authority, assert all graphs in the 
     * graphset with this Authority.
     * 
     * This will add a warrant graph asserting all
     * other graphs to the graph set.
     * 
     * The listOfAuthorityProperties contains list of properties names
     * (as nodes) describing the authority. These properties will be included
     * into the warrant graph, e.g. foaf:name, foaf:mbox
     * </p>
     * <ul>Example:
     * 
     * <li>urn:uuid:X { :G1 swp:assertedBy urn:uuid:X .
	 *		        :G2 swp:assertedBy urn:uuid:X .
     *              urn:uuid:X swp:assertedBy urn:uuid:X .
     *              urn:uuid:X swp:authority <http://www.bizer.de/me> .
     *              <http://www.bizer.de/me> foaf:mbox <mailto:chris@bizer.de> }</li>
     * </ul>
     * <p>
     * The new graph will be named using a UUID.
     * 
     * If the Authority doesn't have a URI, then a blank node will be used to
     * identify the authority and a additional triple containing the foaf:mbox
     * address of the authority will be added.
     *  
     * Return true if successful.
     * </p>
     * 
     * @param authority 
     * @param listOfAuthorityProperties
     * @return boolean
     */
    public boolean swpAssert( SWPAuthority authority, ArrayList<Node> listOfAuthorityProperties );

    public boolean swpAssert( SWPAuthority authority );
    
    /**
     * 
     * <p>Given an SWP Authority, quote all graphs in the 
     * graphset with this Authority.
     * </p>
     * <p>
     * Quotes are not as strong semantically as assertions.
     * Quotes are really used when using second hand 
     * information, i.e. the Authority is not the creator
     * of the original graph.
     * </p>
     * <p>
     * The listOfAuthorityProperties contains list of properties names
     * (as nodes) describing the authority. These properties will be included
     * into the warrant graph, e.g. foaf:name, foaf:mbox,
     * </p>
     * <ul>
     * Example:
     * 
     * <li>urn:uuid:X { :G1 swp:quotedBy urn:uuid:X .
	 *		        :G2 swp:quotedBy urn:uuid:X .
     *              urn:uuid:X swp:assertedBy urn:uuid:X .
     *              urn:uuid:X swp:authority <http://www.bizer.de/me> .
     *              <http://www.bizer.de/me> foaf:mbox <mailto:chris@bizer.de> }</li>
     *  
     * 
     * @param authority
     * @param listOfAuthorityProperties
     * @return true if successful
     */
    public boolean swpQuote( SWPAuthority authority, ArrayList<Node> listOfAuthorityProperties );

	public boolean swpQuote( SWPAuthority authority );
   
    /**
     * 
     * Same as swpAssert, except instead of simply 
     * asserting a graph, we sign the asserted
     * graph with a digital signature according to
     * the specified signatureMethod.
     * 
     * Example:
     * 
     * :Timestamp { :G1 swp:assertedBy :Timestamp .
     *              :G1 swp:digestMethod swp:JjcRdfC14N-sha1 .
     *              :G1 swp:digest "..." .
	 *		        :G2 swp:assertedBy :Timestamp .
     *              :G2 swp:digestMethod swp:JjcRdfC14N-sha1 .
     *              :G2 swp:digest "..." .
	 *		        :Timestamp swp:assertedBy :Timestamp .
     *              :Timestamp swp:authority <http://www.bizer.de/me> .
     *              :Timestamp swp:signatureMethod swp:JjcRdfC14N-rsa-sha1 .
     *              :Timestamp swp:signature "..." } 
     * 
     * The listOfAuthorityProperties contains list of properties names
     * (as nodes) describing the authority. These properties will be included
     * into the warrant graph, e.g. foaf:name, foaf:mbox, swp:key, swp:certificate
     * 
     * Return true is successful.
     * 
     * @param authority
     * @param signatureMethod
     * @param digestMethod
     * @param listOfAuthorityProperties
     * @param keystore
     * @param password
     * @return true if successful
     * @throws SWPBadSignatureException
     * @throws SWPBadDigestException
     */
    public boolean assertWithSignature( SWPAuthority authority, 
    									Node signatureMethod, 
    									Node digestMethod, 
    									ArrayList<Node> listOfAuthorityProperties, 
    									String keystore, 
    									String password ) 
    throws SWPBadSignatureException,
    SWPBadDigestException;
    
    /**
     * 
     * Same as swpQuote, except instead of simply
     * quoting a graph, we sign the quoted graph
     * with a digital signature according to the
     * specified signatureMethod.
     * 
     * Example:
     * 
     * :Timestamp { :G1 swp:quotedBy :Timestamp .
     *              :G1 swp:digestMethod swp:JjcRdfC14N-sha1 .
     *              :G1 swp:digest "..." .
	 *		        :G2 swp:quotedBy :Timestamp .
     *              :G2 swp:digestMethod swp:JjcRdfC14N-sha1 .
     *              :G2 swp:digest "..." .
	 *		        :Timestamp swp:assertedBy :Timestamp .
     *              :Timestamp swp:authority <http://www.bizer.de/me> .
     *              :Timestamp swp:signatureMethod swp:JjcRdfC14N-rsa-sha1 .
     *              :Timestamp swp:signature "..." } 
     * 
     * The listOfAuthorityProperties contains list of properties names
     * (as nodes) describing the authority. These properties will be included
     * into the warrant graph, e.g. foaf:name, foaf:mbox, swp:key, swp:certificate
     * 
     * Return true if successful.
     * 
     * @param authority
     * @param signatureMethod
     * @param digestMethod
     * @param listOfAuthorityProperties
     * @param keystore
     * @param password
     * @return true if successful
     * @throws SWPBadSignatureException
     * @throws SWPBadDigestException
     */
    public boolean quoteWithSignature( SWPAuthority authority, 
    									Node signatureMethod, 
    									Node digestMethod, 
    									ArrayList<Node> listOfAuthorityProperties, 
    									String keystore,
    									String password ) 
    throws SWPBadSignatureException,
    SWPBadDigestException;
   
    /**
     * 
     * Given an list of graph names and an SWP authority, assert
     * each listed graph with this authority.
     *
     * Return true if successful.
     * 
     * @param listOfGraphNames as Nodes
     * @param authority
     * @param listOfAuthorityProperties
     * @return true if successful
     */
    public boolean assertGraphs( ArrayList<Node> listOfGraphNames, SWPAuthority authority, ArrayList<Node> listOfAuthorityProperties );

    public boolean quoteGraphs( ArrayList<Node> listOfGraphNames, SWPAuthority authority, ArrayList<Node> listOfAuthorityProperties );
    
    /**
     * 
     * Given an list of graphs and an SWP Authority, assert
     * each graph in the graphset with this Authority.
     * 
     * Return true if successful.
     * 
     * @param listOfGraphURIs
     * @param authority
     * @param signatureMethod
     * @param digestMethod
     * @param listOfAuthorityProperties
     * @param keystore
     * @param password
     * @return true if successful
     * @throws SWPBadSignatureException
     * @throws SWPBadDigestException
     */
    public boolean assertGraphsWithSignature( ArrayList<String> listOfGraphURIs, 
    										SWPAuthority authority, 
    										Node signatureMethod, 
    										Node digestMethod, 
    										ArrayList<Node> listOfAuthorityProperties, 
    										String keystore,
    										String password ) 
    throws SWPBadSignatureException, 
    SWPBadDigestException;
   
    /**
     * 
     * For all signature graphs in the set,
     * verify all signatures.
     * 
     * Calling this method requires adding a graph called 
     * <http://localhost/trustedinformation> before.
     * This graph has to contain the public keys and 
     * certificates of authorities or root certificates by CAs
     * trusted by the user. The content of this graph will be
     * treated as trustworthy information in the signature 
     * verification process.
     * 
     * The results of the signature verification process will be
     * added as a new graph called <http://localhost/verifiedSignatures>
     * to the graphset.
     * 
     * Example graph <http://localhost/verifiedSignatures>
     * 
     * <http://localhost/verifiedSignatures> { 
     *              :Warrent1 swp:signatureVerification swp:sucessful .
     *              :Warrent2 swp:signatureVerification swp:notSucessful .
     *              :Warrent3 swp:signatureVerification swp:sucessful  } 
     * 
     * Return true if successful.
     * 
     * @return true if successful
     */
    public boolean verifyAllSignatures();
    
	/**
     * 
     * Returns an iterator over all SWPWarrants for a given authority.
     * 
     */
    public ExtendedIterator getAllWarrants( SWPAuthority authority );
    
	/**
     * 
     * Returns an iterator over all named graphs asserted by a given authority.
     * 
     */
    public ExtendedIterator getAllAssertedGraphs( SWPAuthority authority );
	/**
     * 
     * Returns an iterator over all named graphs quoted by a given authority.
     * 
     */
    public ExtendedIterator getAllQuotedGraphs( SWPAuthority authority );
   

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