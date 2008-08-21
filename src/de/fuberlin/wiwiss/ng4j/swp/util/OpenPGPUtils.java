package de.fuberlin.wiwiss.ng4j.swp.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.util.Iterator;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;

public class OpenPGPUtils 
{
	
	public static PrivateKey decryptPGP( String keystore, String password ) 
	throws NoSuchProviderException, IOException, PGPException
	{
		return readSecretKey( keystore, password ).getKey();
	}
	/**
     * A simple routine that opens a key ring file and loads the first available key suitable for
     * encryption.
     * 
     * @param in
     * @return
     * @throws IOException
     * @throws PGPException
     */
	private static PGPPublicKey readPublicKey( InputStream    in )
	        throws IOException, PGPException
	    {
	        in = PGPUtil.getDecoderStream( in );
	        
	        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection( in );

	        //
	        // we just loop through the collection till we find a key suitable for encryption, in the real
	        // world you would probably want to be a bit smarter about this.
	        //
	        PGPPublicKey    key = null;
	        
	        //
	        // iterate through the key rings.
	        //
	        Iterator rIt = pgpPub.getKeyRings();
	        
	        while ( key == null && rIt.hasNext() )
	        {
	            PGPPublicKeyRing    kRing = ( PGPPublicKeyRing )rIt.next();    
	            Iterator                        kIt = kRing.getPublicKeys();
	            //boolean                        encryptionKeyFound = false;
	            
	            while ( key == null && kIt.hasNext() )
	            {
	                PGPPublicKey    k = ( PGPPublicKey )kIt.next();
	                
	                if ( k.isEncryptionKey() )
	                {
	                    key = k;
	                }
	            }
	        }
	        
	        if ( key == null )
	        {
	            throw new IllegalArgumentException( "Can't find encryption key in key ring." );
	        }
	        
	        return key;
	    }
	
	/**
     * Load a secret key ring collection from keyIn and find the secret key corresponding to
     * keyID if it exists.
     * 
     * @param keyIn input stream representing a key ring collection.
     * @param keyID keyID we want.
     * @param pass passphrase to decrypt secret key with.
     * @return
     * @throws IOException
     * @throws PGPException
     * @throws NoSuchProviderException
     */
    private static PGPPrivateKey findSecretKey(
        InputStream keyIn,
        long        keyID,
        char[]      pass)
        throws IOException, PGPException, NoSuchProviderException
    {    
        PGPSecretKeyRingCollection    pgpSec = new PGPSecretKeyRingCollection(
                                                            PGPUtil.getDecoderStream( keyIn ) );
                                                                                        
        PGPSecretKey    pgpSecKey = pgpSec.getSecretKey( keyID );
        
        if ( pgpSecKey == null ) 
        {
            return null;
        }
        
        return pgpSecKey.extractPrivateKey( pass, "BC" );
    }
	
	 /**
	  * A simple routine that opens a key ring file and loads the first available key suitable for
	  * signature generation.
	  * 
	  * @param in
	  * @return
	  * @throws IOException
	  * @throws PGPException
	  */
	 private static PGPPrivateKey readSecretKey(
	     String keystore,
	     String password )
	     throws IOException, PGPException, NoSuchProviderException
	 {    
		 PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection( new FileInputStream( keystore ) );

	     //
	     // we just loop through the collection till we find a key suitable for encryption, in the real
	     // world you would probably want to be a bit smarter about this.
	     //
	     PGPSecretKey    key = null;
	        
	     //
	     // iterate through the key rings.
	     //
	     Iterator rIt = pgpSec.getKeyRings();
	        
	     while ( key == null && rIt.hasNext() )
	     {
	         PGPSecretKeyRing    kRing = ( PGPSecretKeyRing )rIt.next();    
	         Iterator                        kIt = kRing.getSecretKeys();
	            
	         while ( key == null && kIt.hasNext() )
	         {
	             PGPSecretKey    k = ( PGPSecretKey )kIt.next();
	                
	             if ( k.isSigningKey() )
	             {
	                 key = k;
	             }
	         }
	     }
	        
	     if ( key == null )
	     {
	         throw new IllegalArgumentException( "Can't find signing key in key ring." );
	     }
	        
	     return key.extractPrivateKey( password.toCharArray(), "BC" );
	 }
	
}
