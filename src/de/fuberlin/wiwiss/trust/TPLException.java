package de.fuberlin.wiwiss.trust;

/**
 * Thrown when the TPL vocabulary is used incorrectly.
 *
 * @version $Id: TPLException.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TPLException extends RuntimeException {

    public TPLException() {
        super();
    }
    
    public TPLException(String message) {
        super(message);
    }
}
