package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;

/**
 * Service for building a {@link PolicySuite} from an RDF graph containing
 * the policy's description using the {@link TPL} vocabulary.
 *
 * @version $Id: PolicySuiteFromRDFBuilder.java,v 1.8 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Oliver Maresch (oliver-maresch@gmx.de)
 */
public class PolicySuiteFromRDFBuilder {
    private Graph graph = new GraphMem();
    private List warnings = new ArrayList();
    private PrefixMapping prefixes;
    private PolicySuite resultSuite;
    private Node suiteNode;
    private Collection simpleMetricInstances;
    private Collection rankBasedMetricInstances;
    
    /**
     * @param suiteGraph A Jena RDF graph containing a description of a
     *        policy suite using the TPL vocabulary
     * @param simpleMetricInstances A collection of {@link Metric} instances
     *        that will be available to the policies
     * @param rankBasedMetricInstances A collection of
     * 		{@link RankBasedMetric} instances
     */
    public PolicySuiteFromRDFBuilder(Graph suiteGraph,
    	Collection simpleMetricInstances, Collection rankBasedMetricInstances) {
        this.graph.getBulkUpdateHandler().add(suiteGraph);
        this.prefixes = suiteGraph.getPrefixMapping();
        this.simpleMetricInstances = simpleMetricInstances;
        this.rankBasedMetricInstances = rankBasedMetricInstances;
    }
    
    /**
     * Builds the policy suite from the TPL graph.
     * @return A policy suite
     * @throws TPLException on error in the TPL file
     */
    public PolicySuite buildPolicySuite() {
        this.suiteNode = findSuiteNode();
        this.resultSuite = new PolicySuite(getFirstLiteral(this.suiteNode, TPL.suiteName));
        buildPolicies();
        checkForUnlinkedPolicies();
        return this.resultSuite;
    }

    /**
     * Gets all warnings that occured while building the policy suite. Must
     * not be called before {@link #buildPolicySuite}.
     * @return A list of Strings
     */
    public List getWarnings() {
        return this.warnings;
    }

    private Node findSuiteNode() {
        inferTypeFromDomain(TPL.TrustPolicySuite, TPL.suiteName);
        inferTypeFromDomain(TPL.TrustPolicySuite, TPL.includesPolicy);

        requireAtLeastOne(Node.ANY, RDF.Nodes.type, TPL.TrustPolicySuite,
                "No tpl:TrustPolicySuite found in TPL file");
        warnIfMoreThanOne(Node.ANY, RDF.Nodes.type, TPL.TrustPolicySuite,
                "More than one tpl:TrustPolicySuite per TPL file is not supported");
        return getFirstSubject(RDF.Nodes.type, TPL.TrustPolicySuite);
    }

    private void buildPolicies() {
        Collection policyNodes = getAllObjects(this.suiteNode, TPL.includesPolicy);
        if (policyNodes.isEmpty()) {
            warn("Suite doesn't include any policies (tpl:includesPolicy)");
        }
        Iterator it = policyNodes.iterator();
        while (it.hasNext()) {
            Node policyNode = (Node) it.next();
            if (!policyNode.isURI()) {
                throw new TPLException(
                        "tpl:TrustPolicies must be URIs; " + policyNode + " is not");
            }
            buildPolicy(policyNode);
        }
    }

    private void buildPolicy(Node policyNode) {
        String name = getFirstLiteral(policyNode, TPL.policyName);
        String description = getFirstLiteral(policyNode, TPL.policyDescription);
        Map explTemplatesForPattern = new HashMap();
        TrustPolicy policy = new TrustPolicy(policyNode.getURI());
        
        this.resultSuite.addPolicy(policyNode.getURI(), name, description, policy);

        policy.setPrefixMapping(this.prefixes);
        
        Collection patterns = getAllObjects(policyNode, TPL.graphPattern);
        Iterator it = patterns.iterator();
        while (it.hasNext()) {
            Node patternNode = (Node) it.next();
            String pattern = getFirstLiteral(patternNode, TPL.pattern);
            if (pattern == null) {
                throw new TPLException("GraphPattern " + patternNode +
                        " has no tpl:pattern");
            }
            GraphPattern p = buildPattern(pattern);
            policy.addPattern(p);
            
            warnIfMoreThanOne(patternNode, TPL.textExplanation, Node.ANY,
            		"Ignored multiple tpl:textExplanations on tpl:GraphPattern");
            Node textExplanation = getFirstObject(patternNode, TPL.textExplanation);
            if (textExplanation != null) {
                explTemplatesForPattern.put(p, textExplanation);
            }
        }
        
        Collection constraints = getAllObjects(policyNode, TPL.constraint);
        it = constraints.iterator();
        while (it.hasNext()) {
            Node constraintNode = (Node) it.next();
            if (!constraintNode.isLiteral()) {
                throw new TPLException("Objects of tpl:condition must be literals; "
                        + constraintNode + " is not");
            }
            addConstraintToPolicy(constraintNode.getLiteral().getLexicalForm(), policy);
        }
        
        warnIfMoreThanOne(policyNode, TPL.textExplanation, Node.ANY,
                "Ignored multiple tpl:textExplanations on tpl:TrustPolicy");
        Node textExplanation = getFirstObject(policyNode, TPL.textExplanation);
        if (textExplanation == null && explTemplatesForPattern.isEmpty()) {
            return;
        }
        ExplanationTemplate template = new ExplanationTemplateBuilder(
                policy.getGraphPatterns(),
                textExplanation,
                explTemplatesForPattern).explanationTemplate();
        policy.setExplanationTemplate(template);
    }
    
