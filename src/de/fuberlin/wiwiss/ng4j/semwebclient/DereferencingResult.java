package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.List;
import java.util.Map;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * The dereferencing result contains informations about the
 * success or failure of a DereferencingTasks execution.
 * 
 * @author Tobias Gauß
 * @author Olaf Hartig
 */
public class DereferencingResult {
	public final static int STATUS_OK = 0;
	public final static int STATUS_PARSING_FAILED = -1;
	public final static int STATUS_MALFORMED_URL = -2;
	public final static int STATUS_UNABLE_TO_CONNECT = -3;
	public final static int STATUS_NEW_URIS_FOUND = -4;
	public final static int STATUS_REDIRECTED = -5;
	public final static int STATUS_TIMEOUT = -6;

	private DereferencingTask task;
	private int resultCode;
	private NamedGraphSet resultData;
	private Exception resultException;
	private List<String> urilist = null;
	private String redirectURI = null;
	private Map<String,List<String>> headerFields;

	public DereferencingResult(DereferencingTask task, int resultCode, 
			NamedGraphSet resultData, Exception resultException,
			Map<String,List<String>> headerFields ) {
		this.task = task;
		this.resultCode = resultCode;
		this.resultData = resultData;
		this.resultException = resultException;
		this.headerFields = headerFields;
	}
	
	public DereferencingResult(DereferencingTask task, int resultCode, List<String> urilist, Map<String,List<String>> headerFields) {
		this.task = task;
		this.resultCode = resultCode;
		this.urilist = urilist;
		this.headerFields = headerFields;
		
	}
	
	public DereferencingResult(DereferencingTask task, int resultCode, String redirectURI, Map<String,List<String>> headerFields) {
		this.task = task;
		this.resultCode = resultCode;
		this.redirectURI = redirectURI;
		this.headerFields = headerFields;
	}

	public DereferencingTask getTask() {
		return this.task;
	}
	
	public int getResultCode() {
		return this.resultCode;
	}
	
	public NamedGraphSet getResultData() {
		return this.resultData;
	}
	
	public String getURI() {
		return this.task.getURI();
	}

	public String getErrorMessage() {
		if (this.resultException == null) {
			return null;
		}
		return this.resultException.getMessage();
	}

	public Exception getException() {
		return resultException;
	}
	
	public boolean isSuccess() {
		return this.resultCode == DereferencingResult.STATUS_OK;
	}
	
	public List<String> getUriList(){
		return this.urilist;
	}
	
	public String getRedirectURI() {
		return this.redirectURI;
	}

	public Map<String,List<String>> getHeaderFields() {
		return headerFields;
	}

	public String toString () {
		String s = "DereferencingResult for URI <" + task.getURI() + ">: ";
		if ( resultCode == STATUS_OK ) {
			s += "STATUS_OK (" + String.valueOf(resultData.countGraphs()) + " graphs)";
		}
		else if ( resultCode == STATUS_REDIRECTED ) {
			s += "STATUS_REDIRECTED (redirection target: " + redirectURI + ")";
		}
		else if ( resultCode == STATUS_NEW_URIS_FOUND ) {
			s += "STATUS_NEW_URIS_FOUND";
		}
		else if ( resultCode == STATUS_REDIRECTED ) {
			s += "failure (result code: " + String.valueOf(resultCode);
			if ( resultException != null ) {
				s += ", " + resultException.getClass().getName() + ": " + resultException.getMessage();
			}
			s += ")";
		}
		return s;
	}
}
