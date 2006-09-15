package de.fuberlin.wiwiss.ng4j.semwebclient;

public class ConfigValue {
	public int  steps;
	public long timeout;
	public int  threads;
	
	public void setTimeout(long timeout){
		this.timeout = timeout;
	}
	
	public void setMaxThreads(int threads){
		this.threads = threads;
	}
	
	public void setMaxSteps(int steps){
		this.steps = steps;
	}

}
