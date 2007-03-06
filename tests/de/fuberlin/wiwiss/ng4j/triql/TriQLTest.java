//$Id: TriQLTest.java,v 1.5 2007/03/06 14:13:33 zedlitz Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Base class for TriQL tests; provides a simple query result assertion facility
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public abstract class TriQLTest extends TestCase {
	private String query;
	private TriQLQuery q;
	private List results;
	private Map expectedBinding;
	protected NamedGraphSet set;
	
	public void setUp() throws Exception {
		this.set = createNamedGraphSet();
		this.expectedBinding = new HashMap();		
	}

	/**
	 * Creates the NamedGraphSet instance under test. Might be overridden
	 * by subclasses to test other NamedGraphSet implementations.
	 */
	protected NamedGraphSet createNamedGraphSet() throws Exception {
		return new NamedGraphSetImpl();
	}

	public void tearDown() throws Exception {
		this.set.close();
	}

	protected void setQuery(String queryString) {
		this.query = queryString;
		this.q = new TriQLQuery(this.set, this.query);
	}
	
	protected TriQLQuery getQuery() {
		return this.q;
	}

	protected void executeQuery() {
		this.q.setPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		// ... could add more namespaces here
		this.results = this.q.getResultsAsList();
	}
	
	protected void addQuad(Node graphName, Node s, Node p, Node o) {
		this.set.addQuad(new Quad(graphName, s, p, o));
	}
	
	protected void assertExpectedBindingCount(int count) {
		assertEquals(count, this.results.size());
	}
	
	protected void expectBinding(String variableName, Node value) {
		this.expectedBinding.put(variableName, value);
	}
	
	protected void assertExpectedBinding() {
		assertTrue(this.results.contains(this.expectedBinding));
		this.expectedBinding.clear();
	}
	
	protected void dumpResults() {
		System.out.println("result bindings: " + this.results.size());
		int i = 0;
		Iterator it = this.results.iterator();
		while (it.hasNext()) {
			Map binding = (Map) it.next();
			System.out.println(i + ": " + binding);
			i++;
		}
	}
}