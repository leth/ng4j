/*
 * Created on 11-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.exceptions;

import java.security.cert.CertificateException;

/**
 * @author rowland
 *
 * Declarative Systems & Software Engineering Group,
 * School of Electronics & Computer Science,
 * University of Southampton,
 * Southampton,
 * SO17 1BJ
 */
public class SWPCertificateException 
extends CertificateException
{
    private static final long serialVersionUID = 9080351787634764125L;

    public SWPCertificateException( String aMessage ) 
    {
        super( aMessage );
    }
}
