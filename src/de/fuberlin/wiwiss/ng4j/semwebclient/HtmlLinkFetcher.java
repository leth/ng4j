package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlLinkFetcher {
	
	public static ArrayList<String> fetchLinks( String s ){
		return fetchLinks( new StringReader(s) );
	}

	public static ArrayList<String> fetchLinks( InputStream s ){
		return fetchLinks( new InputStreamReader(s) );
	}

	public static ArrayList<String> fetchLinks( Reader reader ){
		BufferedReader bw = ( reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
		String line = null;
		String header = "";
		ArrayList<String> linkList = new ArrayList<String>();
		try {
		while(((line = bw.readLine()).indexOf("</head>") == -1)|(line == null)){
			header = header + line;
		}
		}catch(IOException e){
			//System.out.println(e.getLocalizedMessage());
		}
		if ( line != null ) {
			header = header + line.substring( 0, line.indexOf("</head>") );
		}

	    Pattern linkPattern = Pattern.compile ("<link[^>]*(rel=\"meta\"|rel=\"alternate\")[^>]*href=\"?[^(>| )]*\"?[^>]*(/>|>)", Pattern.CASE_INSENSITIVE);
	    Pattern prePattern  = Pattern.compile ("href=\"?");
	    Pattern postPattern = Pattern.compile ("( |\"|>)");
	    Pattern typePattern = Pattern.compile (".*(type=\"application/rdf\\+xml\"|type=\"application/n3\"|type=\"text/rdf\\+n3\").*");
	    Matcher linkMatcher = linkPattern.matcher (header);
	    LinkedList<String> linkLinkedList = new LinkedList<String> ();
	    while (linkMatcher.find ()) {
		      linkLinkedList.addLast (linkMatcher.group ());
		    }
		    for (int i = 0; i < linkLinkedList.size (); i++) {
		      String linkElmt = linkLinkedList.get(i);
		      if ( typePattern.matcher(linkElmt).matches() ) {
		        CharSequence cs = prePattern.split ((CharSequence)linkLinkedList.get(i))[1];
		        linkList.add(postPattern.split (cs)[0]);
		      }
		    }
		return linkList;
	}
	


}
