package de.fuberlin.wiwiss.trust;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

/**
 * Builds a {@link TriQLQuery} from a {@link NamedGraphSet}, a find query pattern,
 * and a {@link TrustPolicy}. 
 *
 * @version $Id: QueryFactory.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QueryFactory {
	private NamedGraphSet source;
	private Triple findMe;
	private TrustPolicy policy;
	private TriQLQuery query;

	public QueryFactory(NamedGraphSet source, Triple findMe, TrustPolicy policy) {
		this.source = source;
		this.findMe = findMe;
		this.policy = policy;
	}
	
	public TriQLQuery buildQuery() {
		this.query = new TriQLQuery();
		this.query.setSource(this.source);

		this.query.addResultVar(TrustPolicy.GRAPH.getName());

		addResultVarOrPrebind(TrustPolicy.SUBJ, this.findMe.getSubject());
		addResultVarOrPrebind(TrustPolicy.PRED, this.findMe.getPredicate());
		addResultVarOrPrebind(TrustPolicy.OBJ, this.findMe.getObject());
		
		GraphPattern findPattern = new GraphPattern(TrustPolicy.GRAPH);
		findPattern.addTriplePattern(new Triple(
		        TrustPolicy.SUBJ, TrustPolicy.PRED, TrustPolicy.OBJ));
		this.query.addGraphPattern(findPattern);

		for (Iterator iter = this.policy.getGraphPatterns().iterator(); iter.hasNext();) {
			GraphPattern pattern = (GraphPattern) iter.next();
			this.query.addGraphPattern(pattern);
			Iterator it = pattern.getAllVariables().iterator();
			while (it.hasNext()) {
                Node var = (Node) it.next();
                this.query.addResultVar(var.getName());
            }
		}

		Iterator it = this.policy.getPrefixMapping().getNsPrefixMap().keySet().iterator();
		while (it.hasNext()) {
            String prefix = (String) it.next();
            String uri = this.policy.getPrefixMapping().getNsPrefixURI(prefix);
            this.query.setPrefix(prefix, uri);
        }
		
		it = this.policy.getConditions().iterator();
		while (it.hasNext()) {
            Condition condition = (Condition) it.next();
            this.query.addConstraint(condition);
		}
		return this.query;
	}
	
	private void addResultVarOrPrebind(Node var, Node value) {
		if (Node.ANY.equals(value)) {
		    this.query.addResultVar(var.getName());
		} else {
		    this.query.prebindVariableValue(var.getName(), value);
		}
	}
}