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

import org.hibernate.cfg.Mappings;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.mapping.Table;

public class MappingsDatabaseCollector extends AbstractDatabaseCollector {

    private final Mappings mappings;

    public MappingsDatabaseCollector(Mappings mappings, MetaDataDialect metaDataDialect) {
        super( metaDataDialect );
        this.mappings = mappings;
    }

    public Iterator iterateTables() {
        return mappings.iterateTables();
    }

    public Table addTable(String schema, String catalog, String name) {
        return mappings.addTable( quote( schema ), quote( catalog ), quote( name ), null, false );
    }

    public Table getTable(String schema, String catalog, String name) {
        return mappings.getTable( quote( schema ), quote( catalog ), quote( name ) );
    }

}
