// $Id: GraphReaderService.java,v 1.1 2004/09/13 14:37:26 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFReaderF;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.namedgraphs.NamedGraphSet;
import de.fuberlin.wiwiss.namedgraphs.trix.JenaRDFReader;
import de.fuberlin.wiwiss.namedgraphs.trix.NamedGraphSetReader;

/**
 * Reads RDF graphs from external sources (URLs, InputStreams,
 * Readers, Strings). Supports all serializations supported by
 * Jena, and TriX. Supports reading of Named Graphs into
 * {@link NamedGraphSet}s.
 * <p>
 * To read from a source, three steps must be performed:
 * <ol>
 * <li>Set a source using {@link #setSourceURL}, {@link #setSourceReader},
 *   {@link #setSourceInputStream}, {@link #setSourceString} or
 *   {@link #setSourceFile}.</li>
 * <li>Set the serialization language of the source using
 *   {@link #setLanguage}. This step is optional. If no language is
 *   given, the class will try to guess the language based on
 *   MIME types and filename extensions of the URL or base URI.</li>
 * <li>Read the source into a Model (ignoring graph names) or into
 *   a NamedGraphSet (using graph names) using {@link #readInto(Model)}
 *   or {@link #readInto(NamedGraphSet)}.</li>
 * </ol>
 * <p>
 * <em>Design note</em>: This class wraps the document reading functions
 * of Jena and the Named Graph reading functions of NG4J into a single
 * interface without adding more methods and logic to Model.
 * <p>
 * TODO: The baseURI argument to setXXX should be optional for languages
 * that are able provide a base URI within the document
 * (that is, RDF/XML). This is hard because Jena's RDF parser doesn't
 * expose the base URI.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphReaderService {
	private Reader reader = null;
	private InputStream inputStream = null;
	private String url = null;
	private String sourceString = null;
	private String baseURI = null;
	private String lang = null;
	private Map mimeTypes = new HashMap();
	private Map fileExtensions = new HashMap();
	private RDFReaderF readerFactory = new RDFReaderFImpl();

	public GraphReaderService() {
		this.mimeTypes.put("application/rdf+xml", "RDF/XML");
		this.mimeTypes.put("text/rdf+xml", "RDF/XML");
		this.mimeTypes.put("application/n3", "N3");
		this.mimeTypes.put("application/x-turtle", "N3");
//		mimeTypes.put("text/plain", "N-TRIPLES");	// too error-prone
		this.fileExtensions.put("rdf", "RDF/XML");
		this.fileExtensions.put("rdfs", "RDF/XML");
		this.fileExtensions.put("owl", "RDF/XML");
		this.fileExtensions.put("n3", "N3");
		this.fileExtensions.put("ttl", "N3");
		this.fileExtensions.put("nt", "N-TRIPLES");
		this.fileExtensions.put("trix", "TRIX");
		this.readerFactory.setReaderClassName("TRIX",
				JenaRDFReader.class.getName());
	}

	/**
	 * Sets an URL as the source.
	 * @param url An URL of a RDF document
	 */
	public void setSourceURL(String url) {
		this.url = url;
	}
	
	/**
	 * Sets a Reader as the source. A base URI must also be given.
	 * It is used to resolve relative URIs in the document, and
	 * as a graph name when reading documents with unnamed graphs
	 * into a NamedGraphSet.
	 * @param reader A Reader of a RDF document
	 * @param baseURI The base URI of the document
	 */
	public void setSourceReader(Reader reader, String baseURI) {
		this.reader = reader;
		this.baseURI = baseURI;
	}
	
	/**
	 * Sets an InputStream as the source. A base URI must also be given.
	 * It is used to resolve relative URIs in the document, and
	 * as a graph name when reading documents with unnamed graphs
	 * into a NamedGraphSet.
	 * @param inputStream An InputStream of a RDF document
	 */
	public void setSourceInputStream(InputStream inputStream, String baseURI) {
		this.inputStream = inputStream;
		this.baseURI = baseURI;
	}
	
	/**
	 * Sets a String as the source. A base URI must also be given.
	 * It is used to resolve relative URIs in the document, and
	 * as a graph name when reading documents with unnamed graphs
	 * into a NamedGraphSet.
	 * @param sourceString A String containing an RDF document
	 */
	public void setSourceString(String sourceString, String baseURI) {
		this.sourceString = sourceString;
		this.baseURI = baseURI;
	}

	/**
	 * Sets a File as the source.
	 * @param sourceFile A file containing an RDF document
	 */
	public void setSourceFile(File sourceFile) {
		try {
			this.inputStream = new FileInputStream(sourceFile);
			this.baseURI = sourceFile.toURI().toString();
		} catch (IOException ex) {
			throw new JenaException(ex);
		}
	}

	/**
	 * Sets the language of the source. Supported languages are:
	 * <ul>
	 * <li>"<strong>RDF/XML</strong>"</li>
	 * <li>"<strong>N3</strong>" (can also be used for Turtle files)</li>
	 * <li>"<strong>N-TRIPLE</strong>"</li>
	 * <li>"<strong>TRIX</strong>"</li>
	 * </ul>
	 * Setting the language is optional for URL sources. If no language
	 * is given, the implementation will try to guess the language
	 * based on MIME types and filename extensions. This will not work
	 * in all cases and is not tested very well, so it's safest to
	 * specify the language in any case.
	 * @param lang "RDF/XML", "N3", "N-TRIPLE" or "TRIX"
	 */
	public void setLanguage(String lang) {
		this.lang = lang;
	}
	
	/**
	 * Adds one or more Named Graphs from the source to a NamedGraphSet.
	 * If the serialization of the source does not support Named
	 * Graphs natively, then it will be treated like a single Named
	 * Graph with the source's URL or base URI as a graph name.
	 * <p>
	 * Existing graphs in the NamedGraphSet will be replaced by
	 * graphs with the same name from the source.
	 * <p>
	 * setSourceXXX and setLanguage must have been called before
	 * readInto.
	 * 
	 * @param set The NamedGraphSet to which statements will be added
	 */
	public void readInto(NamedGraphSet set) {
		makeSureWeHaveLanguage();
		if (languageSupportsNamedGraphs(this.lang)) {
			// TODO: Hack -- will clean this up when I add TriG support
			// (Problem is there can be other languages than TriX here but our code just assumes it is TriX)
			NamedGraphSetReader ngsReader = new NamedGraphSetReader();
			if (this.url != null) {
				ngsReader.read(set, this.url, this.url);
			} else if (this.reader != null) {
				ngsReader.read(set, this.reader, this.baseURI, this.baseURI);
			} else if (this.inputStream != null) {
				ngsReader.read(set, this.inputStream, this.baseURI, this.baseURI);
			} else if (this.sourceString != null) {
				ngsReader.read(set, new StringReader(this.sourceString), this.baseURI, this.baseURI);
			} else {
				throw new JenaException("No source specified; use setSourceXXX first");
			}
		} else {
			readInto(new ModelCom(set.createGraph(getURI())));
		}
	}

	/**
	 * Adds all statements from the source to a Jena Model.
	 * Ignores graph names if the serialization language supports them.
	 * setSourceXXX and setLanguage must have been called before
	 * readInto.
	 * @param model The model to which statements will be added
	 */
	public void readInto(Model model) {
		makeSureWeHaveLanguage();
		RDFReader rdfReader = this.readerFactory.getReader(this.lang);
		if (this.url != null) {
			rdfReader.read(model, this.url);
		} else if (this.reader != null) {
			rdfReader.read(model, this.reader, this.baseURI);
		} else if (this.inputStream != null) {
			rdfReader.read(model, this.inputStream, this.baseURI);
		} else if (this.sourceString != null) {
			rdfReader.read(model, new StringReader(this.sourceString), this.baseURI);
		} else {
			throw new JenaException("No source specified; use setSourceXXX first");
		}
	}

	/**
	 * Adds support for additional languages.
	 * @param lang A language name
	 * @param className A class implementing {@link RDFReader}
	 * @see RDFReaderF#setReaderClassName(java.lang.String, java.lang.String)
	 */
	public String setReaderClassName(String lang, String className) {
		return this.readerFactory.setReaderClassName(lang, className);
	}

	private boolean languageSupportsNamedGraphs(String language) {
		return "TRIX".equals(language);
	}

	private String getURI() {
		return (this.url != null) ? this.url : this.baseURI;
	}

	private void makeSureWeHaveLanguage() {
		if (this.lang != null) {
			return;
		}
		this.lang = guessLanguage();
		if (this.lang == null) {
			throw new JenaException("No RDF serialization language given and attempt to guess failed");
		}
	}
	
	private String guessLanguage() {
		// There's probably better code for this already somewhere
		// in Jena or java.net.*
		if (this.url != null) {
			String mimeType = getMIMEType(this.url);
			if (this.mimeTypes.get(mimeType) != null) {
				return (String) this.mimeTypes.get(mimeType);
			}
		}
		if (getURI() == null) {
			return null;
		}
		Iterator it = this.fileExtensions.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			if (getURI().endsWith("." + entry.getKey())) {
				return (String) entry.getValue();
			}
		}
		String desperateLastAttempt = findFileExtensionInName(getURI());
		if (desperateLastAttempt == null) {
			return null;
		}
		return (String) this.fileExtensions.get(desperateLastAttempt);
	}
	
	private String getMIMEType(String someURL) {
		try {
			URL realURL = new URL(someURL);
			URLConnection conn = realURL.openConnection();
			return conn.getContentType();
		} catch (MalformedURLException ex) {
			throw new JenaException(ex);
		} catch (IOException ex) {
			throw new JenaException(ex);			
		}
	}
	
	private String findFileExtensionInName(String uri) {
		try {
			URL realURL = new URL(uri);
			String filename = realURL.getFile();
			Iterator it = this.fileExtensions.keySet().iterator();
			while (it.hasNext()) {
				String extension = (String) it.next();
				if (filename.indexOf(extension) >= 0) {
					return extension;
				}
			}
			return null;
		} catch (MalformedURLException ex) {
			// ignore, URI validation should be done somewhere else
			return null;
		}
	}
}
