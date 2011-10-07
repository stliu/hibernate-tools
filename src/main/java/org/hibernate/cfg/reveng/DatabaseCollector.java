/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.hibernate.cfg.reveng;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.mapping.Table;

// split up to readonly/writeable interface

/**
 * Only intended to be used internally in reveng. *not* public api.
 */
public interface DatabaseCollector {

    public Iterator iterateTables();

    public Table addTable(String schema, String catalog, String name);

    public void setOneToManyCandidates(Map oneToManyCandidates);

    public Table getTable(String schema, String catalog, String name);

    public Map getOneToManyCandidates();

    public void addSuggestedIdentifierStrategy(String catalog, String schema, String name, String strategy);

    public String getSuggestedIdentifierStrategy(String catalog, String schema, String name);


}