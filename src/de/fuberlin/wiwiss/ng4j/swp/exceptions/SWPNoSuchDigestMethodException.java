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
public class SWPNoSuchDigestMethodException 
extends Exception
{
    private static final long serialVersionUID = -2146837118222465355L;

    public SWPNoSuchDigestMethodException( String aMessage ) 
    {
        super( aMessage );
    }

    public SWPNoSuchDigestMethodException( String aMessage, Throwable aCause ) 
    {
        super( aMessage, aCause );
    }
    
}
