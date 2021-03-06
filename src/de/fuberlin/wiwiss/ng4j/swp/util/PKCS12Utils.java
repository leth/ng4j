/*
 * Created on 05-Oct-2004
 *
 */
package de.fuberlin.wiwiss.ng4j.swp.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPCertificateException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPPKCS12Exception;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPSignatureException;


public class PKCS12Utils {
	private static final String KEY_STORE_TYPE_PKCS12 = "PKCS12";
	static final Log log = LogFactory.getLog( PKCS12Utils.class );
	/**
	 * Loads and decrypt the PCKS12 file specified in the configuration properties.
	 * 
	 * @return the PKCS12 object, already decrypted.
	 */
	public static KeyStore loadAndDecryptPKCS12( String keyStoreFileName, String password ) 
	throws SWPSignatureException, 
	SWPCertificateException, 
	SWPPKCS12Exception
	{
		try 
		{
			// Load the keystore
			KeyStore ks = KeyStore.getInstance( KEY_STORE_TYPE_PKCS12 );
			FileInputStream fis = new FileInputStream( keyStoreFileName );
			ks.load( fis, password.toCharArray() );
			return ks;
		} 
		catch ( FileNotFoundException fnfex ) 
		{
			throw new SWPPKCS12Exception( "File: " + keyStoreFileName + " could not be found." );
		}
		catch ( NoSuchAlgorithmException nsaex )
		{
			throw new SWPPKCS12Exception( "No such algorithm: "+nsaex.getMessage() );
		}
		catch ( CertificateException cex )
		{		
			throw new SWPCertificateException( "Error accessing certificate." );
		}
		catch ( KeyStoreException ksex ) 
		{
			throw new SWPPKCS12Exception( "Error initialising keystore with " + keyStoreFileName );
		} 
		catch ( IOException ioex ) 
		{
			throw new SWPPKCS12Exception( "Error opening keystore: " + keyStoreFileName );
		} 
	}

	/**
	 * Loads the private key from a file specified in the configuration properties.
	 * 
	 * @return the private key.
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	public static PrivateKey loadPrivateKey( KeyStore ks, String alias, String password ) 
			throws KeyStoreException, 
				NoSuchAlgorithmException, 
				UnrecoverableKeyException 
    {
		return ( PrivateKey ) ks.getKey( alias, password.toCharArray() );
	}
	
	public static PrivateKey decryptPrivateKey( String pkcs12, String password ) throws SWPSignatureException
	{
		KeyStore ks = null;
		PrivateKey pkey = null;
		
		
            try {
				ks = PKCS12Utils.loadAndDecryptPKCS12( pkcs12, password );
			/*
				String errorMessage = "Can not read certificate keystore file (" +
				    pkcs12 + ").\nThe file is either not in PKCS#12 format (.p12) or is corrupted or the password you entered is invalid.";
				throw new SWPSignatureException( errorMessage );
			*/
				Enumeration<String> aliasesEnum = ks.aliases();
		        String alias = null;
				Certificate[] certChain = null;
		        while ( aliasesEnum.hasMoreElements() ) 
		        {
		            alias = aliasesEnum.nextElement();
		            certChain = ks.getCertificateChain( alias );
		        }
		        
		              
				pkey = PKCS12Utils.loadPrivateKey( ks, alias, password );
				
			} 
            catch ( SWPSignatureException e ) 
			{
				throw new RuntimeException(e);
			}
            catch ( SWPCertificateException e ) 
            {
				log.error( e.getMessage() );
				throw new SWPSignatureException( e.getMessage(), e);
			}
            catch ( SWPPKCS12Exception e ) 
            {
				throw new RuntimeException(e);
			} 
            catch ( KeyStoreException e ) 
            {
				throw new RuntimeException(e);
			} 
            catch ( NoSuchAlgorithmException e ) 
            {
				throw new RuntimeException(e);
			} 
            catch ( UnrecoverableKeyException e ) 
            {
				throw new RuntimeException(e);
			}
        
		return pkey;
	}
	
	public static Certificate[] getCertChain( String pkcs12, String password )
	{
		    KeyStore ks = null;
		    String alias = null;
	        Certificate[] certChain = null;
			try 
			{
				ks = PKCS12Utils.loadAndDecryptPKCS12( pkcs12, password );
				Enumeration<String> aliasesEnum = ks.aliases();
				while ( aliasesEnum.hasMoreElements() ) 
		        {
		            alias = aliasesEnum.nextElement();
		            certChain = ks.getCertificateChain( alias );
		        }
			} 
			catch ( SWPSignatureException e ) 
			{
				throw new RuntimeException(e);
			}
			catch ( SWPCertificateException e ) 
			{
				throw new RuntimeException(e);
			}
			catch ( SWPPKCS12Exception e ) 
			{
				throw new RuntimeException(e);
			} 
			catch ( KeyStoreException e ) 
			{
				throw new RuntimeException(e);
			}
			        
	        return certChain;
	}
}

/*
 *  (c)   Copyright 2004 - 2010 Rowland Watkins (rowland@grid.cx) & University of 
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
