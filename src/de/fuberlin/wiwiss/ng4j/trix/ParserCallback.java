/*
 * $Id: ParserCallback.java,v 1.1 2004/10/23 13:31:24 cyganiak Exp $
 */
package de.fuberlin.wiwiss.ng4j.trix;

import java.util.List;

/**
 * Callback that is fed by {@link TriXParser} with graphs and triples from
 * a TriX file (see
 * <a href="http://www.hpl.hp.com/techreports/2004/HPL-2004-56">TriX
 * specification</a>). Implement this if you want to do something
 * with data from TriX files.
 * <p>
 * A TriX file contains zero or more named graphs, each of whom
 * might contain zero or more triples. For each graph, first
 * {@link #startGraph} is called, then all triples are processed,
 * then {@link #endGraph} is called. Processing of triples works
 * like this: For each triple within a graph, first one of the
 * <tt>subjectXXX</tt> methods is called (for example,
 * {@link #subjectURI}), then {@link #predicate} is called, and
 * finally one of the <tt>objectXXX</tt> methods is called (for example,
 * {@link #objectPlainLiteral}).
 * <p>
 * Graphs and triples are processed in the same order as they are
 * encountered in the file.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface ParserCallback {

	/**
	 * Called at the beginning of each trix:graph element.
	 * @param uris zero or more names of the graph, as strings
	 */
	public void startGraph(List uris);

	/**
	 * Called at the end of each trix:graph element
	 */
	public void endGraph();

	/**
	 * Called for each trix:triple element whose subject is a trix:uri.
	 * @param uri the subject URI of the triple
	 */
	public void subjectURI(String uri);

	/**
	 * Called for each trix:triple element whose subject is a trix:id.
	 * @param id the subject blank node ID of the triple
	 */
	public void subjectBNode(String id);

	/**
	 * Called for each trix:triple element whose subject is a
	 * trix:plainLiteral. Note that RDF doesn't allow literals as
	 * subjects, but TriX does.
	 * @param value the subject literal value of the triple
	 * @param lang the language tag of the subject literal, or <tt>null</tt>
	 * 			  if none was given
	 */
	public void subjectPlainLiteral(String value, String lang);

	/**
	 * Called for each trix:triple element whose subject is a
	 * trix:typedLiteral. Note that RDF doesn't allow literals as
	 * subjects, but TriX does.
	 * @param value the subject literal value of the triple
	 * @param datatypeURI the datatype URI of the subject literal
	 */
	public void subjectTypedLiteral(String value, String datatypeURI);

	/**
	 * Called for the predicate of each trix:triple.
	 * @param uri the predicate URI of the triple.
	 */
	public void predicate(String uri);

	/**
	 * Called for each trix:triple element whose object is a trix:uri.
	 * @param uri the object URI of the triple
	 */
	public void objectURI(String uri);

	/**
	 * Called for each trix:triple element whose object is a trix:id.
	 * @param id the object blank node ID of the triple
	 */
	public void objectBNode(String id);

	/**
	 * Called for each trix:triple element whose object is a trix:plainLiteral.
	 * @param value the object literal value of the triple
	 * @param lang the language tag of the object literal, or <tt>null</tt>
	 * 			  if none was given
	 */
	public void objectPlainLiteral(String value, String lang);

	/**
	 * Called for each trix:triple element whose object is a trix:typedLiteral.
	 * @param value the object literal value of the triple
	 * @param datatypeURI the datatype URI of the object literal
	 */
	public void objectTypedLiteral(String value, String datatypeURI);
}

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
 *   All rights reserved.
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
 *
 */
