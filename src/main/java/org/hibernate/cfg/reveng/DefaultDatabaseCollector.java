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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Table;

public class DefaultDatabaseCollector extends AbstractDatabaseCollector {

    private Map<String, Table> tables;
    private Map<String,List<Table>> qualifiers;

    public DefaultDatabaseCollector(MetaDataDialect metaDataDialect) {
        super( metaDataDialect );
        tables = new HashMap<String, Table>();
        qualifiers = new HashMap<String,List<Table>>();
    }

    public Iterator iterateTables() {
        return tables.values().iterator();
    }

    public Table addTable(String schema,
                          String catalog,
                          String name) {

        String key = Table.qualify( quote( catalog ), quote( schema ), quote( name ) );
        Table table = tables.get( key );

        if ( table == null ) {
            table = new Table();
            table.setAbstract( false );
            table.setName( name );
            table.setSchema( schema );
            table.setCatalog( catalog );
            tables.put( key, table );

            String qualifier = StringHelper.qualifier( key );
            List<Table> schemaList = qualifiers.get( qualifier );
            if ( schemaList == null ) {
                schemaList = new ArrayList<Table>();
                qualifiers.put( qualifier, schemaList );
            }
            schemaList.add( table );
        }
        else {
            table.setAbstract( false );
        }

        return table;
    }

    public Table getTable(String schema, String catalog, String name) {
        String key = Table.qualify( quote( catalog ), quote( schema ), quote( name ) );
        return tables.get( key );
    }

    public Iterator getQualifierEntries() {
        return qualifiers.entrySet().iterator();
    }


}
