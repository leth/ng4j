/*
 * Created on 11-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.exceptions;

import java.security.InvalidKeyException;

/**
 * @author rowland
 *
 * Declarative Systems & Software Engineering Group,
 * School of Electronics & Computer Science,
 * University of Southampton,
 * Southampton,
 * SO17 1BJ
 */
public class SWPInvalidKeyException extends InvalidKeyException 
{
    private static final long serialVersionUID = 1L;
    
    public SWPInvalidKeyException( String aMessage ) 
    {
        super( aMessage );
    }

}
