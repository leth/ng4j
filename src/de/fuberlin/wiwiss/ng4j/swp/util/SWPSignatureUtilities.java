/*
 * Created on 06-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.axis.components.uuid.SimpleUUIDGen;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider; 
import org.bouncycastle.jce.provider.JDKDigestSignature;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.util.encoders.Hex;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.c14n.RDFC14NImpl;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPAlgorithmNotSupportedException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPCertificateException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPCertificateValidationException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPInvalidKeyException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPNoSuchAlgorithmException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPNoSuchDigestMethodException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPValidationException;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP_V;

/**
 * 
 * Last commit info    :   $Author: hartig $
 * $Date: 2008/10/29 18:36:46 $
 * $Revision: 1.12 $
 * 
 * 
 * SWPSignatureUtilities
 * 
 * <p>
 * This class contains a lot of supporting methods for manipulating
 * digital signatures in the context of SWP.
 * </p>
 * 
 * We now support:
 * 
 * <ul>
 * 	<li>X.509 "Identity" Certificates</li>
 *  <li>OpenPGP Certificates</li>
 * </ul>
 * 
 * We are investigating supporting other PKIs including:
 * 
 * <ul>
 * 	<li>Simple Public Key Infrastructure (SPKI)</li>
 * </ul>
 * 
 * If you have any questions, please contact me at:
 * 
 * erw@it-innovation.soton.ac.uk
 * 
 * @author Rowland Watkins
 * 
 * Certificate and Chain verification code based on code by Svetlin Nakov
 */
public class SWPSignatureUtilities 
{
    private static final Logger logger = Logger.getLogger( SWPSignatureUtilities.class );
//    private static boolean debug = logger.isDebugEnabled();
    private static boolean info = logger.isInfoEnabled();
    // nb: the following must agree with the Signature.getInstance(...) names
    public static final String ALG_ID_SIGNATURE_SHA1withRSA 	= "SHA1withRSA";
	public static final String ALG_ID_SIGNATURE_SHA224withRSA 	= "SHA224withRSA";
	public static final String ALG_ID_SIGNATURE_SHA256withRSA 	= "SHA256withRSA";
	public static final String ALG_ID_SIGNATURE_SHA384withRSA 	= "SHA384withRSA";
	public static final String ALG_ID_SIGNATURE_SHA512withRSA 	= "SHA512withRSA";
	
    public static final String ALG_ID_SIGNATURE_SHA1withDSA 	= "SHA1withDSA";
    
    public static final String X509_CERTIFICATE_TYPE 			= "X.509";
//    private static final String SPKI_CERTIFICATE_TYPE			= "SPKI";
//    private static final String CERTIFICATION_CHAIN_ENCODING 	= "PkiPath";
//    private static final String CERT_CHAIN_VALIDATION_ALGORITHM = "PKIX";
    
    private static SimpleUUIDGen uuidGen = new SimpleUUIDGen ();
    
    /**
     * <p>
     * Takes a NamedGraph and returns its canonical
     * form.
     * </p>
     * <p>
     * This method fixes the flaw where the graph name is
     * excluded from the canonical form.
     * </p>
     * @param Named Graph graph
     * @return canonicalString
     */
    public static String getCanonicalGraph( NamedGraph graph )
    {
		ArrayList last = new ArrayList();
        NamedGraphSet set = new NamedGraphSetImpl();
		
		try
		{
			set.addGraph( graph );
			Model model = set.asJenaModel( graph.getGraphName().toString() );
			ArrayList canonicalTripleList = new RDFC14NImpl( model ).getCanonicalStringsArray();
        
			Iterator litr = canonicalTripleList.iterator();
        
			last.add( graph.getGraphName().toString() );
			while ( litr.hasNext() )
			{
				last.add( litr.next() );
			}
		}
		finally
		{
			set.removeGraph( graph.getGraphName() );
		}
		
       return last.toString();
    }
    
    /**
     * 
     * Takes a NamedGraphSet and returns its canonical
     * form.
     * 
     * @param NamedGraphSet set
     * @return canonicalString
     */
    public static String getCanonicalGraphSet( NamedGraphSet set )
    {
    	String graph = "urn:uuid"+ uuidGen.nextUUID();
		ArrayList result = new ArrayList();
		
		Model model = set.asJenaModel( graph );
		ArrayList canonicalTripleList = new RDFC14NImpl( model ).getCanonicalStringsArray();
		set.removeGraph( graph );
		Iterator itr = set.listGraphs();
		while ( itr.hasNext() )
		{
			NamedGraph grph = ( NamedGraph )itr.next();
			result.add( grph.getGraphName().toString() );
		}
		Collections.sort( result );
		for ( Iterator it = canonicalTripleList.iterator(); it.hasNext(); )
		{
			result.add( it.next() );
		}
		
    	return result.toString();
    }
    
