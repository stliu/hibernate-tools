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
package org.hibernate.tool.hbm2x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmitry Geraskov
 */
public class DefaultDatabaseCollectorTest extends JDBCMetaDataBinderTestCase {

    private static final String SCHEMA = "cat.cat";
    private static final String QSCHEMA = quote( SCHEMA );

    private static final String TABLE1 = "cat.child";
    private static final String QTABLE1 = quote( TABLE1 );

    private static final String TABLE2 = "cat.master";
    private static final String QTABLE2 = quote( TABLE2 );

    @Test
    public void testReadOnlySpecificSchema() {

        JDBCMetaDataConfiguration configuration = new JDBCMetaDataConfiguration();

        OverrideRepository or = new OverrideRepository();
        or.addSchemaSelection( new SchemaSelection( null, SCHEMA ) );
        configuration.setReverseEngineeringStrategy( or.getReverseEngineeringStrategy( new DefaultReverseEngineeringStrategy() ) );
        configuration.readFromJDBC();

        List tables = getTables( configuration );

        assertEquals( 2, tables.size() );

        Table catchild = (Table) tables.get( 0 );
        Table catmaster = (Table) tables.get( 1 );

        if ( catchild.getName().equals( TABLE2 ) ) {
            catchild = (Table) tables.get( 1 );
            catmaster = (Table) tables.get( 0 );
        }

        TableIdentifier masterid = TableIdentifier.create( catmaster );
        TableIdentifier childid = TableIdentifier.create( catchild );

        assertEquals( new TableIdentifier( "DB1", SCHEMA, TABLE1 ), childid );
        assertEquals( new TableIdentifier( "DB1", SCHEMA, TABLE2 ), masterid );
    }

    @Test
    public void testNeedQuote() {
        MetaDataDialect realMetaData = JDBCReaderFactory.newMetaDataDialect( getDialect(), getJDBCMetaDataConfiguration().getProperties() );
        assertTrue( "The name must be quoted!", realMetaData.needQuote( SCHEMA ) );
        assertTrue( "The name must be quoted!", realMetaData.needQuote( TABLE1 ) );
        assertTrue( "The name must be quoted!", realMetaData.needQuote( TABLE2 ) );
    }

    /**
     * There are 2 solutions:
     * 1. DatabaseCollector#addTable()/getTable() should be called for not quoted parameters - I think it is preferable way.
     * 2. DatabaseCollector#addTable()/getTable() should be called for quoted parameters - here users should
     * use the same quotes as JDBCReader.
     * Because of this there are 2 opposite methods(and they are both failed as addTable uses quoted names
     * but getTable uses non-quoted names )
     */
    @Test
    public void testQuotedNamesAndDefaultDatabaseCollector() {
        Settings buildSettings = getJDBCMetaDataConfiguration().buildSettings( serviceRegistry() );

        MetaDataDialect realMetaData = JDBCReaderFactory.newMetaDataDialect( getDialect(), getJDBCMetaDataConfiguration().getProperties() );

        JDBCReader reader = JDBCReaderFactory.newJDBCReader(
                serviceRegistry(),
                buildSettings,
                new DefaultReverseEngineeringStrategy(),
                realMetaData
        );

        DatabaseCollector dc = new DefaultDatabaseCollector( reader.getMetaDataDialect() );
        reader.readDatabaseSchema( dc, null, SCHEMA );

        assertNotNull( "The table should be found", dc.getTable( SCHEMA, null, TABLE1 ) );
        assertNotNull( "The table should be found", dc.getTable( SCHEMA, null, TABLE2 ) );
        assertNull( "Quoted names should not return the table", dc.getTable( quote( SCHEMA ), null, QTABLE1 ) );
        assertNull( "Quoted names should not return the table", dc.getTable( quote( SCHEMA ), null, QTABLE2 ) );

        assertEquals( "Foreign key 'masterref' was filtered!", 1, dc.getOneToManyCandidates().size() );
    }

    private static String quote(String name) {
        return "\"" + name + "\"";
    }

    private List getTables(JDBCMetaDataConfiguration metaDataConfiguration) {
        List list = new ArrayList();
        Iterator iter = metaDataConfiguration.getTableMappings();
        while ( iter.hasNext() ) {
            Table element = (Table) iter.next();
            list.add( element );
        }
        return list;
    }
    @Override
    protected String[] getCreateSQL() {
        return new String[] {
                "create schema " + QSCHEMA + " AUTHORIZATION sa",
                "create table " + QSCHEMA + "." + QTABLE2 + " (" +
                        " id integer NOT NULL," +
                        "  tt integer," +
                        "  CONSTRAINT master_pk PRIMARY KEY (id)" +
                        ")",
                "create table " + QSCHEMA + "." + QTABLE1 + " (" +
                        " childid integer NOT NULL,\r\n" +
                        " masterref integer,\r\n" +
                        " CONSTRAINT child_pk PRIMARY KEY (childid),\r\n" +
                        " CONSTRAINT masterref FOREIGN KEY (masterref) references " + QSCHEMA + "." + QTABLE2 + "(id)" +
                        ")",
        };
    }
    @Override
    protected String[] getDropSQL() {

        return new String[] {
                "drop table " + QSCHEMA + "." + QTABLE1,
                "drop table " + QSCHEMA + "." + QTABLE2,
                "drop schema " + QSCHEMA
        };
    }

}
