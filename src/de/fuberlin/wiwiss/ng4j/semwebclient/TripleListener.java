package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.EventListener;

public interface TripleListener extends EventListener{
	public void tripleFound(TripleFoundEvent e);
	public void findFinished(TripleFoundEvent e);
}
