package de.fuberlin.wiwiss.ng4j.impl;

import java.util.Iterator;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * 
 * @author timp
 * @since 5 October 2009
 *
 */
public abstract class NamedGraphSetIterable implements NamedGraphSet {


  public Iterator<NamedGraph> iterator() {
    return listGraphs(); 
  }

}
