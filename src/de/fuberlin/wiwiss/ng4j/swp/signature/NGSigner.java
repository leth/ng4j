/*
 * Created on 03-Nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.signature;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Category;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.RDFSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.signature.impl.NGRDFSignature;
import de.fuberlin.wiwiss.ng4j.swp.signature.impl.RDFSignature;
import de.fuberlin.wiwiss.ng4j.swp.signature.impl.SignatureReport;

public class NGSigner 
{
	static final Category log = Category.getInstance( NGSigner.class );
	private NamedGraphSet cleanSet = new NamedGraphSetImpl();
	private NamedGraphSet tempSet = new NamedGraphSetImpl();
	private NamedGraphSet signSet = new NamedGraphSetImpl();
	private NamedGraphSet verifySet = new NamedGraphSetImpl();
	private String password;
	private String certPath;
	private String warrantName;
	private ArrayList canonicalTripleList;
	private String signature;
	private String certificate;
	private String ca;
	
	public NGSigner( NamedGraphSet signSet, String certPath, String password ) 
			throws URISyntaxException, 
			DataLengthException, 
			InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException, 
			InvalidKeySpecException, 
			NoSuchProviderException, 
			KeyStoreException, 
			UnrecoverableKeyException, 
			CryptoException, 
			IOException, 
			RDFSignatureException,
			Exception
	{
		this.password = password;
		this.certPath = certPath;
		this.signSet = signSet;
	}
	
	public NGSigner ( NamedGraphSet signSet )
	{
		this.signSet = signSet;
	}
	
	/**
	 * 
	 * Sign()
	 * 
	 * This method takes a graphSet which does not
	 * already contain any warrant graphs.
	 * 
	 * Each graph in the set is canonicalised and signed,
	 * and attached with a warrant graph with signature
	 * information. The warrant graph itself is not
	 * signed.
	 * 
	 * A later revision will check for previous warrants
	 * and sign those if they have not already been signed.
	 * 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeySpecException
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean sign() throws InvalidKeyException, 
	NoSuchAlgorithmException, 
	SignatureException, 
	InvalidKeySpecException, 
	URISyntaxException, 
	IOException,
	Exception
	{
	    try {
		if ( !signSet.isEmpty() )
		{	
			//Get a list of all graphs
			Iterator itr = signSet.listGraphs();
			
			/**
			 * Now, for each graph we want to place it
			 * in its own graphset. We do this so we can
			 * easily manipulate it as a Jena Model.
			 */
			while ( itr.hasNext() ) 
			{
				tempSet = new NamedGraphSetImpl();
				NamedGraph ng = ( NamedGraph ) itr.next();
				tempSet.addGraph( ng );
				NGRDFSignature rdfSig = new NGRDFSignature( tempSet );

				System.out.println("Create a new warrant graph: ");
				
				/**
				 * We now have a nice clean graph along with its
				 * canonicalisation. We can now proceed to generate
				 * a signature based on this information.
				 * 
				 * We need to provide an X.509 certificate at present.
				 * In the future we might want to add support for PGP
				 * keys etc.
				 */
				SignatureReport sr1 = new SignatureReport( 
					new RDFSignature(
							new File( certPath ), password ),
									rdfSig.getCanonicalTripleListString() );
				
				/**
				 * Attach the signature to the graph that has been signed.
				 * 
				 * We basically create a warrant graph, then add the
				 * SWP.assertedBy statement to the other graph.
				 * 
				 * The warrant graph is identified by the SHA1 hash of the 
				 * first graphs canonicalisation.
				 */
				rdfSig.addSignatureReport( sr1 );

				/**
				 * We now need to add each pair of graphs (original and warrant)
				 * to a graphSet of all graphs. The best way at present is to
				 * copy each graph from one into the other.
				 * 
				 */
				Iterator ngitr = rdfSig.toGraphset().listGraphs();

				while ( ngitr.hasNext() )
				{
					cleanSet.addGraph( ( NamedGraph ) ngitr.next() ); 
				}

				/**
				 * Need to empty the tempSet, otherwise the last signature
				 * pair are taken as input on the next iteration!
				 */ 
				tempSet = null;
			}
		}
		else 
		{
		    String message = "Input graphset empty!";
		    throw new RDFSignatureException( message ); 
		}
		
	    }
	    catch ( RDFSignatureException e)
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
