package de.fuberlin.wiwiss.ng4j.semwebclient;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

public interface DereferencingListener {

	void dereferencingSuccessful(DereferencingTask task, NamedGraphSet result);
	
	void dereferencingFailed(DereferencingTask task, int errorCode);
}
