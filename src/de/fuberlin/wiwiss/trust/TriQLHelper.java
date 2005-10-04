package de.fuberlin.wiwiss.trust;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdql.QueryException;
import com.hp.hpl.jena.rdql.Var;

import de.fuberlin.wiwiss.ng4j.triql.legacy.Value;

/**
 * Some utility methods for working with TriQL.
 * 
 * @version $Id: TriQLHelper.java,v 1.2 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriQLHelper {

	/**
	 * Converts a node from a TriQL parse tree into an RDF node.
	 * Works only if the parse tree node represents an RDF node
	 * or a variable.
	 * @param n A TriQL parse tree node
	 * @return An RDF node or RDF variable
	 */
    public static Node toRDFNode(de.fuberlin.wiwiss.ng4j.triql.parser.Node n) {
        if (n instanceof Var) {
            return Node.createVariable(((Var)n).getVarName()) ;
        }
        if (!(n instanceof Value)) {
            throw new QueryException("convertToGraphNode encountered strange type: "+n.getClass().getName()) ;
        }
        return TriQLHelper.toRDFNode((Value) n);
    }
    
	/**
	 * @param v A TriQL value
	 * @return The corresponding RDF node
	 */
    public static Node toRDFNode(Value v) {
        if (v.isRDFLiteral()) {
            Literal lit = v.getRDFLiteral();
            return lit.asNode();
        }
        if (v.isRDFResource()) {
            return v.getRDFResource().getNode();
        }
        if (v.isURI()) {
            return Node.createURI(v.getURI());
        }
        if (v.isString()) {
            return Node.createLiteral(v.getString(), null, null);
        }
        if (v.isBoolean()) {
            return Node.createLiteral(v.asUnquotedString(), null, null);
        }
        if (v.isInt()) {
            return Node.createLiteral(
                    v.asUnquotedString(), null, XSDDatatype.XSDinteger);
        }
        if (v.isDouble()) {
            return Node.createLiteral(
                    v.asUnquotedString(), null, XSDDatatype.XSDdouble);
        }
        throw new QueryException("BUG: " + v.asPrefixString());
    }
}
