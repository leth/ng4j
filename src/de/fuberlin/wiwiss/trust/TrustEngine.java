package de.fuberlin.wiwiss.trust;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

/**
 * This facade wraps an untrusted {@link NamedGraphSet} and provides
 * find and fetch query interfaces that return only triples matching
 * a {@link TrustPolicy}.
 *
 * @version $Id: TrustEngine.java,v 1.7 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 * 
 * TODO: Is this class necessary? Could be inlined into TrustedGraph or turned into service
 */
public class TrustEngine {
	private NamedGraphSet untrustedRepository;
	private VariableBinding systemVariables;

	/**
	 * Sets up a new trust engine.
	 * @param untrustedRepository The untrusted repository
	 */
	public TrustEngine(NamedGraphSet untrustedRepository) {
	    this(untrustedRepository, new VariableBinding());
	}
	
	/**
	 * Sets up a new trust engine.
	 * @param untrustedRepository The untrusted repository
	 * @param systemVariables A binding of variables that is
	 * 		available in policies; might hold stuff like ?NOW and ?USER 
	 */
	public TrustEngine(NamedGraphSet untrustedRepository, VariableBinding systemVariables) {
		if (untrustedRepository == null) {
			throw new IllegalArgumentException("TrustEngine source must not be null");
		}
		this.untrustedRepository = untrustedRepository;
		this.systemVariables = systemVariables;
	}
	
	/**
	 * Finds all triples matching a pattern, using the policy
	 * <em>Trust Everything</em>.
	 * @param triple A triple pattern
	 * @return The matching triples and explanations
	 */
	public QueryResult find(Triple triple) {
		return find(triple, TrustPolicy.TRUST_EVERYTHING);
	}
	
	/**
	 * Finds all triples matching a pattern and fulfilling
	 * a trust policy.
	 * @param triple A triple pattern
	 * @param policy A trust policy
	 * @return The matching triples and explanations
	 */
	public QueryResult find(Triple triple, TrustPolicy policy) {
		TriQLQuery query = new QueryBuilder(
		        this.untrustedRepository, triple, policy, this.systemVariables).buildQuery();
		ResultTable table = ResultTable.createFromTriQLResult(query.getResults(), triple);
		table = table.filterByConstraints(policy);
		table = table.filterByRank(policy , this.untrustedRepository);
		Iterator it = policy.getCountConstraints().iterator();
		while (it.hasNext()) {
			CountConstraint count = (CountConstraint) it.next();
			table = table.filterByCount(count, ResultTable.SPO);
		}
		return new QueryResult(table, policy);
	}
	
	/**
	 * Finds all triples about a subject using the policy
	 * <em>Trust Everything</em>.
	 * @param subject An RDF resource
	 * @return The matching triples and explanations
	 */
	public QueryResult fetch(Node subject) {
		return fetch(subject, TrustPolicy.TRUST_EVERYTHING);
	}
	
	/**
	 * Finds all triples about a subject that fulfill
	 * a trust policy.
	 * @param subject An RDF resource
	 * @param policy A trust policy
	 * @return The matching triples and explanations
	 */
	public QueryResult fetch(Node subject, TrustPolicy policy) {
		return find(new Triple(subject, Node.ANY, Node.ANY), policy);
	}
}
