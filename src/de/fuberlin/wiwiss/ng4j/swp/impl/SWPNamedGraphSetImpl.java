/*
 * Created on 24-Nov-2004
*/
package de.fuberlin.wiwiss.ng4j.swp.impl;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Category;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.mem.GraphMem;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphImpl;
import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.utils.PKCS12Utils;
import de.fuberlin.wiwiss.ng4j.swp.utils.SWPSignatureUtilities;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP_V;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPBadDigestException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPBadSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPInvalidKeyException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPNoSuchAlgorithmException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPNoSuchDigestMethodException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPValidationException;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

import com.eaio.uuid.UUID;

/**
 * @author Chris Bizer.
 * @author Rowland Watkins.
 */
public class SWPNamedGraphSetImpl extends NamedGraphSetImpl implements SWPNamedGraphSet
{
	 static final Category log = Category.getInstance( SWPNamedGraphSet.class );
   
    public boolean swpAssert(SWPAuthority authority, ArrayList listOfAuthorityProperties) {
		// Create a new warrant graph.
		SWPNamedGraph warrantGraph = createNewWarrantGraph();
		// Assert all graphs in the graphset. 
        Iterator graphIterator = this.listGraphs();
        while (graphIterator.hasNext()) {
            NamedGraph currentGraph = (NamedGraph) graphIterator.next();
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.assertedBy, warrantGraph.getGraphName()));
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
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.quotedBy, warrantGraph.getGraphName()));
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
            warrantGraph.add(new Triple(currentGraph.getGraphName(), SWP.assertedBy, warrantGraph.getGraphName()));
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
    public boolean quoteGraphs( ArrayList listOfGraphNames, SWPAuthority authority, ArrayList listOfAuthorityProperties ) 
    {
        // Create a new warrant graph.
		SWPNamedGraph warrantGraph = createNewWarrantGraph();
		// Assert all graph in the list.
        Iterator graphNameIterator = listOfGraphNames.iterator();
        while ( graphNameIterator.hasNext() ) 
        {
			Node currentGraphName = ( Node ) graphNameIterator.next();
            NamedGraph currentGraph = ( NamedGraph ) this.getGraph( currentGraphName );
            warrantGraph.add( new Triple( currentGraph.getGraphName(), SWP.quotedBy, warrantGraph.getGraphName() ) );
        }
        // Add a description of the authorty to the warrant graph
        
		authority.addDescriptionToGraph( warrantGraph, listOfAuthorityProperties );
		

		// Add warrant graph to graphset
		this.addGraph( warrantGraph );
        return true;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#assertWithSignature(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, java.util.ArrayList)
     */
    public boolean assertWithSignature( SWPAuthority authority, 
    									Node signatureMethod, 
    									Node digestMethod, 
    									ArrayList listOfAuthorityProperties, 
    									String keystore,
    									String password ) throws SWPBadSignatureException, SWPBadDigestException 
    {
		// Create a new warrant graph.
		SWPNamedGraph warrantGraph = createNewWarrantGraph();
		// Assert all graphs in the graphset.
		
		authority.addDescriptionToGraph( warrantGraph, listOfAuthorityProperties );
		
        Iterator graphIterator = this.listGraphs();
        while ( graphIterator.hasNext() ) 
        {
        
            NamedGraph currentGraph = ( NamedGraph ) graphIterator.next();
            
			try 
			{
				String graphDigest = SWPSignatureUtilities.calculateDigest( currentGraph, digestMethod );
				warrantGraph.add( new Triple( currentGraph.getGraphName(), SWP.assertedBy, warrantGraph.getGraphName() ) );
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), 
											SWP.digest, 
											Node.createLiteral( graphDigest, null, XSDDatatype.XSDbase64Binary ) ) );
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), SWP.digestMethod, digestMethod ) );
	            //Not sure if these are needed
	            /*
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), SWP.authority.asNode(), Node.createURI( authority.getEmail() ) ) );
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), 
											SWP.validFrom.asNode(), 
											Node.createLiteral( authority.getCertificate().getNotBefore().toString(), 
																null, 
																XSDDatatype.XSDdateTime ) ) );
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), 
											SWP.validUntil.asNode(), 
											Node.createLiteral( authority.getCertificate().getNotAfter().toString(), 
																null, 
																XSDDatatype.XSDdateTime ) ) );
	            
	            warrantGraph.add( new Triple( warrantGraph.getGraphName(), SWP.signatureMethod.asNode(), signatureMethod ) );
	            
	            String graphSetSignature = SWPSignatureUtilities.calculateSignature( this, signatureMethod, pkey );
	        	warrantGraph.add( new Triple( currentGraph.getGraphName(), 
											SWP.signature.asNode(), 
											Node.createLiteral( graphSetSignature, null, XSDDatatype.XSDbase64Binary ) ) );
											*/
			} 
			catch ( SWPNoSuchDigestMethodException e1 ) 
			{
				return false;
			} 
			/*
			catch ( SWPInvalidKeyException e ) 
			{
				return false;
			} 
			catch ( SWPSignatureException e ) 
			{
				return false;
			} 
			catch ( SWPNoSuchAlgorithmException e ) 
			{
				return false;
			}*/
            
                      
        }  

        warrantGraph.add( new Triple( warrantGraph.getGraphName(), SWP.authority, authority.getID() ) );
        if ( listOfAuthorityProperties != null )
        {
        	if ( listOfAuthorityProperties.contains( ( Object ) SWP.validFrom ) & listOfAuthorityProperties.contains( ( Object ) SWP.validUntil )  ) 
			{
        		warrantGraph.add( new Triple( warrantGraph.getGraphName(), 
						SWP.validFrom, 
						Node.createLiteral( authority.getCertificate().getNotBefore().toString(), 
											null, 
											XSDDatatype.XSDdateTime ) ) );
        		
        		warrantGraph.add( new Triple( warrantGraph.getGraphName(), 
						SWP.validUntil, 
						Node.createLiteral( authority.getCertificate().getNotAfter().toString(), 
											null, 
											XSDDatatype.XSDdateTime ) ) );
			}
        }
        
        
        
        PrivateKey pkey = null;
        // Sign the warrant graph now.
        String warrantGraphSignature = null;
        try 
        {
        	pkey = PKCS12Utils.decryptPrivateKey( keystore, password );
			warrantGraphSignature = SWPSignatureUtilities.calculateSignature( warrantGraph, signatureMethod, pkey );
			warrantGraph.add( new Triple( warrantGraph.getGraphName(), 
										SWP.signature, 
										Node.createLiteral( warrantGraphSignature, null, XSDDatatype.XSDbase64Binary ) ) );
		} 
        catch ( SWPInvalidKeyException e ) 
        {
        	//make the private key unusable
            pkey = null;
			return false;
		}  
        catch ( SWPSignatureException e ) 
        {
        	//make the private key unusable
            pkey = null;
			return false;
		} 
        catch ( SWPNoSuchAlgorithmException e ) 
        {
        	//make the private key unusable
            pkey = null;
			return false;
		}
		
        
		// Add warrant graph to graphset
		this.addGraph( warrantGraph );
        return true;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#quoteWithSignature(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, java.util.ArrayList)
     */
    public boolean quoteWithSignature( SWPAuthority authority, 
    									Node signatureMethod, 
    									Node digestMethod, 
    									ArrayList listOfAuthorityProperties, 
    									String keystore,
    									String password ) throws SWPBadSignatureException
    {
    	//    	 Create a new warrant graph.
		SWPNamedGraph warrantGraph = createNewWarrantGraph();
		// Assert all graphs in the graphset.
		
		authority.addDescriptionToGraph( warrantGraph, listOfAuthorityProperties );
		
        Iterator graphIterator = this.listGraphs();
        while ( graphIterator.hasNext() ) 
        {
        
            NamedGraph currentGraph = ( NamedGraph ) graphIterator.next();
            
			try 
			{
				String graphDigest = SWPSignatureUtilities.calculateDigest( currentGraph, digestMethod );
				warrantGraph.add( new Triple( currentGraph.getGraphName(), SWP.quotedBy, warrantGraph.getGraphName() ) );
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), 
											SWP.digest, 
											Node.createLiteral( graphDigest, null, XSDDatatype.XSDbase64Binary ) ) );
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), SWP.digestMethod, digestMethod ) );
	            
	           
	            //Not sure if these are needed
	            /*
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), SWP.authority.asNode(), Node.createURI( authority.getEmail() ) ) );
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), 
											SWP.validFrom.asNode(), 
											Node.createLiteral( authority.getCertificate().getNotBefore().toString(), 
																null, 
																XSDDatatype.XSDdateTime ) ) );
	            warrantGraph.add( new Triple( currentGraph.getGraphName(), 
											SWP.validUntil.asNode(), 
											Node.createLiteral( authority.getCertificate().getNotAfter().toString(), 
																null, 
																XSDDatatype.XSDdateTime ) ) );
	            
	            warrantGraph.add( new Triple( warrantGraph.getGraphName(), SWP.signatureMethod.asNode(), signatureMethod ) );
	            
	            String graphSetSignature = SWPSignatureUtilities.calculateSignature( this, signatureMethod, pkey );
	        	warrantGraph.add( new Triple( currentGraph.getGraphName(), 
											SWP.signature.asNode(), 
											Node.createLiteral( graphSetSignature, null, XSDDatatype.XSDbase64Binary ) ) );
											*/
			} 
			catch ( SWPNoSuchDigestMethodException e1 ) 
			{
				return false;
			} 
			/*
			catch ( SWPInvalidKeyException e ) 
			{
				return false;
			} 
			catch ( SWPSignatureException e ) 
			{
				return false;
			} 
			catch ( SWPNoSuchAlgorithmException e ) 
			{
				return false;
			}*/
            
                      
        }  
        
        
        warrantGraph.add( new Triple( warrantGraph.getGraphName(), SWP.authority, Node.createURI( authority.getEmail() ) ) );
        
        if ( listOfAuthorityProperties != null )
        {
        	if ( listOfAuthorityProperties.contains( ( Object ) SWP.validFrom ) & listOfAuthorityProperties.contains( ( Object ) SWP.validUntil )  ) 
			{
        		warrantGraph.add( new Triple( warrantGraph.getGraphName(), 
						SWP.validFrom, 
						Node.createLiteral( authority.getCertificate().getNotBefore().toString(), 
											null, 
											XSDDatatype.XSDdateTime ) ) );
        		
        		warrantGraph.add( new Triple( warrantGraph.getGraphName(), 
						SWP.validUntil, 
						Node.createLiteral( authority.getCertificate().getNotAfter().toString(), 
											null, 
											XSDDatatype.XSDdateTime ) ) );
			}
        }
        
        
        
        
        PrivateKey pkey = null;
        // Sign the warrant graph now.
        String warrantGraphSignature = null;
        try 
        {
        	pkey = PKCS12Utils.decryptPrivateKey( keystore, password );
			warrantGraphSignature = SWPSignatureUtilities.calculateSignature( warrantGraph, signatureMethod, pkey );
			warrantGraph.add( new Triple( warrantGraph.getGraphName(), 
										SWP.signature, 
										Node.createLiteral( warrantGraphSignature, null, XSDDatatype.XSDbase64Binary ) ) );
		} 
        catch ( SWPInvalidKeyException e ) 
        {
        	//make the private key unusable
            pkey = null;
			return false;
		}  
        catch ( SWPSignatureException e ) 
        {
        	//make the private key unusable
            pkey = null;
			return false;
		} 
        catch ( SWPNoSuchAlgorithmException e ) 
        {
        	//make the private key unusable
            pkey = null;
			return false;
		}
		
        
		// Add warrant graph to graphset
		this.addGraph( warrantGraph );
        return true;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#assertGraphsWithSignature(java.util.ArrayList, de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, java.util.ArrayList)
     */
    public boolean assertGraphsWithSignature( ArrayList listOfGraphURIs, 
    										SWPAuthority authority, 
    										Node signatureMethod, 
    										Node digestMethod, 
    										ArrayList listOfAuthorityProperties, 
    										String keystore,
    										String password ) throws SWPBadSignatureException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#getAllWarrants(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority)
     */
    /*
    public ExtendedIterator getAllWarrants( SWPAuthority authority ) 
    {
    	Iterator qit = this.findQuads( Node.ANY, Node.ANY, SWP.authority.asNode(), authority.getID() );
    	Quad quad = ( Quad )qit.next();
    	this.findQuads( Node.ANY, quad.getSubject(), Node.ANY, Node.ANY );
        // TODO Auto-generated method stub
        return null;
    }*/

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#getAllAssertedGraphs(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority)
     */
    /*
    public ExtendedIterator getAllAssertedGraphs(SWPAuthority authority) {
        // TODO Auto-generated method stub
        return null;
    }*/

    /* (non-Javadoc)
     * @see de.fuberlin.wiwiss.ng4j.swp.signature.SWPNamedGraphSet#getAllquotedGraphs(de.fuberlin.wiwiss.ng4j.swp.signature.SWPAuthority)
     */
    /*
    public ExtendedIterator getAllquotedGraphs( SWPAuthority authority ) 
    {
    	
    	
        // TODO Auto-generated method stub
        return null;
    }*/

    public boolean verifyAllSignatures() 
    {
    	//First, let's remove any previous verification
    	//graphs.
    	if ( this.containsGraph( SWP_V.default_graph ) )
    	{
    		this.removeGraph( SWP_V.default_graph );
    	}
    	//Now, we can create a new verification graph to record
    	//results.
    	NamedGraph verificationGraph = this.createGraph( SWP_V.default_graph );
    	String canonicalTripleList;
    	Iterator ngsIt = this.listGraphs();
    	
    	// For each NamedGraph in the NamedGraphSet, we will check for 
    	// the swp:assertedBy triple. We then take the object of that
    	// triple and query it to find if it contains a signature and
    	// authority with an associated certificate.
    	while ( ngsIt.hasNext() )
    	{
    		Quad quad = null;
    		NamedGraph ng = ( NamedGraph )ngsIt.next();
        	
    		Iterator it = findQuads( Node.ANY, Node.ANY, SWP.assertedBy, ng.getGraphName() );
    		if ( it.hasNext() )
    		{	
    			quad = ( Quad )it.next();
    			String warrantQuery = "SELECT * WHERE <"+ng.getGraphName().toString()+"> (<"+ng.getGraphName().toString()+"> swp:signature ?signature) (<"+ng.getGraphName().toString()+"> swp:authority ?authority) (?authority swp:X509Certificate ?certificate) USING swp FOR <http://www.w3.org/2004/03/trix/swp-2/>";
    			System.out.println();
    			System.out.println();
	            Iterator witr = TriQLQuery.exec( this, warrantQuery );
	                while ( witr.hasNext() )
	                {
	                    Map result = (Map) witr.next();
        	            Node cert = (Node) result.get("certificate");
        	            Node signature = (Node) result.get("signature");
        	            String certificate = cert.getLiteral().getLexicalForm();
        	            String certs = "-----BEGIN CERTIFICATE-----\n" +
        	            					certificate + "\n-----END CERTIFICATE-----";
        	            // If the certificate and signature are not null, we can use these
        	            // to verify the signature. 
        	            // We, of course, need to provide the warrant graph as it
        	            // was *before* adding the signature. We therefore remove,
        	            // the signature and add back again later.
        	            if ( ( cert != null ) && ( signature != null )  )
        	            {
        	                try 
        	                {
        	                	Iterator exit = ng.find( ng.getGraphName(), SWP.signature, Node.ANY );
        	                	ArrayList li = new ArrayList();
        	                	while ( exit.hasNext() )
        	                	{
        	                		li.add( ( Triple )exit.next() );
        	                	}
        	                	for ( Iterator i = li.iterator(); i.hasNext(); )
        	                	{
        	                		ng.delete( ( Triple )i.next() );
        	                	}
        	                	// If the warrant's signature is ok, we want to test whether the graph
        	                	// digests of the graphs it asserts are ok. 
        	                	// We simply take the graphs and get their digests and compare the 
        	                	// string representations.
        	                	// After this, we then add to our verification graph the results of
        	                	// this process.
        	                    if ( SWPSignatureUtilities.validateSignature( ng, SWP.JjcRdfC14N_rsa_sha1, signature.getLiteral().getLexicalForm(), certs ) )
        	                    {
        	                    	log.info( "Warrant graph " + ng.getGraphName().toString() + " successfully verified." );
        	                    	
        	                    	Iterator dit = findQuads( Node.ANY, Node.ANY, SWP.digest, Node.ANY );
        	                		while ( dit.hasNext() )
        	                		{
        	                			Quad qud = ( Quad )dit.next();
        	                			String digest = qud.getObject().getLiteral().getLexicalForm();
        	                			System.out.println( digest );
        	                			String digest1 = SWPSignatureUtilities.calculateDigest( this.getGraph( qud.getSubject() ), SWP.JjcRdfC14N_sha1 );
        	                			System.out.println( digest1 );
        	                			if ( digest1.equals( digest ) )
        	                			{
        	                				verificationGraph.add( new Triple( qud.getSubject(), SWP_V.successful, Node.createLiteral( "true" ) ) );
        	                			}
        	                			else verificationGraph.add( new Triple( qud.getSubject(), SWP_V.notSuccessful, Node.createLiteral( "true" ) ) );
        	                		}
        	                    }
        	                    else
        	                    {
        	                    	log.info( "Warrant graph " + ng.getGraphName().toString() + " verification failure!" );
        	                    	verificationGraph.add( new Triple( ng.getGraphName(), SWP_V.notSuccessful, Node.createLiteral( "true" ) ) );
        	                    }
        	                    
        	                    for ( Iterator i = li.iterator(); i.hasNext(); )
        	                    {
        	                    	ng.add( ( Triple )i.next() );
        	                    }
        	                }
        	                catch ( SWPInvalidKeyException e ) 
        	                {
								// TODO Auto-generated catch block
        	                	log.info( "Public Key in certificate not valid - corruption in graph likely." );
								return false;
							} 
        	                catch ( SWPSignatureException e ) 
        	                {
								// TODO Auto-generated catch block
        	                	log.info( "Exception while verifying signature." );
								return false;
							}  
        	                catch ( SWPNoSuchAlgorithmException e ) 
        	                {
								// TODO Auto-generated catch block
        	                	log.info( "SHA1withRSA is not a valid algorithm." );
								return false;
							} 
        	                catch ( SWPValidationException e ) 
        	                {
								// TODO Auto-generated catch block
        	                	log.info( "Error constructing signature verifier." );
								return false;
							} catch ( SWPNoSuchDigestMethodException e ) 
							{ 
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        	            }
	                }
    		}
    		else
    			continue;
    			
    	}		
		return true;
    }

	protected NamedGraph createNamedGraphInstance(Node graphName) 
	{
		if ( !graphName.isURI() ) 
		{
			throw new IllegalArgumentException( "Graph names must be URIs" );
		}
		return new SWPNamedGraphImpl( graphName, new GraphMem() );
	}

    protected SWPNamedGraph createNewWarrantGraph() 
    {
		Node warrantGraphName = Node.createURI( "urn:uuid:" + new UUID() );
		SWPNamedGraph warrantGraph = new SWPNamedGraphImpl( warrantGraphName, new GraphMem() );
		warrantGraph.add( new Triple( warrantGraphName, SWP.assertedBy, warrantGraphName ) );
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