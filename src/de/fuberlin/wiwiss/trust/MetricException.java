package de.fuberlin.wiwiss.trust;

/**
 * Indicates invalid arguments to a metric.
 * 
 * @version $Id: MetricException.java,v 1.2 2005/10/04 00:03:44 cyganiak Exp $
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
