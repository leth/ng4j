/*
 * Created on 29-Oct-2004
 *
 */
package de.fuberlin.wiwiss.ng4j.swp.signature.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Category;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import sun.misc.BASE64Encoder;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.signature.c14n.RDFC14NImpl;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;

public class NGRDFSignature 
{
	static final Category log = Category.getInstance( NGRDFSignature.class );
	protected Model currentModel = null;//The current graph
    protected Model cleanModel = null;//The cleaning graph
    protected NamedGraphSet currentNGSet = new NamedGraphSetImpl();
    protected NamedGraphSet cleanNGSet = new NamedGraphSetImpl();
    protected ArrayList signatureReportList = new ArrayList();   //An array list containing SignatureReports        
    protected ArrayList canonicalTripleList;  //The canonical triple list string of the clean graph
    protected String base;
    protected String currentGraphName;
    
    /**
     * @param rdfFile - the TriX file to manipulate
     * @param base
     * @throws URISyntaxException,
     * 		   InvalidKeyException,N
     * 		   NoSuchAlgorithmException,
     *         SignatureException,
     *         InvalidKeySpecException,
     *         IOException
     */
    public NGRDFSignature( File trixFile, String base ) 
    		throws URISyntaxException, 
			InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException, 
			InvalidKeySpecException, 
			IOException 
	{
    	this.base = base;
    	currentNGSet.read( new FileInputStream(trixFile), base, "TRIX" );
    	cleanNGSet.read( new FileInputStream(trixFile), base, "TRIX" );
    	initialize();
    }
    
    
    /**
     * @param originalNGGraphSet 
     * @throws InvalidKeyException,
     *         NoSuchAlgorithmException,
     *         SignatureException,
     *         InvalidKeySpecException,
     *         URISyntaxException,
     *         IOException
     * 
     * 1) Initialise currentModel and cleanModel.
     * 2) Run initialise()
     * 
     */
    public NGRDFSignature(NamedGraphModel asJenaModel) 
    		throws InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException, 
			InvalidKeySpecException, 
			URISyntaxException, 
			IOException 
	{
    	currentModel.add( asJenaModel );
    	cleanModel.add( asJenaModel );
    	initialize();
    }
    
    public NGRDFSignature( NamedGraphSet graphSet ) 
    		throws InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException, 
			InvalidKeySpecException, 
			URISyntaxException, 
			IOException
    {
    	currentNGSet = graphSet;
    	cleanNGSet = graphSet;
    	initialize();
    }
    
    /**
     * remove the paste signature to the graph and fill the signature report list
     * @throws InvalidKeyException,
     * 		NoSuchAlgorithmException,
     * 		SignatureException,
     * 		InvalidKeySpecException,
     * 		URISyntaxException,
     * 		IOException
     * 
     * 1) Output TriX format of graph.
     * 2) Clean the cleanModel. This means removing all SWP statements from the main graph.
     * 3) 
     */
    private void initialize() 
    		throws InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException, 
			InvalidKeySpecException, 
			URISyntaxException, 
			IOException
	{
    	
    	/*
    	 * Ok, we need to check how many graphs are in the current NGSet. If there is less than 1
    	 * or more than 1, we have a problem; we only want 1 graph!
    	 * 
    	 * 
    	 * 
    	 * Once we have our graph, we extract the name
    	 */
    	Iterator itr = currentNGSet.listGraphs();
    	System.out.println( "SignSet has " + currentNGSet.countGraphs() + " graph(s)." );
    	NamedGraph tempNG = null;
    	if ( currentNGSet.countGraphs() == 1 ) 
    	{
    		while ( itr.hasNext() )
    		{
    			 tempNG = ( NamedGraph ) itr.next();
    		}
    	}
    	else 
    	{
    		System.out.println( "Graph too large!" );
    		currentNGSet.write( System.out, "TRIX" );
    	}
    	
    	/*
    	 * Create Jena representation of the graphset, using the graphs name we got
    	 * previously.
    	 * 
    	 * Check to see if there are any triples containing SWP.assertedBy or SWP.quotedBy 
    	 * in the subgraph.
    	 * 
    	 */
    	currentGraphName = tempNG.getGraphName().getURI();
    	currentModel = currentNGSet.asJenaModel( currentGraphName );
    	cleanModel = cleanNGSet.asJenaModel( currentGraphName );
    	
    	clean();
    	fillSignatureList();
    }
    
