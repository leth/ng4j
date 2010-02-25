package de.fuberlin.wiwiss.ng4j.semwebclient.urisearch;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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

	public Set<String> process ( String uri ) throws QueryProcessingException {
		Set<String> result = new HashSet<String> ();

		int curResultPortionNo = 1;
		Set<String> curResultPortion;
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

		final protected Set<String> evaluateResult( Model queryResult ) throws QueryExecutionException {
			Set<String> result = new HashSet<String> ();

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

/*
 * (c) Copyright 2006 - 2010 Christian Bizer (chris@bizer.de) All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
