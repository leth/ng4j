package de.fuberlin.wiwiss.trust;

import java.util.Collections;

import junit.framework.TestCase;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

/**
 * Tests for {@link de.fuberlin.wiwiss.trust.ConstraintParser}.
 *
 * @version $Id: ConstraintParserTest.java,v 1.3 2005/03/28 22:31:51 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConstraintParserTest extends TestCase {

    public void testTrueConstraint() {
        assertTrue(ConstraintFixture.getConstraint("true").evaluate(
                new VariableBinding()).getResult());
    }

    public void testFalseConstraint() {
        assertFalse(ConstraintFixture.getConstraint("false").evaluate(
                new VariableBinding()).getResult());
    }

    public void testMinimalConstraint() {
        ExpressionConstraint condition = new ConstraintParser(
                "(?a > 0)", new PrefixMappingImpl(), Collections.EMPTY_LIST).parseExpressionConstraint();
        
        VariableBinding binding = new VariableBinding();
        binding.setValue("a", Node.createLiteral("17", null, XSDDatatype.XSDinteger));
        assertTrue(condition.evaluate(binding).getResult());
        binding.setValue("a", Node.createLiteral("-5", null, XSDDatatype.XSDinteger));
        assertFalse(condition.evaluate(binding).getResult());
    }
    
    public void testRecognizeCount() {
        String xsdBooleanURI = XSDDatatype.XSDboolean.getURI();
        assertFalse(createParser("?a > 0").isCountConstraint());
        assertFalse(createParser("'false'^^<" + xsdBooleanURI + ">").isCountConstraint());
        assertTrue(createParser("COUNT(?x) == 3").isCountConstraint());
        assertTrue(createParser("COUNT(?x) != 3").isCountConstraint());
        assertTrue(createParser("COUNT(?x) < 3").isCountConstraint());
        assertTrue(createParser("COUNT(?x) > 3").isCountConstraint());
        assertTrue(createParser("COUNT(?x) <= 3").isCountConstraint());
        assertTrue(createParser("COUNT(?x) >= 3").isCountConstraint());
        assertParseException("COUNT(<http://example.org>) == 3");
        assertParseException("COUNT('false'^^" + xsdBooleanURI + ") == 3");
        assertParseException("COUNT(?x) == ?y");
        assertParseException("COUNT(?x) == 5.5");
        assertParseException("COUNT(?x) == <http://example.org>");
        assertParseException("COUNT(?x) == COUNT(?y)");
    }
    
    public void testBuildCountConstraint() {
        String xsdIntURI = XSDDatatype.XSDint.getURI();
        assertBuildCountConstraint("COUNT(?x) <= 5", "x", "<=", 5);
        assertBuildCountConstraint("COUNT(?x) <= '5'^^<" + xsdIntURI + ">", "x", "<=", 5);
    }
    
    private void assertBuildCountConstraint(String constraint, String expectedVariableName,
            String expectedOperator, int expectedValue) {
        CountConstraint count = createParser(constraint).parseCountConstraint();
        assertEquals(expectedVariableName, count.variableName());
        assertEquals(expectedOperator, count.operator());
        assertEquals(expectedValue, count.value());
    }
    
    private ConstraintParser createParser(String constraint) {
        return new ConstraintParser(constraint, new PrefixMappingImpl(), Collections.EMPTY_LIST);
    }
    
    private void assertParseException(String constraint) {
        try {
            createParser(constraint).isCountConstraint();
            fail("Expected TPLException because not a legal constraint: '" + constraint + "'");
        } catch (TPLException e) {
            // is expected
        }
    }
}
