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
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Category;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider; 
import org.bouncycastle.util.encoders.Hex;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.eaio.uuid.UUID;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.c14n.RDFC14NImpl;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPCertificateValidationException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPInvalidKeyException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPNoSuchAlgorithmException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPNoSuchDigestMethodException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPValidationException;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;

/**
 * @author Rowland Watkins
 * 
 * Based on code by Svetlin Nakov
 */
public class SWPSignatureUtilities 
{
    static final Category log = Category.getInstance( SWPSignatureUtilities.class );
    private static final String ALG_ID_SIGNATURE_SHA1withRSA 	= "SHA1withRSA";
    private static final String ALG_ID_SIGNATURE_SHA1withDSA 	= "SHA1withDSA";
    
    private static final String X509_CERTIFICATE_TYPE 			= "X.509";
    private static final String CERTIFICATION_CHAIN_ENCODING 	= "PkiPath";
    private static final String CERT_CHAIN_VALIDATION_ALGORITHM = "PKIX";
    
    /**
     * getCanonicalGraph
     * 
     * Takes a NamedGraph and returns its canonical
     * form.
     * 
     * This method fixes the flaw where the graph name is
     * excluded from the canonical form.
     * 
     * @param graph
     * @return canonicalString
     */
    public static String getCanonicalGraph( NamedGraph graph )
    {
        NamedGraphSet set = new NamedGraphSetImpl();
        set.addGraph( graph );
        Model model = set.asJenaModel( graph.getGraphName().toString() );
        ArrayList canonicalTripleList = new RDFC14NImpl( model, "" ).getCanonicalStringsArray();
        
        Iterator litr = canonicalTripleList.iterator();
        ArrayList last = new ArrayList();
       
        last.add( graph.getGraphName().toString() );
        while ( litr.hasNext() )
        {
            last.add( litr.next() );
        }
        set.removeGraph( graph.getGraphName() );
        
       return last.toString();
    }
    
