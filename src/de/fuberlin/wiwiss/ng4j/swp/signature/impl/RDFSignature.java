/*
 * Created on 05-Oct-2004
 *
 */
package de.fuberlin.wiwiss.ng4j.swp.signature.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.log4j.Category;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.RDFSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.signature.keystores.pkcs12.PKCS12Utils;

import sun.misc.BASE64Encoder;


/**
 * RDFSignature. This class signs data with a certificate loaded from
 * PKCS12Utils.
 * 
 * 
 * 
 * This implementation is based on the DBin code by Giovanni Tummarello.
 */
public class RDFSignature 
{
	static final Category log = Category.getInstance( RDFSignature.class );
	private PrivateKey privateKey;
    private URI publicKey;
    private PublicKey RSAPublicKey;
    private String sigMethod;
    private String subjectDN;
    private X509Certificate certificate;
    private X509Certificate ca;
    private String issuerDN;
   
    
    /**
     * Building a new LiveDbinSignature. It generate a key pair
     * @param publicKeyStore - the URI where do you store the public key
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws RDFSignatureException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     */
    public RDFSignature(File keyStoreFileName, String password) 
    		throws NoSuchProviderException, 
			IOException, 
			RDFSignatureException, 
			KeyStoreException, 
			NoSuchAlgorithmException, 
			UnrecoverableKeyException 
	{
            
    	
    	
    	KeyStore userKeyStore = null;
        try 
		{
            userKeyStore = PKCS12Utils.loadAndDecryptPKCS12( keyStoreFileName.getAbsolutePath(), password );
        } catch (Exception ex) 
		{
            String errorMessage = "Can not read certificate keystore file (" +
                keyStoreFileName + ").\nThe file is either not in PKCS#12 format (.p12) or is corrupted or the password you entered is invalid.";
            throw new RDFSignatureException(errorMessage, ex);
        }
        
        Enumeration aliasesEnum = userKeyStore.aliases();
        String alias = null;
        Certificate[] certChain = null;
        while ( aliasesEnum.hasMoreElements() ) 
        {
            alias = (String)aliasesEnum.nextElement();
            certChain = userKeyStore.getCertificateChain( alias );
        }
        
              
		PrivateKey pKey = PKCS12Utils.loadPrivateKey( userKeyStore, alias, password );
        
        
        this.publicKey = keyStoreFileName.toURI();
        this.privateKey = pKey;
        this.RSAPublicKey = certChain[ 0 ].getPublicKey();
        this.certificate = ( X509Certificate ) certChain[ 0 ];
        this.subjectDN = certificate.getSubjectDN().getName();
        this.issuerDN = certificate.getIssuerDN().getName();
            
        if ( certChain[ 1 ] != null )
        {
        	this.ca = ( X509Certificate ) certChain[ 1 ];
        }
    }
    
    /**
     * Sign a data string and return the encode 64 string signature
     * @param dataToSign
     * @return the encode 64 string signature
     * @throws NoSuchAlgorithmException,InvalidKeyException,SignatureException
     * @throws CryptoException
     * @throws DataLengthException
     */
    public String sign( String dataToSign ) 
    		throws NoSuchAlgorithmException, 
			InvalidKeyException, 
			SignatureException, 
			DataLengthException, 
			CryptoException 
	{ 
    	/*
            SHA1Digest digEng = new SHA1Digest();
            RSAEngine rsaEng = new RSAEngine();
           
            PSSSigner signer = new PSSSigner(rsaEng, digEng, 64);
            signer.init(true, privKeyParams);
            signer.update(dataToSign.getBytes(), 0, dataToSign.getBytes().length); 
            byte [] sig = signer.generateSignature();
            String result = new String( Base64.encode(sig) );

            return result;      */ 
            /*
            Signature dsa = Signature.getInstance("SHA1withDSA");
            dsa.initSign(privateKey);
            dsa.update(dataToSign.getBytes());*/
            
            
            Signature sig = Signature.getInstance( "SHA1WithRSA" ); 
            this.sigMethod = sig.getAlgorithm();
            sig.initSign( privateKey ); 
            sig.update( dataToSign.getBytes() ); 
            //return sig.sign(); 
            BASE64Encoder encoder = new BASE64Encoder();

            return encoder.encodeBuffer( sig.sign() );
    }

    /**
     * @return the URI where the public key is stored
     */
    public URI getPublicKeyURI() 
    {
            return publicKey;
    }
    
    public PublicKey getRSAPublicKey()
    {
    	return RSAPublicKey;
    }
    
    public String getSigMethod()
    {
    	return sigMethod;
    }
    
    public String getSubjectDN()
    {
    	return subjectDN;
    }
    
    public String getIssuerDN()
    {
    	return issuerDN;
    }
    
    public X509Certificate getCertificate()
    {
    	return certificate;
    }
    
    public X509Certificate getCA()
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
