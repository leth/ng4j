/*
 * Created on 02.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.signature;

import java.util.ArrayList;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import de.fuberlin.wiwiss.ng4j.NamedGraph;

import com.hp.hpl.jena.graph.Node;

/**
 * @author BIZER
 *
 * Utility Methods for calculating digests, signing graphs and validating signatures 
 * 
 * Question: What class do we use for representing base64Binary binaries in Java?
 *  
 */
public interface SWPSignatureUtilities 
{

    /**
     * Calculates a digest from a graph and returns the digest as a base64Binary.
     * 
     * Currently only the swp:JjcRdfC14N-sha1 digest method is supported.
     * 
     * @return
     */
    public String calculateDigest( NamedGraph graph, Node digestMethod );
	
    /**
     * Calculates a signature for a graph and returns the signature as a base64Binary.
     * 
     * Currently only the swp:JjcRdfC14N-rsa-sha1 signature method is supported.
     * 
     * @return
     */
    public String calculateSignature( NamedGraph graph, 
            						  Node signatureMethod, 
            						  PrivateKey key );

    /**
     * Validates a signature for a given graph and a given public key.
     * 
     * Currently only the swp:JjcRdfC14N-rsa-sha1 signature method is supported.
     * 
     * @return true if successful
     */
    public boolean validateSignature( NamedGraph graph, 
            						  Node signatureMethod, 
            						  String signatureValue, 
            						  PublicKey key );
    
    /**
     * Validates a signature for a given graph and a given certificate and
     * a given set of trusted root certifcates.
     * 
     * Currently only the swp:JjcRdfC14N-rsa-sha1 signature method is supported.
     * 
     * @return return true if successful
     */
    public boolean validateSignature( NamedGraph graph, 
            						  Node signatureMethod,
            						  String signatureValue, 
            						  X509Certificate certificate, 
            						  ArrayList trustedCertificates );
 
    
    /**
     * Validates a signature for a given graph and a given certificate,
     * a given set of trusted root certifcates together with set of other
     * certificates which might form a certificaction chain.
     * 
     * Currently only the swp:JjcRdfC14N-rsa-sha1 signature method is supported.
     * 
     * Maybe we leave the implementation of this for a later version
     * if it becomes too difficult.
     * 
     * @return true if successful
     */
    public boolean validateSignature( NamedGraph graph, 
            						  Node signatureMethod, 
            						  String signatureValue, 
            						  X509Certificate certificate, 
            						  ArrayList trustedCertificates, 
            						  ArrayList otherCertificates );
}
