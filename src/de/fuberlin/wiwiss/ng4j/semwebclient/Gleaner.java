package de.fuberlin.wiwiss.ng4j.semwebclient;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
 
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.URI;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Model;

class DoneParsing extends SAXException
{
    public DoneParsing() {
        super("Done Parsing");
    }
}

class MyHandler extends org.xml.sax.helpers.DefaultHandler
{
    public static String DATA_VIEW = "http://www.w3.org/2003/g/data-view";

    private URI base;
    private boolean inHead = false;
    private boolean prof = false;
    private ArrayList tr = new ArrayList(3);
    ArrayList ls = new ArrayList(3);

    public List transformations() { return this.tr; }
    public ArrayList links() { return this.ls; }

    public MyHandler(String b) throws java.net.URISyntaxException
    {
	this.base = new URI(b);
    }
    public void startElement(String uri,
			     String localName,
			     String qName,
			     org.xml.sax.Attributes attributes)
	throws SAXException
    {
	// System.out.println("Uri="+uri+" localName="+localName+" qName="+qName);
	if ("head".equals(localName)) {
	    this.inHead = true;
	    String v = attributes.getValue("profile");
	    this.prof = v != null && v.indexOf(DATA_VIEW)!=-1;
	}
	else if (this.inHead && "link".equals(localName)) {
	    String rel = attributes.getValue("rel");
	    if (this.prof && "transformation".equals(rel))
	    	this.tr.add(this.base.resolve(attributes.getValue("href")));
	    else if ("meta".equals(rel) || "alternate".equals(rel) || 
		     "seeAlso".equals(rel))
		this.ls.add(this.base.resolve(attributes.getValue("href")).toString());
	}
	else if (this.inHead && "base".equals(localName))
	    try {
		this.base = new URI(attributes.getValue("href"));
	    } catch (java.net.URISyntaxException e) {}
    }
    public void endElement(String uri,
			   String localName,
			   String qName)
	throws SAXException
    {
	if ("head".equals(localName))
	    throw new DoneParsing();
    }
}
/**
 * The Gleaner examines an HTML representation of a web resource
 * to locate applicable GRDDL transformations and subsequently 
 * applies those transformations to the original document to get
 * RDF descriptions.
 * 
 * @author Stelios Sfakianakis
 */
public class Gleaner
{
    String u;
    InputStream is;
    MyHandler h;
    public Gleaner(String u, InputStream is)
    {
	this.u = u;
	this.is = is;
    }

    public ArrayList links()
    { 
	return this.h == null? new ArrayList() : this.h.links();
    }
    
    public void glean(NamedGraphSet ngs)
    {
	ByteArrayInputStream bais = null;
	try {
	    ByteArrayOutputStream baous = new ByteArrayOutputStream();
	    
	    byte[] buf = new byte[1<<6];
	    for(int read; (read = is.read(buf)) != -1; baous.write(buf, 0, read))
                ;
	    buf = null;
 
	    bais = new ByteArrayInputStream(baous.toByteArray());
	}
	catch (Throwable t) {
            t.printStackTrace();
            return;
        }


	try {
            this.h  = new MyHandler(this.u);

	    javax.xml.parsers.SAXParserFactory f = 
		javax.xml.parsers.SAXParserFactory.newInstance();
	    f.setNamespaceAware(true);
            f.setValidating(false);
	    javax.xml.parsers.SAXParser p = f.newSAXParser();
	    
	    org.xml.sax.InputSource in = new org.xml.sax.InputSource(bais);
	    
            //System.out.println("Starting SAX parsing...");
	    p.parse(in, this.h);
	}
	catch (DoneParsing dp) {
	}
	catch (Throwable t) {
 	    System.out.println("Gleaner: Error in <"+this.u+">");
            t.printStackTrace();
	    return;
        }
	
        //System.out.println("Done SAX parsing!");
	DocumentBuilderFactory factory =
	    DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	
	try {
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Iterator it = h.transformations().iterator();
	    Model m = ModelFactory.createDefaultModel();
	    while(it.hasNext()) {
		String stylesheet = it.next().toString();
		//System.out.println("<!-- Transformation = "+stylesheet+" -->");
		StreamSource stylesource = new StreamSource(stylesheet);
		Transformer transformer = tFactory.newTransformer(stylesource);

		bais.reset();
		SAXSource source = new SAXSource(new org.xml.sax.InputSource(bais));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(out);
                // System.out.println("Starting Transformation...");
		transformer.transform(source, result);
		m.read(new ByteArrayInputStream(out.toByteArray()), this.u);
	    }
	    // It is possible that in addition to GRDDL transformations
	    // the retrieved document has references to "alternate" documents
	    // so we record this information as rdfs:seeAlso properties
	    // (thanks Richard!)
	    //
	    it = h.links().iterator();
	    while (it.hasNext()) {
		String lnk = it.next().toString();
		m.add( m.createStatement(m.createResource(this.u),
					 m.createProperty("http://www.w3.org/2000/01/rdf-schema#", 
							  "seeAlso"),
					 m.createResource(lnk)) );
	    }
	    ngs.addGraph( new NamedGraphImpl(this.u, m.getGraph()) );
        } catch (TransformerConfigurationException tce) {
	    // Error generated by the parser
	    System.out.println ("Gleaner: Transformer Factory error");
	    System.out.println("   " + tce.getMessage() );

	    // Use the contained exception, if any
	    Throwable x = tce;
	    if (tce.getException() != null)
		x = tce.getException();
	    x.printStackTrace();
      
        } catch (TransformerException te) {
	    // Error generated by the parser
	    System.out.println ("Gleaner: Transformation error");
	    System.out.println("   " + te.getMessage() );

	    // Use the contained exception, if any
	    Throwable x = te;
	    if (te.getException() != null)
		x = te.getException();
	    x.printStackTrace();
           
	}

    }
}