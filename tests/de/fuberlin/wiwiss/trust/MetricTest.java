package de.fuberlin.wiwiss.trust;

import java.util.Collections;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.trust.ExplanationPart;
import de.fuberlin.wiwiss.trust.Metric;
import de.fuberlin.wiwiss.trust.MetricConstraint;
import de.fuberlin.wiwiss.trust.MetricException;
import de.fuberlin.wiwiss.trust.VariableBinding;

/**
 * @version $Id: MetricTest.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MetricTest extends TestCase {

    public void testIsFooMetric() throws MetricException {
        Metric id = new IsFooMetric();
        id.setup(new NamedGraphSetImpl());
        assertTrue(id.calculateMetric(Collections.singletonList(
                Node.createLiteral("foo"))).getResult());
        assertFalse(id.calculateMetric(Collections.singletonList(
                Node.createLiteral("bar"))).getResult());
        assertEquals(new ExplanationPart(
                Collections.singletonList(Node.createLiteral("foo"))),
                id.calculateMetric(Collections.singletonList(
                Node.createLiteral("foo"))).getTextExplanation());
    }
    
    public void testMetricConstraint() {
        MetricConstraint mc = new MetricConstraint(
                new IsFooMetric(),
                Collections.singletonList(Node.createVariable("x")));

        VariableBinding satisfyingBinding = new VariableBinding();
        satisfyingBinding.setValue("x", Node.createLiteral("foo"));
        assertTrue(mc.isSatisfied(satisfyingBinding));

        VariableBinding notSatisfyingBinding = new VariableBinding();
        notSatisfyingBinding.setValue("x", Node.createLiteral("bar"));
        assertFalse(mc.isSatisfied(notSatisfyingBinding));
    }
}
