package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * <p>A TriQL or TriQL.P result table. This is basically a collection
 * of {@link VariableBinding}s with helper methods. Each binding can
 * be seen as a row, and each variable as a column.</p>
 * 
 * @version $Id: ResultTable.java,v 1.6 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 * 
 * TODO: Remove all mentions of SUBJ, PRED and OBJ because class should be generic?
 * TODO: Move the filterByXXX methods to the respective constraint classes?
 */
public class ResultTable {

	/**
	 * Creates a new result table from a TriQL query result. Takes
	 * care of filling in ?SUBJ, ?PRED and ?OBJ if they were fixed
	 * in the triple.
	 * @param resultIt A TriQL result iterator over Maps from String to Node
	 * @param triplePattern The triple pattern originally queried for
	 * @return A result table
	 */
    public static ResultTable createFromTriQLResult(Iterator resultIt,
    	Triple triplePattern) {
        ResultTable table = new ResultTable();
        while (resultIt.hasNext()) {
            Map binding = (Map) resultIt.next();
            VariableBinding b = new VariableBinding(binding);
            if (!b.containsName(TrustPolicy.SUBJ.getName())) {
                b.setValue(TrustPolicy.SUBJ.getName(), triplePattern.getSubject());
            }
            if (!b.containsName(TrustPolicy.PRED.getName())) {
                b.setValue(TrustPolicy.PRED.getName(), triplePattern.getPredicate());
            }
            if (!b.containsName(TrustPolicy.OBJ.getName())) {
                b.setValue(TrustPolicy.OBJ.getName(), triplePattern.getObject());
            }
            table.addBinding(b);
        }
        return table;
    }
	
	/**
	 * A collection containing the names of the variables
	 * ?SUBJ, ?PRED and ?OBJ. Useful for calls to {@link #selectDistinct}
	 * and friends.
	 */
    public static final Collection SPO = Arrays.asList(new String[] {
            TrustPolicy.SUBJ.getName(),
            TrustPolicy.PRED.getName(),
            TrustPolicy.OBJ.getName()});

    private List bindings = new ArrayList();
    private Set variables = new HashSet();
    
    /**
     * Adds a variable binding to the result table. No duplicate
     * filtering is performed.
     * @param binding A variable binding
     */
    public void addBinding(VariableBinding binding) {
        this.bindings.add(binding);
        this.variables.addAll(binding.variableNames());
    }

    /**
     * Adds all bindings from another result table to this table.
     * No duplicate filtering is performed.
     * @param table A result table
     */
    public void addAll(ResultTable table) {
        Iterator it = table.bindingIterator();
        while (it.hasNext()) {
			addBinding((VariableBinding) it.next());
        }
    }
    
    /**
     * @return An iterator over all {@link VariableBinding}s
     */
    public Iterator bindingIterator() {
        return this.bindings.iterator();
    }
    
    /**
     * @return The number of bindings (rows) in the table
     */
    public int countBindings() {
        return this.bindings.size();
    }

    /**
     * @param variableName A variable name
     * @return The number of distinct values for this variable in
     * 		the result table, or 0 if it doesn't occur at all
     */
    public int countDistinct(String variableName) {
		if (!this.variables.contains(variableName)) {
			return 0;
		}
		return selectDistinct(
				Collections.singleton(variableName)).countBindings();
    }

    /**
     * Returns a new table containing only the columns named in the
     * argument, with duplicates removed.
     * @param variableNames A collection of variable names
     * @return A new table containing all distinct values of these variables
     */
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

    /**
     * Returns a new result table containing only those rows of the
     * original table whose values for certain variables match a given
     * binding.
     * @param specification A binding whose variables and values are
     * 		used to filter the table 
     * @return A new table containing only matching rows
     */
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

