package de.fuberlin.wiwiss.ng4j.sparql;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/** The original SPARQLTest testQuery() was unfinished and has been commented out.<p>
 * 
 * These tests are modeled after tests that were in class TriQLGraphTest.  
 * Probably overkill; the testing of TriQL was necessary because NG4J contained
 * the implementation of TriQL.  The same is not true for SPARQL. 
 * 
 * Only implemented the first few such tests.
 * 
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class SPARQLTest extends TestCase {

	protected NamedGraphSet set;
	protected NamedGraphDataset localDS;
//	private Map expectedBinding;
	ResultSetRewindable results;
	
	public void setUp() throws Exception {
		this.set = createNamedGraphSet();
		this.localDS = new NamedGraphDataset( set );
//		this.expectedBinding = new HashMap();
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

	protected void assertExpectedBindingCount(int count) {
		assertEquals(count, this.results.size());
	}

//	protected void expectBinding(String variableName, Node value) {
//		this.expectedBinding.put(variableName, value);
//	}

//	protected void assertExpectedBinding() {
//		results.
//		assertTrue(this.results.contains(this.expectedBinding));
//		this.expectedBinding.clear();
//	}
//
//	protected void dumpResults() {
//		System.out.println("result bindings: " + this.results.size());
//		int i = 0;
//		Iterator it = this.results.iterator();
//		while (it.hasNext()) {
//			Map binding = (Map) it.next();
//			System.out.println(i + ": " + binding);
//			i++;
//		}
//	}


	private static final Node aURI = Node.createURI("http://example.org/data#a");
	private static final Node bURI = Node.createURI("http://example.org/data#b");
	private static final Node cURI = Node.createURI("http://example.org/data#c");
	private static final Node graph1 = Node.createURI("http://example.com/graph1");
	private static final Node graph2 = Node.createURI("http://example.com/graph2");
	//private static final Node graph3 = Node.createURI("http://example.com/graph3");

	public static final String NL = System.getProperty("line.separator") ;

	/**
	 * Check for correct results with a minimal query
	 */
	public void testSimpleResult() {
		this.set.addQuad(new Quad(graph1, aURI, bURI, cURI));
		
		String queryString = "SELECT ?graph ?subject ?predicate ?object" + NL
			+ "WHERE { GRAPH ?graph {" + NL
			+ "?subject ?predicate ?object" + NL
			+ " } }";

		QueryExecution qe = QueryExecutionFactory.create( queryString, localDS );
		results = ResultSetFactory.copyResults( qe.execSelect() );
		Graph graph = results.getResourceModel().getGraph();

//		dumpResults();
		assertExpectedBindingCount(1);
		assertTrue(graph.contains(aURI, bURI, cURI));
	}

	/**
	 * ?a is not bound and must not be returned
	 */
	public void testUnboundVariable() {
		this.set.addQuad(new Quad(graph1, aURI, bURI, cURI));
		this.set.addQuad(new Quad(graph1, aURI, bURI, aURI));
		
		String queryString = "SELECT ?predicate" + NL
			+ "WHERE { GRAPH ?graph {" + NL
			+ "?subject ?predicate <" + cURI + ">" + NL
			+ " } }";
		
		QueryExecution qe = QueryExecutionFactory.create( queryString, localDS );
		results = ResultSetFactory.copyResults( qe.execSelect() );
		
		assertExpectedBindingCount(1);
		
		Binding binding = results.nextBinding();
		Node node = binding.get((Var)binding.vars().next());
		assertEquals(node, bURI);
	}

	
	/**
	 * Graph name specified; graph2 does not match and must not be returned
	 */
	public void testGraphSelection() {
		this.set.addQuad(new Quad(graph1, aURI, aURI, aURI));
		this.set.addQuad(new Quad(graph2, bURI, bURI, bURI));
		
		String queryString = "SELECT ?subject" + NL
			+ "WHERE { GRAPH <" + graph1 + "> {" + NL
			+ "?subject ?predicate ?object" + NL
			+ " } }";
		
		QueryExecution qe = QueryExecutionFactory.create( queryString, localDS );
		results = ResultSetFactory.copyResults( qe.execSelect() );
		
//		dumpResults();
		assertExpectedBindingCount(1);
		
		Binding binding = results.nextBinding();
		Node node = binding.get((Var)binding.vars().next());
		assertEquals(node, aURI);
	}

//	/**
//	 * Check if graph names are returned as results
//	 */
//	public void testFetchGraphName() {
//		this.set.addQuad(new Quad(graph1, aURI, aURI, aURI));
//		this.set.addQuad(new Quad(graph2, bURI, bURI, bURI));
//		this.set.addQuad(new Quad(graph3, aURI, bURI, cURI));
//		
//		String queryString = "SELECT ?graph ?object" + NL
//			+ "WHERE { GRAPH ?graph {" + NL
//			+ "<" + aURI + "> ?predicate ?object" + NL
//			+ " } }";
//		
//		QueryExecution qe = QueryExecutionFactory.create( queryString, localDS );
//		results = ResultSetFactory.copyResults( qe.execSelect() );
//		
////		dumpResults();
//		assertExpectedBindingCount(2);
//		
//		setQuery("SELECT ?a, ?c WHERE ?a (<" + aURI + ">, ?b, ?c)");
//		
//		expectBinding("a", graph1);
//		expectBinding("c", aURI);
//		assertExpectedBinding();
//		expectBinding("a", graph3);
//		expectBinding("c", cURI);
//		assertExpectedBinding();
//	}

