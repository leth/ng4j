package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;

/**
 * A trust policy defines rules for accepting or rejecting RDF statements.
 * A policy consists of graph patterns and conditions. Taken together,
 * they form an implicit TriQL.P query. A statement is trusted if it matches
 * the query. The policy also provides explanations templates which
 * can generate an explanation stating why a particular statement was trusted
 * (but not why a statement was rejected).
 *
 * @version $Id: TrustPolicy.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TrustPolicy {

    /**
     * Default trust policy: Trust all information from any source.
     * TODO: Assign real URI
     */
	public static final TrustPolicy TRUST_EVERYTHING = new TrustPolicy(
	        "http://example.com/trust/policies#TrustEverything");

	/**
	 * Node for use in policy graph patterns, representing the Named Graph
	 * a piece of information is found in 
	 */
	public static final Node GRAPH = Node.createVariable("GRAPH");

	/**
	 * Node for use in policy graph patterns, representing the
	 * subject of the found triple.
	 */
	public static final Node SUBJ = Node.createVariable("SUBJ");

	/**
	 * Node for use in policy graph patterns, representing the
	 * predicate of the found triple.
	 */
	public static final Node PRED = Node.createVariable("PRED");

	/**
	 * Node for use in policy graph patterns, representing the
	 * object of the found triple.
	 */
	public static final Node OBJ = Node.createVariable("OBJ");

	/**
	 * Constant for {@link #addCountRestriction}
	 */
	public static final String OPERATOR_EQUALS = "=";
	
	/**
	 * Constant for {@link #addCountRestriction}
	 */
	public static final String OPERATOR_IS_GREATER = ">";
	
	/**
	 * Constant for {@link #addCountRestriction}
	 */
	public static final String OPERATOR_IS_GREATER_OR_EQUAL = ">=";
	
	/**
	 * Constant for {@link #addCountRestriction}
	 */
	public static final String OPERATOR_IS_LESS = "<";
	
	/**
	 * Constant for {@link #addCountRestriction}
	 */
	public static final String OPERATOR_IS_LESS_OR_EQUAL = "<=";

	private String uri;
	private List graphPatterns = new ArrayList();
	private Collection conditions = new ArrayList();
	
	// Variable names => int (COUNT_XXX constants)
	private Map countRestrictionOperators = new HashMap();
	
	// Variable names => int
	private Map countRestrictionValues = new HashMap();

	private PrefixMapping prefixes = PrefixMapping.Standard;

	private ExplanationTemplate explanationTemplate;
	private Collection metrics = new ArrayList();	// [MetricConstraint]
	
	public TrustPolicy(String uri) {
	    this.uri = uri;
	}
	
	public String getURI() {
	    return this.uri;
	}
	
	public void addPattern(GraphPattern pattern) {
		this.graphPatterns.add(pattern);
	}

	public List getGraphPatterns() {
		return Collections.unmodifiableList(this.graphPatterns);
	}
	
	public void addCountRestriction(String variable, String operator, int value) {
	    this.countRestrictionOperators.put(variable, operator);
	    this.countRestrictionValues.put(variable, new Integer(value));
	}
	
	public Collection getCountRestrictedVars() {
	    return this.countRestrictionOperators.keySet();
	}

	public void setPrefixMapping(PrefixMapping prefixes) {
	    this.prefixes = prefixes;
	}
	
	public PrefixMapping getPrefixMapping() {
	    return this.prefixes;
	}
	
	public boolean isMatchingCount(String variable, int value) {
	    String op = (String) this.countRestrictionOperators.get(variable);
	    if (op == null) {
	        return true;
	    } else if (op.equals(OPERATOR_EQUALS)) {
	        return getCountRestrictionValue(variable) == value;
	    } else if (op.equals(OPERATOR_IS_GREATER)) {
	        return getCountRestrictionValue(variable) > value;
	    } else if (op.equals(OPERATOR_IS_GREATER_OR_EQUAL)) {
	        return getCountRestrictionValue(variable) >= value;
	    } else if (op.equals(OPERATOR_IS_LESS)) {
	        return getCountRestrictionValue(variable) < value;
	    } else if (op.equals(OPERATOR_IS_LESS_OR_EQUAL)) {
	        return getCountRestrictionValue(variable) <= value;
	    } else {
	        throw new IllegalStateException(
	                "Illegal COUNT operator: '" + op + "'");
	    }
	}
	
	public void addCondition(Condition condition) {
	    this.conditions.add(condition);
	}

	public Collection getConditions() {
	    return this.conditions;
	}
	
	public void setExplanationTemplate(ExplanationTemplate expl) {
	    this.explanationTemplate = expl;
	}
	
	public ExplanationTemplate getExplanationTemplate() {
	    return this.explanationTemplate;
	}
	
	public void addMetricConstraint(Metric metric, List arguments) {
	    this.metrics.add(new MetricConstraint(metric, arguments));
	}
	
	public boolean matchesMetricConstraints(VariableBinding binding) {
	    Iterator it = this.metrics.iterator();
	    while (it.hasNext()) {
            MetricConstraint constraint = (MetricConstraint) it.next();
            if (!constraint.isSatisfied(binding)) {
                return false;
            }
        }
	    return true;
	}
	
	private int getCountRestrictionValue(String variable) {
	    Integer value = (Integer) this.countRestrictionValues.get(variable);
	    return value.intValue();
	}
}