    /**
     * @param triple An RDF triple
     * @return A new table containing only the rows whose ?SUBJ, ?PRED
     * 		and ?OBJ match the triple
     */
    public ResultTable selectMatching(Triple triple) {
        VariableBinding binding = new VariableBinding();
        binding.setValue(TrustPolicy.SUBJ.getName(), triple.getSubject());
        binding.setValue(TrustPolicy.PRED.getName(), triple.getPredicate());
        binding.setValue(TrustPolicy.OBJ.getName(), triple.getObject());
        return selectMatching(binding);
    }

    /**
     * Returns a new result table containing only the rows matching
     * a count constraint. The table is first split into groups
     * with common values for the groupByVariables, then the distinct
     * values for the constraint's variable are counted in each group,
     * and only those groups where the count matches the constraint are
     * kept in the new table.
     * @param count A count constraint
     * @param groupByVariables A collection of string variable names, or
     * 		null to count without splitting into groups
     * @return A new table containig only groups matching the count constraint
     */
    public ResultTable filterByCount(CountConstraint count, Collection groupByVariables) {
		ResultTable result = new ResultTable();
    		if (groupByVariables == null) {
    			groupByVariables = Collections.EMPTY_SET;
    		}
    		Iterator it = selectDistinct(groupByVariables).bindingIterator();
    		while (it.hasNext()) {
    			VariableBinding commonVariables = (VariableBinding) it.next();
    			ResultTable group = selectMatching(commonVariables);
    			if (!count.isMatchingCount(group.countDistinct(count.variableName()))) {
    				continue;
    			}
    			result.addAll(group);
    		}
    		return result;
    	}

    /**
     * Returns a new result table containing only the rows that match
     * all {@link ExpressionConstraint}s of the given trust policy.
     * @param policy A trust policy
     * @return A new result table with all matching rows
     */
    public ResultTable filterByConstraints(TrustPolicy policy) {
		ResultTable result = new ResultTable();
		Iterator it = this.bindings.iterator();
		while (it.hasNext()) {
		    VariableBinding binding = (VariableBinding) it.next();
		    if (policy.matchesExpressionConstraints(binding)) {
		        result.addBinding(binding);
		    }
		}
        
		return result;
    }

    /**
     * Returns a new result table containig only the rows that are
     * accepted by the {@link RankBasedMetric}s of a given trust policy.
     * @param policy The trust policy whose metrics should be evaluated
     * @param source The untrusted repository
     * @return A result table with the matching rows
     */
    public ResultTable filterByRank(TrustPolicy policy, NamedGraphSet source){
        Collection constraints = policy.getRankBasedConstraints();
        if(constraints.isEmpty()){
        	return this;
        }
        ResultTable result = new ResultTable();

        // build binding list for RankBasedMetric arguments
        RankBasedMetricConstraint constraint = (RankBasedMetricConstraint) constraints.iterator().next();
        List args = constraint.getArgumentBindings(this);

        // use only the first RankeBasedMetric in the list
        RankBasedMetric metric = constraint.getRankBasedMetric() ;

        // call metric
        try{
            metric.init(source, args);

            // filter by results 
            for(int i = 0; i < this.bindings.size(); i++){
                VariableBinding binding = (VariableBinding) this.bindings.get(i);

                if(metric.isAccepted(i)){
                    result.addBinding(binding);
                }

                ExplanationPart expl = metric.explain(i);
                if(expl != null){
                    binding.addTextExplanation(expl);
                }

                Graph g = metric.explainRDF(i);
                if(g != null){
                    binding.addGraphExplanation(g);
                }
            }
        } catch(MetricException e){
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * @param binding A variable binding
     * @return True if this binding is part of this table
     */
    public boolean containsBinding(VariableBinding binding) {
        return this.bindings.contains(binding);
    }

    /**
     * Two result tables are equal if they contain the same
     * bindings. Order does not matter.
     */
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

    /**
     * @return An iterator over the {@link Triple}s in this table,
     * 		obtained by taking the ?SUBJ, ?PRED and ?OBJ columns
     */
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
}
