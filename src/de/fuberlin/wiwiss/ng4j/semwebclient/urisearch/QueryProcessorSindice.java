package de.fuberlin.wiwiss.ng4j.semwebclient.urisearch;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;


/**
 * A query processor for the Sindice search engine.
 *
 * @author Olaf Hartig
 */
public class QueryProcessorSindice implements QueryProcessor {

	// members

	static final public int defaultMaxResultPortions = 100;
	final protected int maxResultPortions;
	final protected SingleQueryProcessor worker = new SingleQueryProcessor();


	// initialization

	public QueryProcessorSindice () {
		this( defaultMaxResultPortions );
	}

	public QueryProcessorSindice ( int maxResultPortions ) {
		this.maxResultPortions = maxResultPortions;
	}


	// implementation of the QueryProcessor interface

	public Set process ( String uri ) throws QueryProcessingException {
		Set result = new HashSet ();

		int curResultPortionNo = 1;
		Set curResultPortion;
		do {
			worker.setCurPortionNo( curResultPortionNo );
			curResultPortion = worker.process( uri );
			result.addAll( curResultPortion );
			curResultPortionNo++;
		} while ( (curResultPortionNo <= maxResultPortions) && ! curResultPortion.isEmpty() );

		return result;
	}


	/**
	 * Helper class for single queries that give only a portion (10 elements) of
	 * the overall result.
	 */
	protected class SingleQueryProcessor extends HTTPbasedQueryProcessor {

		// members

		protected int curPortionNo;


		// accessor methods

		public void setCurPortionNo ( int curPortionNo ) {
			this.curPortionNo = curPortionNo;
		}


		// implementation of the HTTPbasedQueryProcessor interface

		final protected URL prepareQuery( String uri ) throws QueryProcessingException {
			try {
				String encodedURI = URLEncoder.encode( uri, "UTF-8" );
				String query = "http://sindice.com/query/lookup?page=" + String.valueOf(curPortionNo) + "&uri=" + encodedURI;

				return new URL( query );
			} catch ( UnsupportedEncodingException e ) {
				throw new QueryProcessingException( this, uri, "URL encoding failed.", e );
			} catch ( MalformedURLException e ) {
				throw new QueryProcessingException( this, uri, "Creating the query URL failed.", e );
			}
		}

		final protected Set evaluateResult( Model queryResult ) throws QueryExecutionException {
			Set result = new HashSet ();

			StmtIterator it = queryResult.listStatements( null, RDFS.seeAlso, (RDFNode) null );
			while ( it.hasNext() ) {
				Statement s = it.nextStatement();
				String url = ( (Resource) s.getObject() ).getURI();
				result.add( url );
			}

			return result;
		}
	} // end of SingleQueryProcessor

}