    private void clean() 
    {
    	/*
    	 * Remove any triples from the graph that point to a warrant graph.
    	 * 
    	 * If the triple exists, remove the associated warrant graph from graphset.
    	 */
        
    	ResIterator rti = cleanModel.listSubjects();
    	
		while ( rti.hasNext() )
        {
        	Resource res = rti.nextResource();
            if ( res.hasProperty( SWP.assertedBy ) ) 
            {
            	cleanNGSet.removeGraph( res.getRequiredProperty( SWP.assertedBy ).getObject().toString() );
            	cleanModel.remove( res.getProperty( SWP.assertedBy ) );
            }
            if ( res.hasProperty( SWP.quotedBy ) )
            {
            	cleanNGSet.removeGraph( res.getRequiredProperty( SWP.quotedBy ).getObject().toString() );
            	cleanModel.remove( res.getProperty( SWP.quotedBy ) );
            }
        }
        
        
        // Unlike the dbin solution we don't have any reifications to worry about
        
        /*
         * Print out information about the clean graphset (as a Jena model).
         * 
         * Show its canonical representation as well.
         */
        //System.out.println( "Clean graph: \n" );
        //cleanModel.write( System.out, "TRIX" );
        canonicalTripleList = new RDFC14NImpl( cleanModel, base ).getCanonicalStringsArray();
        System.out.println( "Canonical triple list of Named Graph: "+'\n'+canonicalTripleList.toString() );    
    }
                                            
    /**
     * @return the canonical triple ArrayList
     */
    public ArrayList getCanonicalTripleList()
    {
    	return canonicalTripleList;
    }
    
    public NamedGraphSet getNamedGraphSet()
    {
    	return currentNGSet;
    }
    
    
    /**
     * @return the canonical triple list string. As [subject1 predicate1 object1,subject2 predicate2 object2,....]
     */
    public String getCanonicalTripleListString()
    {
    	return canonicalTripleList.toString();
    }
    
    /**  return true if all the signature in the signatureList are valid */
    public boolean isAllValid() 
    {
    	boolean test = true;
    	for( int i=0;i<signatureReportList.size();i++ )
    	{
    		SignatureReport sr = ( SignatureReport ) signatureReportList.get( i );
    		if ( sr.isValid() )
    		{
    			continue;
    		}
    		else test = false;
        }                
        return test;
    }
    
    /**  Add a SignatureReport to the signatureList */
    
    public void addSignatureReport( SignatureReport sr ) 
    		throws InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException 
	{
    	signatureReportList.add( sr );                
    }        
    
    public ArrayList getSignatureReporttList() 
    {
    	return signatureReportList;
    };
    
    /**  Remove a SignatureReport from the signatureList */
    public boolean removeSignatureReport( SignatureReport signatureReport ) 
    {
    	boolean test = false;
    	for(int i=0;i<signatureReportList.size();i++)
    	{
    		SignatureReport sr = ( SignatureReport ) signatureReportList.get( i );
    		if ( sr.equals(signatureReport) )
    		{
    			test = signatureReportList.remove( sr );
    		}
    	}                
    	return test;
    }  
    
