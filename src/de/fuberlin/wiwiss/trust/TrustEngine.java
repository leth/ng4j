package de.fuberlin.wiwiss.trust;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

/**
 * TODO: Describe this type
 *
 * @version $Id: TrustEngine.java,v 1.4 2005/03/22 01:01:47 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TrustEngine {
	private NamedGraphSet source;
	private VariableBinding systemVariables;
	
	public TrustEngine(NamedGraphSet source) {
	    this(source, new VariableBinding());
	}
	
	public TrustEngine(NamedGraphSet source, VariableBinding systemVariables) {
		if (source == null) {
			throw new IllegalArgumentException("TrustEngine source must not be null");
		}
		this.source = source;
		this.systemVariables = systemVariables;
	}
	
	public QueryResult find(Triple triple) {
		return find(triple, TrustPolicy.TRUST_EVERYTHING);
	}
	
	public QueryResult find(Triple triple, TrustPolicy policy) {
		TriQLQuery query = new QueryFactory(
		        this.source, triple, policy, this.systemVariables).buildQuery();
		ResultTable table = ResultTable.createFromTriQLResult(query.getResults(), triple);
		return new QueryResult(table.filterByConstraints(policy), policy);
	}
	
	public QueryResult fetch(Node subject) {
		return fetch(subject, TrustPolicy.TRUST_EVERYTHING);
	}
	
	public QueryResult fetch(Node subject, TrustPolicy policy) {
		return find(new Triple(subject, Node.ANY, Node.ANY), policy);
	}
}
