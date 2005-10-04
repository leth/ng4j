package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * <p>A node in an {@link Explanation} tree. A node has zero or more
 * children which are also ExplanationParts, and a text fragment
 * which is a list of RDF nodes (literals or URIs which should be
 * rendered as links). The children are unordered.</p>
 * 
 * <p>An ExplanationPart may have a more detailed alternative
 * version, accessible through {@link #getDetails}. Which version
 * is shown should be determined based on user preferences.</p>
 * 
 * @version $Id: ExplanationPart.java,v 1.4 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Oliver Maresch (oliver-maresch@gmx.de)
 */
public class ExplanationPart {
    private Collection parts = new ArrayList();
    private List explanation;
    private ExplanationPart details = null;

    /**
     * Creates a new explanation part without children and with
     * an empty text representation.
     */
    public ExplanationPart() {
        this.explanation = Collections.EMPTY_LIST;
    }

    /**
     * Creates a new explanation part without children and with
     * a text representation.
     * @param explanation The text representation, a list of RDF {@link Node}s
     */
    public ExplanationPart(List explanation) {
        this.explanation = explanation;
    }
    
    /**
     * Adds a child to this explanation part.
     * @param part The new child
     */
    public void addPart(ExplanationPart part) {
        this.parts.add(part);
    }
    
    /**
     * @return The children of this explanation part
     */
    public Collection parts() {
        return this.parts;
    }
    
    /**
     * @return The text representation of this explanation part; a
     * 		list of RDF {@link Node}s.
     */
    public List explanationNodes() {
        return this.explanation;
    }
    
    /**
     * Sets the alternative detailed version of this explanation part.
     * @param details The alternative version
     */
    public void setDetails(ExplanationPart details){
        this.details = details;
    }
    
    /**
     * @return The alternative detailed version of this explanation part,
     * or null if none exists
     */
    public ExplanationPart getDetails(){
        return this.details;
    }
    
    /**
     * Writes this explanation part and all of its children into
     * a Jena RDF {@link Graph}.
     * @param target Statements will be added to this graph
     * @return The RDF node representing this part
     */
    public Node writeAsRDF(Graph target) {
        Node thisPart = Node.createAnon();
        target.add(new Triple(thisPart, RDF.Nodes.type, EXPL.ExplanationPart));
        
        int counter = 1;
        Iterator it = this.explanation.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            Node seqPredicate = Node.createURI(RDF.getURI() + "_"
                    + Integer.toString(counter));
            target.add(new Triple(thisPart, seqPredicate, node));
            counter++;
        }
        
        it = this.parts.iterator();
        while (it.hasNext()) {
            ExplanationPart part = (ExplanationPart) it.next();
            Node child = part.writeAsRDF(target);
            target.add(new Triple(thisPart, EXPL.parts, child));
        }
        
        if (this.details != null){
            Node node = this.details.writeAsRDF(target);
            target.add(new Triple(thisPart, EXPL.details, node));
        }
        
        return thisPart;
    }
    
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ExplanationPart)) {
            return false;
        }
        ExplanationPart otherExplanationPart = (ExplanationPart) other;
        if (!otherExplanationPart.explanation.equals(this.explanation)) {
            return false;
        }
        if (otherExplanationPart.parts.size() != this.parts.size()) {
            return false;
        }
        if (!otherExplanationPart.parts.containsAll(this.parts)) {
            return false;
        }
        if ((this.details != null && !this.details.equals(otherExplanationPart.details))
          || (this.details == null && otherExplanationPart.details != null)){
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        // don't use this.parts.hashCode() because hashCode must stay the
        // same irrespective of order of children
        return this.explanation.hashCode() + this.parts.size();
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer("Part" + this.explanation);
        if (this.parts.isEmpty() && this.details == null) {
            return result.toString();
        }
        result.append(" <");
        if(!this.parts.isEmpty()) {
            result.append("Children(");
            Iterator it = this.parts.iterator();
            while (it.hasNext()) {
                ExplanationPart child = (ExplanationPart) it.next();
                result.append(child);
                if (it.hasNext()) {
                    result.append(", ");
                }
            }
            result.append(")");
            if(this.details != null){
                result.append(", ");
            }
        }
        
        if(this.details != null){
            result.append("Details(" + this.details + ")");
        }
        result.append(">");
        return result.toString();
    }
}
