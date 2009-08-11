package de.fuberlin.wiwiss.ng4j.semwebclient;


/**
 * The configuration of the Semantic Web CLient Library.
 *
 * @author Olaf Hartig (hartig@informatik.hu-berlin.de)
 */
public class SemanticWebClientConfig
{
	// the available configuration options

	public static final String MAXSTEPS = "maxsteps";
	public static final String MAXTHREADS = "maxthreads";
	public static final String TIMEOUT = "timeout";
	public static final String DEREF_CONNECT_TIMEOUT = "derefconnecttimeout";
	public static final String DEREF_READ_TIMEOUT = "derefreadtimeout";
	public static final String MAXFILESIZE = "maxfilesize";
	public static final String ENABLEGRDDL = "enablegrddl"; // Notice, GRDDL support is deprecated!
	public static final String ENABLE_SINDICE = "enablesindicesearch"; // enables Sindice-based URI search during query execution

	// default values for the configuration options

	private static final int MAXSTEPS_DEFAULT = 3;
	private static final int MAXTHREADS_DEFAULT = 10;
	private static final long TIMEOUT_DEFAULT = 30000;
	private static final int DEREF_CONNECT_TIMEOUT_DEFAULT = 0; // 0 means no timeout (i.e. infinity)
	private static final int DEREF_READ_TIMEOUT_DEFAULT = 0;
	private static final int MAXFILESIZE_DEFAULT = 100000000;
	private static final boolean ENABLEGRDDL_DEFAULT = false;
	private static final boolean ENABLE_SINDICE_DEFAULT = false;

	// current values

	private int maxsteps = MAXSTEPS_DEFAULT;
	private int maxthreads = MAXTHREADS_DEFAULT;
	private long timeout = TIMEOUT_DEFAULT;
	private int derefConnectTimeout = DEREF_CONNECT_TIMEOUT_DEFAULT;
	private int derefReadTimeout = DEREF_READ_TIMEOUT_DEFAULT;
	private int maxfilesize = MAXFILESIZE_DEFAULT;
	private boolean enablegrddl = ENABLEGRDDL_DEFAULT;
	private boolean enableSindice = ENABLE_SINDICE_DEFAULT;

	// generic accessor methods

	/**
	 * Sets a configuration option.
	 *
	 * @param option denotes the configuration option to be set
	 * @param value a string representation of the value
	 * @exception IllegalArgumentException The given option is unknown or the
	 *                                     given value cannot be parsed.
	 */
	public void setValue ( String option, String value ) throws IllegalArgumentException
	{
		if ( option.equalsIgnoreCase(MAXSTEPS) )
		{
			try {
				maxsteps = Integer.parseInt( value );
			} catch ( NumberFormatException e ) {
				throw new IllegalArgumentException( "value '" + value + "' for config " + MAXSTEPS + " is not numeric", e );
			}
		}
		else if ( option.equalsIgnoreCase(MAXTHREADS) )
		{
			try {
				maxthreads = Integer.parseInt( value );
			} catch ( NumberFormatException e ) {
				throw new IllegalArgumentException( "value '" + value + "' for config " + MAXTHREADS + " is not numeric", e );
			}
		}
		else if ( option.equalsIgnoreCase(TIMEOUT) )
		{
			try {
				timeout = Long.parseLong(value);
			} catch ( NumberFormatException e ) {
				throw new IllegalArgumentException( "value '" + value + "' for config " + TIMEOUT + " is not numeric", e );
			}
		}
		else if ( option.equalsIgnoreCase(DEREF_CONNECT_TIMEOUT) )
		{
			try {
				derefConnectTimeout = Integer.parseInt(value);
			} catch ( NumberFormatException e ) {
				throw new IllegalArgumentException( "value '" + value + "' for config " + DEREF_CONNECT_TIMEOUT + " is not numeric", e );
			}
		}
		else if ( option.equalsIgnoreCase(DEREF_READ_TIMEOUT) )
		{
			try {
				derefReadTimeout = Integer.parseInt(value);
			} catch ( NumberFormatException e ) {
				throw new IllegalArgumentException( "value '" + value + "' for config " + DEREF_READ_TIMEOUT + " is not numeric", e );
			}
		}
		else if ( option.equalsIgnoreCase(MAXFILESIZE) )
		{
			try {
				maxfilesize = Integer.parseInt( value );
			} catch ( NumberFormatException e ) {
				throw new IllegalArgumentException( "value '" + value + "' for config " + MAXFILESIZE + " is not numeric", e );
			}
		}
		else if ( option.equalsIgnoreCase(ENABLEGRDDL) )
		{
			enablegrddl = "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "1".equals(value);
		}
		else if ( option.equalsIgnoreCase(ENABLE_SINDICE) )
		{
			this.enableSindice = "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "1".equals(value);
		}
		else
		{
			throw new IllegalArgumentException( "The given option '" + option + "' is unknown." );
		}
	}

	/**
	 * Returns a string representation of the value for the given configuration
	 * option.
	 *
	 * @param option denotes the configuration option to be queried
	 * @exception IllegalArgumentException The given option is unknown.
	 */
	public String getValue ( String option ) throws IllegalArgumentException
	{
		String value = null;

		if ( option.equalsIgnoreCase(MAXSTEPS) ) {
			value = String.valueOf( maxsteps );
		}
		else if ( option.equalsIgnoreCase(MAXTHREADS) ) {
			value = String.valueOf( maxthreads );
		}
		else if ( option.equalsIgnoreCase(TIMEOUT) ) {
			value = String.valueOf( timeout );
		}
		else if ( option.equalsIgnoreCase(DEREF_CONNECT_TIMEOUT) ) {
			value = String.valueOf( derefConnectTimeout );
		}
		else if ( option.equalsIgnoreCase(DEREF_READ_TIMEOUT) ) {
			value = String.valueOf( derefReadTimeout );
		}
		else if ( option.equalsIgnoreCase(MAXFILESIZE) ) {
			value = String.valueOf( maxfilesize );
		}
		else if ( option.equalsIgnoreCase(ENABLEGRDDL) ) {
			value = String.valueOf( enablegrddl );
		}
		else if ( option.equalsIgnoreCase(ENABLE_SINDICE) ) {
			value = String.valueOf( enableSindice );
		}
		else {
			throw new IllegalArgumentException( "The given option '" + option + "' is unknown." );
		}

		return value;
	}

	// option-specific accessor methods

	final public int getMaxSteps () { return maxsteps; }
	final public int getMaxThreads () { return maxthreads; }
	final public long getTimeout () { return timeout; }
	final public int getDerefConnectTimeout () { return derefConnectTimeout; }
	final public int getDerefReadTimeout () { return derefReadTimeout; }
	final public int getMaxFileSize () { return maxfilesize; }
	final public boolean getEnableGRDDL () { return enablegrddl; }
	final public boolean getEnableSindice () { return enableSindice; }
}

/*
 * (c) Copyright 2009 Christian Bizer (chris@bizer.de)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
