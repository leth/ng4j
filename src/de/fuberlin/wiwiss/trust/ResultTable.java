package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Triple;

/**
 * @version $Id: ResultTable.java,v 1.2 2005/03/21 00:23:28 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ResultTable {
    private static final Collection SPO = Arrays.asList(new String[] {
            TrustPolicy.SUBJ.getName(),
            TrustPolicy.PRED.getName(),
            TrustPolicy.OBJ.getName()});

    private List bindings = new ArrayList();
    
    public void addBinding(VariableBinding binding) {
        this.bindings.add(binding);
    }

    public Iterator bindingIterator() {
        return this.bindings.iterator();
    }
    
    public int countBindings() {
        return this.bindings.size();
    }
    
    public ResultTable selectDistinct(Collection variableNames) {
        ResultTable result = new ResultTable();
        Iterator it = this.bindings.iterator();
        while (it.hasNext()) {
            VariableBinding binding = (VariableBinding) it.next();
            VariableBinding selection = binding.selectSubset(variableNames);
            if (!result.containsBinding(selection)) {
                result.addBinding(selection);
            }
        }
        return result;
    }

    public ResultTable selectMatching(VariableBinding specification) {
        ResultTable result = new ResultTable();
        Iterator it = this.bindings.iterator();
        while (it.hasNext()) {
            VariableBinding binding = (VariableBinding) it.next();
            if (specification.isSubsetOf(binding)) {
                result.addBinding(binding);
            }
        }
        return result;
    }

    public ResultTable selectMatching(Triple triple) {
        VariableBinding binding = new VariableBinding();
        binding.setValue(TrustPolicy.SUBJ.getName(), triple.getSubject());
        binding.setValue(TrustPolicy.PRED.getName(), triple.getPredicate());
        binding.setValue(TrustPolicy.OBJ.getName(), triple.getObject());
        return selectMatching(binding);
    }

    public boolean containsBinding(VariableBinding binding) {
        return this.bindings.contains(binding);
    }

    public boolean equals(Object other) {
        ResultTable otherTable = (ResultTable) other;
        if (otherTable.bindings.size() != this.bindings.size()) {
            return false;
        }
        return otherTable.bindings.containsAll(this.bindings);
    }
    
    public int hashCode() {
        return 0;
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer("ResultTable{");
        Iterator it = this.bindings.iterator();
        while (it.hasNext()) {
            VariableBinding binding = (VariableBinding) it.next();
            result.append(binding);
            if (it.hasNext()) {
                result.append(", ");
            }
        }
        result.append("}");
        return result.toString();
    }

    public Iterator tripleIterator() {
        return new TripleIterator(selectDistinct(SPO).bindingIterator());
    }
    
	private class TripleIterator implements Iterator {
		private Iterator sourceIt;

		public TripleIterator(Iterator source) {
			this.sourceIt = source;
		}

		public boolean hasNext() {
		    return this.sourceIt.hasNext();
		}

		public Object next() {
		    return makeTriple((VariableBinding) this.sourceIt.next());
		}

		public void remove() {
		    throw new UnsupportedOperationException();
		}
		
		private Triple makeTriple(VariableBinding binding) {
		    return new Triple(
		            binding.value(TrustPolicy.SUBJ.getName()),
		            binding.value(TrustPolicy.PRED.getName()),
		            binding.value(TrustPolicy.OBJ.getName()));
		}
	}

    public static ResultTable createFromTriQLResult(Iterator resultIt, Triple triple) {
        ResultTable table = new ResultTable();
        while (resultIt.hasNext()) {
            Map binding = (Map) resultIt.next();
            VariableBinding b = new VariableBinding(binding);
            if (!b.containsName(TrustPolicy.SUBJ.getName())) {
                b.setValue(TrustPolicy.SUBJ.getName(), triple.getSubject());
            }
            if (!b.containsName(TrustPolicy.PRED.getName())) {
                b.setValue(TrustPolicy.PRED.getName(), triple.getPredicate());
            }
            if (!b.containsName(TrustPolicy.OBJ.getName())) {
                b.setValue(TrustPolicy.OBJ.getName(), triple.getObject());
            }
            table.addBinding(b);
        }
        return table;
    }
}