    /**
     * 
     * @param graph
     * @param digestMethod
     * @return digest
     * @throws SWPNoSuchDigestMethodException
     */
    public static String calculateDigest( NamedGraph graph, Node digestMethod )
    throws SWPNoSuchDigestMethodException
    {
        String data = getCanonicalGraph( graph );
        Security.addProvider( new BouncyCastleProvider() );
		Digest digest;
	
        byte[] res = null;
        if ( digestMethod.equals( SWP.JjcRdfC14N_sha1 ) )
        {
            digest = new SHA1Digest();
        }
        else if ( digestMethod.equals( SWP.JjcRdfC14N_sha224 ) )
		{
			digest = new SHA224Digest();
		}
		else if ( digestMethod.equals( SWP.JjcRdfC14N_sha256 ) )
		{
			digest = new SHA256Digest();
		}
		else if ( digestMethod.equals( SWP.JjcRdfC14N_sha384 ) )
		{
			digest = new SHA384Digest();
		}
		else if ( digestMethod.equals( SWP.JjcRdfC14N_sha512 ) )
		{
			digest = new SHA512Digest();
            
		}
        else
        {
            throw new SWPNoSuchDigestMethodException( "The digest method: "+digestMethod +
            		" does not exist." );
        }
        
		byte[]  resBuf = new byte[ digest.getDigestSize() ];
        digest.update( data.getBytes(), 0, data.getBytes().length );
        digest.doFinal( resBuf, 0 );
    
        res = Hex.encode( resBuf );
		
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode( res );
    }
    
    /**
     * 
     * @param set
     * @param digestMethod
     * @return digest
     * @throws SWPNoSuchDigestMethodException
     */
    public static String calculateDigest( NamedGraphSet set, Node digestMethod )
    throws SWPNoSuchDigestMethodException
    {
    	String data = getCanonicalGraphSet( set );
        Security.addProvider( new BouncyCastleProvider() );
	
        byte[] res = null;
        if ( digestMethod.equals( SWP.JjcRdfC14N_sha1 ) )
        {
            Digest  digest = new SHA1Digest();
            byte[]  resBuf = new byte[ digest.getDigestSize() ];
            digest.update( data.getBytes(), 0, data.getBytes().length );
            digest.doFinal( resBuf, 0 );
	    
            res = Hex.encode( resBuf );
        }
        else if ( digestMethod.equals( SWP.JjcRdfC14N_sha224 ) )
		{
			 Digest  digest = new SHA224Digest();
	         byte[]  resBuf = new byte[ digest.getDigestSize() ];
	         digest.update( data.getBytes(), 0, data.getBytes().length );
	         digest.doFinal( resBuf, 0 );
		    
	         res = Hex.encode( resBuf );
		}
		else if ( digestMethod.equals( SWP.JjcRdfC14N_sha256 ) )
		{
			 Digest  digest = new SHA256Digest();
	         byte[]  resBuf = new byte[ digest.getDigestSize() ];
	         digest.update( data.getBytes(), 0, data.getBytes().length );
			 digest.doFinal( resBuf, 0 );
		    
	         res = Hex.encode( resBuf );
		}
		else if ( digestMethod.equals( SWP.JjcRdfC14N_sha384 ) )
		{
			 Digest  digest = new SHA384Digest();
	         byte[]  resBuf = new byte[ digest.getDigestSize() ];
	         digest.update( data.getBytes(), 0, data.getBytes().length );
			 digest.doFinal( resBuf, 0 );
		    
	         res = Hex.encode( resBuf );
		}
		else if ( digestMethod.equals( SWP.JjcRdfC14N_sha512 ) )
		{
			 Digest  digest = new SHA512Digest();
	         byte[]  resBuf = new byte[ digest.getDigestSize() ];
	         digest.update( data.getBytes(), 0, data.getBytes().length );
	         digest.doFinal( resBuf, 0 );
		    
	         res = Hex.encode( resBuf );
		}
        else
        {
            throw new SWPNoSuchDigestMethodException( "The digest method: "+digestMethod +
            		" does not exist." );
        }
        
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode( res );
    }

