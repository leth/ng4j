// $Id: SpecExampleTest.java,v 1.3 2007/03/06 18:20:01 zedlitz Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Runs the examples from the
 * <a href="http://www.wiwiss.fu-berlin.de/suhl/bizer/TriQL/">TriQL spec</a>.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SpecExampleTest extends TriQLTest {
	private final static String base = "http://www.example.org/exampleDocument#";
	private final static Node monica = Node.createURI(base + "Monica");
	private final static Node chris = Node.createURI(base + "Chris");
	private final static Node g1 = Node.createURI(base + "G1");
	private final static Node g2 = Node.createURI(base + "G2");
	private final static Node g3 = Node.createURI(base + "G3");
	private final static Node w1 = Node.createAnon(new AnonId("w1"));
	private final static Node w2 = Node.createAnon(new AnonId("w2"));

	public void setUp() throws Exception {
		super.setUp();
		addQuad(g1, monica, ex("name"), Node.createLiteral("Monica Murphy", null, null));
		addQuad(g1, monica, ex("homepage"), Node.createURI("http://www.monicamurphy.org"));
		addQuad(g1, monica, ex("email"), Node.createURI("mailto:monica@monicamurphy.org"));
		addQuad(g1, monica, ex("hasSkill"), ex("Management"));
		addQuad(g2, monica, RDF.Nodes.type, ex("Person"));
		addQuad(g2, monica, ex("hasSkill"), ex("Programming"));
		addQuad(g3, g1, swp("assertedBy"), w1);
		addQuad(g3, w1, swp("authority"), chris);
		addQuad(g3, w1, DC_11.date.getNode(), Node.createLiteral("2003-10-02", null, XSDDatatype.XSDdate));
		addQuad(g3, g2, swp("quotedBy"), w2);
		addQuad(g3, g3, swp("assertedBy"), w2);
		addQuad(g3, w2, DC_11.date.getNode(), Node.createLiteral("2003-09-03", null, XSDDatatype.XSDdate));
		addQuad(g3, w2, swp("authority"), chris);
		addQuad(g3, chris, RDF.Nodes.type, ex("Person"));
		addQuad(g3, chris, ex("email"), Node.createURI("mailto:chris@bizer.de"));		
	}

	public void test1() {
		setQuery("SELECT ?person, ?email " +
				"WHERE ( ?person ex:email ?email ) " +
				"      ( ?person rdf:type ex:Person ) " +
				"USING ex FOR <http://www.example.org/vocabulary#> " +
				"      rdf FOR <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		executeQuery();
		assertExpectedBindingCount(2);
		expectBinding("person", monica);
		expectBinding("email", Node.createURI("mailto:monica@monicamurphy.org"));
		assertExpectedBinding();
		expectBinding("person", chris);
		expectBinding("email", Node.createURI("mailto:chris@bizer.de"));
		assertExpectedBinding();
	}

	public void test2() {
		setQuery("SELECT ?person, ?email " +
				"WHERE ( ?person ex:email ?email . " +
				"        ?person rdf:type ex:Person ) " +
				"USING ex FOR <http://www.example.org/vocabulary#> " +
				"      rdf FOR <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		executeQuery();
		assertExpectedBindingCount(1);
		expectBinding("person", chris);
		expectBinding("email", Node.createURI("mailto:chris@bizer.de"));		
		assertExpectedBinding();
	}
	
	public void test3() {
		setQuery("SELECT ?skill " +
				"WHERE ?graph ( doc:Monica ex:hasSkill ?skill ) " +
				"      (?graph swp:assertedBy ?warrant) " +
				"      (?warrant swp:authority doc:Chris) " +
				"USING ex FOR <http://www.example.org/vocabulary#> " +
				"      swp FOR <http://www.w3.org/2004/03/trix/swp-1/> " +
				"      doc FOR <http://www.example.org/exampleDocument#>");
		executeQuery();
		assertExpectedBindingCount(1);
		expectBinding("skill", ex("Management"));
		assertExpectedBinding();
	}

	public void test4() {
		setQuery("SELECT ?skill " +
				"WHERE ?graph ( doc:Monica ex:hasSkill ?skill ) " +
				"      (?graph swp:assertedBy ?warrant . " +
				"       ?warrant swp:authority doc:Chris . " +
				"       ?warrant dc:date ?date ) " +
		//TODO		"AND ?date > \"2003-01-01\"^^xsd:date " +
				"USING ex FOR <http://www.example.org/vocabulary#> " +
				"      xsd FOR <http://www.w3.org/2001/XMLSchema#> " +
				"      swp FOR <http://www.w3.org/2004/03/trix/swp-1/> " +
				"      doc FOR <http://www.example.org/exampleDocument#> " +
				"      dc FOR <http://purl.org/dc/elements/1.1/>");
		executeQuery();
		assertExpectedBindingCount(1);
		expectBinding("skill", ex("Management"));
		assertExpectedBinding();
	}

	private static Node ex(String localPart) {
		return Node.createURI("http://www.example.org/vocabulary#" + localPart);
	}
	
	private static Node swp(String localPart) {
		return Node.createURI("http://www.w3.org/2004/03/trix/swp-1/" + localPart);
	}
}
