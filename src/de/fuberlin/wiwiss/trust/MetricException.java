package de.fuberlin.wiwiss.trust;

/**
 * Indicates invalid arguments to a metric
 * @version $Id: MetricException.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MetricException extends Exception {

    public MetricException() {
        super();
    }
    
    public MetricException(String message) {
        super(message);
    }
}
