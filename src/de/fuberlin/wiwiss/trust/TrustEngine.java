package de.fuberlin.wiwiss.trust;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

/**
 * TODO: Describe this type
 *
 * @version $Id: TrustEngine.java,v 1.6 2005/10/02 21:59:28 cyganiak Exp $
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
		table = table.filterByConstraints(policy);
		table = table.filterByRank(policy , this.source);
		Iterator it = policy.getCountConstraints().iterator();
		while (it.hasNext()) {
			CountConstraint count = (CountConstraint) it.next();
			table = table.filterByCount(count, ResultTable.SPO);
		}
		return new QueryResult(table, policy);
	}
	
	public QueryResult fetch(Node subject) {
		return fetch(subject, TrustPolicy.TRUST_EVERYTHING);
	}
	
	public QueryResult fetch(Node subject, TrustPolicy policy) {
		return find(new Triple(subject, Node.ANY, Node.ANY), policy);
	}
}
