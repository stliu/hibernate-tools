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

import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author max
 */
public class SearchEscapeStringTest extends JDBCMetaDataBinderTestCase {

    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop table b_tab",
                "drop table b2tab",
        };
    }

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table b_tab ( a int not null, name varchar(20), primary key (a)  )",
                "create table b2tab ( a int not null, name varchar(20), primary key (a)  )",
        };
    }

    @Test
    public void testBasic() throws SQLException {

        assertHasNext( "There should be 2 tables!", 2, getJDBCMetaDataConfiguration().getTableMappings() );

        Table table = getTable( identifier( "b_tab" ) );
        Table table2 = getTable( identifier( "b2tab" ) );

        assertNotNull( table );
        assertNotNull( table2 );

        assertEquals( table.getColumnSpan(), 2 );
        assertEquals( table2.getColumnSpan(), 2 );

    }


}
