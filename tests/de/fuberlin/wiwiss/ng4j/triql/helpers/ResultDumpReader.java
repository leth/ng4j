// $Id: ResultDumpReader.java,v 1.1 2004/12/17 01:44:30 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.ResultSet;

/**
 * Reads an RDF dump of a query result into a list of maps. Each map is a result
 * binding. This functionality is present in Jena's QueryResultsMem, which we can't
 * use in NG4J because of incompatibilities between Jena 2.1 and 2.2.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ResultDumpReader {

	public static List readDump(Model resultsModel) {
        List varNames = new ArrayList() ;
        List rows = new ArrayList();
        StmtIterator sIter = resultsModel.listStatements(null, RDF.type, ResultSet.ResultSet) ;
        for ( ; sIter.hasNext() ;)
        {
            Statement s = sIter.nextStatement() ;
            Resource root = s.getSubject() ;

            // Variables
            StmtIterator rVarsIter = root.listProperties(ResultSet.resultVariable) ;
            for ( ; rVarsIter.hasNext() ; )
            {
                String varName = rVarsIter.nextStatement().getString() ;
                varNames.add(varName) ;
            }
            rVarsIter.close() ;
            // Now the results themselves
            int count = 0 ;
            StmtIterator solnIter = root.listProperties(ResultSet.solution) ;
            for ( ; solnIter.hasNext() ; )
            {
                // foreach row
                Map rb = new HashMap() ;
                count++ ;

                Resource soln = solnIter.nextStatement().getResource() ;
                StmtIterator bindingIter = soln.listProperties(ResultSet.binding) ;
                for ( ; bindingIter.hasNext() ; )
                {
                    Resource binding = bindingIter.nextStatement().getResource() ;
                    String var = binding.getRequiredProperty(ResultSet.variable).getString() ;
                    RDFNode val = binding.getRequiredProperty(ResultSet.value).getObject() ;
                    // We include the value even if it is the marker term "rs:undefined"
                    //if ( val.equals(ResultSet.undefined))
                    //    continue ;
                    // The QueryResultFormatter code equates null (not found) with
                    // rs:undefined.  When Jena JUnit testing, it does not matter if the
                    // recorded result has the term absent or explicitly undefined.

                    rb.put(var, val.asNode()) ;
                }
                bindingIter.close() ;
                rows.add(rb) ;
            }
            solnIter.close() ;

            if ( root.hasProperty(ResultSet.size))
            {
                try {
                    int size = root.getRequiredProperty(ResultSet.size).getInt() ;
                    if ( size != count )
                        System.err.println("Warning: Declared size = "+size+" : Count = "+count) ;
                } catch (JenaException rdfEx) {}
            }
            sIter.close() ;
        }
        return rows;
    }
}