    /**  With this function, this object will return and RDF with ONLY the valid signatures in the signatureList */
    public NamedGraphSet toGraphset() throws Exception 
	{
    	System.out.println( "Number of signature reports to add: " + signatureReportList.size() );
    	for(int i=0;i<signatureReportList.size();i++)
    	{
    		SignatureReport sr = ( SignatureReport )signatureReportList.get( i );
    		if ( sr.isValid() )
    			{
    				this.appendSignature( ( SignatureReport )signatureReportList.get( i ) );
    			}
    		else continue;
    	}
    	return cleanNGSet;
    }
    /**
     * 
     * Attach a signatureReport to the current model.
     * 
     * Unlike the DBin solution, we will create an SWP warrant graph which we will then add to the model.
     *  
     * It's important to note that we are signifying the signing of the first graph with the warrant graph. 
     * This means the first graph will reference itself with the warrant graph:
     * 
     * :G1{....
     * 	   :G1 swp:assertedby :G2
     *    }
     * 
     * :G2{....
     * 
     * 	  } 
     * 
     * FIXME AT PRESENT THIS WARRANT GRAPH IS NOT PERFORMATIVE.
     * TODO Add performative semantics by signing the warrant graph itself.
     * 
     * @param finalModel 
     * @param signatureReport
     * @throws Exception
     */
    private void appendSignature( SignatureReport signatureReport ) 
    		throws Exception
	{
    	String warrantGraphName = "urn:sha1:" + getDigest( canonicalTripleList.toString() );
    	Model warrantModel = cleanNGSet.asJenaModel( warrantGraphName );
    	
    	/*
    	 * SWP.Authority resource.
    	 */
    	Resource authority = warrantModel.createResource( SWP.Authority );
    	
    	/*
    	 * By default the encoded certificate is just a byte[]. I've reencoded it as a base64binary string
    	 * like that described in the SWP paper.
    	 */
    	BASE64Encoder encoder = new BASE64Encoder();
    	authority.addProperty( SWP.certificate, encoder.encode( signatureReport.getCertificate().getEncoded() ) );
    	
    	Resource ca = warrantModel.createResource( SWP.CertificationAuthority );
    	ca.addProperty( SWP.caCertificate, encoder.encode( signatureReport.getCA().getEncoded() ) );
    	
    	/*
    	 * SWP.Warrant resource.
    	 */
    	Resource warrant = warrantModel.createResource( SWP.Warrant );
    	
    	/*
    	 * Add signature value of associated graph. It's already a base64binary string so life is easy.
    	 */
    	warrant.addProperty( SWP.signature, signatureReport.getSignatureValue() );
    	
    	/*
    	 * Add signature method as found in the client certificate. This is either SHA1/DSA or SHA1/RSA.
    	 * 
    	 * TODO This needs to become a literal URI, which can be dereferenced on the Web to retrieve a document
    	 * describing the method of forming the signature in detail.
    	 */
    	warrant.addProperty( SWP.signatureMethod, signatureReport.getSigMethod() );
    	
    	/*
    	 * Connect up the dots and add an authority to the warrant.
    	 */
    	warrant.addProperty( SWP.authority, authority.getId() );
    	
    	warrant.addProperty( SWP.certificationAuthority, ca.getId() );
    	
    	cleanNGSet.getGraph( currentGraphName ).add( new Triple( 
    													Node.createURI( currentGraphName ), 
														Node.createURI( SWP.assertedBy.getURI() ), 
														Node.createURI( warrantGraphName ) ) );
    	
    }
    
    /** 
     * This method analises the current model to list and test all warrant graphs asserted by it.
     * 
     * The model is tested for any occurances of swp:assertedBy. The graph name is used to assess 
     * the signature information.
     * 
     * Once found, the signature value and public key, along with the canonical triple list from the
     * clean model are put into a SignatureReport which can then verify the signature.
     *  
     **/
    
    private void fillSignatureList() 
    		throws URISyntaxException, 
			InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException, 
			InvalidKeySpecException, 
			IOException 
	{                
            
    	ResIterator rti = currentModel.listSubjects();
    	
    	while ( rti.hasNext() )
    	{
    		Resource res = rti.nextResource();
    		if ( res.hasProperty( SWP.certificate ) && res.hasProperty( SWP.signature ) )
    		{
    			String publicKey = res.getRequiredProperty( SWP.certificate ).getObject().toString();
    			String signValue = res.getRequiredProperty(SWP.signature).getObject().toString();
    			SignatureReport sr = new SignatureReport( signValue, publicKey, canonicalTripleList.toString() );  
    			signatureReportList.add(sr);  //add signature to list
    		}
    	}   
    }
    
    public static String getDigest(String data) 
    {
		Security.addProvider(new BouncyCastleProvider());
			
		Digest  digest = new SHA1Digest();
		byte[]  resBuf = new byte[digest.getDigestSize()];
		String  resStr;
		digest.update(data.getBytes(), 0, data.getBytes().length);
		digest.doFinal(resBuf, 0);
		    
		byte[] res = Hex.encode(resBuf);
			
		return new String(res);
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
