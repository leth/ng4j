/*
 * (c) Copyright 2001, 2002, 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package de.fuberlin.wiwiss.ng4j.triql.helpers;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.ResultSet;

import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

/** <p>Takes a QueryResult object and returns formatted (in various ways)
 *  Useful for the scripting interface.
 *  May help for display in other contexts.</p>
 *
 *  <p>Note: this is compute intensive and memory intensive.
 *  It needs to read all the results first (all the results are now in-memory - not kept here)
 *  in order to find things like the maximum length of a column value; then it needs
 *  to pass over the results again, turning them into Strings again, in order to return them.
 *  </p>
 *  <p>We prefer slow and less memory intensive because it is more rebust for scripting.</p>
 *
 *  Don't keep QueryResultsFormatter's around unnecessarily!
 * 
 * @author   Andy Seaborne
 * @version  $Id: QueryResultsFormatter.java,v 1.1 2004/12/17 01:44:30 cyganiak Exp $
 */

public class QueryResultsFormatter
{
	private List queryResults;
	private TriQLQuery query;
    int numRows = -2 ;
    int numCols = -2 ;
    int colWidths[] = null ;
    static final String notThere = "<<unset>>" ;
    
    public static final String resultsNamespace = "http://jena.hpl.hp.com/2003/03/queryResults#" ;

    /** Create a formatter for a QueryResults object */

    public QueryResultsFormatter(TriQLQuery query, List results)
    {
    		this.query = query;
    		this.queryResults = results;
    }

    private List getResultVars() {
    		return this.query.getBoundVars();
    }
    /** How wide is the result table */
    public int numColumns() { return getResultVars().size() ; }

    /** How deep is the result table.  Negative implies unknown */
    public int numRows() { return numRows ; }

    private void colWidths()
    {
    		Iterator all = this.queryResults.iterator();
        numCols = getResultVars().size() ;
        numRows = 0 ;
        colWidths = new int[numCols] ;

        // Widths at least that of the variable name.  Assumes we will print col headings.
        for ( int i = 0 ; i < numCols ; i++ )
            colWidths[i] = ((String)getResultVars().get(i)).length() ;

        // Preparation pass : find the maximum width for each column
        for ( ; all.hasNext() ; )
        {
            numRows++ ;
            Map env = (Map)all.next() ;
            int col = -1 ;
            for ( Iterator iter = getResultVars().iterator() ; iter.hasNext() ; )
            {
                col++ ;
                String rVar = (String)iter.next() ;
                String s = getVarAsString(env, rVar) ;
                if ( colWidths[col] < s.length() )
                    colWidths[col] = s.length() ;
            }
        }
    }

    /** Encode the result set as RDF.
     * @return Model       Model contains the results
     */

    public Model toModel()
    {
        Model m = ModelFactory.createDefaultModel() ;
        asRDF(m) ;
        return m ;
    }
    
    /** Encode the result set as RDF in the model provided.
     *  
     * @param model     The place where to put the RDF.
     * @return Resource The resource for the result set.
     */ 

    public Resource asRDF(Model model)
    {
        Resource results = model.createResource() ;
        results.addProperty(RDF.type, ResultSet.ResultSet) ;
        
        for (Iterator iter = getResultVars().iterator(); iter.hasNext();)
        {
            String vName = (String) iter.next();
            results.addProperty(ResultSet.resultVariable, vName) ;
        }
        
        int count = 0 ;
        Iterator solutionsIter = queryResults.iterator();
        for (  ; solutionsIter.hasNext() ; )
        {
            count++ ;
            Map env = (Map)solutionsIter.next() ;
            Resource thisSolution = model.createResource() ;
            results.addProperty(ResultSet.solution, thisSolution) ;
            for (Iterator iter = getResultVars().iterator() ; iter.hasNext() ; )
            {
                Resource thisBinding = model.createResource() ;
                String rVar = (String)iter.next() ;
                Object tmp = env.get(rVar) ;
                RDFNode n = null ;
                if ( tmp == null )
                    // This variable was not found in the results.
                    // Encode the result set with an explicit "not defined" 
                    n = ResultSet.undefined ;
                else
                		n = StatementImpl.createObject((Node) tmp, null);
                    
                thisBinding.addProperty(ResultSet.variable, rVar) ;
                thisBinding.addProperty(ResultSet.value, n) ;
                thisSolution.addProperty(ResultSet.binding, thisBinding) ;
            }
        }
        results.addProperty(ResultSet.size, count) ;
        return results ;
    }



    // Generalise: there are two algorithms : the one pass and the two pass

    /** Write out a compact form.  This encodes all the information is a vaguely
     *  readable way but is suitable for reading in again.  Used for testing.
     */


	public void dump(PrintWriter pw, boolean format)
	{
		if (getResultVars().size() == 0)
		{
			pw.println("# ==== No variables ====");
			pw.flush();
			return;
		}
		else
		{
			pw.println("# Variables:");
			for (Iterator iter = getResultVars().iterator(); iter.hasNext();)
			{
				String vName = (String) iter.next();
				pw.print("?" + vName+" ");
			}
			pw.println(".") ;
			pw.println("# Data:");
			pw.flush() ;
		}

		if (format)
			dumpAligned(pw);
		else
			dumpRaw(pw);
	}

    // One pass algorithm
    private void dumpRaw(PrintWriter pw)
    {
        numCols = getResultVars().size() ;
        Iterator tableIter = queryResults.iterator();
        for ( ; tableIter.hasNext() ; )
        {
            Map env = (Map)tableIter.next() ;
            for (Iterator iter = getResultVars().iterator() ; iter.hasNext() ; )
            {
                String rVar = (String)iter.next() ;
                String s = getVarAsString(env, rVar) ;
                pw.print("?") ;
                pw.print(rVar) ;
                pw.print(" ");
                pw.print(s);
                pw.print(" ");
            }
            pw.println(".") ;
        }
    }

