/*
 * Created on 05-Oct-2004
 *
 */
package de.fuberlin.wiwiss.ng4j.swp.signature.keystores.pkcs12;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;

import org.apache.log4j.Category;

import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.RDFSignatureException;


public class PKCS12Utils {
	/**
	 * Loads and decrypt the PCKS12 file specified in the configuration properties.
	 * 
	 * @return the PKCS12 object, already decrypted.
	 */
	
	static final Category log = Category.getInstance( PKCS12Utils.class );
	public static KeyStore loadAndDecryptPKCS12( String keyStoreFileName, String password ) throws Exception
	{
		try 
		{
			// Load the keystore
			KeyStore ks = KeyStore.getInstance( "PKCS12" );
			FileInputStream fis = new FileInputStream( keyStoreFileName );
			ks.load( fis, password.toCharArray() );
			return ks;
		} 
		catch ( FileNotFoundException fnfex ) 
		{
			String message = "File: " + keyStoreFileName + " could not be found.";
			throw new RDFSignatureException( message, fnfex );
		} 
		catch ( IOException ioex ) 
		{
			String message = "Error opening keystore: " + keyStoreFileName;
			throw new RDFSignatureException( message, ioex );
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
