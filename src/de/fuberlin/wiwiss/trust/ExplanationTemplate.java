package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;

/**
 * <p>A template that can be instantiated into an {@link ExplanationPart}.
 * It is created from a string like this:</p>
 * 
 * <pre>
 * "The information was stated by @@?person@@ who works for @@?company@@."
 * </pre>
 * 
 * <p>A template is instantiated by chopping it into its text and
 * variable parts, replacing the text parts by equivalent RDF literal
 * nodes, and replacing the variables with values from a
 * {@link VariableBinding} that is provided to the {@link #instantiate}
 * method. The resulting list of RDF nodes can be used as the text
 * fragment of an ExplanationPart.</p>
 * 
 * <p>A template can also have additional templates as children.
 * Such a template tree is instantiated by providing not a single
 * variable binding, but a set of variable bindings -- a
 * {@link ResultTable}. The process works in these steps:</p>
 * 
 * <ol>
 * <li>Group the result table by those variables that are used in
 *   the parent template</li>
 * <li>For each group, instantiate the parent template once</li>
 * <li>To each of these parent explanations, add the explanations
 *   obtained from instantiating all child templates with the
 *   bindings in the current group.</li>
 * </ol>
 * 
 * <p>The result is a collection of ExplanationParts, one for each
 * group. This process is implemented in {@link #instantiateTree}.</p>
 * 
 * @version $Id: ExplanationTemplate.java,v 1.3 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 * 
 * TODO: Extract ExplanationTemplateString into its own class?
 */
public class ExplanationTemplate {
    private Node[] templateNodes;
    private Set variableNames = new HashSet();
    private Set variablesUsedInTree = new HashSet();
    private Collection childTemplates = new ArrayList();
    
    /**
     * Creates a new explanation template.
     * @param pattern The template string
     * @param lang A language tag for the resulting explanation RDF nodes
     */
    public ExplanationTemplate(String pattern, String lang) {
        compilePattern(pattern, lang);
    }
    
    /**
     * Creates a new explanation template.
     * @param pattern The template string
     */
    public ExplanationTemplate(String pattern) {
        this(pattern, null);
    }
    
    /**
     * Creates a new explanation template that, when instantiated,
     * creates empty text fragments but may still have children.
     */
    public ExplanationTemplate() {
        this("", null);
    }

    /**
     * Adds a child template.
     * @param childTemplate The child template
     */
    public void addChild(ExplanationTemplate childTemplate) {
        this.childTemplates.add(childTemplate);
        this.variablesUsedInTree.addAll(childTemplate.usedVariables());
    }

    /**
     * @return All variables used in this template and all its children,
     * 		as RDF {@link Node}s.
     */
    public Set usedVariables() {
        return this.variablesUsedInTree;
    }
    
    private void compilePattern(String pattern, String lang) {
        Matcher matcher = Pattern.compile("@@\\?([^@]+)@@").matcher(pattern);
        int currentStart = 0;
        List nodes = new ArrayList();
        while (matcher.find()) {
	        if (matcher.start() > currentStart) {
	            nodes.add(createTextNode(pattern.substring(
	                    currentStart, matcher.start()), lang));
	        }
	        Node variable = Node.createVariable(matcher.group(1));
	        nodes.add(variable);
	        this.variablesUsedInTree.add(variable);
	        this.variableNames.add(matcher.group(1));
	        currentStart = matcher.end();
        }
        if (currentStart < pattern.length()) {
            nodes.add(createTextNode(pattern.substring(currentStart), lang));
        }
        this.templateNodes = (Node[]) nodes.toArray(new Node[nodes.size()]);
    }
    
    /**
     * Instantiates the template, without its children.
     * @param binding A variable binding used to fill in the variable slots
     * @return An explanation part without children
     */
    public ExplanationPart instantiate(VariableBinding binding) {
        List instanceNodes = new ArrayList();
        for (int i = 0; i < this.templateNodes.length; i++) {
            if (!this.templateNodes[i].isVariable()) {
                instanceNodes.add(this.templateNodes[i]);
                continue;
            }
            if (!binding.containsName(this.templateNodes[i].getName())) {
                throw new IllegalArgumentException(
                        "Unbound variable: " + this.templateNodes[i]);
            }
            instanceNodes.add(binding.value(this.templateNodes[i].getName()));
        }
        return new ExplanationPart(instanceNodes);
    }

    /**
     * Instantiates this template and all its children.
     * @param results A query result table
     * @return The instantiated ExplanationParts
     */
    public Collection instantiateTree(ResultTable results) {
        List resultParts = new ArrayList();
        Iterator it = results.selectDistinct(this.variableNames).bindingIterator();
        while (it.hasNext()) {
            VariableBinding binding = (VariableBinding) it.next();
            ExplanationPart explanationPart = instantiate(binding);
            ResultTable matchingResults = results.selectMatching(binding);
            Iterator childTemplateIt = this.childTemplates.iterator();
            while (childTemplateIt.hasNext()) {
                ExplanationTemplate childTemplate = (ExplanationTemplate) childTemplateIt.next();
                childTemplate.addInstantiations(explanationPart, matchingResults);
            }
            resultParts.add(explanationPart);
        }
        return resultParts;
    }
    
    private Node createTextNode(String text, String lang) {
        return Node.createLiteral(text, lang, (RDFDatatype) null);
    }

    private void addInstantiations(ExplanationPart parent, ResultTable results) {
        Iterator it2 = instantiateTree(results).iterator();
        while (it2.hasNext()) {
            ExplanationPart part = (ExplanationPart) it2.next();
            parent.addPart(part);
        }
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer("Explanation:");
        if (this.templateNodes.length > 0) {
            result.append(" '");
            for (int i = 0; i < this.templateNodes.length; i++) {
                if (this.templateNodes[i].isVariable()) {
                    result.append("@@?" + this.templateNodes[i].getName() + "@@");
                } else {
                    result.append(this.templateNodes[i].getLiteral().getLexicalForm());
                }
            }
	        result.append("'");
        }
        if (!this.childTemplates.isEmpty()) {
            result.append(" {");
            Iterator it = this.childTemplates.iterator();
            while (it.hasNext()) {
                ExplanationTemplate template = (ExplanationTemplate) it.next();
                result.append(template);
                if (it.hasNext()) {
                    result.append(", ");
                }
            }
            result.append("}");
        }
        return result.toString();
    }
}