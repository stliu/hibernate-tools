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

package org.hibernate.cfg.reveng.dialect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;


/**
 * MetaData dialect that work around tweaks in the H2 database.
 *
 * @author Max Rydahl Andersen
 */
public class H2MetaDataDialect extends JDBCMetaDataDialect {

    private static boolean understandsCatalogName = true;

    public H2MetaDataDialect() {
        super();
        try {
            Class constants = ReflectHelper.classForName( "org.h2.engine.Constants" );
            Integer build = (Integer) constants.getDeclaredField( "BUILD_ID" ).get( null );
            if ( build.intValue() < 55 ) {
                understandsCatalogName = false;
            }
        }
        catch ( Throwable e ) {
            // ignore (probably H2 not in the classpath)
        }
    }

    protected void putTablePart(Map element, ResultSet tableRs) throws SQLException {
        super.putTablePart( element, tableRs );
        if ( !understandsCatalogName ) {
            element.put( "TABLE_CAT", null );
        }
    }

    protected void putExportedKeysPart(Map element, ResultSet rs) throws SQLException {
        super.putExportedKeysPart( element, rs );
        if ( !understandsCatalogName ) {
            element.put( "PKTABLE_CAT", null );
        }
    }

    public Iterator getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String table) {
        try {
            catalog = caseForSearch( catalog );
            schema = caseForSearch( schema );
            table = caseForSearch( table );

            log.debug( "geSuggestedPrimaryKeyStrategyName(" + catalog + "." + schema + "." + table + ")" );

            String sql = "SELECT idx.TABLE_CATALOG TABLE_CAT, idx.TABLE_SCHEMA TABLE_SCHEM, idx.TABLE_NAME, idx.COLUMN_NAME, cols.COLUMN_DEFAULT COLUMN_DEFAULT FROM " +
                    "INFORMATION_SCHEMA.INDEXES idx, INFORMATION_SCHEMA.COLUMNS cols " +
                    "WHERE " +
                    "idx.TABLE_CATALOG = cols.TABLE_CATALOG " +
                    "and idx.TABLE_SCHEMA = cols.TABLE_SCHEMA " +
                    "and idx.TABLE_NAME = cols.TABLE_NAME " +
                    "AND idx.PRIMARY_KEY = TRUE " +
                    "AND COLUMN_DEFAULT like '%NEXT VALUE FOR%' ";
            if ( catalog != null ) {
                sql += "AND idx.TABLE_CATALOG like '" + catalog + "' ";
            }
            if ( schema != null ) {
                sql += "AND idx.TABLE_SCHEMA like '" + schema + "' ";
            }
            if ( table != null ) {
                sql += "AND idx.TABLE_NAME like '" + table + "' ";
            }

            PreparedStatement statement = getConnection().prepareStatement( sql );

            return new ResultSetIterator( statement.executeQuery(), getSQLExceptionConverter() ) {

                Map element = new HashMap();

                protected Object convertRow(ResultSet tableRs) throws SQLException {
                    element.clear();
                    putTablePart( element, tableRs );
                    String string = tableRs.getString( "COLUMN_DEFAULT" );
                    element.put( "HIBERNATE_STRATEGY", StringHelper.isEmpty( string ) ? null : "identity" );
                    return element;
                }

                protected Throwable handleSQLException(SQLException e) {
                    // schemaRs and catalogRs are only used for error reporting if
                    // we get an exception
                    throw getSQLExceptionConverter().convert(
                            e,
                            "Could not get list of suggested identity strategies from database. Probably a JDBC driver problem. ",
                            null
                    );
                }
            };
        }
        catch ( SQLException e ) {
            throw getSQLExceptionConverter().convert(
                    e,
                    "Could not get list of suggested identity strategies from database. Probably a JDBC driver problem.",
                    null
            );
        }
    }
}
