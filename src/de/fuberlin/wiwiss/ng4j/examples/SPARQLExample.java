package de.fuberlin.wiwiss.ng4j.examples;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.sparql.NamedGraphDataset;

public class SPARQLExample {

	/**
	 * Executes a SPARQL query against a NamedGraphSet and prints results.
	 */
	public static void main(String[] args) {
		NamedGraphSet set = new NamedGraphSetImpl();
		set.read("http://richard.cyganiak.de/foaf.rdf", "RDF/XML");
		set.read("http://www.wiwiss.fu-berlin.de/suhl/bizer/foaf.rdf", "RDF/XML");
		Query sparql = QueryFactory.create("SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } }");
		QueryExecution qe = QueryExecutionFactory.create(sparql, new NamedGraphDataset(set));
		ResultSet results = qe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
		    RDFNode graph = result.get("graph");
		    RDFNode s = result.get("s");
		    RDFNode p = result.get("p");
		    RDFNode o = result.get("o");
		    System.out.println(graph + " { " + s + " " + p + " " + o + " . }");
		}
	}
}
