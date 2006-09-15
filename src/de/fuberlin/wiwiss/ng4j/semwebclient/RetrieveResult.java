package de.fuberlin.wiwiss.ng4j.semwebclient;

/**
 * The URI retrieval result.
 * 
 * @author Tobias Gauﬂ
 *
 */
public class RetrieveResult {
	/**
	 * The URI.
	 */
	private String uri;
	/**
	 * The corresponding http response code.
	 */
	private int response;
	
	/**
	 * Constructor
	 * 
	 * @param uri The URI.
	 * @param response The response.
	 */
	public RetrieveResult(String uri, int response){
		this.uri = uri;
		this.response = response;
	}
	
	/**
	 * @return The URI String.
	 */
	public String getUri(){
		return this.uri;
	}
	
	/**
	 * @return The response code.
	 */
	public int getResponse(){
		return this.response;
	}

}