    /**
     * 
     * Takes a NamedGraphSet and returns its canonical
     * form.
     * 
     * @param set
     * @return canonicalString
     */
    public static String getCanonicalGraphSet( NamedGraphSet set )
    {
    	String graph = "urn:uuid"+ new UUID();
    	NamedGraphSet sSet = new NamedGraphSetImpl();
    	
    	Model model = set.asJenaModel( graph );
    	ArrayList canonicalTripleList = new RDFC14NImpl( model, "" ).getCanonicalStringsArray();
    	ArrayList result = new ArrayList();
    	Iterator itr = set.listGraphs();
    	while ( itr.hasNext() )
    	{
    		NamedGraph grph = ( NamedGraph )itr.next();
    		result.add( grph.getGraphName().toString() );
    	}
    	Collections.sort( result );
    	set.removeGraph( graph );
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
	
        byte[] res = null;
        if ( digestMethod.equals( SWP.JjcRdfC14N_sha1 ) )
        {
            Digest  digest = new SHA1Digest();
            byte[]  resBuf = new byte[ digest.getDigestSize() ];
            String  resStr;
            digest.update( data.getBytes(), 0, data.getBytes().length );
            digest.doFinal( resBuf, 0 );
	    
            res = Hex.encode( resBuf );
        }
        else
        {
            throw new SWPNoSuchDigestMethodException("The digest method: "+digestMethod +
            		" does not exist.");
        }
        
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
            String  resStr;
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
    
    /**
     * 
     * @param graph
     * @param signatureMethod
     * @param key
     * @return signature
     * @throws SWPNoSuchAlgorithmException
     * @throws SWPSignatureException
     * @throws SWPInvalidKeyException
     */
    public static String calculateSignature( NamedGraph graph, 
            						  Node signatureMethod, 
            						  PrivateKey key ) 
    throws SWPNoSuchAlgorithmException, 
    SWPSignatureException, 
    SWPInvalidKeyException
    {
        String canonicalGraph = getCanonicalGraph( graph );
        String signature = null;
        /**
         * Let's figure out which signature algorithm we're 
         * using.
         */
        Signature sig = null;
        String algo = null;
            
        if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
        {
            algo = ALG_ID_SIGNATURE_SHA1withRSA;
            log.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
        }
        else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
        {
            algo = ALG_ID_SIGNATURE_SHA1withDSA;
            log.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withDSA );
        }
        else 
        {
            throw new SWPNoSuchAlgorithmException("The signature" +
            		"method: "+signatureMethod+" does not exist.");
        }
               
        try 
        {
            sig = Signature.getInstance( algo );
            sig.initSign( key );
            sig.update( canonicalGraph.getBytes( "UTF-8" ) );
        } 
        catch ( NoSuchAlgorithmException e )
        {
            log.fatal( ALG_ID_SIGNATURE_SHA1withRSA +" not found! " +e.getMessage() );
            throw new SWPNoSuchAlgorithmException( "The signature" +
            		"method: "+signatureMethod+" does not exist.", e ); 
        }
        catch ( InvalidKeyException e1 ) 
        { 
            log.fatal( "Public key supplied is invalid. "+ e1.getMessage() );
            throw new SWPInvalidKeyException( "Public key supplied is invalid." );
        } 
        catch ( SignatureException e3 ) 
        {
            log.fatal( "Error updating input data. "+ e3.getMessage() );
            throw new SWPSignatureException( "Error updating input data." );
        } catch (UnsupportedEncodingException e) {
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
            log.fatal("Error generating signature. "+ e2.getMessage() );
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
    	String algo = null;

    	if ( signatureMethod.equals( SWP.JjcRdfC14N_rsa_sha1 ) ) 
    	{
    		algo = ALG_ID_SIGNATURE_SHA1withRSA;
    		log.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withRSA );
    	}
    	else if ( signatureMethod.equals( SWP.JjcRdfC14N_dsa_sha1 ) ) 
    	{
    		algo = ALG_ID_SIGNATURE_SHA1withDSA;
    		log.info( "Using algorithm: "+ALG_ID_SIGNATURE_SHA1withDSA );
    	}
    	else 
    	{
    		throw new SWPNoSuchAlgorithmException("The signature" +
    				"method: "+signatureMethod+" does not exist.");
    	}

    	try 
    	{
    		sig = Signature.getInstance( algo );
    		sig.initSign( key );
    		sig.update( canonicalGraph.getBytes() );
    	} 
    	catch ( NoSuchAlgorithmException e )
    	{
    		log.fatal( ALG_ID_SIGNATURE_SHA1withRSA +" not found! " +e.getMessage() );
    		throw new SWPNoSuchAlgorithmException( "The signature" +
    				"method: "+signatureMethod+" does not exist.", e ); 
    	}
    	catch ( InvalidKeyException e1 ) 
    	{ 
    		log.fatal( "Public key supplied is invalid. "+ e1.getMessage() );
    		throw new SWPInvalidKeyException( "Public key supplied is invalid." );
    	} 
    	catch ( SignatureException e3 ) 
    	{
    		log.fatal( "Error updating input data. "+ e3.getMessage() );
    		throw new SWPSignatureException( "Error updating input data." );
    	} 

    	try 
    	{
    		BASE64Encoder encoder = new BASE64Encoder();
    		signature = encoder.encodeBuffer( sig.sign() );
    	} 
    	catch ( SignatureException e2 ) 
    	{
    		log.fatal("Error generating signature. "+ e2.getMessage() );
    		throw new SWPSignatureException( "Error generating signature." );
    	}

    	return signature;
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
        try 
        {
        	CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
    		certificate = ( X509Certificate ) cf.generateCertificate( new ByteArrayInputStream( pem.getBytes() ) );
            sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withRSA );
            BASE64Decoder decoder = new BASE64Decoder();
        	signature = decoder.decodeBuffer( signatureValue );
        	sig.initVerify( certificate.getPublicKey() );
        	sig.update( canonicalGraph.getBytes( "UTF-8" ) );
        } 
        catch ( NoSuchAlgorithmException e ) 
        {
            log.fatal( ALG_ID_SIGNATURE_SHA1withRSA +" not found! " +e.getMessage() );
            throw new SWPNoSuchAlgorithmException( "The signature" +
            		"method: "+signatureMethod+" does not exist.", e ); 
        }
        catch ( IOException e1 ) 
        {	
            log.fatal( "Unable to access signature: " +e1.getMessage() );
            throw new SWPValidationException( "I/O error: Unable to access " +
            		"signature value.", e1 );
        }
        catch (InvalidKeyException e2) 
        {
            log.fatal("Public key supplied is invalid. "+ e2.getMessage() );
            throw new SWPInvalidKeyException( "Public key supplied is invalid." );
        }
    	catch (SignatureException e3) 
    	{
            log.fatal("Error updating input data. "+ e3.getMessage() );
            throw new SWPSignatureException( "Error updating input data." );
        } 
    	catch (CertificateException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
    	try 
    	{
            return sig.verify( signature );
        } 
    	catch (SignatureException e4) 
    	{
            log.fatal("Error verifying signature. "+e4.getMessage() );
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
     */
    public static boolean validateSignature( NamedGraph graph, 
			  Node signatureMethod, 
			  String signatureValue, 
			  X509Certificate certificate ) 
    throws SWPNoSuchAlgorithmException,
    SWPValidationException, 
    SWPInvalidKeyException, 
    SWPSignatureException
	{
    	String canonicalGraph = getCanonicalGraph( graph );
    	boolean result = false;
    	
    	Signature sig = null;
    	byte[] signature = null;
    	try 
    	{
    		sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withRSA );
    		BASE64Decoder decoder = new BASE64Decoder();
    		signature = decoder.decodeBuffer( signatureValue );
    		sig.initVerify( certificate.getPublicKey() );
    		sig.update( canonicalGraph.getBytes() );
    	} 
    	catch ( NoSuchAlgorithmException e ) 
    	{
    		log.fatal( ALG_ID_SIGNATURE_SHA1withRSA +" not found! " +e.getMessage() );
    		throw new SWPNoSuchAlgorithmException( "The signature" +
    				"method: "+signatureMethod+" does not exist.", e ); 
    	}
    	catch ( IOException e1 ) 
    	{	
    		log.fatal( "Unable to access signature: " +e1.getMessage() );
    		throw new SWPValidationException( "I/O error: Unable to access " +
    				"signature value.", e1 );
    	}
    	catch (InvalidKeyException e2) 
    	{
    		log.fatal("Public key supplied is invalid. "+ e2.getMessage() );
    		throw new SWPInvalidKeyException( "Public key supplied is invalid." );
    	}
    	catch (SignatureException e3) 
    	{
    		log.fatal("Error updating input data. "+ e3.getMessage() );
    		throw new SWPSignatureException( "Error updating input data." );
    	} 

    	try 
    	{
    		return sig.verify( signature );
    	} 
    	catch (SignatureException e4) 
    	{
    		log.fatal("Error verifying signature. "+e4.getMessage() );
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
            log.warn( "Certificate has expired." );
            throw new SWPValidationException( "Certificate has expired.", e );
        }
        catch ( CertificateNotYetValidException e ) 
        {
            log.warn( "Certificate not yet valid." );
            throw new SWPValidationException( "Certificate not yet valid.", e );
        }
        catch ( GeneralSecurityException e ) 
        {
            log.warn( "Certificate not signed by some trusted certificates." );
            throw new SWPValidationException( "Certificate not signed by some trusted certificates.", e );
        }
        
        Signature sig;
        byte[] signature;
        try 
        {
            sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withRSA );
            BASE64Decoder decoder = new BASE64Decoder();
        	signature = decoder.decodeBuffer( signatureValue );
        	sig.initVerify( certificate.getPublicKey() );
        	sig.update( canonicalGraph.getBytes( "UTF-8" ) );
        } 
        catch (NoSuchAlgorithmException e) 
        {
            log.fatal( ALG_ID_SIGNATURE_SHA1withRSA +" not found! " +e.getMessage() );
            throw new SWPNoSuchAlgorithmException( "The signature" +
            		"method: "+signatureMethod+" does not exist.", e ); 
        }
        catch (IOException e1) 
        {
            log.fatal("Unable to access signature: " +e1.getMessage() );
            throw new SWPValidationException( "I/O error: Unable to access " +
                    "signature value.", e1 );
        }
        catch (InvalidKeyException e2) 
        {
            log.fatal("Public key supplied is invalid. "+ e2.getMessage() );
            throw new SWPInvalidKeyException( "Public key supplied is invalid." );
        }
    	catch (SignatureException e3) 
    	{
            log.fatal("Error updating input data. "+ e3.getMessage() );
            throw new SWPSignatureException( "Error updating input data." );
        } 
        
    	try 
    	{
            result = sig.verify( signature );
        } 
    	catch (SignatureException e4) 
    	{
            log.fatal("Error verifying signature. "+e4.getMessage() );
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
            log.warn( "Certificate has expired." );
            throw new SWPValidationException( "Certificate has expired.", e );
        }
        catch ( CertificateNotYetValidException e ) 
        {
            log.warn( "Certificate not yet valid." );
            throw new SWPValidationException( "Certificate not yet valid.", e );
        }
        catch ( GeneralSecurityException e ) 
        {
            log.warn( "Certificate not signed by some trusted certificates." );
            throw new SWPValidationException( "Certificate not signed by some trusted certificates.", e );
        }
        
        Signature sig;
        byte[] signature;
        try 
        {
            sig = Signature.getInstance( ALG_ID_SIGNATURE_SHA1withRSA );
            BASE64Decoder decoder = new BASE64Decoder();
        	signature = decoder.decodeBuffer( signatureValue );
        	sig.initVerify( certificate.getPublicKey() );
        	sig.update( canonicalGraph.getBytes( "UTF-8" ) );
        } 
        catch (NoSuchAlgorithmException e) 
        {
            log.fatal( ALG_ID_SIGNATURE_SHA1withRSA +" not found! " +e.getMessage() );
            throw new SWPNoSuchAlgorithmException( "The signature" +
            		"method: "+signatureMethod+" does not exist.", e ); 
        }
        catch (IOException e1) 
        {
            log.fatal("Unable to access signature: " +e1.getMessage() );
            throw new SWPValidationException( "I/O error: Unable to access " +
                    "signature value.", e1 );
        }
        catch (InvalidKeyException e2) 
        {
            log.fatal("Public key supplied is invalid. "+ e2.getMessage() );
            throw new SWPInvalidKeyException( "Public key supplied is invalid." );
        }
    	catch (SignatureException e3) 
    	{
            log.fatal("Error updating input data. "+ e3.getMessage() );
            throw new SWPSignatureException( "Error updating input data." );
        } 
        
    	try 
    	{
            result = sig.verify( signature );
        } 
    	catch (SignatureException e4) 
    	{
            log.fatal("Error verifying signature. "+e4.getMessage() );
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
                    log.warn("Certificate not signed by: "+ trustedCert.getIssuerDN().getName() );
                }
            }
        }
            
        // Certificate is not signed by any of the trusted certificates, so it is invalid
        throw new SWPCertificateValidationException(
            "Can not find trusted parent certificate.");
    }
    
    /**
     * Verifies certification chain using "PKIX" algorithm, defined in RFC-3280. It is
     * considered that the given certification chain start with the target certificate
     * and finish with some root CA certificate. The certification chain is valid if no
     * exception is thrown.
     *
     * @param aCertChain the certification chain to be verified.
     * @param aTrustedCACertificates a list of most trusted root CA certificates.
     * @throws CertPathValidatorException if the certification chain is invalid.
     */
    public static void verifyCertificationChain( CertPath aCertChain,
        ArrayList aTrustedCACertificates)
    throws GeneralSecurityException 
    {
        int chainLength = aCertChain.getCertificates().size();
        if (chainLength < 2) 
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
    }
    
    /**
     * Removes the last certificate from given certification chain.
     * 
     * @param aCertChain
     * 
     * @return given cert chain without the last certificate in it.
     */
    private static CertPath removeLastCertFromCertChain( CertPath aCertChain )
    throws CertificateException 
    {
        List certs = aCertChain.getCertificates();
        int certsCount = certs.size();
        List certsWithoutLast = certs.subList( 0, certsCount-1 );
        CertificateFactory cf = CertificateFactory.getInstance( X509_CERTIFICATE_TYPE );
        CertPath certChainWithoutLastCertificate = cf.generateCertPath( certsWithoutLast );
        
        return certChainWithoutLastCertificate;
    }
}

/*
 *  (c)   Copyright 2004 Rowland Watkins (rowland@grid.cx) 
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