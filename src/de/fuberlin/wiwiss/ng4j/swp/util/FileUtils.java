package de.fuberlin.wiwiss.ng4j.swp.util;

public class FileUtils 
{
	public static String getExtension( String filename )
    {
    	String extension = "";
    	int whereDot = filename.lastIndexOf( '.' );
    	if ( 0 < whereDot && whereDot <= filename.length()-2 )
    	{
    	    extension = filename.substring( whereDot+1 );
    	}
    	return extension;
    }
}
