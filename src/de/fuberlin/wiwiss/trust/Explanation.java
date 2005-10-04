package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * <p>A textual explanation why a given triple matched a given trust
 * policy.</p>
 * 
 * <p>Explanations are structured as a tree of text fragments.
 * This class represents the root of the tree and has no associated
 * text fragment; the other nodes are {@link ExplanationPart}s and all
 * have associated text fragments. The main process for creating
 * explanations is the {@link ExplanationTemplate}.<p>
 * 
 * <p>The main capability of an explanation is to write itself into
 * an RDF graph using the {@link EXPL} vocabulary.</p>
 * 
 * @version $Id: Explanation.java,v 1.4 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Oliver Maresch
 */
public class Explanation {
    private Collection parts = new ArrayList();
    private Triple triple;
    private TrustPolicy policy;
   
    /**
     * Creates a new, empty explanation.
     * 
     * @param triple An RDF triple
     * @param policy The policy that has accepted the triple
     */
    public Explanation(Triple triple, TrustPolicy policy) {
        this.triple = triple;
        this.policy = policy;
    }
    
    /**
     * Adds a new child to the root node of this explanation tree.
     * @param part The new child
     */
    public void addPart(ExplanationPart part) {
        this.parts.add(part);
    }
    
    /**
     * @return all children of the root of this explanation tree
     */
    public Collection parts() {
        return this.parts;
    }

    /**
     * Checks two explanations for equality. Two explanations are
     * equal if the trees have the same structure and the same text
     * at every node, and if they explain the same triple for the
     * same policy.
     * @param other Another object
     * @return true if other is an explanation and equal to this 
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Explanation)) {
            return false;
        }
        Explanation otherExplanation = (Explanation) other;
        if (otherExplanation.parts.size() != this.parts.size()) {
            return false;
        }
        if (!otherExplanation.parts.containsAll(this.parts)) {
            return false;
        }
        if (!otherExplanation.triple.equals(this.triple)) {
            return false;
        }
        if (!otherExplanation.policy.getURI().equals(this.policy.getURI())) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        // don't use this.parts.hashCode() because hashCode must stay the
        // same irrespective of order of children
        return this.parts.size() + this.triple.hashCode()
        			+ this.policy.getURI().hashCode();
    }

    /**
     * @return A Jena graph representation of the explanation using
     * the {@link EXPL} vocabulary
     */
    public Graph toRDF() {
        Graph result = ModelFactory.createDefaultModel().getGraph();
        Node explanation = Node.createAnon(new AnonId("explanation"));
        Node statement = Node.createAnon(new AnonId("explainedStatement"));
        result.add(new Triple(explanation, RDF.Nodes.type, EXPL.Explanation));
        result.add(new Triple(explanation, EXPL.policy, Node.createURI(
                this.policy.getURI())));
        result.add(new Triple(explanation, EXPL.statement, statement));
        result.add(new Triple(statement, RDF.Nodes.type, RDF.Nodes.Statement));
        result.add(new Triple(statement, RDF.Nodes.subject, this.triple.getSubject()));
        result.add(new Triple(statement, RDF.Nodes.predicate, this.triple.getPredicate()));
        result.add(new Triple(statement, RDF.Nodes.object, this.triple.getObject()));
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            ExplanationPart part = (ExplanationPart) it.next();
            Node child = part.writeAsRDF(result);
            result.add(new Triple(explanation, EXPL.parts, child));
        }
        return result;
    }
    
    /**
     * @return The RDF triple of this explanation
     */
    public Triple getExplainedTriple() {
        return this.triple;
    }
    
    /**
     * @return The URI of the policy of this explanation
     */
    public Node getPolicyURI() {
        return Node.createURI(this.policy.getURI());
    }
}
