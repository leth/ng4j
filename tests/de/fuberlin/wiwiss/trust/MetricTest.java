package de.fuberlin.wiwiss.trust;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * @version $Id: MetricTest.java,v 1.3 2005/03/21 21:51:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MetricTest extends TestCase {
    private Metric isFooMetric;
    private Metric equalsMetric;
    private Metric trueMetric;
    private PrefixMapping prefixes;
    private List metrics;
    
    public void setUp() {
        this.isFooMetric = new IsFooMetric();
        this.isFooMetric.setup(new NamedGraphSetImpl());
        this.equalsMetric = new EqualsMetric();
        this.equalsMetric.setup(new NamedGraphSetImpl());
        this.trueMetric = new TrueMetric();
        this.trueMetric.setup(new NamedGraphSetImpl());
        this.prefixes = new PrefixMappingImpl();
        this.prefixes.setNsPrefix("ex", "http://example.org/metrics#");
        this.metrics = Arrays.asList(
                new Metric[] {this.isFooMetric, this.equalsMetric, this.trueMetric});
    }
    
    public void testIsFooMetric() throws MetricException {
        assertTrue(this.isFooMetric.calculateMetric(Collections.singletonList(
                Node.createLiteral("foo"))).getResult());
        assertFalse(this.isFooMetric.calculateMetric(Collections.singletonList(
                Node.createLiteral("bar"))).getResult());
        assertEquals(new ExplanationPart(
                Collections.singletonList(Node.createLiteral("foo"))),
                this.isFooMetric.calculateMetric(Collections.singletonList(
                Node.createLiteral("foo"))).getTextExplanation());
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

    public void testNoArguments() {
        Constraint constraint = makeConstraint("METRIC(ex:TrueMetric)");
        assertTrue(constraint.evaluate(new VariableBinding()).getResult());
    }
    
    public void testSimpleMetricConstraint() {
        Constraint constraint = makeConstraint(
                "METRIC(<http://example.org/metrics#IsFoo>, ?a)");
        VariableBinding binding = new VariableBinding();
        binding.setValue("a", Node.createLiteral("foo"));
        assertTrue(constraint.evaluate(binding).getResult());
        binding.setValue("a", Node.createLiteral("bar"));
        assertFalse(constraint.evaluate(binding).getResult());
    }
    
    public void testQNameInMetricURI() {
        makeConstraint("METRIC(ex:IsFoo, ?a)");
    }

    public void testMetricParameters() {
        assertTrue(makeConstraint(
                "METRIC(<ex:Equals>, 5, 5)").evaluate(new VariableBinding()).getResult());
        assertFalse(makeConstraint(
                "METRIC(<ex:Equals>, 4, 5)").evaluate(new VariableBinding()).getResult());
    }

    public void testReturnExplanation() {
        Constraint constraint = makeConstraint(
                "METRIC(ex:TrueMetric, 'Explanation')");
        assertEquals("Part[\"Explanation\"]",
                constraint.evaluate(new VariableBinding()).getTextExplanation().toString());
    }
    
    public void testNoExplanation() {
        Constraint constraint = makeConstraint("METRIC(ex:TrueMetric)");
        assertNull(constraint.evaluate(new VariableBinding()).getTextExplanation());
    }
    
    public void testSeveralExplanations() {
        Constraint constraint = makeConstraint(
                "(METRIC(ex:TrueMetric, 'Expl1') && !(METRIC(ex:TrueMetric, 'Expl2')))");
        assertEquals("Part[] <Part[\"Expl1\"], Part[\"Expl2\"]>",
                constraint.evaluate(new VariableBinding()).getTextExplanation().toString());
    }
    
    private Constraint makeConstraint(String expression) {
        return new ConstraintParser(expression, this.prefixes, this.metrics).parse();
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
    
    class TrueMetric implements Metric {
        public MetricResult calculateMetric(final List arguments)
                throws MetricException {
            return new MetricResult() {
                public boolean getResult() {
                    return true;
                }
                public ExplanationPart getTextExplanation() {
                    if (arguments.isEmpty()) {
                        return null;
                    }
                    return new ExplanationPart(arguments);
                }
                public Graph getGraphExplanation() {
                    return null;
                }
            };
        }
        public String getURI() {
            return "http://example.org/metrics#TrueMetric";
        }
        public void setup(NamedGraphSet sourceData) {
            // do nothing
        }
    }
}
