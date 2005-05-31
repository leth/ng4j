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
 * A trust policy defines rules for accepting or rejecting RDF statements.
 * A policy consists of graph patterns and conditions. Taken together,
 * they form an implicit TriQL.P query. A statement is trusted if it matches
 * the query. The policy also provides explanations templates which
 * can generate an explanation stating why a particular statement was trusted
 * (but not why a statement was rejected).
 *
 * @version $Id: TrustPolicy.java,v 1.7 2005/05/31 09:53:56 maresch Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
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

	private PrefixMapping prefixes = PrefixMapping.Standard;

	private ExplanationTemplate explanationTemplate;
	
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
	
	public void addCountConstraint(CountConstraint count) {
	    this.countConstraints.add(count);
	}

	public Collection getCountConstraints() {
	    return Collections.unmodifiableCollection(this.countConstraints);
	}
	
	public void setPrefixMapping(PrefixMapping prefixes) {
	    this.prefixes = prefixes;
	}
	
	public PrefixMapping getPrefixMapping() {
	    return this.prefixes;
	}
	
	public void addExpressionConstraint(ExpressionConstraint condition) {
	    this.constraints.add(condition);
	}

	public Collection getExpressionConstraints() {
	    return this.constraints;
	}
	
	public boolean matchesConstraints(VariableBinding binding) {
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
	
	public void setExplanationTemplate(ExplanationTemplate expl) {
	    this.explanationTemplate = expl;
	}
	
	public ExplanationTemplate getExplanationTemplate() {
	    return this.explanationTemplate;
	}
}