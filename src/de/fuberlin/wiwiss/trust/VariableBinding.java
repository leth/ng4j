package de.fuberlin.wiwiss.trust;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;

/**
 * @version $Id: VariableBinding.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class VariableBinding {
    private Map namesToValues;

    public VariableBinding() {
        this(new HashMap());
    }
    
    public VariableBinding(Map binding) {
        this.namesToValues = binding;
    }
    
    public boolean containsName(String variableName) {
        return this.namesToValues.containsKey(variableName);
    }
    
    public Node value(String variableName) {
        return (Node) this.namesToValues.get(variableName);
    }
    
    public void setValue(String variableName, Node variableValue) {
        this.namesToValues.put(variableName, variableValue);
    }
    
    public Set variableNames() {
        return this.namesToValues.keySet();
    }
    
    public VariableBinding selectSubset(Collection variableNames) {
        VariableBinding result = new VariableBinding();
        Iterator it = variableNames.iterator();
        while (it.hasNext()) {
            String variableName = (String) it.next();
            result.setValue(variableName, value(variableName));
        }
        return result;
    }
    
    public boolean isSubsetOf(VariableBinding other) {
        Iterator it = this.namesToValues.keySet().iterator();
        while (it.hasNext()) {
            String variableName = (String) it.next();
            Node value = value(variableName);
            if (value == null && other.value(variableName) != null) {
                return false;
            }
            if (value != null && !value.equals(other.value(variableName))) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        return "VariableBinding" + this.namesToValues;
    }
    
    public boolean equals(Object other) {
        VariableBinding otherBinding = (VariableBinding) other;
        return otherBinding.namesToValues.equals(this.namesToValues);
    }
    
    public int hashCode() {
        return this.namesToValues.hashCode();
    }
}
