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
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.SQLException;

import org.junit.Test;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author max
 */
public class MetaDataTest extends JDBCMetaDataBinderTestCase {

    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop table basic",
                "drop table somecolumnsnopk",
                "drop table multikeyed"
        };
    }

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table basic ( a int not null, name varchar(20), primary key (a)  )",
                "create table somecolumnsnopk ( pk varchar(25) not null, b char, c int not null, aBoolean boolean )",
                "create table multikeyed ( orderid varchar(10), customerid varchar(10), name varchar(10), primary key(orderid, customerid) )"
        };
    }

    @Test
    public void testBasic() throws SQLException {

        assertHasNext(
                "There should be three tables!", 3, getJDBCMetaDataConfiguration()
                .getTableMappings()
        );

        Table table = getTable( identifier( "basic" ) );

        assertEqualIdentifiers( "basic", table.getName() );
        assertEquals( 2, table.getColumnSpan() );

        Column basicColumn = table.getColumn( 0 );
        assertEqualIdentifiers( "a", basicColumn.getName() );
        // TODO: we cannot call getSqlType(dialect,cfg) without a
        // MappingassertEquals("INTEGER", basicColumn.getSqlType() ); // at
        // least on hsqldb
        // assertEquals(22, basicColumn.getLength() ); // at least on oracle

        PrimaryKey key = table.getPrimaryKey();
        assertNotNull( "There should be a primary key!", key );
        assertEquals( key.getColumnSpan(), 1 );

        Column column = key.getColumn( 0 );
        assertTrue( column.isUnique() );

        assertSame( basicColumn, column );

    }

    @Test
    public void testScalePrecisionLength() {

        Table table = getTable( identifier( "basic" ) );

        Column nameCol = table.getColumn( new Column( identifier( "name" ) ) );
        assertEquals( nameCol.getLength(), 20 );
        assertEquals( nameCol.getPrecision(), Column.DEFAULT_PRECISION );
        assertEquals( nameCol.getScale(), Column.DEFAULT_SCALE );
    }

    /*
      * @Test public void testGetTables() {
      *
      * Table table = new Table(); table.setName("dummy"); cfg.addTable(table);
      *
      * Table foundTable = cfg.getTable(null,null,"dummy");
      *
      * assertSame(table,foundTable);
      *
      * foundTable = cfg.getTable(null,"dschema", "dummy");
      *
      * assertNotSame(table, foundTable); }
      */

    @Test
    public void testCompositeKeys() {

        Table table = getTable( identifier( "multikeyed" ) );

        PrimaryKey primaryKey = table.getPrimaryKey();

        assertEquals( 2, primaryKey.getColumnSpan() );
    }


}
