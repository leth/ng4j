package de.fuberlin.wiwiss.trust;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

/**
 * TODO: Describe this type
 *
 * @version $Id: TrustEngine.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TrustEngine {
	private NamedGraphSet source;

	public TrustEngine(NamedGraphSet source) {
		if (source == null) {
			throw new IllegalArgumentException("TrustEngine source must not be null");
		}
		this.source = source;
	}
	
	public QueryResult find(Triple triple) {
		return find(triple, TrustPolicy.TRUST_EVERYTHING);
	}
	
	public QueryResult find(Triple triple, TrustPolicy policy) {
		TriQLQuery query = new QueryFactory(this.source, triple, policy).buildQuery();
		ResultTable table = ResultTable.createFromTriQLResult(query.getResults(), triple);
		return new QueryResult(table.filterByMetrics(policy), policy);
	}
	
	public QueryResult fetch(Node subject) {
		return fetch(subject, TrustPolicy.TRUST_EVERYTHING);
	}
	
	public QueryResult fetch(Node subject, TrustPolicy policy) {
		return find(new Triple(subject, Node.ANY, Node.ANY), policy);
	}
}
