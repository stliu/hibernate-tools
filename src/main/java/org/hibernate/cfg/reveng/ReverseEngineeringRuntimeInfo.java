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


import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.mapping.Table;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

/**
 * Provides runtime-only information for reverse engineering process.
 * e.g. current connection provider, exception converter etc.
 *
 * @author max
 */
public class ReverseEngineeringRuntimeInfo {

    private final ConnectionProvider connectionProvider;
    private final SQLExceptionConverter SQLExceptionConverter;
    private final DatabaseCollector dbs;

    public static ReverseEngineeringRuntimeInfo createInstance(ConnectionProvider provider, SQLExceptionConverter sec, DatabaseCollector dbs) {
        return new ReverseEngineeringRuntimeInfo( provider, sec, dbs );
    }

    protected ReverseEngineeringRuntimeInfo(ConnectionProvider provider, SQLExceptionConverter sec, DatabaseCollector dbs) {
        this.connectionProvider = provider;
        this.SQLExceptionConverter = sec;
        this.dbs = dbs;
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    public SQLExceptionConverter getSQLExceptionConverter() {
        return SQLExceptionConverter;
    }

    /**
     * Shorthand for {@link getTable(String,String,String)} *
     */
    public Table getTable(TableIdentifier ti) {
        return dbs.getTable( ti.getSchema(), ti.getCatalog(), ti.getName() );
    }

    /**
     * Look up the table identified by the parameters in the currently found tables.
     * Warning: The table might not be fully initialized yet.
     *
     * @return Table if found in processd tables, null if not
     */
    public Table getTable(String catalog, String schema, String name) {
        return dbs.getTable( schema, catalog, name );
    }


}
