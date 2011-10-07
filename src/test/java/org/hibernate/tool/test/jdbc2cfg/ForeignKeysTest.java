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
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author max
 */
public class ForeignKeysTest extends JDBCMetaDataBinderTestCase {
    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table master ( id char not null, name varchar(20), primary key (id) )",

                "create table child  ( childid character not null, masterref character, primary key (childid), foreign key (masterref) references master(id) )",

                "create table connection  ( conid int, name varchar(50), masterref character, childref1 character, childref2 character, primary key(conid), " +
                        "constraint con2master foreign key (masterref) references master(id)," +
                        "constraint childref1 foreign key  (childref1) references child(childid), " +
                        "constraint childref2 foreign key  (childref2) references child(childid) " +
                        ")",
                // todo - link where pk is fk to something

        };
    }
    @Override
    protected String[] getDropSQL() {

        return new String[] {
                "drop table connection",
                "drop table child",
                "drop table master",
        };
    }

    @Test
    public void testMultiRefs() {

        Table table = getTable( identifier( "connection" ) );

        ForeignKey foreignKey = getForeignKey( table, identifier( "con2master" ) );
        assertNotNull( foreignKey );

        assertEquals( toClassName( "master" ), foreignKey.getReferencedEntityName() );
        assertEquals( identifier( "connection" ), foreignKey.getTable().getName() );

        assertEquals( getTable( identifier( "master" ) ), foreignKey.getReferencedTable() );
        assertNotNull( getForeignKey( table, identifier( "childref1" ) ) );
        assertNotNull( getForeignKey( table, identifier( "childref2" ) ) );
        assertNull( getForeignKey( table, identifier( "dummy" ) ) );
        assertHasNext( 3, table.getForeignKeyIterator() );

    }

    @Test
    public void testMasterChild() {

        assertNotNull( getTable( identifier( "master" ) ) );
        Table child = getTable( identifier( "child" ) );

        Iterator iterator = child.getForeignKeyIterator();

        ForeignKey fk = (ForeignKey) iterator.next();

        assertFalse( "should only be one fk", iterator.hasNext() );

        assertEquals( 1, fk.getColumnSpan() );

        assertSame( fk.getColumn( 0 ), child.getColumn( new Column( identifier( "masterref" ) ) ) );


    }


    @Test
    public void testExport() {

        new SchemaExport( serviceRegistry(), getJDBCMetaDataConfiguration() ).create( true, false );

    }

}
