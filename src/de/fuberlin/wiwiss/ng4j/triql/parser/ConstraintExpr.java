/*
 * (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package de.fuberlin.wiwiss.ng4j.triql.parser;

import com.hp.hpl.jena.rdql.EvalFailureException;
import com.hp.hpl.jena.rdql.Query;

import de.fuberlin.wiwiss.ng4j.triql.ResultBinding;
import de.fuberlin.wiwiss.ng4j.triql.legacy.Constraint;

//import com.hp.hpl.jena.util.* ;

public class ConstraintExpr implements Constraint
{
    Expr expr ;

    ConstraintExpr(Expr _expr)
    {
        expr = _expr ;
    }

    public boolean isSatisfied(Query q, ResultBinding env)
    {
        if ( expr == null )
            // This expression is in error
            return false ;

        try {
            return expr.eval(q, env).getBoolean() ;
        }
        catch (EvalFailureException e) //Includes EvalTypeException
        {
            // Check all exceptions possible.
            //expr = null ;
            return false ;
        }
    }

    // Used in printing a query - not for getting the string
    // value of a expression (which itself must be unquoted)
    public String toString()
    {
        return expr.asInfixString() ;
    }
}

/*
 * (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
