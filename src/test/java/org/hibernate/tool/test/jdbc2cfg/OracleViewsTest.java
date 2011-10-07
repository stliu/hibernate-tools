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

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.dialect.OracleMetaDataDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author max
 */
@RequiresDialect(Oracle8iDialect.class)
public class OracleViewsTest extends JDBCMetaDataBinderTestCase {
    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop table basic",
                "drop table somecolumnsnopk",
                "drop table multikeyed",
                "drop view basicView",
                "drop synonym weirdname"
        };
    }

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table basic ( a int not null, primary key (a) )",
                "create table somecolumnsnopk ( pk varchar(25) not null, b char, c int not null )",
                "create table multikeyed ( orderid varchar(10), customerid varchar(10), name varchar(10), primary key(orderid, customerid) )",
                "create view basicView as select a from basic",
                "create synonym weirdname for multikeyed",
                "comment on table basic is 'a basic comment'",
                "comment on column basic.a is 'a solid key'"
        };
    }

    @Override
    protected void configure(JDBCMetaDataConfiguration configuration) {
        configuration.setProperty( "hibernatetool.metadatadialect", OracleMetaDataDialect.class.getName() );
    }

    @Test
    public void testViewAndSynonyms() throws SQLException {
        PersistentClass classMapping = getJDBCMetaDataConfiguration().getClassMapping( toClassName( "basicview" ) );
        assertNotNull( classMapping );

        classMapping = getJDBCMetaDataConfiguration().getClassMapping( toClassName( "weirdname" ) );
        assertTrue( "If this is not-null synonyms apparently work!", classMapping == null );

        // get comments
        Table table = getTable( identifier( "basic" ) );
        assertEquals( "a basic comment", table.getComment() );
        assertEquals( "a solid key", table.getPrimaryKey().getColumn( 0 ).getComment() );

        table = getTable( identifier( "multikeyed" ) );
        assertNull( table.getComment() );
        assertNull( table.getColumn( 0 ).getComment() );
    }


}
