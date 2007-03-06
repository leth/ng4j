package de.fuberlin.wiwiss.ng4j.sparql;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

public class SPARQLTest extends TestCase {

	public void testQuery() {
		String query = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
			+ "SELECT ?foafFile ?name ?mbox WHERE {"
			+ "?alice foaf:mbox <mailto:alice@example.com> . "
			+ "?alice foaf:knows [ foaf:mbox ?mbox ] . "
			+ "GRAPH ?foafFile { OPTIONAL { ?known foaf:name ?name } "
			+ "?known foaf:mbox ?mbox } }";
		NamedGraphSet set = new NamedGraphSetImpl();
		set.read(this.getClass().getResourceAsStream("test.trig"), "TRIG", null);
		ResultSet rs = QueryExecutionFactory.create(
				QueryFactory.create(query),
				new NamedGraphDataset(set, Node.createURI("http://example.com/aliceFoaf"))).execSelect();
		MyResultSet expected = new MyResultSet(ModelFactory.createDefaultModel());
		expected.addVar("foafFile", Node.createURI("http://example.com/bobFoaf"));
		expected.addVar("mbox", Node.createURI("mailto:bob@example.com"));
		expected.addSolution();
		expected.addVar("foafFile", Node.createURI("http://example.com/charlieFoaf"));
		expected.addVar("name", Node.createLiteral("Charlie"));
		expected.addVar("mbox", Node.createURI("mailto:charlie@example.com"));
		expected.addSolution();
		// TODO Unfinished!!!
	}
}
