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
 * @version $Id: TriQLPExpToHTMLRenderer.java,v 1.6 2005/05/31 09:53:56 maresch Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriQLPExpToHTMLRenderer {
    private Explanation expl;
    private NamedGraphSet ngs;
    private PrefixMapping prefixes = new PrefixMappingImpl();
    private List detailsBuffer;
    private long timestamp;
    private String description;
    
    public TriQLPExpToHTMLRenderer(Explanation expl, NamedGraphSet ngs, String desc) {
        this.expl = expl;
        this.detailsBuffer = new ArrayList();
        this.timestamp = System.currentTimeMillis();
        this.ngs = ngs;
        this.description = desc;
    }
    
    public static String renderExplanationPart(ExplanationPart part, NamedGraphSet ngs) {
        Explanation dummyExpl = new Explanation(
                new Triple(Node.ANY, Node.ANY, Node.ANY),
                TrustPolicy.TRUST_EVERYTHING);
        dummyExpl.addPart(part);
        return new TriQLPExpToHTMLRenderer(dummyExpl, ngs, null).getExplanationPartsAsHTML();
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
    
    public String getDetailsAsHTML(){
        StringBuffer buffer = new StringBuffer();
        if(detailsBuffer.isEmpty()){
            buffer.append("No details.");
        }else{
            for(int i = 0; i < detailsBuffer.size(); i++){
                int number = i + 1;
                ExplanationPart detail = (ExplanationPart) detailsBuffer.get(i);
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
    
    public String getExplanationAsHTML() {
    	String expl ="<dl><dt><a name='expanation" + this.timestamp + "'/><h2>The Triple:</h2></dt><dd>"
	            + getSubjectAsHTML() + " " + getPredicateAsHTML() + " " + getObjectAsHTML() + " .</dd>"
	            + "<dt><h2>fulfils the policy:</h2></dt><dd>" + this.description + "</dd>"
	            + "<dt><h2>because:</h2></dt><dd>" + getExplanationPartsAsHTML() + "</dd>";
                String details = null;
    			if(getDetailsAsHTML().indexOf("No details.") != -1){
                	details = "</dl>";
                }else{
                	details = "<dt><h2>Details:</h2></dt><dd>" + getDetailsAsHTML() + "</dd></dl>";
                }
    	return expl + details;
    }
    
    private String getNodeAsHTML(Node node) {
        if (node == null) {
            return "<tt>[null]</tt>";
        }
        if (node.isLiteral()) {
            return escape(node.getLiteral().getLexicalForm());
        }
        if (node.isBlank()) {
        	return "<a href=\"/piggy-bank/default?command=focus&objectURI=urn:bnode:" + node.getBlankNodeId() + "\" target=\"_blank\"><b>(anonymous item)</b></a>";
            //return "<tt>_:" + escape(node.getBlankNodeId().toString()) + "</tt>";
        }
        if(node.isURI()) {
            return "<a href=\"/piggy-bank/default?command=focus&objectURI=" + node.getURI() + "\" target=\"_blank\"><b>" + escape(findLabel((Node_URI) node)) + "</b></a>";
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
                    buffer.append(" (<a href='#detail" + this.timestamp + "_" + detailsBuffer.size() + "'>Detail number " + (detailsBuffer.size() + 1) + "</a>)");
                    detailsBuffer.add(detail);
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
        Iterator it = this.ngs.findQuads(quad);
        
        if(it.hasNext()){
            // if at least one label was found, take the first
            return ((Quad) it.next()).getObject().getLiteral().getLexicalForm();
        } else {
            quad = new Quad(Node.ANY,uri, FOAF.name.getNode(), Node.ANY);
            it = ngs.findQuads(quad);
            
            if(it.hasNext()){
                return ((Quad) it.next()).getObject().getLiteral().getLexicalForm();
            } else {
                // if no label was found try to prefix the uri
                String label = this.prefixes.qnameFor(uri.getURI());
                if (label == null) {
                    // if no prefix is available use the complete URI
                    label = uri.getURI();
                }
                return label;
            }
        }
    }
    
    private String getDescription(PolicySuite ps){
    	return ps.getPolicyDescription(this.expl.getPolicyURI().getURI());
    }
    
}
