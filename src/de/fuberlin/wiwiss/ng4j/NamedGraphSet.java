// $Id: NamedGraphSet.java,v 1.14 2009/09/04 10:07:30 timp Exp $
package de.fuberlin.wiwiss.ng4j;

import java.io.InputStream;

import java.io.OutputStream;

import java.io.Reader;

import java.io.Writer;

import java.util.Iterator;



import com.hp.hpl.jena.graph.Graph;

import com.hp.hpl.jena.graph.Node;

/**
 * <p>A set of {@link NamedGraph}s and the core part of the <em>
 * Named Graphs for Jena</em> API. For details about Named Graphs see
 * <a href="http://www.w3.org/2004/03/trix/">http://www.w3.org/2004/03/trix/</a>.</p>
 * 
 * <p>A set of named graphs is a collection of RDF graphs where each graph
 * has a unique URI name. The collection can be accessed and modified
 * <ul>
 * <li>by adding, removing and finding {@link NamedGraph} instances,
 * <li>by adding, removing and finding {@link Quad}s (RDF triples with
 *   an additional graph name),
 * <li>through it's union graph (the RDF graph containing all statements
 *   from all graphs in the set) which is accessible as a Jena Graph and
 *   as a Jena Model.
 * </ul></p>
 * 
 * TODO: define equals()
 * 
 * @author Chris Bizer
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface NamedGraphSet {

//	=== Graph level methods ========================	
	
	/**
	 * Adds a NamedGraph to the set. Will replace a NamedGraph with the
	 * same name that is already in the set.
	 * @param graph The NamedGraph to be added
	 */
	public void addGraph(NamedGraph graph)	;

	/**
	 * Removes a NamedGraph from the set. Nothing happens
	 * if no graph with that name is contained in the set.
	 * Node.ANY will remove all graphs from the set.
	 * @param graphName The name of the NamedGraph to be removed
	 */
	public void removeGraph(Node graphName);	
	
	/**
	 * Removes the NamedGraph with a specific name. Nothing happens
	 * if no graph with that name is contained in the set.
	 * Node.ANY will remove all graphs from the set.
	 * @param graphNameURI The name of the NamedGraph to be removed
	 */
	public void removeGraph(String graphNameURI);

	/**
	 * Tells whether the NamedGraphSet contains a NamedGraph.
	 * @param graphName The name of a NamedGraph
	 * @return True if the set contains a graph with that name,
	 * 		false otherwise.
	 */
	public boolean containsGraph(Node graphName);

	/**
	 * Tells whether the NamedGraphSet contains a NamedGraph.
	 * @param graphNameURI The name of a NamedGraph
	 * @return True if the set contains a graph with that name,
	 * 		false otherwise.
	 */
	public boolean containsGraph(String graphNameURI);

	/**
	 * Returns the NamedGraph with a specific name from the GraphSet.
	 * Changes to the graph will be reflected in the set.
	 * @param graphName The name of the NamedGraph to be returned
	 * @return The graph with that name, or <tt>null</tt> if no graph
	 * 		with that name is contained in the set
	 */
	public NamedGraph getGraph(Node graphName);	
	
	/**
	 * Returns the NamedGraph with a specific name from the GraphSet.
	 * Changes to the graph will be reflected in the set.
	 * @param graphNameURI The name of the NamedGraph to be returned
	 * @return The graph with that name, or <tt>null</tt> if no graph
	 * 		with that name is contained in the set
	 */
	public NamedGraph getGraph(String graphNameURI);	

	/**
	 * Creates a new NamedGraph and adds it to the set. An existing
	 * graph with the same name will be replaced.
	 * @param graphName The name of the NamedGraph to be created;
	 * 		must be an URI node
	 * @return The newly created NamedGraph instance
	 */
	public NamedGraph createGraph(Node graphName);
	
	/**
	 * Creates a new NamedGraph and adds it to the set. An existing
	 * graph with the same name will be replaced.
	 * @param graphNameURI The name of the NamedGraph to be created;
	 * 		must be an URI
	 * @return The newly created NamedGraph instance
	 */
	public NamedGraph createGraph(String graphNameURI);

	/**
	 * Returns an iterator over all {@link NamedGraph}s in the set.
	 * @return An iterator over all NamedGraphs in the set
	 */
	public Iterator<NamedGraph> listGraphs();	

	/**
	 * Deletes all NamedGraphs from the set.
	 */
	public void clear();

	/** 
	 * Returns the number of NamedGraphs in the set. Empty graphs
	 * are counted.
	 * @return The number of NamedGraphs in the set.
	 */
	long countGraphs() ;

	/**
	 * Tells whether the set contains any NamedGraphs.
	 * @return True if the set contains any NamedGraphs, false otherwise
	 */
	public boolean isEmpty();	
	
