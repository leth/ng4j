package de.fuberlin.wiwiss.trust;

/**
 * @version $Id: CountConstraint.java,v 1.1 2005/03/28 22:31:51 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CountConstraint {
    private String variableName;
    private String operator;
    private int value;
    
    public CountConstraint(String variableName, String operator, int value) {
        this.variableName = variableName;
        this.operator = operator;
        this.value = value;
    }
    
    public String variableName() {
        return this.variableName;
    }

    public String operator() {
        return this.operator;
    }

    public int value() {
        return this.value;
    }
}
