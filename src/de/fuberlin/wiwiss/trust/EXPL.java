package de.fuberlin.wiwiss.trust;

import com.hp.hpl.jena.graph.Node;

/**
 * Jena Nodes for the
 * <a href="http://www.wiwiss.fu-berlin.de/suhl/bizer/TriQLP/#blocks">Explanation vocabulary</a>.
 * 
 * @version $Id: EXPL.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class EXPL {
    public static final String ns = "http://www.wiwiss.fu-berlin.de/suhl/bizer/ExplanationLanguage/";
    
    // Classes
    public static final Node Explanation = Node.createURI(ns + "Explanation");
    public static final Node ExplanationPart = Node.createURI(ns + "ExplanationPart");

    // Properties
    public static final Node statement = Node.createURI(ns + "statement");
    public static final Node policy = Node.createURI(ns + "policy");
    public static final Node parts = Node.createURI(ns + "parts");
    public static final Node subExplanation = Node.createURI(ns + "subExplanation");
    
    public static String getURI() {
        return ns;
    }
}
