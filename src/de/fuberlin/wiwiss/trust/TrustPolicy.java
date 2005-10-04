package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;

/**
 * <p>A trust policy defines rules for accepting or rejecting RDF statements.
 * A policy consists of graph patterns ({@link GraphPattern})
 * and various forms of conditions ({@link ExpressionConstraint},
 * {@link CountConstraint}, {@link RankBasedMetricConstraint}). Taken together,
 * they form an implicit TriQL.P query. A statement is trusted if it matches
 * the query. The policy also provides explanations templates which
 * can generate an explanation stating why a particular statement was trusted
 * (but not why a statement was rejected).</p>
 *
 * <p>A trust policy must be identified by a unique URI.</p>
 * 
 * <p>Trust policy instances are created mainly through the
 * {@link de.fuberlin.wiwiss.trust.PolicySuiteFromRDFBuilder}.</p>
 * 
 * @version $Id: TrustPolicy.java,v 1.9 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Oliver Maresch (oliver-maresch@gmx.de)
 */
public class TrustPolicy {

    /**
     * Default trust policy: Trust all information from any source.
     * TODO: Assign real URI
     */
	public static final TrustPolicy TRUST_EVERYTHING = new TrustPolicy(
	        "http://www.wiwiss.fu-berlin.de/suhl/bizer/TPL/TrustEverything");

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

	private String uri;
	private List graphPatterns = new ArrayList();
	private Collection constraints = new ArrayList();
	private Collection countConstraints = new ArrayList();
    private Collection rankBasedConstraints = new ArrayList();

	private PrefixMapping prefixes = PrefixMapping.Standard;

	private ExplanationTemplate explanationTemplate;

	/**
	 * Creates a new trust policy.
	 * @param uri A unique ID for the policy
	 */
	public TrustPolicy(String uri) {
	    this.uri = uri;
	}
	
	/**
	 * @return The policy's unique ID
	 */
	public String getURI() {
	    return this.uri;
	}
	
	/**
	 * Adds a graph pattern to the policy.
	 * @param pattern A graph pattern
	 */
	public void addPattern(GraphPattern pattern) {
		this.graphPatterns.add(pattern);
	}

	/**
	 * @return An unordered collection of {@link GraphPattern}s
	 */
	public List getGraphPatterns() {
		return Collections.unmodifiableList(this.graphPatterns);
	}
	
	/**
	 * Adds a count constraint that limits how often a variable
	 * must be bound in order for a triple to be accepted.
	 * @param count The count constraint 
	 */
	public void addCountConstraint(CountConstraint count) {
	    this.countConstraints.add(count);
	}

	/**
	 * @return An unordered collection of the policy's
	 * 		{@link CountConstraint}s
	 */
	public Collection getCountConstraints() {
	    return Collections.unmodifiableCollection(this.countConstraints);
	}

	/**
	 * Adds a rank-based metric constraint to the policy.
	 * @param constraint The constraint
	 */
    public void addRankBasedConstraint(RankBasedMetricConstraint constraint){
        this.rankBasedConstraints.add(constraint);
    }
    
    /**
     * @return An unordered collection of the policy's
     * 		{@link RankBasedMetricConstraint}s
     */
    public Collection getRankBasedConstraints(){
        return this.rankBasedConstraints;
    }
	
    /**
     * Sets a prefix map that is used throughout the policy to expand and
     * contract URIs/QNames.
     * @param prefixes
     */
	public void setPrefixMapping(PrefixMapping prefixes) {
	    this.prefixes = prefixes;
	}
	
	/**
	 * @return The prefix map that is used throughout the policy to expand
	 * and contract URIs/QNames
	 */
	public PrefixMapping getPrefixMapping() {
	    return this.prefixes;
	}
	
	/**
	 * Adds an expression constraint to the trust policy.
	 * @param condition
	 */
	public void addExpressionConstraint(ExpressionConstraint condition) {
	    this.constraints.add(condition);
	}

	/**
	 * @return An unordered collection of the policy's
	 * {@link ExpressionConstraint}s
	 */
	public Collection getExpressionConstraints() {
	    return this.constraints;
	}
	
	/**
	 * Checks if a variable binding matches the expression constraints
	 * of the policy.
	 * @param binding A variable binding 
	 * @return True if the binding matches the expression constraints
	 * TODO: Has the side effect of adding explanations from metrics to the binding. Ugly!
	 */
	public boolean matchesExpressionConstraints(VariableBinding binding) {
	    Iterator it = this.constraints.iterator();
	    while (it.hasNext()) {
            ExpressionConstraint constraint = (ExpressionConstraint) it.next();
            EvaluationResult result = constraint.evaluate(binding);
            ExplanationPart expl = result.getTextExplanation();
            if(expl != null){
                binding.addTextExplanation(expl);
            }
            Graph g = result.getGraphExplanation();
            if(g != null){
                binding.addGraphExplanation(g);
            }
            if (!result.getResult()) {
                return false;
            }
        }
	    return true;
	}
	
	/**
	 * Sets the explanation template used to explain why a triple
	 * matches the policy.
	 * @param expl An explanation template
	 */
	public void setExplanationTemplate(ExplanationTemplate expl) {
	    this.explanationTemplate = expl;
	}
	
	/**
	 * @return The policy's explanation template
	 */
	public ExplanationTemplate getExplanationTemplate() {
	    return this.explanationTemplate;
	}
}