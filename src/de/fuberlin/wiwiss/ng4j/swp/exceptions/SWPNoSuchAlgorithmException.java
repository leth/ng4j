/*
 * Created on 11-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.exceptions;

/**
 * @author rowland
 *
 * Declarative Systems & Software Engineering Group,
 * School of Electronics & Computer Science,
 * University of Southampton,
 * Southampton,
 * SO17 1BJ
 */
public class SWPNoSuchAlgorithmException 
extends Exception
{
    private static final long serialVersionUID = 5205680685788788514L;

    public SWPNoSuchAlgorithmException ( String aMessage ) 
    {
        super( aMessage );
    }

    public SWPNoSuchAlgorithmException ( String aMessage, Throwable aCause ) 
    {
        super( aMessage, aCause );
    }
    
}
