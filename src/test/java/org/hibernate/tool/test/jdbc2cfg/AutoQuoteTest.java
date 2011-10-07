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

import org.junit.Test;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author max
 */
public class AutoQuoteTest extends JDBCMetaDataBinderTestCase {
    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table \"us-ers\" ( userid INTEGER NOT NULL, department VARCHAR(3), PRIMARY KEY (userid) )",
                "create table typ ( indexid INTEGER NOT NULL, text varchar(10) NOT NULL, korr INTEGER NOT NULL, PRIMARY KEY (indexid) )",
                "create table workLogs ( indexid INTEGER NOT NULL, loggedid INTEGER NOT NULL, userid INTEGER NOT NULL, typ INTEGER NOT NULL, PRIMARY KEY (indexid, userid), FOREIGN KEY (userid) REFERENCES \"us-ers\"(userid), FOREIGN KEY (typ) REFERENCES typ(indexid) )"
        };
    }
    @Override
    protected String[] getDropSQL() {

        return new String[] {
                "drop table workLogs",
                "drop table \"us-ers\"",
                "drop table typ",
        };
    }

    @Test
    public void testForQuotes() {

        Table table = getTable( "us-ers" );
        assertNotNull( table );
        assertTrue( table.isQuoted() );

        assertEquals( 2, table.getColumnSpan() );

        PersistentClass classMapping = getJDBCMetaDataConfiguration().getClassMapping( "Worklogs" );
        assertNotNull( classMapping );
        Property property = classMapping.getProperty( "usErs" );
        assertNotNull( property );

    }


}
