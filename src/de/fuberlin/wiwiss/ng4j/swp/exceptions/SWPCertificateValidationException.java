/*
 * Created on 10-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.exceptions;

import java.security.GeneralSecurityException;

/**
 * @author rowland
 *
 * Declarative Systems & Software Engineering Group,
 * School of Electronics & Computer Science,
 * University of Southampton,
 * Southampton,
 * SO17 1BJ
 */
public class SWPCertificateValidationException 
extends GeneralSecurityException 
{
    private static final long serialVersionUID = -8951045378696879228L;

    public SWPCertificateValidationException( String aMessage ) 
    {
        super(aMessage);
    }
}