    /** Retrieve a Signature object of the given type using BouncyCastle.
     * signatureMethod is the URI used for error reporting.
     */
    public static Signature getSignature( String type, Node signatureMethod )
    throws SWPNoSuchAlgorithmException {

    	try {
    		return Signature.getInstance(type,
    				new BouncyCastleProvider() );
    	} catch ( NoSuchAlgorithmException e ) {
    		logger.fatal( ALG_ID_SIGNATURE_SHA1withDSA +" not found! " +e.getMessage() );
    		throw new SWPNoSuchAlgorithmException( "The signature" +
    				"method: "+signatureMethod.toString()+" does not exist.", e ); 
    	}
    }
    
    /**
     * 
     * @param graph
     * @param signatureMethod
     * @param key
     * @return signature
     * @throws SWPNoSuchAlgorithmException
     * @throws SWPSignatureException
     * @throws SWPInvalidKeyException
     * @throws SWPAlgorithmNotSupportedException 
     */
    public static String calculateSignature( NamedGraph graph, 
            						  Node signatureMethod, 
            						  Object key ) 
    throws SWPNoSuchAlgorithmException, 
    SWPSignatureException, 
    SWPInvalidKeyException, 
    SWPAlgorithmNotSupportedException
    {
		Security.addProvider( new BouncyCastleProvider() );
        String canonicalGraph = getCanonicalGraph( graph );
		
        String signature = null;
        /**
         * Let's figure out which signature algorithm we're 
         * using.
         */
        Signature sig = null;
            
		if ( key instanceof RSAPrivateKey  )
		{
			if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA1withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha224 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA224withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA224withRSA );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha256 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA256withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA256withRSA );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha384 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA384withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA384withRSA );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha512 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA512withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA512withRSA );
	        }
	        else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
	        {
				throw new SWPAlgorithmNotSupportedException( "RSA private key detected. DSA encyption is not supported." );
	        }
	        else 
	        {
	            throw new SWPNoSuchAlgorithmException( "The signature" +
	            		"method: "+signatureMethod+" does not exist." );
	        }
		}
		else if ( key instanceof PGPPrivateKey )
		{
			if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA1withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha224 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA224withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA224withRSA );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha256 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA256withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA256withRSA );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha384 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA384withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA384withRSA );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha512 ) ) 
	        {
				sig = getSignature(ALG_ID_SIGNATURE_SHA512withRSA, signatureMethod);
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA512withRSA );
	        }
	        else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
	        {
				throw new SWPAlgorithmNotSupportedException( "RSA private key detected. PGP DSA encryption is not supported." );
	        }
	        else 
	        {
	            throw new SWPNoSuchAlgorithmException( "The signature" +
	            		"method: "+signatureMethod+" does not exist." );
	        }
		}
		else if ( key instanceof DSAPrivateKey )
		{
			if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
	        {
				throw new SWPAlgorithmNotSupportedException( "DSA private key detected. RSA is not supported." );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha224 ) ) 
	        {
				throw new SWPAlgorithmNotSupportedException( "DSA private key detected. RSA is not supported." );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha256 ) ) 
	        {
				throw new SWPAlgorithmNotSupportedException( "DSA private key detected. RSA is not supported." );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha384 ) ) 
	        {
				throw new SWPAlgorithmNotSupportedException( "DSA private key detected. RSA is not supported." );
	        }
			else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha512 ) ) 
	        {
				throw new SWPAlgorithmNotSupportedException( "DSA private key detected. RSA is not supported." );
	        }
	        else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
	        {
				try 
				{
					sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withDSA, new BouncyCastleProvider() );
				}
				catch ( NoSuchAlgorithmException e )
		    	{
		    		logger.fatal( ALG_ID_SIGNATURE_SHA1withDSA +" not found! " +e.getMessage() );
		    		throw new SWPNoSuchAlgorithmException( "The signature" +
		    				"method: "+signatureMethod+" does not exist.", e ); 
		    	}
				if ( info )
					logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withDSA );
	        }
	        else 
	        {
	            throw new SWPNoSuchAlgorithmException( "The signature" +
	            		"method: "+signatureMethod+" does not exist." );
	        }
		}
        
        try 
        {
			
			if ( key instanceof PrivateKey )
			{
				sig.initSign( ( PrivateKey )key );
			}
			else if ( key instanceof PGPPrivateKey )
			{
				PGPPrivateKey pkey = ( PGPPrivateKey )key;
				sig.initSign( pkey.getKey() );
			}
			else 
				throw new SWPInvalidKeyException( "No suitable private key found." );
            
            sig.update( canonicalGraph.getBytes( "UTF-8" ) );
        } 
        catch ( InvalidKeyException e1 ) 
        { 
            logger.fatal( "Public key supplied is invalid. "+ e1.getMessage() );
            throw new SWPInvalidKeyException( "Public key supplied is invalid." );
        } 
        catch ( SignatureException e3 ) 
        {
            logger.fatal( "Error updating input data. "+ e3.getMessage() );
            throw new SWPSignatureException( "Error updating input data." );
        } 
		catch ( UnsupportedEncodingException e ) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
            
        try 
        {
            BASE64Encoder encoder = new BASE64Encoder();
            signature = encoder.encodeBuffer( sig.sign() );
        } 
        catch ( SignatureException e2 ) 
        {
            logger.fatal( "Error generating signature. "+ e2.getMessage() );
            throw new SWPSignatureException( "Error generating signature." );
        }
        
        return signature;
    }
    
    /**
     * 
     * @param set
     * @param signatureMethod
     * @param key
     * @return signature
     * @throws SWPNoSuchAlgorithmException
     * @throws SWPSignatureException
     * @throws SWPInvalidKeyException
     */
    public static String calculateSignature( NamedGraphSet set, 
			  								Node signatureMethod, 
			  								PrivateKey key ) 
    throws SWPNoSuchAlgorithmException, 
    SWPSignatureException, 
    SWPInvalidKeyException
    {
    	String canonicalGraph = getCanonicalGraphSet( set );
    	String signature = null;
    	/**
    	 * Let's figure out which signature algorithm we're 
    	 * using.
    	 */
    	Signature sig = null;
    	
    	if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA1withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
    	}
    	else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha224 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA224withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA224withRSA );
    	}
    	else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha256 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA256withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA256withRSA );
    	}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha384 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA384withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA384withRSA );
    	}
    	else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha512 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA512withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA512withRSA );
    	}
    	else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
    	{
			try 
			{
				sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withDSA, new BouncyCastleProvider() );
			}
			catch ( NoSuchAlgorithmException e )
	    	{
	    		logger.fatal( ALG_ID_SIGNATURE_SHA1withDSA +" not found! " +e.getMessage() );
	    		throw new SWPNoSuchAlgorithmException( "The signature" +
	    				"method: "+signatureMethod+" does not exist.", e ); 
	    	}
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withDSA );
    	}
    	else 
    	{
    		throw new SWPNoSuchAlgorithmException( "The signature" +
    				"method: "+signatureMethod+" does not exist.");
    	}

    	try 
    	{
    		sig.initSign( key );
    		sig.update( canonicalGraph.getBytes() );
    	} 
    	catch ( InvalidKeyException e1 ) 
    	{ 
    		logger.fatal( "Public key supplied is invalid. "+ e1.getMessage() );
    		throw new SWPInvalidKeyException( "Public key supplied is invalid." );
    	} 
    	catch ( SignatureException e3 ) 
    	{
    		logger.fatal( "Error updating input data. "+ e3.getMessage() );
    		throw new SWPSignatureException( "Error updating input data." );
    	} 

    	try 
    	{
    		BASE64Encoder encoder = new BASE64Encoder();
    		signature = encoder.encodeBuffer( sig.sign() );
    	} 
    	catch ( SignatureException e2 ) 
    	{
    		logger.fatal("Error generating signature. "+ e2.getMessage() );
    		throw new SWPSignatureException( "Error generating signature." );
    	}

    	return signature;
    }
    
    public static Signature getSignatureAlgorithm( Node signatureMethod ) 
	throws SWPNoSuchAlgorithmException
	{
		Signature sig = null;
		if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA1withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
    	}
    	else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha224 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA224withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA224withRSA );
    	}
    	else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha256 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA256withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA256withRSA );
    	}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha384 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA384withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA384withRSA );
    	}
    	else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha512 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA512withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA512withRSA );
    	}
    	else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
    	{
			try 
			{
				sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withDSA, new BouncyCastleProvider() );
			}
			catch ( NoSuchAlgorithmException e )
	    	{
	    		logger.fatal( ALG_ID_SIGNATURE_SHA1withDSA +" not found! " +e.getMessage() );
	    		throw new SWPNoSuchAlgorithmException( "The signature" +
	    				"method: "+signatureMethod+" does not exist.", e ); 
	    	}
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withDSA );
    	}
    	else 
    	{
    		throw new SWPNoSuchAlgorithmException("The signature" +
    				"method: "+signatureMethod+" does not exist.");
    	}
		
		return sig;
	}

    /**
     * 
     * @param graph
     * @param signatureMethod
     * @param signatureValue
     * @param pem
     * @return boolean
     * @throws SWPNoSuchAlgorithmException
     * @throws SWPValidationException
     * @throws SWPInvalidKeyException
     * @throws SWPSignatureException
     */
    public static boolean validateSignature( NamedGraph graph, 
            						  Node signatureMethod, 
            						  String signatureValue, 
            						  String pem ) 
    throws SWPNoSuchAlgorithmException,
    SWPValidationException, 
    SWPInvalidKeyException, 
    SWPSignatureException
    {
        String canonicalGraph = getCanonicalGraph( graph );
        X509Certificate certificate;
		
        Signature sig = null;
        byte[] signature = null;
        
		if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA1withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha224 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA224withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA224withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha256 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA256withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA256withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha384 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA384withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA384withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha512 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA512withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA512withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
		{
			try 
			{
				sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withDSA, new BouncyCastleProvider() );
			}
			catch ( NoSuchAlgorithmException e )
	    	{
	    		logger.fatal( ALG_ID_SIGNATURE_SHA1withDSA +" not found! " +e.getMessage() );
	    		throw new SWPNoSuchAlgorithmException( "The signature" +
	    				"method: "+signatureMethod+" does not exist.", e ); 
	    	}
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withDSA );
		}
		else 
		{
			throw new SWPNoSuchAlgorithmException( "The signature" +
					"method: "+signatureMethod+" does not exist." );
		}
		
        try 
        {
        	CertificateFactory cf = CertificateFactory.getInstance( X509_CERTIFICATE_TYPE );
    		certificate = ( X509Certificate ) cf.generateCertificate( new ByteArrayInputStream( pem.getBytes() ) );
            BASE64Decoder decoder = new BASE64Decoder();
        	signature = decoder.decodeBuffer( signatureValue );
        	sig.initVerify( certificate.getPublicKey() );
        	sig.update( canonicalGraph.getBytes( "UTF-8" ) );
        } 
        catch ( IOException e1 ) 
        {	
            logger.fatal( "Unable to access signature: " +e1.getMessage() );
            throw new SWPValidationException( "I/O error: Unable to access " +
            		"signature value.", e1 );
        }
        catch ( InvalidKeyException e2 ) 
        {
			logger.fatal( "Public key supplied is invalid. "+ e2.getMessage() );
            throw new SWPInvalidKeyException( "Public key supplied is invalid." );
        }
    	catch ( SignatureException e3 ) 
    	{
			logger.fatal( "Error updating input data. "+ e3.getMessage() );
            throw new SWPSignatureException( "Error updating input data." );
        } 
    	catch ( CertificateException e ) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
    	try 
    	{
            return sig.verify( signature );
        } 
    	catch ( SignatureException e4 ) 
    	{
			logger.error( "Error verifying signature. "+e4.getMessage() );
            throw new SWPSignatureException( "Error verifying signature." );
        }
    }
    
    /**
     * 
     * @param graph
     * @param signatureMethod
     * @param signatureValue
     * @param certificate
     * @return boolean
     * @throws SWPNoSuchAlgorithmException
     * @throws SWPValidationException
     * @throws SWPInvalidKeyException
     * @throws SWPSignatureException
     * @throws SWPCertificateException 
     */
    public static boolean validateSignature( NamedGraph graph, 
			  Node signatureMethod, 
			  String signatureValue, 
			  X509Certificate certificate ) 
    throws SWPNoSuchAlgorithmException,
    SWPValidationException, 
    SWPInvalidKeyException, 
    SWPSignatureException, 
    SWPCertificateException
	{
    	String canonicalGraph = getCanonicalGraph( graph );
    	
    	Signature sig = null;
    	byte[] signature = null;
		        
		if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA1withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha224 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA224withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA224withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha256 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA256withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA256withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha384 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA384withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA384withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha512 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA512withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA512withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
		{
			try 
			{
				sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withDSA, new BouncyCastleProvider() );
			}
			catch ( NoSuchAlgorithmException e )
	    	{
	    		logger.fatal( ALG_ID_SIGNATURE_SHA1withDSA +" not found! " +e.getMessage() );
	    		throw new SWPNoSuchAlgorithmException( "The signature" +
	    				"method: "+signatureMethod+" does not exist.", e ); 
	    	}
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withDSA );
		}
		else 
		{
			throw new SWPNoSuchAlgorithmException( "The signature" +
					"method: "+signatureMethod+" does not exist." );
		}
		
    	try 
    	{
    		BASE64Decoder decoder = new BASE64Decoder();
			if ( signatureValue != null )
			{
				signature = decoder.decodeBuffer( signatureValue );
			}
			else throw new SWPSignatureException( "The input signature value was empty." );
			if ( certificate.getPublicKey() != null )
			{
				sig.initVerify( certificate.getPublicKey() );
			}
			else throw new SWPCertificateException( "Input X.509 certificate was found to be empty." );
    		sig.update( canonicalGraph.getBytes( "UTF-8" ) );
    	} 
    	catch ( IOException e ) 
    	{	
    		logger.fatal( "Unable to access signature: " +e.getMessage() );
    		throw new SWPValidationException( "I/O error: Unable to access " +
    				"signature value.", e );
    	}
    	catch ( InvalidKeyException e ) 
    	{
    		logger.fatal( "Public key supplied is invalid. "+ e.getMessage() );
    		throw new SWPInvalidKeyException( "Public key supplied is invalid." );
    	}
    	catch ( SignatureException e ) 
    	{
    		logger.fatal( "Error updating input data. "+ e.getMessage() );
    		throw new SWPSignatureException( "Error updating input data. "+e.getMessage() );
    	} 

    	try 
    	{
    		return sig.verify( signature );
    	} 
    	catch ( SignatureException e4 ) 
    	{
    		logger.fatal( "Error verifying signature. "+e4.getMessage() );
    		throw new SWPSignatureException( "Error verifying signature." );
    	}
	}

    /**
     * 
     * @param graph
     * @param signatureMethod
     * @param signatureValue
     * @param certificate
     * @param trustedCertificates
     * @return boolean
     * @throws SWPNoSuchAlgorithmException
     * @throws SWPValidationException
     * @throws SWPInvalidKeyException
     * @throws SWPSignatureException
     */
    public static boolean validateSignature( NamedGraph graph, 
            						  Node signatureMethod, 
            						  String signatureValue,
            						  X509Certificate certificate, 
            						  ArrayList trustedCertificates ) 
    throws SWPNoSuchAlgorithmException,
    SWPValidationException, 
    SWPInvalidKeyException, 
    SWPSignatureException
    {
        String canonicalGraph = getCanonicalGraph( graph );
        boolean result = false;
        
        try 
        {
            verifyCertificate( certificate, trustedCertificates );
        }
        catch ( CertificateExpiredException e ) 
        {
            logger.warn( "Certificate has expired." );
            throw new SWPValidationException( "Certificate has expired.", e );
        }
        catch ( CertificateNotYetValidException e ) 
        {
            logger.warn( "Certificate not yet valid." );
            throw new SWPValidationException( "Certificate not yet valid.", e );
        }
        catch ( GeneralSecurityException e ) 
        {
            logger.warn( "Certificate not signed by some trusted certificates." );
            throw new SWPValidationException( "Certificate not signed by some trusted certificates.", e );
        }
        
        Signature sig;
        byte[] signature;
        
		if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA1withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha224 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA224withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA224withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha256 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA256withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA256withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha384 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA384withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA384withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha512 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA512withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA512withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
		{
			try 
			{
				sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withDSA, new BouncyCastleProvider() );
			}
			catch ( NoSuchAlgorithmException e )
	    	{
	    		logger.fatal( ALG_ID_SIGNATURE_SHA1withDSA +" not found! " +e.getMessage() );
	    		throw new SWPNoSuchAlgorithmException( "The signature" +
	    				"method: "+signatureMethod+" does not exist.", e ); 
	    	}
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withDSA );
		}
		else 
		{
			throw new SWPNoSuchAlgorithmException( "The signature" +
					"method: "+signatureMethod+" does not exist." );
		}
		
        try 
        {
            BASE64Decoder decoder = new BASE64Decoder();
        	signature = decoder.decodeBuffer( signatureValue );
        	sig.initVerify( certificate.getPublicKey() );
        	sig.update( canonicalGraph.getBytes( "UTF-8" ) );
        } 
        catch ( IOException e1 ) 
        {
            logger.fatal( "Unable to access signature: " +e1.getMessage() );
            throw new SWPValidationException( "I/O error: Unable to access " +
                    "signature value.", e1 );
        }
        catch ( InvalidKeyException e2 ) 
        {
            logger.fatal( "Public key supplied is invalid. "+ e2.getMessage() );
            throw new SWPInvalidKeyException( "Public key supplied is invalid." );
        }
    	catch ( SignatureException e3 ) 
    	{
            logger.fatal( "Error updating input data. "+ e3.getMessage() );
            throw new SWPSignatureException( "Error updating input data." );
        } 
        
    	try 
    	{
            result = sig.verify( signature );
        } 
    	catch ( SignatureException e4 ) 
    	{
            logger.fatal("Error verifying signature. "+e4.getMessage() );
            throw new SWPSignatureException( "Error verifying signature." );
        }
        
        return result;
    }

    /**
     * 
     * @param graph
     * @param signatureMethod
     * @param signatureValue
     * @param certificate
     * @param trustedCertificates
     * @param otherCertificates
     * @return boolean
     * @throws SWPNoSuchAlgorithmException
     * @throws SWPValidationException
     * @throws SWPInvalidKeyException
     * @throws SWPSignatureException
     */
    public static boolean validateSignature( NamedGraph graph, 
            						  Node signatureMethod, 
            						  String signatureValue,
            						  X509Certificate certificate, 
            						  ArrayList trustedCertificates, 
            						  ArrayList otherCertificates )
    throws SWPNoSuchAlgorithmException,
    SWPValidationException, 
    SWPInvalidKeyException, 
    SWPSignatureException
    
    {
        String canonicalGraph = getCanonicalGraph( graph );
        boolean result = false;
		
        try 
        {
            verifyCertificate( certificate, trustedCertificates );
        }
        catch ( CertificateExpiredException e ) 
        {
            logger.warn( "Certificate has expired." );
            throw new SWPValidationException( "Certificate has expired.", e );
        }
        catch ( CertificateNotYetValidException e ) 
        {
            logger.warn( "Certificate not yet valid." );
            throw new SWPValidationException( "Certificate not yet valid.", e );
        }
        catch ( GeneralSecurityException e ) 
        {
            logger.warn( "Certificate not signed by some trusted certificates." );
            throw new SWPValidationException( "Certificate not signed by some trusted certificates.", e );
        }
        
        Signature sig;
        byte[] signature;
        
		if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA1withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha224 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA224withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA224withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha256 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA256withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA256withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha384 ) ) 
    	{
			sig = getSignature(ALG_ID_SIGNATURE_SHA384withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA384withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha512 ) ) 
		{
			sig = getSignature(ALG_ID_SIGNATURE_SHA512withRSA, signatureMethod);
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA512withRSA );
		}
		else if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
		{
			try 
			{
				sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withDSA, new BouncyCastleProvider() );
			}
			catch ( NoSuchAlgorithmException e )
	    	{
	    		logger.fatal( ALG_ID_SIGNATURE_SHA1withDSA +" not found! " +e.getMessage() );
	    		throw new SWPNoSuchAlgorithmException( "The signature" +
	    				"method: "+signatureMethod+" does not exist.", e ); 
	    	}
			if ( info )
				logger.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withDSA );
		}
		else 
		{
			throw new SWPNoSuchAlgorithmException( "The signature" +
					"method: "+signatureMethod+" does not exist." );
		}
		
        try 
        {
            BASE64Decoder decoder = new BASE64Decoder();
        	signature = decoder.decodeBuffer( signatureValue );
        	sig.initVerify( certificate.getPublicKey() );
        	sig.update( canonicalGraph.getBytes( "UTF-8" ) );
        } 
        catch ( IOException e1 ) 
        {
            logger.fatal( "Unable to access signature: " +e1.getMessage() );
            throw new SWPValidationException( "I/O error: Unable to access " +
                    "signature value.", e1 );
        }
        catch ( InvalidKeyException e2 ) 
        {
            logger.fatal( "Public key supplied is invalid. "+ e2.getMessage() );
            throw new SWPInvalidKeyException( "Public key supplied is invalid." );
        }
    	catch ( SignatureException e3 ) 
    	{
            logger.fatal( "Error updating input data. "+ e3.getMessage() );
            throw new SWPSignatureException( "Error updating input data." );
        } 
        
    	try 
    	{
            result = sig.verify( signature );
        } 
    	catch ( SignatureException e4 ) 
    	{
            logger.fatal("Error verifying signature. "+e4.getMessage() );
            throw new SWPSignatureException( "Error verifying signature." );
        }
        
        return result;
    }

    /**
     * Verifies a certificate. Checks its validity period and tries to find a trusted
     * certificate from given list of trusted certificates that is directly signed given
     * certificate. The certificate is valid if no exception is thrown.
     *
     * @param aCertificate the certificate to be verified.
     * @param aTrustedCertificates a list of trusted certificates to be used in
     * the verification process.
     *
     * @throws CertificateExpiredException if the certificate validity period is expired.
     * @throws CertificateNotYetValidException if the certificate validity period is not
     * yet started.
     * @throws SWPCertificateValidationException if the certificate is invalid (can not be
     * validated using the given set of trusted certificates.
     */
    public static void verifyCertificate( X509Certificate aCertificate,
        ArrayList aTrustedCertificates )
    throws GeneralSecurityException,
    CertificateExpiredException,
    CertificateNotYetValidException
    
    {
        // First check certificate validity period
        
        aCertificate.checkValidity();

        // Check if the certificate is signed by some of the given trusted certificates
        Iterator itr = aTrustedCertificates.iterator();
        if ( itr.hasNext() )
        {
            while ( itr.hasNext() )
            {
                X509Certificate trustedCert = ( X509Certificate ) itr.next();
                
                try 
                {
                    aCertificate.verify( trustedCert.getPublicKey() );
                    // Found parent certificate. Certificate is verified to be valid
                    return;
                }
                catch ( GeneralSecurityException ex ) 
                {
                    // Certificate is not signed by current trustedCert. Try the next one
                    logger.warn( "Certificate not signed by: "+ trustedCert.getIssuerDN().getName() );
                }
            }
        }
            
        // Certificate is not signed by any of the trusted certificates, so it is invalid
        throw new SWPCertificateValidationException(
            "Can not find trusted parent certificate." );
    }
    
    /**
     * Check if all signatures in a verifiedSignatures graph are avlid.
     * 
     * After invoking {@link SWPNamedGraphSet#verifyAllSignatures()} a new graph
     * called <http://localhost/verifiedSignatures> will be added to the 
     * graphset. This methods checks if all signatures in this graph are valid.
     * 
     * @param verifiedSignatures
     */
    public static boolean isEverySignatureValid(
            final NamedGraph verifiedSignatures) {

        if (!SWP_V.default_graph.equals(verifiedSignatures.getGraphName())) {
            throw new IllegalArgumentException(
                    "provided graph is not 'verifiedSignatures' graph");
        }

        final boolean containsNotSuccessful = verifiedSignatures.contains(
                Node.ANY, SWP_V.notSuccessful, Node.createLiteral("true"));
        final boolean containsSuccessfulFalse = verifiedSignatures.contains(
                Node.ANY, SWP_V.successful, Node.createLiteral("false"));

        return !(containsNotSuccessful || containsSuccessfulFalse);
    }
    
    /**
     * Verifies certification chain using "PKIX" algorithm, defined in RFC-3280.
     * It is considered that the given certification chain start with the target
     * certificate and finish with some root CA certificate. The certification
     * chain is valid if no exception is thrown.
     * 
     * @param aCertChain
     *            the certification chain to be verified.
     * @param aTrustedCACertificates
     *            a list of most trusted root CA certificates.
     * @throws CertPathValidatorException
     *             if the certification chain is invalid.
     */
	/*
    public static void verifyCertificationChain( CertPath aCertChain,
        ArrayList aTrustedCACertificates )
    throws GeneralSecurityException 
    {
        int chainLength = aCertChain.getCertificates().size();
        if ( chainLength < 2 ) 
        {
            throw new CertPathValidatorException( "The certification chain is too " +
                "short. It should consist of at least 2 certiicates." );
        }

        // Create a set of trust anchors from given trusted root CA certificates
        HashSet trustAnchors = new HashSet();
        Iterator itr = aTrustedCACertificates.iterator();
        if ( itr.hasNext() )
        {
            while ( itr.hasNext() )
            {
                TrustAnchor trustAnchor = new TrustAnchor( ( X509Certificate ) itr.next(), null );
                trustAnchors.add( trustAnchor );
            }
        }

        // Create a certification chain validator and a set of parameters for it
        PKIXParameters certPathValidatorParams = new PKIXParameters( trustAnchors );
        certPathValidatorParams.setRevocationEnabled( false );
        CertPathValidator chainValidator =
            				CertPathValidator.getInstance( CERT_CHAIN_VALIDATION_ALGORITHM );

        // Remove the root CA certificate from the end of the chain. This is required by
        // the validation algorithm because by convention the trust anchor certificates
        // should not be a part of the chain that is validated
        CertPath certChainForValidation = removeLastCertFromCertChain( aCertChain );

        // Execute the certification chain validation
        chainValidator.validate( certChainForValidation, certPathValidatorParams );
    }*/
    
    /**
     * Removes the last certificate from given certification chain.
     * 
     * @param aCertChain
     * 
     * @return given cert chain without the last certificate in it.
     */
	/*
    private static CertPath removeLastCertFromCertChain( CertPath aCertChain )
    throws CertificateException 
    {
        List certs = aCertChain.getCertificates();
        int certsCount = certs.size();
        List certsWithoutLast = certs.subList( 0, certsCount-1 );
        CertificateFactory cf = CertificateFactory.getInstance( X509_CERTIFICATE_TYPE );
        CertPath certChainWithoutLastCertificate = cf.generateCertPath( certsWithoutLast );
        
        return certChainWithoutLastCertificate;
    }*/
}

/*
 *  (c)   Copyright 2004, 2005, 2006, 2007, 2008 Rowland Watkins (rowland@grid.cx) 
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