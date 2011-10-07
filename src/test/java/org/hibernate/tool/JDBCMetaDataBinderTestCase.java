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

/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

/**
 * @author max
 */
public abstract class JDBCMetaDataBinderTestCase extends BaseTestCase {
    private Log log = LogFactory.getLog( JDBCMetaDataBinderTestCase.class );
    protected JDBCMetaDataConfiguration cfg;
    private static boolean storesLowerCaseIdentifiers;
    private static boolean storesUpperCaseIdentifiers;
    /**
     * should this maybe be on dialect ? *
     */
    protected String identifier(String actual) {
        if ( storesLowerCaseIdentifiers ) {
            return actual.toLowerCase();
        }
        else if ( storesUpperCaseIdentifiers ) {
            return actual.toUpperCase();
        }
        else {
            return actual;
        }
    }

    /**
     * Tries to adjust for different behaviors on databases regarding cases on identifiers.
     * Used if you don't care about cases in comparisons.
     */
    protected void assertEqualIdentifiers(String expected, String actual) {
        Assert.assertEquals( identifier( expected ), identifier( actual ) );
    }

    protected void executeDDL(String[] sqls, boolean ignoreErrors) throws SQLException {
        Statement statement = null;
        Connection con = null;
        try {
            con = jdbcServices().getConnectionProvider().getConnection();
            DatabaseMetaData metaData = con.getMetaData();
            storesLowerCaseIdentifiers = metaData.storesLowerCaseIdentifiers();
            storesUpperCaseIdentifiers = metaData.storesUpperCaseIdentifiers();
            statement = con.createStatement();
            for ( String ddlsql : sqls ) {
                log.info( "Execute: " + ddlsql );
                try {
                    statement.execute( ddlsql );
                }
                catch ( SQLException se ) {
                    if ( ignoreErrors ) {
                        log.info( se.toString() + " for " + ddlsql );
                    }
                    else {
                        log.error( ddlsql, se );
                        throw se;
                    }
                }
            }
            con.commit();
        }
        finally {
            if ( statement != null ) {
                statement.close();
            }
            jdbcServices().getConnectionProvider().closeConnection( con );

        }
    }

    protected abstract String[] getCreateSQL();

    protected abstract String[] getDropSQL();

    @Before
    public void initialize() throws Exception {
        if ( cfg == null ) { // only do if we haven't done it before - to save time!
            try {
                executeDDL( getDropSQL(), true );
            }
            catch ( SQLException se ) {
                System.err.println( "Error while dropping - normally ok." );
                se.printStackTrace();
            }
            cfg = new JDBCMetaDataConfiguration();
            configure( cfg );
            String[] sqls = getCreateSQL();
            executeDDL( sqls, false );
            cfg.readFromJDBC(serviceRegistry());
        }
    }

    @After
    public void cleanupTables() throws Exception {
        executeDDL( getDropSQL(), true );
        cfg = null;
    }

    protected void configure(JDBCMetaDataConfiguration configuration) {
    }

    protected String toPropertyName(String column) {
        return getJDBCMetaDataConfiguration().getReverseEngineeringStrategy().columnToPropertyName( null, column );
    }

    protected String toClassName(String table) {
        return getJDBCMetaDataConfiguration().getReverseEngineeringStrategy().tableToClassName( new TableIdentifier( null, null, table ) );
    }

    /**
     * Return the first foreignkey with the matching name ... there actually might be multiple foreignkeys with same name, but then they point to different entitities.
     */
    protected ForeignKey getForeignKey(Table table, String fkName) {
        Iterator iter = table.getForeignKeyIterator();
        while ( iter.hasNext() ) {
            ForeignKey fk = (ForeignKey) iter.next();
            if ( fk.getName().equals( fkName ) ) {
                return fk;
            }
        }
        return null;
    }

    /**
     * Find the first table matching the name (without looking at schema/catalog)
     */
    protected Table getTable(String tabName) {
        return getTable( getJDBCMetaDataConfiguration(), tabName );
    }

    protected Table getTable(Configuration configuration, String tabName) {
        Iterator iter = configuration.getTableMappings();
        while ( iter.hasNext() ) {
            Table table = (Table) iter.next();
            if ( table.getName().equals( tabName ) ) {
                return table;
            }
        }
        return null;
    }

    protected Table getTable(Configuration configuration, String schemaName, String tabName) {
        Iterator iter = configuration.getTableMappings();
        while ( iter.hasNext() ) {
            Table table = (Table) iter.next();
            if ( table.getName().equals( tabName ) && safeEquals( schemaName, table.getSchema() ) ) {
                return table;
            }
        }
        return null;
    }

    private boolean safeEquals(Object value, Object tf) {
        if ( value == tf ) {
            return true;
        }
        if ( value == null ) {
            return false;
        }
        return value.equals( tf );
    }

    public JDBCMetaDataConfiguration getJDBCMetaDataConfiguration() {
        return cfg;
    }
}