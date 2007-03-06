/*
 * NamedGraphResource.java
 * Created on 06.03.2007
 */
package de.fuberlin.wiwiss.ng4j.impl;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.NamedGraphResource;


/**
 * @author Jesper Zedlitz &lt;jesper@zedlitz.de&gt;
 *
 */
public class NamedGraphResourceImpl extends ResourceImpl implements NamedGraphResource {
    private NamedGraphModel model;

    public NamedGraphResourceImpl(final Resource resource,
        final NamedGraphModel model) {
        super(resource, model);
        this.model = model;
    }

    /**
     * @see com.hp.hpl.jena.rdf.model.impl.ResourceImpl#listProperties()
     */
    public StmtIterator listProperties() {
        return new NamedGraphStatementIterator(super.listProperties(),
            this.model);
    }

    /**
     * @see com.hp.hpl.jena.rdf.model.impl.ResourceImpl#listProperties(com.hp.hpl.jena.rdf.model.Property)
     */
    public StmtIterator listProperties(final Property p) {
        return new NamedGraphStatementIterator(super.listProperties(p),
            this.model);
    }
}
