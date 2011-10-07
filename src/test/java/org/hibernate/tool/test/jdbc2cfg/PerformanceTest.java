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
import java.util.ArrayList;
import java.util.List;


import org.junit.Assert;
import org.junit.Test;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.type.Type;

/**
 * @author max
 */
public class PerformanceTest extends JDBCMetaDataBinderTestCase {

    static final int TABLECOUNT = 200;
    static final int COLCOUNT = 10;

    List createSQL = new ArrayList();
    List dropSQL = new ArrayList();

    @Override
    protected String[] getDropSQL() {
        return (String[]) dropSQL.toArray( new String[dropSQL.size()] );
    }

    @Override
    protected String[] getCreateSQL() {

        Dialect dia = getDialect();

        Mapping map = new Mapping() {

            public String getIdentifierPropertyName(String className)
                    throws MappingException {
                return null;
            }

            public Type getIdentifierType(String className) throws MappingException {
                return null;
            }

            public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
                return null;
            }

            public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
                return null;
            }

        };


        dropSQL = new ArrayList( TABLECOUNT );
        createSQL = new ArrayList( TABLECOUNT );
        Table lastTable = null;
        for ( int tablecount = 0; tablecount < TABLECOUNT; tablecount++ ) {
            Table table = new Table( "perftest" + tablecount );
            Column col = new Column( "id" );
            SimpleValue sv = new SimpleValue( configuration().createMappings(), table );
            sv.setTypeName( "string" );
            col.setValue( sv );
            table.addColumn( col );
            PrimaryKey pk = new PrimaryKey();
            pk.addColumn( col );
            table.setPrimaryKey( pk );

            for ( int colcount = 0; colcount < COLCOUNT; colcount++ ) {
                col = new Column( "col" + tablecount + "_" + colcount );
                sv = new SimpleValue( configuration().createMappings(), table );
                sv.setTypeName( "string" );
                col.setValue( sv );
                table.addColumn( col );

            }


            createSQL.add( table.sqlCreateString( dia, map, null, null ) );
            dropSQL.add( table.sqlDropString( dia, null, null ) );

            if ( lastTable != null ) {
                ForeignKey fk = new ForeignKey();
                fk.setName( col.getName() + lastTable.getName() + table.getName() );
                fk.addColumn( col );
                fk.setTable( table );
                fk.setReferencedTable( lastTable );
                createSQL.add( fk.sqlCreateString( dia, map, null, null ) );
                dropSQL.add( 0, fk.sqlDropString( dia, null, null ) );
            }

            lastTable = table;


        }


        return (String[]) createSQL.toArray( new String[createSQL.size()] );
    }


    @Test
    public void testBasic() throws SQLException {

        assertHasNext( TABLECOUNT, getJDBCMetaDataConfiguration().getTableMappings() );

        Table tab =  getJDBCMetaDataConfiguration().getTableMappings().next();
        Assert.assertEquals( tab.getColumnSpan(), COLCOUNT + 1 );


    }




}