//	=== Quad level methods ========================

	/**
	 * Adds a quad to the NamedGraphSet. The argument must not contain any
	 * wildcards. If the quad is already present, nothing happens. A new
	 * named graph will automatically be created if necessary.
	 * @param quad A quad to be added to the NamedGraphSet
	 */
	public void addQuad(Quad quad);

	/**
	 * Tells whether the NamedGraphSet contains a quad or
	 * quads matching a pattern. Quad patterns are {@link Quad}
	 * instances with {@link Node#ANY} in one or more positions.
	 * 
	 * @param pattern A quad or quad pattern
	 * @return True if The NamedGraphSet contains matching quads, false
	 * 		otherwise
	 */
	public boolean containsQuad(Quad pattern);

	/**
	 * Deletes Quads from the NamedGraphSet. The argument may contain
	 * wildcards ({@link Node#ANY}).
	 * All matching Quads will be deleted. If no Quads match, nothing
	 * happens. This operation will not delete any NamedGraphs from the set.
	 * Empty NamedGraphs will be retained.
	 * @param pattern A quad or quad pattern to be deleted
	 */
	public void removeQuad(Quad pattern);
	
	/**
	 * Counts the Quads in the NamedGraphSet. Identical Triples in
	 * different NamedGraphs are counted individually.
	 * @return The number of Quads in the set
	 */
	public int countQuads();

	/**
	 * Finds Quads that match a quad pattern. The argument may contain
	 * wildcards ({@link Node#ANY}).
	 * @param pattern A quad or quad pattern
	 * @return An iterator over all {@link Quad}s that match the pattern
	 */
	public Iterator findQuads(Quad pattern);

	/**
	 * Finds Quads that match a pattern. All arguments may be
	 * {@link Node#ANY} to match everything in that position.
	 * @param graphName The graph to find triples from
	 * @param subject The subject to be matched
	 * @param predicate The predicate to be matched
	 * @param object The object to be matched
	 * @return An iterator over all {@link Quad}s that match the pattern
	 */
	public Iterator findQuads(Node graphName, Node subject, Node predicate, Node object);	
	
//	=== Views on the Graph Set ========================		
	
	/**
	 * Returns the union graph of the NamedGraphSet. The graph is
	 * backed by the NamedGraphSet: Subsequent changes to one are
	 * reflected in the other.
	 * <p>
	 * <tt>Add</tt> operations to the union graph are all written to
	 * the default graph specified as the argument.
	 * <p>
	 * <tt>Delete</tt> operations remove the triple from all
	 * NamedGraphs.
	 * 
	 * @param defaultGraphForAdding The name of the default graph used for
	 * 		adding triples; must be an URI
	 * @return A Graph view on the NamedGraphSet
	 */
	public Graph asJenaGraph(Node defaultGraphForAdding);	
	
	/**
	 * Returns a Jena Model view on the NamedGraphSet, equivalent to the
	 * union graph of all graphs in the graph set.
	 * <p>
	 * <tt>Add</tt> operations on the returned model are all written
	 * to the default graph.
	 * <p>
	 * <tt>Read</tt> and <tt>write</tt> operations on the returned
	 * model have the behaviour of {@link #read(String, String)} and
	 * {@link #write(OutputStream, String, String)}.
	 * <p>
	 * All Statements returned by the NamedGraphModel can be casted to 
	 * {@link NamedGraphStatement} to access information about the graphs
	 * they are contained in.
	 * @param defaultGraphForAdding The name of the default graph used for
	 * 		adding triples; must be an URI
	 * @return A Model view on the NamedGraphSet
	 */
	public NamedGraphModel asJenaModel(String defaultGraphForAdding);	

