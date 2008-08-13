package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.List;

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
	private List urilist = null;
	private String redirectURI = null;

	public DereferencingResult(DereferencingTask task, int resultCode, 
			NamedGraphSet resultData, Exception resultException) {
		this.task = task;
		this.resultCode = resultCode;
		this.resultData = resultData;
		this.resultException = resultException;
	}
	
	public DereferencingResult(DereferencingTask task, int resultCode, List urilist) {
		this.task = task;
		this.resultCode = resultCode;
		this.urilist = urilist;
		
	}
	
	public DereferencingResult(DereferencingTask task, int resultCode, String redirectURI) {
		this.task = task;
		this.resultCode = resultCode;
		this.redirectURI = redirectURI;
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
	
	public List getUriList(){
		return this.urilist;
	}
	
	public String getRedirectURI() {
		return this.redirectURI;
	}
}
