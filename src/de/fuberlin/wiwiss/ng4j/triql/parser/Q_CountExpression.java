package de.fuberlin.wiwiss.ng4j.triql.parser;

import com.hp.hpl.jena.query.QueryException;

import de.fuberlin.wiwiss.ng4j.triql.legacy.Value;

public class Q_CountExpression extends SimpleNode {
    private String varName;
    private String operator;
    private long value;
    
    Q_CountExpression(int id) {
        super(id);
    }

    Q_CountExpression(TriQLParser p, int id) {
        super(p, id);
    }

    public void jjtClose() {
        int n = jjtGetNumChildren();
        if (n != 2) {
            throw new QueryException("Q_CountExpression: Wrong number of children: " + n);
        }
        this.varName = ((Q_Var) jjtGetChild(0)).getVarName();
        Value v = (ParsedLiteral) jjtGetChild(1);
        if (!v.isInt()) {
            throw new QueryException("Q_CountExpression: Not an integer: " + v);
        }
        this.value = v.getInt();
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public String variableName() {
        return this.varName;
    }
    
    public String operator() {
        return this.operator;
    }
    
    public long value() {
        return this.value;
    }
}