/*
 * Created on 24-Nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.signature;

import com.hp.hpl.jena.graph.Node;

import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

/**
 * 
 * Represents an SWP signature
 * 
 * @author chris bizer
 * @author rowland watkins
 *
 */
public class SWPSignature extends Signature 
{
	
	/**
     * @param algorithm
     */
    protected SWPSignature(String algorithm) 
    { 
        super(algorithm);
        // TODO Auto-generated constructor stub
    }

    private Node sigMethod;
	
	
    public void setSignatureMethod(Node sigMethod) 
    {
    		this.sigMethod = sigMethod;
    	}
    
    public Node getSignatureMethod() 
    {
    	   return null;
    	}

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineSign()
     */
    protected byte[] engineSign() throws 
    SignatureException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineUpdate(byte)
     */
    protected void engineUpdate(byte b) throws 
    SignatureException 
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineVerify(byte[])
     */
    protected boolean engineVerify(byte[] sigBytes) throws 
    SignatureException 
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineUpdate(byte[], int, int)
     */
    protected void engineUpdate(byte[] b, int off, int len) throws 
    SignatureException 
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineInitSign(java.security.PrivateKey)
     */
    protected void engineInitSign(PrivateKey privateKey) throws 
    InvalidKeyException 
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineInitVerify(java.security.PublicKey)
     */
    protected void engineInitVerify(PublicKey publicKey) throws 
    InvalidKeyException 
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineGetParameter(java.lang.String)
     */
    protected Object engineGetParameter(String param) throws 
    InvalidParameterException 
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineSetParameter(java.lang.String, java.lang.Object)
     */
    protected void engineSetParameter(String param, Object value) throws 
    InvalidParameterException 
    {
        // TODO Auto-generated method stub
        
    }
}

/*
 *  (c)   Copyright 2004 Chris Bizer (chris@bizer.de) & Rowland Watkins (rowland@grid.cx) 
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