/*
 * Created on 05-Oct-2004
 *
 */
package de.fuberlin.wiwiss.ng4j.swp.signature.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import org.apache.log4j.Category;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;


import sun.misc.BASE64Decoder;

/**
 * Signature Report represents the data held in a digital signature
 * along with methods for verifying that signature.
 * 
 * This implementation is based on the DBin code by Giovanni Tummarello.
 */

public class SignatureReport extends Object
{
		static final Category log = Category.getInstance( SignatureReport.class );
        private String signedData; 
        private String signatureValue;// base64 encoded
        private PublicKey publicKey; //where the public key is stored
        private boolean isValid;// the result of test signature
        private String sigMethod;
        private String subjectDN;
        private String issuerDN;
        private X509Certificate certificate;
        private X509Certificate ca;
        
    /**
    * Build a new signature report from the given signature value, public key and data
    * @param signatureValue - the signature string 64-encoded
    * @param publicKey - the URI where the public key is stored
    * @param data - data signed
    */
    
    public SignatureReport( String signatureValue, String publicKey, String data ) 
    		throws InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException, 
			InvalidKeySpecException, 
			IOException
    {
    	/*
    	 * We first need to convert our base64bin public key into something the JCE
    	 * understands.
    	 */
    	X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec( publicKey.getBytes() );
    	KeyFactory keyFactory = KeyFactory.getInstance( "RSA" );
    	PublicKey pubKey = keyFactory.generatePublic( pubKeySpec );
    	
    	this.signedData = data;
    	this.signatureValue = signatureValue;
    	this.publicKey = pubKey;
    	this.isValid = testRSASignature( signatureValue, pubKey, data );
    }
    
    public SignatureReport( String signatureValue, String cert, ArrayList data) throws CertificateException, IOException
    {
    	CertificateFactory cf = CertificateFactory.getInstance("X.509");
		certificate = ( X509Certificate ) cf.generateCertificate( new ByteArrayInputStream( cert.getBytes() ) );
		this.signedData = data.toString();
		this.signatureValue = signatureValue;
    }
    
    public SignatureReport( RDFSignature signature, String data ) 
    		throws DataLengthException, 
			CryptoException, 
			InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException, 
			InvalidKeySpecException, 
			IOException
    {
    	this.signedData = data;
    	this.signatureValue = signature.sign( data );
    	this.sigMethod = signature.getSigMethod();
    	this.subjectDN = signature.getSubjectDN();
    	this.issuerDN = signature.getIssuerDN();
    	this.certificate = signature.getCertificate();
    	this.ca = signature.getCA();
    	this.isValid = testRSASignature( signatureValue, signature.getRSAPublicKey(), data );
    }
    
    public String toString()
    {
        return  "Signed data: "+getSignedData()+'\n'
               +"Signature value: "+getSignatureValue()
               +"Public key :"+getPublicKey().toString()+'\n'
               +"Verification status: "+isValid()+'\n';
    }
        
    /**
    * @return the URI where the public key is stored
    */
    public PublicKey getPublicKey() 
    {
        return publicKey;
    }

    /**
    * @return the data string signed
    */
    private String getSignedData() 
    {
        return signedData;
    }
        
    /**
    * @return the signature base 64 encoded
    */
    public String getSignatureValue() 
    {
        return signatureValue;
    }

    /**
    * @return the test result of the current report
    */
    public boolean isValid() 
    {
        return isValid;
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
        
    /**
    * verify the validity of given signature using the given public key and the data to verify
    * @param signatureValue
    * @param publicKey
    * @param dataToVerify
    * @return the test result of the verify
    * @throws IOException,
    *         NoSuchAlgorithmException,
    *         InvalidKeyException,
    *         SignatureException,
    *         InvalidKeySpecException
    */
    
    static public boolean testSignature(String signatureValue,URI publicKey,String dataToVerify) 
    		throws IOException, 
			NoSuchAlgorithmException, 
			InvalidKeyException, 
			SignatureException, 
			InvalidKeySpecException
	{
                
    	Signature dsa = Signature.getInstance("SHA1withDSA");
    	BASE64Decoder decoder = new BASE64Decoder();
    	byte[]signature=decoder.decodeBuffer(signatureValue);
                
    	File f=new File(publicKey);
    	FileInputStream fis=new FileInputStream(f);
    	int fileSize = (int) f.length();
    	byte[] puk = new byte[fileSize];
    	fis.read(puk);
    	fis.close();                                
    	X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(puk);
    	KeyFactory keyFactory = KeyFactory.getInstance("DSA");
    	PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

    	dsa.initVerify(pubKey);
    	dsa.update(dataToVerify.getBytes("UTF-8"));
                                
        return dsa.verify(signature);                
    }
        
    /**
    * verify the validity of given signature using the given public key and the data to verify
    * @param signatureValue
    * @param publicKey
    * @param dataToVerify
    * @return the test result of the verify
    * @throws IOException,NoSuchAlgorithmException,InvalidKeyException,SignatureException,InvalidKeySpecException
    */
    static public boolean testRSASignature( String signatureValue, PublicKey publicKey, String dataToVerify ) 
    		throws IOException, 
			NoSuchAlgorithmException, 
			InvalidKeyException, 
			SignatureException, 
			InvalidKeySpecException
	{
                
    	Signature rsa = Signature.getInstance( "SHA1withRSA" );
    	BASE64Decoder decoder = new BASE64Decoder();
    	byte[]signature = decoder.decodeBuffer( signatureValue );

    	rsa.initVerify( publicKey );
    	rsa.update( dataToVerify.getBytes( "UTF-8" ) );
                                
    	return rsa.verify( signature );                
    } 
    
    public boolean verify() throws NoSuchAlgorithmException, 
	IOException, 
	InvalidKeyException, 
	UnsupportedEncodingException, 
	SignatureException
    {
    	Signature rsa = Signature.getInstance( "SHA1withRSA" );
    	BASE64Decoder decoder = new BASE64Decoder();
    	byte[]signature = decoder.decodeBuffer( signatureValue );
    	
    	rsa.initVerify( certificate.getPublicKey() );
    	rsa.update( signedData.getBytes( "UTF-8" ) );
    	
    	return rsa.verify( signature );
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