    // Dump formated : columns padded for readability.
    // Requires reading all the data into memory - its a two pass algorithm.
    private void dumpAligned(PrintWriter pw)
    {
    		Iterator all = this.queryResults.iterator();
 
        if ( colWidths == null )
            colWidths() ;

        String row[] = new String[numCols] ;
        int lineWidth = 0 ;
        for ( int col = 0 ; col < numCols ; col++ )
        {
            String rVar = (String)getResultVars().get(col) ;
            row[col] = rVar ;
            lineWidth += colWidths[col] ;
        }

        for ( Iterator tableIter = all ; tableIter.hasNext() ; )
        {
            Map env = (Map)tableIter.next() ;
            for ( int col = 0 ; col < numCols ; col++ )
            {
                StringBuffer sbuff = new StringBuffer(120) ;
                String rVar = (String)getResultVars().get(col) ;
                sbuff.append('?') ;
                sbuff.append(rVar) ;
                sbuff.append(' ') ;
                String s = getVarAsString(env, rVar) ;

                int pad = colWidths[col] ;
                sbuff.append(s) ;

                for ( int j = 0 ; j < pad-s.length() ; j++ )
                    sbuff.append(' ') ;
                // Always has a trailing space
                sbuff.append(' ') ;
                pw.print(sbuff) ;
            }
            pw.println(" .") ;
        }
        pw.flush() ;
    }

    /** Textual representation : default layout using " | " to separate columns
     *  @param printwriter Output
     */
    public void printAll(PrintWriter printwriter) { printAll(printwriter, " | ", null) ; }
    
    /** Textual representation : layout using given separator
     *  @param printwriter Output
     *  @param colSep      Column separator
     */
    public void printAll(PrintWriter printwriter, String colSep) { printAll(printwriter, colSep, null) ; }
    
    /** Textual representation : layout using given separator
     *  @param printwriter Output
     *  @param colSep      Column separator
     *  @param lineEnd     String to add to end of lines
     */
    public void printAll(PrintWriter printwriter, String colSep, String lineEnd)
    {
        if ( getResultVars().size() == 0 )
        {
            printwriter.println("==== No variables ====") ;
            printwriter.flush() ;
            return ;
        }

    		Iterator all = this.queryResults.iterator();
        if ( colWidths == null )
            colWidths() ;

        String row[] = new String[numCols] ;
        int lineWidth = 0 ;
        for ( int col = 0 ; col < numCols ; col++ )
        {
            String rVar = (String)getResultVars().get(col) ;
            row[col] = rVar ;
            lineWidth += colWidths[col] ;
            if ( col > 0 )
                lineWidth += colSep.length() ;
        }
        printRow(printwriter, row, colSep, lineEnd) ;

        for ( int i = 0 ; i < lineWidth ; i++ )
            printwriter.print('=') ;
        printwriter.println() ;

        for ( Iterator tableIter = all ; tableIter.hasNext() ; )
        {
            Map env = (Map)tableIter.next() ;
            for ( int col = 0 ; col < numCols ; col++ )
            {
                String rVar = (String)getResultVars().get(col) ;
                row[col] = this.getVarAsString(env, rVar );
            }
            printRow(printwriter, row, colSep, lineEnd) ;
        }
        printwriter.flush() ;
    }

    private void printRow(PrintWriter pw, String[] row, String colSep, String lineEnd)
    {
        if ( row.length != numCols )
            throw new JenaException("QueryResultsFormatter.printRow: Row length ("+row.length+") != numCols ("+numCols+")") ;

        for ( int col = 0 ; col < numCols ; col++ )
        {
            String s = row[col] ;
            int pad = colWidths[col] ;
            StringBuffer sbuff = new StringBuffer(120) ;

            if ( col > 0 )
                sbuff.append(colSep) ;

            sbuff.append(s) ;
            for ( int j = 0 ; j < pad-s.length() ; j++ )
                sbuff.append(' ') ;

            pw.print(sbuff) ;
        }
        if ( lineEnd != null )
            pw.print(lineEnd);
        pw.println() ;
    }

    private String getVarAsString(Map env, String varName)
    {
        // Without adornment.
        //Value val = env.getValue(rVar) ;
        //String s = (val==null)? notThere : val.asQuotedString() ;
        //return s ;
                
        // Print in all details
        Object obj = env.get(varName) ;
                
        if ( obj != null )
        {
            if ( ! ( obj instanceof RDFNode ) )
                return "Found a "+(obj.getClass().getName()) ;
            else if ( obj instanceof Literal )
            {
                Literal l = (Literal)obj ;
                StringBuffer sb = new StringBuffer() ;
                sb.append('"').append(l.getLexicalForm()).append('"') ;
                
                if ( ! l.getLanguage().equals(""))
                    sb.append("@").append(l.getLanguage()) ;
                if ( l.getDatatype() != null )
                    sb.append("^^<").append(l.getDatatypeURI()).append(">") ;
                return sb.toString() ;
            }
            else if ( obj instanceof Resource )
            {
                Resource r = (Resource)obj ;
                if ( r.isAnon() )
                    return "anon:"+r.getId() ;
                else
                    return "<"+r.getURI()+">" ;
            }
        }
        return notThere ;
    }
}

/*
 *  (c) Copyright 2001, 2002, 2003, 2004 Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */