// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/semwebclient/graph/SWClNamedGraphSetImpl.java,v 1.5 2009/06/09 17:18:54 hartig Exp $
package de.fuberlin.wiwiss.ng4j.semwebclient.graph;

import de.fuberlin.wiwiss.ng4j.impl.idbased.IdBasedNamedGraphSetImpl;


/**
 * Named graph set that is particularily well suited for the Semantic Web
 * client.
 * Uses specific graph implementations that are optimized for read-only access.
 * Furthermore, during the execution of triple pattern queries these graph
 * implementations use identifiers for RDF nodes instead of the nodes themself.
 *
 * @author Olaf Hartig
 */
public class SWClNamedGraphSetImpl extends IdBasedNamedGraphSetImpl
{

}

/*
 * (c) Copyright 2009 Christian Bizer (chris@bizer.de)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */