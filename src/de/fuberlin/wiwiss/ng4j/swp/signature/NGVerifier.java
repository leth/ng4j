/*
 * Created on 13-Nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.signature;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Category;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.signature.c14n.RDFC14NImpl;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.RDFSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.signature.impl.SignatureReport;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

/**
 * @author rowland
 *
 * Declarative Systems & Software Engineering Group,
 * School of Electronics & Computer Science,
 * University of Southampton,
 * Southampton,
 * SO17 1BJ
 */
public class NGVerifier {

    static final Category log = Category.getInstance( NGVerifier.class );
	private NamedGraphSet cleanSet = new NamedGraphSetImpl();
	private NamedGraphSet signSet = new NamedGraphSetImpl();
	//private NamedGraphSet tempSet = new NamedGraphSetImpl();
	//private NamedGraphSet verifySet = new NamedGraphSetImpl();
	private String warrantName;
	private ArrayList canonicalTripleList;
	private String signature;
	private String certificate;
	private String ca;
	
	public NGVerifier ( NamedGraphSet signSet )
	{
		this.signSet = signSet;
		this.cleanSet = signSet;
	}
	
	public NGVerifier ( NamedGraph signGraph ) 
	{
	    this.signSet.addGraph( signGraph );
	    this.cleanSet.addGraph( signGraph );
	}
    
	
	public boolean verify( String a )
	{
	    try {
	        if ( !signSet.isEmpty() )
	        {
	            //Iterator itr = signSet.listGraphs();
	            //NamedGraphSet tempSet = new NamedGraphSetImpl();
                //NamedGraph ng = ( NamedGraph ) itr.next();
                
                //tempSet.addGraph( ng );
                
                String query = "SELECT * WHERE ?graph (?s swp:assertedBy ?warrant) USING swp FOR <http://www.w3.org/2004/03/trix/swp-1/>";
        	    Iterator titr = TriQLQuery.exec( signSet, query );
        	    if ( titr.hasNext() )
        	    {
        	        while ( titr.hasNext() )
        	        {
        	            Map oneResult = (Map) titr.next();
        	            Node graph = (Node) oneResult.get("graph");
        	            Node s = (Node) oneResult.get("s");
        	            Node warrantURI = (Node) oneResult.get("warrant");
        	            log.info(graph + " { " + s + " " + warrantURI + " . }");
        	           
        	            NamedGraphSet tempSet = new NamedGraphSetImpl();
        	            NamedGraph ng = signSet.getGraph( graph );
        	            tempSet.addGraph( ng );
        	            tempSet.removeQuad( new Quad( graph, 
        	                    					  graph, 
        	                    					  Node.createURI( SWP.assertedBy.getURI() ),
        	                    					  Node.ANY ) );
        	            Model model = tempSet.asJenaModel( ng.getGraphName().getURI() );
        	            canonicalTripleList = new RDFC14NImpl( model, "" ).getCanonicalStringsArray();
        	            
        	            NamedGraph warrant = signSet.getGraph( warrantURI );
        	            NamedGraphSet warrantSet = new NamedGraphSetImpl();
           	            warrantSet.addGraph( warrant );
        	            String warrantQuery = "SELECT * WHERE ?graph (?s swp:certificate ?certificate) (?p swp:signature ?signature) USING swp FOR <http://www.w3.org/2004/03/trix/swp-1/>";
        	            Iterator witr = TriQLQuery.exec( warrantSet, warrantQuery );
        	            if ( witr.hasNext() )
        	            {
        	                while ( witr.hasNext() )
        	                {
        	                    Map result = (Map) witr.next();
                	            Node warrantGraph = (Node) result.get("graph");
                	            Node cert = (Node) result.get("certificate");
                	            Node signature = (Node) result.get("signature");
                	            log.info(warrantGraph + " { " + cert + " " + signature + " . }");
                	            String certs = "-----BEGIN CERTIFICATE-----\n" +
                	            					cert + "\n-----END CERTIFICATE-----";
                	            if ( ( cert != null ) && ( signature != null )  )
                	            {
                	                SignatureReport sigRep = null;
                	                try {
                	                    sigRep = new SignatureReport( signature.toString(), 
                	                            					  certs, 
                	                            					  canonicalTripleList );
                	                } catch (CertificateException e1) {
                	                    log.error( e1.getMessage() );
                	                    return false;
                	                } catch (IOException e1) {
                	                    log.error( e1.getMessage() );
                	                    return false;
                	                }
                	            
                	                try {
                	                    if ( sigRep.verify() )
                	                    {
                	                        String message = "Warrant graph " + warrantGraph.getURI() + " successfully verified.";
                	                        System.out.println( message );
                	                        log.info( message );
                	                    }
                	                    else 
                	                    {
                	                        String message = "Warrant graph " + warrantGraph.getURI() + " verification failure!";
                	                        throw new RDFSignatureException( message );
                	                    }
                	                } catch (InvalidKeyException e2) {
                	                    log.error( e2.getMessage() );
                	                    return false;
                	                } catch (NoSuchAlgorithmException e2) {
                	                    log.error( e2.getMessage() );
                	                    return false;
                	                } catch (UnsupportedEncodingException e2) {
                	                    log.error( e2.getMessage() );
                	                    return false;
                	                } catch (SignatureException e2) {
                	                    log.error( e2.getMessage() );
                	                    return false;
                	                } catch (IOException e2) {
                	                    log.error( e2.getMessage() );
                	                    return false;
                	                }
                	            }
        	                }
        	            }
        	        }
        	    }
	        } 
	    }
	    catch ( RDFSignatureException e )
	    {
	        log.error( e.getMessage() );
	        return false;
	    }
	    return true;
	}
    
