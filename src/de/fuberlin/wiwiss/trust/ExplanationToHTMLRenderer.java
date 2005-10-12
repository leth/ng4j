package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_URI;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.vocabulary.RDFS;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.FOAF;

/**
 * Service that returns HTML representations for an entire
 * {@link Explanation} or some of its parts.
 * 
 * TODO: Move to browser code?
 * 
 * @version $Id: ExplanationToHTMLRenderer.java,v 1.8 2005/10/12 12:35:05 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationToHTMLRenderer {
    private Explanation expl;
    private NamedGraphSet repository;
    private PrefixMapping prefixes = new PrefixMappingImpl();
    private List detailsBuffer;
    private long timestamp;

    /**
     * Sets up a new renderer.
     * @param expl The explanation to be rendered
     * @param repository The untrusted repository,
     * 		used to retrieve labels for resources
     */
    public ExplanationToHTMLRenderer(Explanation expl, NamedGraphSet repository) {
        this.expl = expl;
        this.detailsBuffer = new ArrayList();
        this.timestamp = System.currentTimeMillis();
        this.repository = repository;
    }
    
    /**
     * Convenience method for rendering a single explanation part.
     * @param part An explanation part
     * @param repository The untrusted repository, used to retrieve labels
     * 		for resources
     * @return An HTML fragment representing the explanation part
     */
    public static String renderExplanationPart(ExplanationPart part, NamedGraphSet repository) {
        Explanation dummyExpl = new Explanation(
                new Triple(Node.ANY, Node.ANY, Node.ANY),
                TrustPolicy.TRUST_EVERYTHING);
        dummyExpl.addPart(part);
        return new ExplanationToHTMLRenderer(dummyExpl, repository).getExplanationPartsAsHTML();
    }
    
    /**
     * Sets a prefix map that will be used to render URIs as QNames.
     * @param prefixes A prefix map
     */
    public void setPrefixes(PrefixMapping prefixes) {
        this.prefixes = prefixes;
    }
    
    /**
     * @return An HTML representation of the explained triple's subject,
     * 		including QName compression and URI linking 
     */
    public String getSubjectAsHTML() {
        return getNodeAsHTML(this.expl.getExplainedTriple().getSubject());
    }
    
    /**
     * @return An HTML representation of the explained triple's predicate,
     * 		including QName compression and URI linking 
     */
    public String getPredicateAsHTML() {
        return getNodeAsHTML(this.expl.getExplainedTriple().getPredicate());
    }
    
    /**
     * @return An HTML representation of the explained triple's object,
     * 		including QName compression and URI linking 
     */
    public String getObjectAsHTML() {
        return getNodeAsHTML(this.expl.getExplainedTriple().getObject());
    }

    /**
     * @return An HTML representation of the explained triple's policy URI,
     * 		including QName compression and URI linking 
     */
    public String getPolicyAsHTML() {
        return getNodeAsHTML(this.expl.getPolicyURI());
    }

    /**
     * @return An HTML representation of the parts of the explanation
     */
    public String getExplanationPartsAsHTML() {
        if (this.expl.parts().isEmpty()) {
            return "<em>This policy does not generate explanations</em>";
        }
        StringBuffer result = new StringBuffer();
        renderExplanationParts(this.expl.parts(), result);
        return result.toString();
    }
    
    /**
     * @return An HTML representation of the more detailed alternative
     * 		version of the explanation, if available
     */
    public String getDetailsAsHTML(){
        StringBuffer buffer = new StringBuffer();
        if (this.detailsBuffer.isEmpty()){
            buffer.append("No details.");
        }else{
            for(int i = 0; i < this.detailsBuffer.size(); i++){
                int number = i + 1;
                ExplanationPart detail = (ExplanationPart) this.detailsBuffer.get(i);
                buffer.append("<p><a name='detail" + this.timestamp + "_" + i + "'/><b>Detail number " + number + "</b></p>");
                buffer.append("<p>");
                List parts = new ArrayList();
                parts.add(detail);
                renderExplanationParts(parts, buffer);
                buffer.append("</p>");
            }
        }
        return buffer.toString();
    }
    
    /**
     * @return An HTML representation of the entire explanation
     */
    public String getExplanationAsHTML() {
	    return "<dl><dt><a name='expanation" + this.timestamp + "'/>Triple:</dt><dd>"
	            + getSubjectAsHTML() + " " + getPredicateAsHTML() + " " + getObjectAsHTML() + " .</dd>"
	            + "<dt>Policy:</dt><dd>" + getPolicyAsHTML() + "</dd>"
	            + "<dt>Explanation:</dt><dd>" + getExplanationPartsAsHTML() + "</dd>"
                + "<dt>Details:</dt><dd>" + getDetailsAsHTML() + "</dd></dl>";
    }
    
    private String getNodeAsHTML(Node node) {
        if (node == null) {
            return "<tt>[null]</tt>";
        }
        if (node.isLiteral()) {
            return escape(node.getLiteral().getLexicalForm());
        }
        if (node.isBlank()) {
            return "<tt>_:" + escape(node.getBlankNodeId().toString()) + "</tt>";
        }
        if(node.isURI()) {
            return "<a href=\"" + escape(node.getURI()) + "\">" + escape(findLabel((Node_URI) node)) + "</a>";
//            return "<a href=\"" + escape(node.getURI()) + "\">" + escape(node.getURI()) + "</a>";
        }
        return "<tt>[null]</tt>";
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
                
                // add link to detials 
                ExplanationPart detail = part.getDetails();
                if(detail != null){
                    buffer.append("(<a href='#detail" + this.timestamp + "_" + this.detailsBuffer.size() + "'>detail number " + (this.detailsBuffer.size() + 1) + "</a>)");
                    this.detailsBuffer.add(detail);
                }
                renderExplanationParts(part.parts(), buffer);
            }
            buffer.append("</li>");
        }
        buffer.append("</ul>");
    }
    
    private String escape(String s) {
        s = s.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
        s = s.replaceAll("ä", "&auml;").replaceAll("Ä", "&Auml;");
        s = s.replaceAll("ö", "&ouml;").replaceAll("Ö", "&Ouml;");
        s = s.replaceAll("ü", "&uuml;").replaceAll("Ü", "&Uuml;");
        s = s.replaceAll("ß", "&szlig;");
        return s;
    }
    
    private String findLabel(Node_URI uri){
        Quad quad = new Quad(Node.ANY,uri, RDFS.Nodes.label , Node.ANY); 
        Iterator it = this.repository.findQuads(quad);
        
        if(it.hasNext()){
            // if at least one label was found, take the first
            return ((Quad) it.next()).getObject().getLiteral().getLexicalForm();
        }
        quad = new Quad(Node.ANY,uri, FOAF.name.getNode(), Node.ANY);
        it = this.repository.findQuads(quad);
        
        if(it.hasNext()){
            return ((Quad) it.next()).getObject().getLiteral().getLexicalForm();
        }
        // if no label was found try to prefix the uri
        String label = this.prefixes.qnameFor(uri.getURI());
        if (label == null) {
            // if no prefix is available use the complete URI
            label = uri.getURI();
        }
        return label;
    }
}
