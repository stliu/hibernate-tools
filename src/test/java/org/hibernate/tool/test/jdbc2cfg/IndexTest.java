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
package org.hibernate.tool.test.jdbc2cfg;

import java.util.Iterator;

import org.junit.Test;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author max
 */
public class IndexTest extends JDBCMetaDataBinderTestCase {

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table withIndex (first int, second int, third int)",
                "create index myIndex on withIndex(first,third)",
                "create unique index otherIdx on withIndex(third)",
        };
    }
    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop index otherIdx",
                "drop index myIndex",
                "drop table withIndex",
        };
    }

    @Test
    public void testUniqueKey() {

        Table table = getTable( identifier( "withIndex" ) );

        UniqueKey uniqueKey = table.getUniqueKey( identifier( "otherIdx" ) );
        assertNotNull( uniqueKey );

        assertEquals( 1, uniqueKey.getColumnSpan() );

        Column keyCol = uniqueKey.getColumn( 0 );
        assertTrue( keyCol.isUnique() );

        assertSame( keyCol, table.getColumn( keyCol ) );

    }

    @Test
    public void testWithIndex() {

        Table table = getTable( identifier( "withIndex" ) );

        assertEqualIdentifiers( "withIndex", table.getName() );

        assertNull( "there should be no pk", table.getPrimaryKey() );
        Iterator iterator = table.getIndexIterator();


        int cnt = 0;
        while ( iterator.hasNext() ) {
            iterator.next();
            cnt++;
        }
        assertEquals( 1, cnt );

        Index index = table.getIndex( identifier( "myIndex" ) );

        assertNotNull( "No index ?", index );
        assertEqualIdentifiers( "myIndex", index.getName() );

        assertEquals( 2, index.getColumnSpan() );

        assertSame( index.getTable(), table );
        Iterator cols = index.getColumnIterator();
        Column col1 = (Column) cols.next();
        Column col2 = (Column) cols.next();

        assertEqualIdentifiers( "first", col1.getName() );
        assertEqualIdentifiers( "third", col2.getName() );

        Column example = new Column();
        example.setName( col2.getName() );
        assertSame( "column with same name should be same instance!", table.getColumn( example ), col2 );

    }


}