//	=== IO and other methods ========================	
	
	/**
	 * Closes the NamedGraphSet and frees up resources held. Any subsequent
	 * calls to methods of the object have undefined results.
	 */
	public void close();

	/**
	 * Read Named Graphs from an URL into the NamedGraphSet. 
	 * Supported RDF serialization languages are "TRIX", "TRIX-EXT", "TRIG",
	 * "RDF/XML", "N-TRIPLE" and "N3".
	 * <p>
	 * Serialization languages that support named graphs (like TriX)
	 * will take the graph name(s) from the serialization. Other
	 * languages will take the URL as the name of the graph.
	 * <p>
	 * If some of the graph names from the source are already used
	 * in the NamedGraphSet, then the statements from the old
	 * graphs will be replaced by those from the source.
	 * 
	 * @param url The source of the input serialization
	 * @param lang The RDF serialization language of the input
	 */
	public void read(String url, String lang);
	
	/**
	 * Read Named Graphs from an InputStream into the NamedGraphSet. 
	 * Supported RDF serialization languages are "TRIX", "TRIX-EXT", "TRIG",
	 * "RDF/XML", "N-TRIPLE" and "N3".
	 * <p>
	 * Serialization languages that support named graphs (like TriX)
	 * will take the graph name(s) from the serialization. Other
	 * languages will take the base URI as the name of the graph.
	 * <p>
	 * If some of the graph names from the source are already used
	 * in the NamedGraphSet, then the statements from the old
	 * graphs will be replaced by those from the source.
	 * 
	 * @param source The source of the input serialization
	 * @param lang The RDF serialization language of the input
	 * @param baseURI The URI from where the input was read
	 */
	public void read(InputStream source, String lang, String baseURI);
	
	/**
	 * Read Named Graphs from a Reader into the NamedGraphSet. 
	 * Supported RDF serialization languages are "TRIX", "TRIX-EXT", "TRIG",
	 * "RDF/XML", "N-TRIPLE" and "N3".
	 * <p>
	 * Serialization languages that support named graphs (like TriX)
	 * will take the graph name(s) from the serialization. Other
	 * languages will take the base URI as the name of the graph.
	 * <p>
	 * If some of the graph names from the source are already used
	 * in the NamedGraphSet, then the statements from the old
	 * graphs will be replaced by those from the source.
	 * 
	 * @param source The source of the input serialization
	 * @param baseURI The URI from where the input was read
	 * @param lang The RDF serialization language of the input
	 */
	public void read(Reader source, String lang, String baseURI);

	/** 
	 * Writes a serialized representation of the NamedGraphSet to
	 * an OutputStream. Supported RDF serialization languages are
	 * "TRIX", "TRIG", "RDF/XML", "N-TRIPLE" and "N3".
	 * <p>
	 * If the specified serialization language doesn't support
	 * named graphs, then the union graph will be serialized, and
	 * knowledge about the graph name of each statement is lost.
	 * Only TriX supports named graphs.
	 * <p>
	 * The serialization will be UTF-8 encoded. There is currently no
	 * way to select a different encoding.
	 * 
	 * @param out The stream into which the serialization will be written
	 * @param lang The RDF serialization language to be used
	 * @param baseURI The base URI of the output file, or null if don't care
	 */
	public void write(OutputStream out, String lang, String baseURI);
	
	/** 
	 * Writes a serialized representation of the NamedGraphSet to
	 * a Writer. Supported RDF serialization languages are
	 * "TRIX", "TRIG", "RDF/XML", "N-TRIPLE" and "N3".
	 * <p>
	 * If the specified serialization language doesn't support
	 * named graphs, then the union graph will be serialized, and
	 * knowledge about the graph name of each statement is lost.
	 * Only TriX supports named graphs.
	 * <p>
	 * Note that this method might generate wrong results if the Writer
	 * is translated to bytes using anything but the system's default
	 * encoding. For this reason, {@link #write(OutputStream, String, String)}
	 * should be used if possible.
	 *
	 * @param out The stream into which the serialization will be written
	 * @param lang The RDF serialization language to be used
	 * @param baseURI The base URI of the output file, or null if don't care
	 */
	public void write(Writer out, String lang, String baseURI);
}

/*
 *  (c) Copyright 2004 - 2009 Christian Bizer (chris@bizer.de)
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