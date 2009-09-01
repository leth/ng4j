package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraphSetFactory;
import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.Task;
import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.TaskExecutorBase;
import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.TaskQueueBase;


/**
 * The DereferencingTaskQueue is a thread which observes the
 * DereferencerThreads. It starts all DereferencerThreads tries to 
 * assign new tasks to free DereferencerThreads and interrupts them
 * if the timeout is reached.
 * 
 * @author Tobias Gauß
 * @author Olaf Hartig
 * @author Hannes Mühleisen
 */
public class DereferencingTaskQueue extends TaskQueueBase
                                    implements DereferencingListener
{
	static private Log log = LogFactory.getLog( DereferencingTaskQueue.class );

	final protected NamedGraphSetFactory ngsFactory;

	private int maxfilesize;
        private boolean enablegrddl;
	final private boolean enableRDFa;
	private int connectTimeout = 0;
	private int readTimeout = 0;
	private Map<String,DereferencingTask> currentTasks = new HashMap<String,DereferencingTask> (); // maps task IDs to tasks

	static final private String RDFA_XSLT_URL = "http://www.w3.org/2008/07/rdfa-xslt";
	final private Templates xsltTemplateForRDFa;

	/**
	 * Old constructor.
	 * @deprecated Please use the other constructor instead.
	 */
        public DereferencingTaskQueue(NamedGraphSetFactory ngsFactory, int maxThreads,int maxfilesize, boolean enablegrddl) {
		this( ngsFactory, maxThreads, maxfilesize, enablegrddl, false, 0, 0 );
	}

	public DereferencingTaskQueue(NamedGraphSetFactory ngsFactory, int maxThreads,int maxfilesize, boolean enablegrddl, boolean enableRDFa, int connectTimeout, int readTimeout) {
		super( maxThreads );
		this.ngsFactory = ngsFactory;
		this.maxfilesize = maxfilesize;
		this.enablegrddl = enablegrddl;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;

		xsltTemplateForRDFa = enableRDFa ? createRDFaTemplate() : null;
		this.enableRDFa = ( xsltTemplateForRDFa != null );

		setName("DereferencingTaskQueue");
		start();
	}

	static private Templates createRDFaTemplate () {
		Templates t;
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setErrorListener( new XSLTErrorListener() );
			t = factory.newTemplates( new StreamSource(RDFA_XSLT_URL) );
		} catch ( TransformerConfigurationException e ) {
			log.error( "Failed to get and use XSLT template from <" + RDFA_XSLT_URL + "> (" + e.getMessage() + ").", e );
			t = null;
		}
		return t;
	}


	// implementation of the TaskQueueBase interface

	protected TaskExecutorBase createThread () {
		DereferencerThread thread = new DereferencerThread( ngsFactory );
		thread.setMaxfilesize(this.maxfilesize);
		thread.setEnableGrddl(this.enablegrddl);
		thread.setConnectTimeout(this.connectTimeout);
		thread.setReadTimeout(this.readTimeout);

		Transformer t = enableRDFa ? getRDFaTransformer() : null;
		thread.setEnableRDFa( t != null );
		if ( t != null ) {
			thread.setRDFaTransformer( t );
		}

		return  thread;
	}

	@Override
	public synchronized void addTask ( Task task ) {
		currentTasks.put( task.getIdentifier(), (DereferencingTask) task );
		super.addTask( task );
	}


	// implementation of the DereferencingListener interface

	public synchronized void dereferenced ( DereferencingResult result ) {
		currentTasks.remove( result.getTask().getIdentifier() );
	}


	// accessors

	/**
	 * Returns the task identified by the given ID (if any).
	 */
	public synchronized DereferencingTask getTask ( String identifier ) {
		return currentTasks.get( identifier );
	}

	private Transformer getRDFaTransformer () {
		Transformer t;
		try {
			t = xsltTemplateForRDFa.newTransformer();
		} catch ( TransformerConfigurationException e ) {
			log.debug( "Unexpected " + e.getClass().getName() + " caught: " + e.getMessage(), e );
			t = null;
		}
		return t;
	}


	static class XSLTErrorListener implements ErrorListener {
		private Log log = LogFactory.getLog( XSLTErrorListener.class );

		public void error ( TransformerException e ) {
			log.debug( "XSLT warning: " + e.getMessage() + ".", e );
		}

		public void fatalError ( TransformerException e ) {
			log.debug( "XSLT fatal: " + e.getMessage() + ".", e );
		}

		public void warning ( TransformerException e ) {
			log.debug( "XSLT warning: " + e.getMessage() + "." );
		}
	}

}
