package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;

/**
 * @version $Id: MetricConstraint.java,v 1.1 2005/02/18 01:45:00 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MetricConstraint {
    private Metric metric;
    private List arguments;
    
    public MetricConstraint(Metric metric, List arguments) {
        this.metric = metric;
        this.arguments = arguments;
    }
    
    public boolean isSatisfied(VariableBinding binding) {
        List actualArguments = getArgumentsWithVariablesReplaced(binding);
        try {
            MetricResult result = this.metric.calculateMetric(actualArguments);
            return result.getResult();
        } catch (MetricException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private List getArgumentsWithVariablesReplaced(VariableBinding binding) {
        List result = new ArrayList(this.arguments.size());
        Iterator it = this.arguments.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (node.isVariable()) {
                result.add(binding.value(node.getName()));
            } else {
                result.add(node);
            }
        }
        return result;
    }
}
