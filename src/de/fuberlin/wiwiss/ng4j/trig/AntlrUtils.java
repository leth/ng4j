/*
 * (c) Copyright 2001, 2002, 2003, Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package de.fuberlin.wiwiss.ng4j.trig;
import java.io.* ;

import com.hp.hpl.jena.n3.N3AntlrParser;

import antlr.* ;
import antlr.collections.*;

/** Miscellaneous things in support of Antlr-derived parsers.
 * 
 * @author		Andy Seaborne
 * @version 	$Id: AntlrUtils.java,v 1.1 2004/11/22 00:46:19 cyganiak Exp $
 */

public class AntlrUtils
{
	/** Format an AST node */
	public static String ast(AST t)
	{
		return "[" + t.getText() + ", " + TriGParser.getTokenNames()[t.getType()] + "]";
	}
	
	/** Print an AST node (but not its subnodes) */
	public static void ast(PrintStream out, AST t)
	{
		String s = ast(t) ;
		out.println(s);
	}

	/** Print an AST node (but not its subnodes) */
	public static void ast(Writer w, AST t)
	{
		String s = ast(t) ;
		try { w.write(s); } catch (IOException ioEx) {}
	}

	/** Format an AST node and its subnodes.  Derived from the antlr code */
	static public String ASTout(AST t)
	{
		String ts = "";
		if (t.getFirstChild() != null)
			ts += " (";
		ts += " '" + t.toString()+"'";
		if (t.getFirstChild() != null)
		{
			ts += ASTout((BaseAST) t.getFirstChild()) ;
		}
		if (t.getFirstChild() != null)
			ts += " )";
		if (t.getNextSibling() != null)
		{
			ts += ASTout((BaseAST) t.getNextSibling()) ;
		}
		return ts;
	}
}
/*
 *  (c) Copyright 2001, 2002, 2003 Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
