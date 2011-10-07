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

package org.hibernate.tool.test;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Various tests to validate the "sanity" of the jdbc drivers meta data implementation.
 *
 * @author Max Rydahl Andersen
 */
public class DriverMetaDataTest extends JDBCMetaDataBinderTestCase {
    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table tab_master ( id char not null, name varchar(20), primary key (id) )",
                "create table tab_child  ( childid character not null, masterref character, primary key (childid), foreign key (masterref) references tab_master(id) )",
        };
    }
    @Override
    protected String[] getDropSQL() {

        return new String[] {
                "drop table tab_child",
                "drop table tab_master",
        };
    }

    @Test
    public void testExportedKeys() {

        MetaDataDialect dialect = new JDBCMetaDataDialect();

        Settings settings = getJDBCMetaDataConfiguration().buildSettings( serviceRegistry() );
        dialect.configure(
                ReverseEngineeringRuntimeInfo.createInstance(
                        jdbcServices().getConnectionProvider(),
                        jdbcServices().getSqlExceptionHelper().getSqlExceptionConverter(),
                        new DefaultDatabaseCollector( dialect )
                )
        );

        Iterator tables = dialect.getTables(
                settings.getDefaultCatalogName(),
                settings.getDefaultSchemaName(),
                identifier( "tab_master" )
        );

        boolean foundMaster = false;
        while ( tables.hasNext() ) {
            Map map = (Map) tables.next();

            String tableName = (String) map.get( "TABLE_NAME" );
            String schemaName = (String) map.get( "TABLE_SCHEM" );
            String catalogName = (String) map.get( "TABLE_CAT" );

            if ( tableName.equals( identifier( "tab_master" ) ) ) {
                foundMaster = true;
                Iterator exportedKeys = dialect.getExportedKeys( catalogName, schemaName, tableName );
                int cnt = 0;
                while ( exportedKeys.hasNext() ) {
                    Map element = (Map) exportedKeys.next();
                    cnt++;
                }
                assertEquals( 1, cnt );
                /*	assertEquals(schemaName, settings.getDefaultSchemaName());
                    assertEquals(catalogName, settings.getDefaultCatalogName());*/
            }
        }

        assertTrue( foundMaster );
    }

    @Test
    public void testDataType() {

        MetaDataDialect dialect = new JDBCMetaDataDialect();

        Settings settings = getJDBCMetaDataConfiguration().buildSettings( serviceRegistry() );

        dialect.configure(
                ReverseEngineeringRuntimeInfo.createInstance(
                        jdbcServices().getConnectionProvider(),
                        jdbcServices().getSqlExceptionHelper().getSqlExceptionConverter(),
                        new DefaultDatabaseCollector( dialect )
                )
        );

        Iterator tables = dialect.getColumns(
                settings.getDefaultCatalogName(),
                settings.getDefaultSchemaName(),
                "test",
                null
        );


        while ( tables.hasNext() ) {
            Map map = (Map) tables.next();

            System.out.println( map );

        }
    }

    @Test
    public void testCaseTest() {


        MetaDataDialect dialect = new JDBCMetaDataDialect();

        Settings settings = getJDBCMetaDataConfiguration().buildSettings( serviceRegistry() );

        dialect.configure(
                ReverseEngineeringRuntimeInfo.createInstance(
                        jdbcServices().getConnectionProvider(),
                        jdbcServices().getSqlExceptionHelper().getSqlExceptionConverter(),
                        new DefaultDatabaseCollector( dialect )
                )
        );

        Iterator tables = dialect.getTables(
                settings.getDefaultCatalogName(),
                settings.getDefaultSchemaName(),
                identifier( "TAB_MASTER" )
        );

        assertHasNext( 1, tables );


    }


}
