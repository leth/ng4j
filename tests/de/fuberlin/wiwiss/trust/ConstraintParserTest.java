package de.fuberlin.wiwiss.trust;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Tests for {@link de.fuberlin.wiwiss.trust.ConstraintParser}.
 *
 * @version $Id: ConstraintParserTest.java,v 1.1 2005/03/21 00:23:24 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConstraintParserTest extends TestCase {
    private Metric isFooMetric;
    private Metric equalsMetric;
    
    public void setUp() {
        this.isFooMetric = new IsFooMetric();
        this.isFooMetric.setup(new NamedGraphSetImpl());
        this.equalsMetric = new EqualsMetric();
        this.equalsMetric.setup(new NamedGraphSetImpl());
    }
    
    public void testTrueConstraint() {
        assertTrue(ConstraintFixture.getConstraint("true").isSatisfiedBy(new VariableBinding()));
    }

    public void testFalseConstraint() {
        assertFalse(ConstraintFixture.getConstraint("false").isSatisfiedBy(new VariableBinding()));
    }

    public void testMinimalConstraint() {
        Constraint condition = new ConstraintParser(
                "(?a > 0)", new PrefixMappingImpl(), Collections.EMPTY_LIST).parse();
        
        VariableBinding binding = new VariableBinding();
        binding.setValue("a", Node.createLiteral("17", null, XSDDatatype.XSDinteger));
        assertTrue(condition.isSatisfiedBy(binding));
        binding.setValue("a", Node.createLiteral("-5", null, XSDDatatype.XSDinteger));
        assertFalse(condition.isSatisfiedBy(binding));
    }
    
    public void testSimpleMetricConstraint() {
        ConstraintParser parser = new ConstraintParser(
                "METRIC(<http://example.org/metrics#IsFoo>, ?a)",
                new PrefixMappingImpl(),
                Arrays.asList(new Metric[] {this.isFooMetric}));
        Constraint condition = parser.parse();

        VariableBinding binding = new VariableBinding();
        binding.setValue("a", Node.createLiteral("foo"));
        assertTrue(condition.isSatisfiedBy(binding));
        binding.setValue("a", Node.createLiteral("bar"));
        assertFalse(condition.isSatisfiedBy(binding));
    }
    
    public void testUnknownMetric() {
        ConstraintParser parser = new ConstraintParser(
                "METRIC(<http://example.com/metrics#unknown>, ?a)",
                new PrefixMappingImpl(),
                Collections.EMPTY_LIST);
        try {
            parser.parse();
            fail("Expected TPLException because metric ex:unknown doesn't exist");
        } catch (TPLException ex) {
            // is expected
        }
    }
    
    public void testQNameInMetricURI() {
        PrefixMapping prefixes = new PrefixMappingImpl();
        prefixes.setNsPrefix("ex", "http://example.org/metrics#");
        ConstraintParser parser = new ConstraintParser(
                "METRIC(ex:IsFoo, ?a)",
                prefixes,
                Arrays.asList(new Metric[] {this.isFooMetric}));
        parser.parse();
    }

    public void testMetricParameters() {
        ConstraintParser parser = new ConstraintParser(
                "METRIC(<http://example.org/metrics#Equals>, 5, 5)",
                new PrefixMappingImpl(),
                Arrays.asList(new Metric[] {this.equalsMetric}));
        Constraint condition = parser.parse();
        assertTrue(condition.isSatisfiedBy(new VariableBinding()));

        parser = new ConstraintParser(
                "METRIC(<http://example.org/metrics#Equals>, 4, 5)",
                new PrefixMappingImpl(),
                Arrays.asList(new Metric[] {this.equalsMetric}));
        condition = parser.parse();
        assertFalse(condition.isSatisfiedBy(new VariableBinding()));
    }
    
    class EqualsMetric implements Metric {
        public void setup(NamedGraphSet sourceData) {
            // do nothing
        }
        public String getURI() {
            return "http://example.org/metrics#Equals";
        }
        public MetricResult calculateMetric(final List arguments)
        			throws MetricException {
            return new MetricResult() {
                public boolean getResult() {
                    return arguments.get(0).equals(arguments.get(1));
                }
                public ExplanationPart getTextExplanation() {
                    return null;
                }
                public Graph getGraphExplanation() {
                    return null;
                }
            };
        }
    }
}
