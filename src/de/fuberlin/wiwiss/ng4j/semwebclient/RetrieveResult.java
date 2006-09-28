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
	 * The reason why the uri could not be retrieved.
	 */
	private String reason;

	/**
	 * Constructor
	 * 
	 * @param uri
	 *            The URI.
	 * @param reason.
	 */
	public RetrieveResult(String uri, String reason) {
		this.uri = uri;
		this.reason = reason;
	}

	/**
	 * @return The URI String.
	 */
	public String getUri() {
		return this.uri;
	}

	/**
	 * @return The reason.
	 */
	public String getReason() {
		return this.reason;
	}

}
