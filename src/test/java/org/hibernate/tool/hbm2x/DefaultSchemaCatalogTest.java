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
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * @author max
 */
public class DefaultSchemaCatalogTest extends JDBCMetaDataBinderTestCase {

    @Test
    public void testReadOnlySpecificSchema() {

        JDBCMetaDataConfiguration configuration = new JDBCMetaDataConfiguration();

        OverrideRepository or = new OverrideRepository();
        or.addSchemaSelection( new SchemaSelection( null, "OVRTEST" ) );
        configuration.setReverseEngineeringStrategy( or.getReverseEngineeringStrategy( new DefaultReverseEngineeringStrategy() ) );
        configuration.readFromJDBC();

        List tables = getTables( configuration );

        assertEquals( 2, tables.size() );

        Table catchild = (Table) tables.get( 0 );
        Table catmaster = (Table) tables.get( 1 );

        if ( catchild.getName().equals( "CATMASTER" ) ) {
            catchild = (Table) tables.get( 1 );
            catmaster = (Table) tables.get( 0 );
        }

        TableIdentifier masterid = TableIdentifier.create( catmaster );
        TableIdentifier childid = TableIdentifier.create( catchild );

        assertEquals( new TableIdentifier( "DB1", "OVRTEST", "CATMASTER" ), masterid );
        assertEquals( new TableIdentifier( "DB1", "OVRTEST", "CATCHILD" ), childid );

    }

    @Test
    public void testOverlapping() {

        JDBCMetaDataConfiguration configuration = new JDBCMetaDataConfiguration();

        OverrideRepository or = new OverrideRepository();
        or.addSchemaSelection( new SchemaSelection( null, "OVRTEST" ) );
        or.addSchemaSelection( new SchemaSelection( null, null ) );
        configuration.setReverseEngineeringStrategy( or.getReverseEngineeringStrategy( new DefaultReverseEngineeringStrategy() ) );
        configuration.readFromJDBC();

        Set tables = new HashSet();
        Iterator iter = configuration.getTableMappings();
        while ( iter.hasNext() ) {
            Table element = (Table) iter.next();
            boolean added = tables.add( TableIdentifier.create( element ) );
            if ( !added ) {
                fail( "duplicate table found for " + element );
            }
        }

        assertEquals( 4, tables.size() );
    }

    @Test
    public void testUseDefault() {

        JDBCMetaDataConfiguration configuration = new JDBCMetaDataConfiguration();
        configuration.setProperty( Environment.DEFAULT_SCHEMA, "OVRTEST" );
        configuration.setProperty( Environment.DEFAULT_SCHEMA, "OVRTEST" );
        configuration.readFromJDBC();

        List tables = getTables( configuration );

        assertEquals( 2, tables.size() );

        Table catchild = (Table) tables.get( 0 );
        Table catmaster = (Table) tables.get( 1 );

        if ( catchild.getName().equals( "CATMASTER" ) ) {
            catchild = (Table) tables.get( 1 );
            catmaster = (Table) tables.get( 0 );
        }

        TableIdentifier masterid = TableIdentifier.create( catmaster );
        TableIdentifier childid = TableIdentifier.create( catchild );

        assertEquals(
                "jdbcreader has not nulled out according to default schema",
                new TableIdentifier( "DB1", null, "CATMASTER" ),
                masterid
        );
        assertEquals(
                "jdbcreader has not nulled out according to default schema",
                new TableIdentifier( "DB1", null, "CATCHILD" ),
                childid
        );
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
                "create schema ovrtest AUTHORIZATION sa",
                "create table ovrtest.catmaster ( id char not null, name varchar(20), primary key (id) )",
                "create table ovrtest.catchild  ( childid char not null, masterref char, primary key (childid), foreign key (masterref) references catmaster(id) )",
                "create table master ( id char not null, name varchar(20), primary key (id) )",
                "create table child  ( childid char not null, masterref char, primary key (childid), foreign key (masterref) references master(id) )",
        };
    }
    @Override
    protected String[] getDropSQL() {

        return new String[] {
                "drop table child",
                "drop table master",
                "drop table ovrtest.catchild",
                "drop table ovrtest.catmaster",
                "drop schema ovrtest"
        };
    }

}
