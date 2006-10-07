package de.fuberlin.wiwiss.ng4j.semwebclient;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * The URI retrieval result.
 * 
 * @author Tobias Gauﬂ
 * 
 */
public class DereferencingResult {
	public final static int STATUS_OK = 0;
	public final static int STATUS_PARSING_FAILED = -1;
	public final static int STATUS_MALFORMED_URL = -2;
	public final static int STATUS_UNABLE_TO_CONNECT = -3;

	private DereferencingTask task;
	private int resultCode;
	private NamedGraphSet resultData;
	private Exception resultException;

	public DereferencingResult(DereferencingTask task, int resultCode, 
			NamedGraphSet resultData, Exception resultException) {
		this.task = task;
		this.resultCode = resultCode;
		this.resultData = resultData;
		this.resultException = resultException;
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
	
	public boolean isSuccess() {
		return this.resultCode == DereferencingResult.STATUS_OK;
	}
}
