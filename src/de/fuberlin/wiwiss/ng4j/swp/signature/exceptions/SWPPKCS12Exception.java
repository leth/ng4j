/*
 * Created on 11-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp.signature.exceptions;

/**
 * @author rowland
 *
 * Declarative Systems & Software Engineering Group,
 * School of Electronics & Computer Science,
 * University of Southampton,
 * Southampton,
 * SO17 1BJ
 */
public class SWPPKCS12Exception 
extends Exception
{
    private static final long serialVersionUID = -6287938737185449614L;

    public SWPPKCS12Exception( String aMessage ) 
    {
        super( aMessage );
    }

    public SWPPKCS12Exception( String aMessage, Throwable aCause ) 
    {
        super( aMessage, aCause );
    }
}