    private GraphPattern buildPattern(String pattern) {
        return new GraphPatternParser(pattern, this.prefixes).parse();
    }
    
    private void addConstraintToPolicy(String constraint, TrustPolicy policy) {
        ConstraintParser parser = new ConstraintParser(
                constraint, this.prefixes, this.simpleMetricInstances, this.rankBasedMetricInstances);
        if (parser.isCountConstraint()) {
            policy.addCountConstraint(parser.parseCountConstraint());
        } else if(parser.isRankBasedConstraint()){
            policy.addRankBasedConstraint(parser.parseRankBasedConstraint());
        } else {
            policy.addExpressionConstraint(parser.parseExpressionConstraint());
        }
    }
    
    private void checkForUnlinkedPolicies() {
        inferTypeFromDomain(TPL.TrustPolicy, TPL.graphPattern);
        inferTypeFromDomain(TPL.TrustPolicy, TPL.constraint);
        Collection unlinkedPolicyNodes = getAllSubjects(RDF.Nodes.type, TPL.TrustPolicy);
        unlinkedPolicyNodes.removeAll(getAllObjects(this.suiteNode, TPL.includesPolicy));
        Iterator it = unlinkedPolicyNodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            warn("Trust policy " + node + " is not included in suite");
        }
    }
    
    private void inferTypeFromDomain(Node type, Node property) {
        Iterator it = this.graph.find(Node.ANY, property, Node.ANY);
        while (it.hasNext()) {
            Triple t = (Triple) it.next();
            this.graph.add(new Triple(t.getSubject(), RDF.Nodes.type, type));
        }
    }

    private void requireAtLeastOne(Node s, Node p, Node o, String error) {
        if (!this.graph.find(s, p, o).hasNext()) {
            throw new TPLException(error);
        }
    }

    private void warnIfMoreThanOne(Node s, Node p, Node o, String error) {
        List list = new ArrayList();
        Iterator it = this.graph.find(s, p, o);
        while (it.hasNext()) {
            list.add(it.next());
        }
        if (list.size() <= 1) {
            return;
        }
        warn(error);
    }

    private Node getFirstSubject(Node predicate, Node object) {
        Iterator it = this.graph.find(Node.ANY, predicate, object);
        if (!it.hasNext()) {
            return null;
        }
        return ((Triple) it.next()).getSubject();
    }

    private Node getFirstObject(Node subject, Node predicate) {
        Iterator it = this.graph.find(subject, predicate, Node.ANY);
        if (!it.hasNext()) {
            return null;
        }
        return ((Triple) it.next()).getObject();
    }

    private String getFirstLiteral(Node subject, Node predicate) {
        Iterator it = this.graph.find(subject, predicate, Node.ANY);
        while (it.hasNext()) {
            Triple t = (Triple) it.next();
            if (!t.getObject().isLiteral()) {
                continue;
            }
            return t.getObject().getLiteral().getLexicalForm();
        }
        return null;
    }
    
    private Collection getAllSubjects(Node predicate, Node object) {
        Collection subjects = new ArrayList();
        Iterator it = this.graph.find(Node.ANY, predicate, object);
        while (it.hasNext()) {
            Triple t = (Triple) it.next();
            subjects.add(t.getSubject());
        }
        return subjects;
    }
    
    private Collection getAllObjects(Node subject, Node predicate) {
        Collection objects = new ArrayList();
        Iterator it = this.graph.find(subject, predicate, Node.ANY);
        while (it.hasNext()) {
            Triple t = (Triple) it.next();
            objects.add(t.getObject());
        }
        return objects;
    }
    
    private void warn(String error) {
        this.warnings.add(error);
    }
}
