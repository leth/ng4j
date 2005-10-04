package de.fuberlin.wiwiss.trust;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;

/**
 * <p>Service that builds an {@link ExplanationTemplate} from the parts
 * provided in a trust policy. The inputs are:</p>
 * 
 * <ul>
 * <li>A collection of {@link GraphPattern}s</li>
 * <li>An explanation template string for each graph pattern</li>
 * <li>An explanation template string for the root of the explanation
 *   template tree.</li>
 * </ul>
 * 
 * <p>The output is the root of a tree of explanation templates.</p>
 * 
 * <p>The main responsibility is to arrange the graph patterns
 * into a tree (done in {@link GraphPatternTreeBuilder} and then walk
 * the tree and build a corresponding tree of ExplanationTemplates.</p>
 * 
 * @version $Id: ExplanationTemplateBuilder.java,v 1.3 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationTemplateBuilder {
    private List patterns;
    private Node rootTemplateNode;
    private Map patternsToTemplateNodes;
    
    /**
     * Sets up a new explanation template builder.
     * @param graphPatterns A list of {@link GraphPattern}s
     * @param rootTextExplanation The root template string 
     * @param patternTextExplanations A map from graph patterns 
     * 		to template string {@link Node}s
     */
    public ExplanationTemplateBuilder(List graphPatterns, Node rootTextExplanation,
            Map patternTextExplanations) {
        this.patterns = graphPatterns;
        this.rootTemplateNode = rootTextExplanation;
        this.patternsToTemplateNodes = patternTextExplanations;
    }

    /**
     * @return The complete explanation template with children
     */
    public ExplanationTemplate explanationTemplate() {
        if (this.rootTemplateNode == null && this.patternsToTemplateNodes.isEmpty()) {
            return null;	// no explanations at all defined
        }
        return createExplanationTemplate(
                this.rootTemplateNode,
                new GraphPatternTreeBuilder(this.patterns).getRootNode());
    }

    private ExplanationTemplate createExplanationTemplate(Node rdfExplanationTemplate, 
            GraphPatternTreeNode currentPattern) {
        ExplanationTemplate result;
        if (rdfExplanationTemplate == null) {
            result = new ExplanationTemplate();
        } else {
            result = new ExplanationTemplate(
		            rdfExplanationTemplate.getLiteral().getLexicalForm(),
		            rdfExplanationTemplate.getLiteral().language());
        }
        addChildTemplates(result, currentPattern);
        return result;
    }

    private void addChildTemplates(ExplanationTemplate parent,
            GraphPatternTreeNode currentPattern) {
        Iterator it = currentPattern.getChildren().iterator();
        while (it.hasNext()) {
            GraphPatternTreeNode child = (GraphPatternTreeNode) it.next();
            Node rdfTemplate = getRDFExplanationTemplateNode(child);
            if (rdfTemplate == null) {
                addChildTemplates(parent, child);
            } else {
                parent.addChild(createExplanationTemplate(rdfTemplate, child));
            }
        }
    }
    
    private Node getRDFExplanationTemplateNode(GraphPatternTreeNode forPattern) {
        return (Node) this.patternsToTemplateNodes.get(
                forPattern.getGraphPattern());        
    }
}
