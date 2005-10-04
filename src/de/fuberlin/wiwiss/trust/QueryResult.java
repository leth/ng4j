package de.fuberlin.wiwiss.trust;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Triple;

/**
 * Encapsulates the result of a find query against the trusted
 * graph: an iterator over the matching triples, and a map
 * from the triples to their explanations.
 * 
 * @version $Id: QueryResult.java,v 1.2 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 * 
 * TODO: Introduce ExplainableTriple and remove this class?
 */
public class QueryResult {
    private ResultTable resultTable;
    private TrustPolicy policy;
    
    /**
     * Sets up a new result.
     * @param resultTable The result table whose ?SUBJ, ?PRED and ?OBJ
     * 		columns form the result triples
     * @param policy The trust policy used to find the results
     */
    public QueryResult(ResultTable resultTable, TrustPolicy policy) {
        this.resultTable = resultTable;
        this.policy = policy;
    }
    
    /**
     * @return An iterator over the result {@link Triple}s
     */
    public Iterator tripleIterator() {
        return this.resultTable.tripleIterator();
    }

    /**
     * @param triple A result triple
     * @return The corresponding explanation
     */
    public Explanation explain(Triple triple) {
        return new Explainer(this.resultTable, triple, this.policy).explain();
    }
}