    /**
	 * verify()
	 * 
	 * Takes a graphset and checks each graph's signature held
	 * in a warrant graph.
	 * 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeySpecException
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CertificateException
	 */
    
	public boolean verify() throws InvalidKeyException, 
	NoSuchAlgorithmException, 
	SignatureException, 
	InvalidKeySpecException, 
	URISyntaxException, 
	IOException, 
	CertificateException
	{
	    try 
	    {
	        if ( !signSet.isEmpty() )
	        {
	            Iterator itr = signSet.listGraphs();
	            while ( itr.hasNext() ) 
	            {
	                NamedGraphSet tempSet = new NamedGraphSetImpl();
	                NamedGraph ng = ( NamedGraph ) itr.next();
	                
	                tempSet.addGraph( ng );
				
	                /**
				 * Once we have *a* graph, we want to see if it
				 * is a warrant graph or not.
				 */
	                System.out.println( "Current graph: "+ ng.getGraphName()+'\n' );
	                Model model = tempSet.asJenaModel( ng.getGraphName().getURI() );  
	                ResIterator mitr = model.listSubjects();
	                String warrantURI = null;
				
	                /**
				 * Test if it is a warrant graph or not by the existance of a
				 * SWP.assertedBy statement in the model.
				 */
	                while ( mitr.hasNext() )
	                {
	                    Resource res = mitr.nextResource();
	                    if ( res.hasProperty( SWP.assertedBy ) && !res.hasProperty( SWP.authority ) )
	                    {
	                        /**
						 * Ok, so we have a normal graph. We access its 
						 * warrant via the SWP.assertedBy statement
						 */
	                        warrantURI = res.getProperty( SWP.assertedBy ).getObject().toString();
	                        System.out.println("Warrant graph name: " + warrantURI);
						
	                        /**
						 * In order to get the original canon statement. After all, we
						 * don't really need it any more.
						 */
	                        model.remove( res.getProperty( SWP.assertedBy ) );
	                        canonicalTripleList = new RDFC14NImpl( model, "" ).getCanonicalStringsArray();
						
	                        /**
						 * We've got the name of the associated warrant graph, so we can access it from
						 * the original graphset we inputted.
						 */
	                        NamedGraph verifyGraph = signSet.getGraph( warrantURI );
	                        NamedGraphSet verifySet = new NamedGraphSetImpl();
	                        verifySet.addGraph( verifyGraph );
	                        Model verifyModel = verifySet.asJenaModel( verifyGraph.getGraphName().getURI() );
						
	                        /**
						 * We can now think about what we want from the warrant graph to successfully
						 * verify the signature. Only three things (two really!) we need:
						 * 
						 * The signature
						 * The certificate
						 * The CA certificate (only need this to verify the certificate chain - which we
						 * don't do at present).
						 * 
						 * NOTE: you must have the -----BEGIN CERTIFICATE----- etc. otherwise Java JCE goes
						 * mental and can't figure out that these are certificates.
						 */
	                        ResIterator vitr = verifyModel.listSubjects();
	                        while ( vitr.hasNext() )
	                        {
	                            Resource vres = vitr.nextResource();
	                            if ( vres.hasProperty( SWP.signature ) )
	                            {
	                                signature = vres.getProperty( SWP.signature ).getObject().toString();
	                                System.out.println( "Signature: " + signature);
	                            }
	                            if ( vres.hasProperty( SWP.certificate ) )
	                            {
	                                certificate = "-----BEGIN CERTIFICATE-----\n" 
	                                    + vres.getProperty( SWP.certificate ).getObject().toString()
	                                    + "\n-----END CERTIFICATE-----";
	                                System.out.println( "Certificate: " + certificate);
	                            }
	                            if ( vres.hasProperty( SWP.caCertificate ) )
	                            {
	                                ca = "-----BEGIN CERTIFICATE-----\n" 
	                                    + vres.getProperty( SWP.caCertificate ).getObject().toString()
	                                    + "\n-----END CERTIFICATE-----";
	                                System.out.println( "CA Certificate: " + ca);
	                            }
	                        }
	                        /**
						 * Now we can verify. Give it the signature, certificate and canonical list.
						 * 
						 * Say whether it worked or not - Done!
						 */
	                        SignatureReport sigRep = new SignatureReport( signature, certificate, canonicalTripleList );
	                        if ( sigRep.verify() )
	                        {
	                            String message = "Warrant graph " + warrantURI + " successfully verified.";
	                            System.out.println( message );
	                            log.info( message );
	                        }
	                        else 
	                        {
	                            String message = "Warrant graph " + warrantURI + " verification failure!";
	                            System.out.println( message );
	                            throw new RDFSignatureException( message );
	                        }
	                    }
	                }
	            }
	        }
	        else 
	        {
	            String message = "Input graphset empty!";
	            System.out.println( message );
	            throw new RDFSignatureException( message );
	        }
	    }
	    catch ( RDFSignatureException e )
	    {
	        log.error( e.getMessage() );
	        return false;
	    }
	    return true;
	}
	
	public NamedGraphSet getSignedNGSet()
	{
		return cleanSet;
	}
	
	public String getCertificate()
	{
	    return certificate;
	}
	
	public String getCACertificate()
	{
	    return ca;
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
