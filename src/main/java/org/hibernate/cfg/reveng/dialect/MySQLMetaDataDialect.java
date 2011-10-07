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

public class MySQLMetaDataDialect extends JDBCMetaDataDialect {

    /**
     * Based on info from http://dev.mysql.com/doc/refman/5.0/en/show-table-status.html
     * Should work on pre-mysql 5 too since it uses the "old" SHOW TABLE command instead of SELECT from infotable.
     */
    public Iterator getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String table) {
        String sql = null;
        try {
            catalog = caseForSearch( catalog );
            schema = caseForSearch( schema );
            table = caseForSearch( table );

            log.debug( "geSuggestedPrimaryKeyStrategyName(" + catalog + "." + schema + "." + table + ")" );

            sql = "show table status " + ( catalog == null ? "" : " from " + catalog + " " ) + ( table == null ? "" : " like '" + table + "' " );
            PreparedStatement statement = getConnection().prepareStatement( sql );

            final String sc = schema;
            final String cat = catalog;
            return new ResultSetIterator( statement.executeQuery(), getSQLExceptionConverter() ) {

                Map element = new HashMap();

                protected Object convertRow(ResultSet tableRs) throws SQLException {
                    element.clear();
                    element.put( "TABLE_NAME", tableRs.getString( "NAME" ) );
                    element.put( "TABLE_SCHEM", sc );
                    element.put( "TABLE_CAT", cat );

                    String string = tableRs.getString( "AUTO_INCREMENT" );
                    if ( string == null ) {
                        element.put( "HIBERNATE_STRATEGY", null );
                    }
                    else {
                        element.put( "HIBERNATE_STRATEGY", "identity" );
                    }
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
                    "Could not get list of suggested identity strategies from database. Probably a JDBC driver problem. ",
                    sql
            );
        }
    }
}
	