//	/**
//	 * Only graph3 contains both required patterns
//	 */
//	public void testGraphWithTwoTriples() {
//		setQuery("SELECT ?g WHERE ?g (?a ?b ?c . ?a ?c ?b)");
//		addQuad(graph1, aURI, bURI, cURI);
//		addQuad(graph2, aURI, cURI, bURI);
//		addQuad(graph3, aURI, bURI, cURI);
//		addQuad(graph3, aURI, cURI, bURI);
//		executeQuery();
////		dumpResults();
//		assertExpectedBindingCount(2);
//		expectBinding("g", graph3);
//		assertExpectedBinding();
//		expectBinding("g", graph3);
//		assertExpectedBinding();
//	}

//	public void testLiteralInPattern() {
//		setQuery("SELECT * WHERE (?x ?y 5)");
//		addQuad(graph1, aURI, bURI, Node.createLiteral("5", null, XSDDatatype.XSDinteger));
//		addQuad(graph1, aURI, bURI, Node.createLiteral("5.7", null, null));
//		executeQuery();
////		dumpResults();
//		assertExpectedBindingCount(1);
//		expectBinding("x", aURI);
//		expectBinding("y", bURI);
//		assertExpectedBinding();		
//	}
//
//	public void testLiteralInPattern2() {
//		setQuery("SELECT * WHERE (?x ?y \"5\")");
//		addQuad(graph1, aURI, bURI, Node.createLiteral("5", null, null));
//		addQuad(graph1, aURI, bURI, Node.createLiteral("5.7", null, null));
//		executeQuery();
////		dumpResults();
//		assertExpectedBindingCount(1);
//		expectBinding("x", aURI);
//		expectBinding("y", bURI);
//		assertExpectedBinding();
//	}
//
//	public void testLiteralInPattern3() {
//		setQuery("SELECT * WHERE (?x ?y \"5\"^^xsd:integer)");
//		addQuad(graph1, aURI, bURI, Node.createLiteral("5", null, XSDDatatype.XSDinteger));
//		addQuad(graph1, aURI, bURI, Node.createLiteral("5.7", null, null));
//		executeQuery();
////		dumpResults();
//		assertExpectedBindingCount(1);
//		expectBinding("x", aURI);
//		expectBinding("y", bURI);
//		assertExpectedBinding();
//	}
//
//	public void testGraphNameInPattern() {
//		setQuery("SELECT * WHERE ?a (?a ?b ?c)");
//		addQuad(graph1, graph1, bURI, cURI);
//		addQuad(graph1, graph2, bURI, cURI);
//		executeQuery();
////		dumpResults();
//		assertExpectedBindingCount(1);
//		expectBinding("a", graph1);
//		expectBinding("b", bURI);
//		expectBinding("c", cURI);
//		assertExpectedBinding();
//	}
//
//	public void testDuplicateVariableInPattern1() {
//		setQuery("SELECT * WHERE (?a ?a ?c)");
//		addQuad(graph1, aURI, aURI, cURI);
//		addQuad(graph1, aURI, bURI, cURI);
//		executeQuery();
////		dumpResults();
//		assertExpectedBindingCount(1);
//		expectBinding("a", aURI);
//		expectBinding("c", cURI);
//		assertExpectedBinding();
//	}
//
//	public void testDuplicateVariableInPattern2() {
//		setQuery("SELECT * WHERE (?a ?b ?a)");
//		addQuad(graph1, aURI, bURI, aURI);
//		addQuad(graph1, aURI, bURI, cURI);
//		executeQuery();
////		dumpResults();
//		assertExpectedBindingCount(1);
//		expectBinding("a", aURI);
//		expectBinding("b", bURI);
//		assertExpectedBinding();
//	}
//
//	public void testDuplicateVariableInPattern3() {
//		setQuery("SELECT * WHERE (?a ?b ?b)");
//		addQuad(graph1, aURI, bURI, bURI);
//		addQuad(graph1, aURI, bURI, cURI);
//		executeQuery();
////		dumpResults();
//		assertExpectedBindingCount(1);
//		expectBinding("a", aURI);
//		expectBinding("b", bURI);
//		assertExpectedBinding();
//	}




//	public void testQuery() {
//		String query = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
//			+ "SELECT ?foafFile ?name ?mbox WHERE {"
//			+ "?alice foaf:mbox <mailto:alice@example.com> . "
//			+ "?alice foaf:knows [ foaf:mbox ?mbox ] . "
//			+ "GRAPH ?foafFile { OPTIONAL { ?known foaf:name ?name } "
//			+ "?known foaf:mbox ?mbox } }";
//		set.read(this.getClass().getResourceAsStream("test.trig"), "TRIG", null);
//		ResultSet rs = QueryExecutionFactory.create(
//				QueryFactory.create(query),
//				new NamedGraphDataset(set, Node.createURI("http://example.com/aliceFoaf"))).execSelect();
//		
//		MyResultSet expected = new MyResultSet(ModelFactory.createDefaultModel());
//		expected.addVar("foafFile", Node.createURI("http://example.com/bobFoaf"));
//		expected.addVar("mbox", Node.createURI("mailto:bob@example.com"));
//		expected.addSolution();
//		expected.addVar("foafFile", Node.createURI("http://example.com/charlieFoaf"));
//		expected.addVar("name", Node.createLiteral("Charlie"));
//		expected.addVar("mbox", Node.createURI("mailto:charlie@example.com"));
//		expected.addSolution();
//		
//		// TODO Unfinished!!!
//	}
}
