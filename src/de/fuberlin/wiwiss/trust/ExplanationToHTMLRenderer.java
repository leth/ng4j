package de.fuberlin.wiwiss.trust;

import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

/**
 * @version $Id: ExplanationToHTMLRenderer.java,v 1.1 2005/03/22 01:01:48 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationToHTMLRenderer {
    private Explanation expl;
    private PrefixMapping prefixes = new PrefixMappingImpl();
    
    public ExplanationToHTMLRenderer(Explanation expl) {
        this.expl = expl;
    }
    
    public static String renderExplanationPart(ExplanationPart part) {
        Explanation dummyExpl = new Explanation(
                new Triple(Node.ANY, Node.ANY, Node.ANY),
                TrustPolicy.TRUST_EVERYTHING);
        dummyExpl.addPart(part);
        return new ExplanationToHTMLRenderer(dummyExpl).getExplanationPartsAsHTML();
    }
    
    public void setPrefixes(PrefixMapping prefixes) {
        this.prefixes = prefixes;
    }
    
    public String getSubjectAsHTML() {
        return getNodeAsHTML(this.expl.getExplainedTriple().getSubject());
    }
    
    public String getPredicateAsHTML() {
        return getNodeAsHTML(this.expl.getExplainedTriple().getPredicate());
    }
    
    public String getObjectAsHTML() {
        return getNodeAsHTML(this.expl.getExplainedTriple().getObject());
    }

    public String getPolicyAsHTML() {
        return getNodeAsHTML(this.expl.getPolicyURI());
    }

    public String getExplanationPartsAsHTML() {
        if (this.expl.parts().isEmpty()) {
            return "<em>This policy does not generate explanations</em>";
        }
        StringBuffer result = new StringBuffer();
        renderExplanationParts(this.expl.parts(), result);
        return result.toString();
    }
    
    public String getExplanationAsHTML() {
	    return "<dl><dt>Triple:</dt><dd>"
	            + getSubjectAsHTML() + " " + getPredicateAsHTML() + " " + getObjectAsHTML() + " .</dd>"
	            + "<dt>Policy:</dt><dd>" + getPolicyAsHTML() + "</dd>"
	            + "<dt>Explanation:</dt><dd>" + getExplanationPartsAsHTML() + "</dd></dl>";
    }
    
    private String getNodeAsHTML(Node node) {
        if (node.isLiteral()) {
            return escape(node.getLiteral().getLexicalForm());
        }
        if (node.isBlank()) {
            return "<tt>_:" + escape(node.getBlankNodeId().toString()) + "</tt>";
        }
        String label = this.prefixes.qnameFor(node.getURI());
        if (label == null) {
            label = node.getURI();
        }
        return "<a href=\"" + escape(node.getURI()) + "\">" + escape(label) + "</a>";
    }
    
    private void renderExplanationParts(Collection parts, StringBuffer buffer) {
        if (parts.isEmpty()) {
            return;
        }
        buffer.append("<ul>");
        Iterator it = parts.iterator();
        while (it.hasNext()) {
            ExplanationPart part = (ExplanationPart) it.next();
            buffer.append("<li>");
            if (part.explanationNodes().isEmpty() && part.parts().isEmpty()) {
	            buffer.append("<em>empty ExplanationPart</em>");
            } else {
                Iterator it2 = part.explanationNodes().iterator();
                while (it2.hasNext()) {
                    Node node = (Node) it2.next();
                    buffer.append(getNodeAsHTML(node));
                }
                renderExplanationParts(part.parts(), buffer);
            }
            buffer.append("</li>");
        }
        buffer.append("</ul>");
    }
    
    private String escape(String s) {
        return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
    }
}
