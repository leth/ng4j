package de.fuberlin.wiwiss.trust;

/**
 * Thrown when the TPL vocabulary is used incorrectly.
 *
 * @version $Id: TPLException.java,v 1.2 2005/03/15 08:59:08 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TPLException extends RuntimeException {

    public TPLException() {
        super();
    }
    
    public TPLException(String message) {
        super(message);
    }
    
    public TPLException(Throwable cause) {
        super(cause);
    }
}
