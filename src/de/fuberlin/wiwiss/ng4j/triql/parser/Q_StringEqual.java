/*
 * (c) Copyright 2001, 2002, 2003, Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package de.fuberlin.wiwiss.ng4j.triql.parser;

import java.io.PrintWriter;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryException;

import de.fuberlin.wiwiss.ng4j.triql.ResultBinding;
import de.fuberlin.wiwiss.ng4j.triql.legacy.QueryPrintUtils;
import de.fuberlin.wiwiss.ng4j.triql.legacy.Settable;
import de.fuberlin.wiwiss.ng4j.triql.legacy.Value;
import de.fuberlin.wiwiss.ng4j.triql.legacy.WorkingVar;

public class Q_StringEqual extends SimpleNode implements Expr, ExprBoolean
{
    Expr left ;
    Expr right ;

    static protected boolean enableRDFLiteralSameValueAs = true ;

    protected static String printName = "str=" ;
    protected static String opSymbol = "eq" ;

    Q_StringEqual(int id) { super(id); }

    Q_StringEqual(TriQLParser p, int id) { super(p, id); }

    protected boolean rawEval(Value x, Value y)
    {
        // There is a decision here : do we allow anything to be
        // tested as string or do restrict ourselves to things
        // that started as strings.  Example: A URI is not string
        // so should be it be possible to have:
        //      ?x ne <uri>
        // Decision here is to allow string tests on anything.
        
        // Jena2 - another decision point.
        // If we know left and right are types/lang-tagged literals,
        // do we apply a stricter test of "equal"?

        if ( enableRDFLiteralSameValueAs && x.isRDFLiteral() && y.isRDFLiteral() )
        {
            Literal xLit = x.getRDFLiteral() ;
            Literal yLit = y.getRDFLiteral() ;
            
//            String xDT = xLit.getDatatypeURI() ;
//            if ( xDT == null )
//                xDT = "<<null>>" ;
//            String yDT = yLit.getDatatypeURI() ;
//            if ( yDT == null )
//                yDT = "<<null>>" ;
//            
//            System.err.println("StringEq: "+xLit+"^^"+xDT+" eq "+yLit+"^^"+yDT) ;
            
            boolean b = xLit.sameValueAs(yLit) ;  
            return b ;
        }

        // Allow anything to be forced to be a string.
        /*
        if ( ! x.isString() )
            throw new EvalTypeException("Q_StringEqual: Wanted a string: "+x) ;
        if ( ! y.isString() )
            throw new EvalTypeException("Q_StringEqual: Wanted a string: "+y) ;
        String xx = x.getString() ;
        String yy = y.getString() ;
        */

        String xx = x.valueString() ;
        String yy = y.valueString() ;

        return (xx.equals(yy)) ;
    }

    public Value eval(Query q, ResultBinding env)
    {
        Value x = left.eval(q, env) ;
        Value y = right.eval(q, env) ;
        
        boolean b = rawEval(x, y) ;
                
        Settable result ;
        if ( x instanceof Settable )
            result = (Settable)x ;
        else if ( y instanceof Settable )
            result = (Settable)y ;
        else
            result = new WorkingVar() ;

        result.setBoolean(b) ;
        return result ;
    }

    public void jjtClose()
    {
        int n = jjtGetNumChildren() ;
        if ( n != 2 )
            throw new QueryException("Q_StringEqual: Wrong number of children: "+n) ;

        left = (Expr)jjtGetChild(0) ;
        right = (Expr)jjtGetChild(1) ;
    }

    public String asInfixString()
    {
        return QueryPrintUtils.asInfixString2(left, right, printName, opSymbol) ;
    }

    public String asPrefixString()
    {
        return QueryPrintUtils.asPrefixString(left, right, printName, opSymbol) ;
    }

    public void print(PrintWriter pw, int level)
    {
        QueryPrintUtils.print(pw, left, right, printName, opSymbol, level) ;
    }

    public String toString()
    {
        return asInfixString() ;
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
