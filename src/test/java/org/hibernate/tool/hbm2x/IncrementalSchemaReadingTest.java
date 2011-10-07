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
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbmlint.detector.TableSelectorStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;


/**
 * @author max
 */
public class IncrementalSchemaReadingTest extends JDBCMetaDataBinderTestCase {

    public class MockedMetaDataDialect extends JDBCMetaDataDialect {
        List<String> gottenTables = new ArrayList<String>();

        public Iterator getTables(String catalog, String schema, String table) {
            gottenTables.add( table );
            return super.getTables( catalog, schema, table );
        }

    }

    @Test
    public void testReadSchemaIncremental() {
        Settings buildSettings = getJDBCMetaDataConfiguration().buildSettings( serviceRegistry() );
        TableSelectorStrategy tss = new TableSelectorStrategy( new DefaultReverseEngineeringStrategy() );
        MockedMetaDataDialect mockedMetaDataDialect = new MockedMetaDataDialect();
        JDBCReader reader = JDBCReaderFactory.newJDBCReader(
                serviceRegistry(),
                buildSettings,
                tss,
                mockedMetaDataDialect
        );

        tss.addSchemaSelection( new SchemaSelection( null, null, "CHILD" ) );

        DatabaseCollector dc = new DefaultDatabaseCollector( reader.getMetaDataDialect() );
        reader.readDatabaseSchema( dc, null, null );

        assertEquals( mockedMetaDataDialect.gottenTables.size(), 1 );
        assertEquals( mockedMetaDataDialect.gottenTables.get( 0 ), "CHILD" );

        Iterator iterator = dc.iterateTables();
        Table firstChild = (Table) iterator.next();
        assertEquals( firstChild.getName(), "CHILD" );
        assertFalse( iterator.hasNext() );

        assertFalse(
                "should not record foreignkey to table it doesn't know about yet",
                firstChild.getForeignKeyIterator().hasNext()
        );

        tss.clearSchemaSelections();
        tss.addSchemaSelection( new SchemaSelection( null, null, "MASTER" ) );

        mockedMetaDataDialect.gottenTables.clear();
        reader.readDatabaseSchema( dc, null, null );

        assertEquals( mockedMetaDataDialect.gottenTables.size(), 1 );
        assertEquals( mockedMetaDataDialect.gottenTables.get( 0 ), "MASTER" );


        iterator = dc.iterateTables();
        assertNotNull( iterator.next() );
        assertNotNull( iterator.next() );
        assertFalse( iterator.hasNext() );

        Table table = dc.getTable( null, null, "CHILD" );
        assertSame( firstChild, table );

        assertHasNext( "should have recorded one foreignkey to child table", 1, firstChild.getForeignKeyIterator() );


        tss.clearSchemaSelections();
        reader.readDatabaseSchema( dc, null, null );

        Table finalMaster = dc.getTable( null, null, "MASTER" );

        assertSame( firstChild, dc.getTable( null, null, "CHILD" ) );
        assertHasNext( 1, firstChild.getForeignKeyIterator() );
        assertHasNext( 0, finalMaster.getForeignKeyIterator() );


    }
    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table master ( id char not null, name varchar(20), primary key (id) )",
                "create table child  ( childid char not null, masterref char, primary key (childid), foreign key (masterref) references master(id) )",
        };
    }
    @Override
    protected String[] getDropSQL() {

        return new String[] {
                "drop table child",
                "drop table master",
        };
    }

}
