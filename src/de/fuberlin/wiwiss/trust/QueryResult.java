package de.fuberlin.wiwiss.trust;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Triple;

/**
 * @version $Id: QueryResult.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QueryResult {
    private ResultTable resultTable;
    private TrustPolicy policy;
    
    public QueryResult(ResultTable resultTable, TrustPolicy policy) {
        this.resultTable = resultTable;
        this.policy = policy;
    }
    
    public Iterator tripleIterator() {
        return this.resultTable.tripleIterator();
    }

    public Explanation explain(Triple triple) {
        return new Explainer(this.resultTable, triple, this.policy).explain();
    }
}
