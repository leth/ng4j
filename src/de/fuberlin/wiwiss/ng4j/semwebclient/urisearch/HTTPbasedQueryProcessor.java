package de.fuberlin.wiwiss.ng4j.semwebclient.urisearch;

import java.net.HttpURLConnection;
import java.net.URL;

import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A base class for all query processors that use a HTTP based API which
 * returns RDF documents.
 *
 * @author Olaf Hartig
 */
abstract public class HTTPbasedQueryProcessor implements QueryProcessor {

	static private Log log = LogFactory.getLog( HTTPbasedQueryProcessor.class );


	// implementation of the QueryProcessor interface

	public Set process ( String uri ) throws QueryProcessingException {
		log.trace( "prepare query URL for " + uri.toString() );
		URL queryURL = prepareQuery( uri );

		log.trace( "execute search query (" + queryURL.toString() + ")"  );
		Model resultModel;
		try {
			resultModel = executeQuery( queryURL );
		} catch ( QueryExecutionException e ) {
			throw new QueryProcessingException( this, uri, "Execution failed: " + e.getMessage(), e );
		}

		log.trace( "evaluate search result (" + queryURL.toString() + ")" );

		if ( resultModel == null )
			return new HashSet ();

		try {
			return evaluateResult( resultModel );
		} catch ( QueryExecutionException e ) {
			throw new QueryProcessingException( this, uri, "Result evaluation failed: " + e.getMessage(), e );
		}
	}


	// interface

	/**
	 * Creates the URL for the specific search service.
	 */
	abstract protected URL prepareQuery( String uri ) throws QueryProcessingException;

	/**
	 * Evaluates the search result (expressed in the given RDF document) to
	 * extract the requested RDF documents (resp. their URLs).
	 */
	abstract protected Set evaluateResult( Model queryResult ) throws QueryExecutionException;


	// operations

	protected Model executeQuery ( URL queryURL ) throws QueryExecutionException {
		HttpURLConnection con = getConnection( queryURL );
		prepareConnection( con );
		openConnection( con );
		checkResult( con );
		return fetchRDF( con );
	}

	protected HttpURLConnection getConnection ( URL queryURL ) throws QueryExecutionException {
		try {
			return (HttpURLConnection) queryURL.openConnection();
		} catch ( IOException e ) {
			throw new QueryExecutionException( "Creating the connection failed (" + queryURL.toString() + ").", e );
		}
	}

	protected void prepareConnection ( HttpURLConnection con ) throws QueryExecutionException {
		con.addRequestProperty( "Accept",
		                        "application/rdf+xml;q=1," +
		                        "text/rdf+n3;q=0.9," +
		                        "application/x-turtle;q=0.5" );
	}

	protected void openConnection ( HttpURLConnection con ) throws QueryExecutionException {
		try {
			con.connect();
		} catch ( IOException e ) {
			throw new QueryExecutionException( "Opening the connection failed (" + con.getURL().toString() + ").", e );
		}
	}

	protected void checkResult ( HttpURLConnection con ) throws QueryExecutionException {
		try {
			if ( con.getResponseCode() != 200 )
				throw new QueryExecutionException( "Unexpected response code: " + con.getResponseCode() + " (" + con.getURL().toString() + ")" );

			String lang = getLang( con.getContentType() );
			if ( lang == null )
				throw new QueryExecutionException( "Unexpected content type (" + con.getContentType() + ")." );
		} catch ( IOException e ) {
			throw new QueryExecutionException( "Accessing the connection result failed (" + con.getURL().toString() + ").", e );
		}
	}

	protected Model fetchRDF ( HttpURLConnection con ) throws QueryExecutionException {
		try {
			Model m = ModelFactory.createDefaultModel();
			m.read( con.getInputStream(), getLang(con.getContentType()) );
			return m;
		} catch ( IOException e ) {
			throw new QueryExecutionException( "Accessing the connection result failed (" + con.getURL().toString() + ").", e );
		}
	}

	protected String getLang ( String contentType ) {
		if ( contentType.startsWith("application/rdf+xml") )
			return "RDF/XML";
		else if (    contentType.startsWith("application/n3")
		          || contentType.startsWith("application/x-turtle")
		          || contentType.startsWith("text/rdf+n3") )
			return "N3";
		else
			return null;
	}


	protected class QueryExecutionException extends Exception {

		public QueryExecutionException ( String msg, Throwable cause ) {
			super( msg, cause );
		}

		public QueryExecutionException ( String msg ) {
			super( msg );
		}
	}

}

