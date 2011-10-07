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

package org.hibernate.tool.test.jdbc2cfg;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.cfg.JDBCBinderException;
import org.hibernate.cfg.MetaDataDialectFactory;
import org.hibernate.cfg.reveng.dialect.H2MetaDataDialect;
import org.hibernate.cfg.reveng.dialect.HSQLMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MySQLMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.OracleMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.dialect.OracleDialect;

public class MetaDataDialectFactoryTest {

    private MetaDataDialectFactory mdf;

    static class NoNameDialect extends Dialect {

    }

    static class H2NamedDialect extends Dialect {

    }
    @Before
    public void setUp() throws Exception {
        mdf = new MetaDataDialectFactory();
    }

    @Test
    public void testCreateMetaDataDialect() {
        assertSameClass(
                "Generic metadata for dialects with no specifics",
                JDBCMetaDataDialect.class,
                mdf.createMetaDataDialect( new NoNameDialect(), new HashMap<String, String>() )
        );
        assertSameClass(
                H2MetaDataDialect.class,
                mdf.createMetaDataDialect( new H2NamedDialect(), new HashMap<String, String>() )
        );
        assertSameClass(
                OracleMetaDataDialect.class,
                mdf.createMetaDataDialect( new OracleDialect(), new HashMap<String, String>() )
        );
        assertSameClass(
                MySQLMetaDataDialect.class,
                mdf.createMetaDataDialect( new MySQL5Dialect(), new HashMap<String, String>() )
        );

        Map<String, String> p = new HashMap<String, String>();
        p.put( "hibernatetool.metadatadialect", H2MetaDataDialect.class.getCanonicalName() );
        assertSameClass(
                "property should override specific dialect",
                H2MetaDataDialect.class,
                mdf.createMetaDataDialect( new MySQL5Dialect(), p )
        );

    }

    public void testCreateMetaDataDialectNonExistingOverride(Map<String, String> p) {
        p.put( "hibernatetool.metadatadialect", "DoesNotExists" );
        try {
            mdf.createMetaDataDialect( new MySQL5Dialect(), p );
            fail();
        }
        catch ( JDBCBinderException jbe ) {
            // expected
        }
        catch ( Exception e ) {
            fail();
        }
    }

    @Test
    public void testFromDialect() {
        assertSameClass(
                "Generic metadata for dialects with no specifics",
                null,
                mdf.fromDialect( new NoNameDialect() )
        );

        assertSameClass( OracleMetaDataDialect.class, mdf.fromDialect( new Oracle8iDialect() ) );
        assertSameClass( OracleMetaDataDialect.class, mdf.fromDialect( new Oracle9Dialect() ) );
        assertSameClass( OracleMetaDataDialect.class, mdf.fromDialect( new Oracle10gDialect() ) );
        assertSameClass( OracleMetaDataDialect.class, mdf.fromDialect( new Oracle9iDialect() ) );
        assertSameClass( MySQLMetaDataDialect.class, mdf.fromDialect( new MySQL5InnoDBDialect() ) );
        assertSameClass( H2MetaDataDialect.class, mdf.fromDialect( new H2Dialect() ) );
        assertSameClass( HSQLMetaDataDialect.class, mdf.fromDialect( new HSQLDialect() ) );

    }

    @Test
    public void testFromDialectName() {
        assertSameClass( null, mdf.fromDialectName( "BlahBlah" ) );
        assertSameClass( OracleMetaDataDialect.class, mdf.fromDialectName( "mYorAcleDialect" ) );
        assertSameClass( OracleMetaDataDialect.class, mdf.fromDialectName( Oracle8iDialect.class.getName() ) );
        assertSameClass( OracleMetaDataDialect.class, mdf.fromDialectName( Oracle9Dialect.class.getName() ) );
        assertSameClass( MySQLMetaDataDialect.class, mdf.fromDialectName( MySQL5InnoDBDialect.class.getName() ) );
        assertSameClass( H2MetaDataDialect.class, mdf.fromDialectName( H2Dialect.class.getName() ) );
        assertSameClass( HSQLMetaDataDialect.class, mdf.fromDialectName( HSQLDialect.class.getName() ) );

    }

    public void assertSameClass(Class clazz, Object instance) {
        if ( clazz == null && instance == null ) {
            return;
        }
        if ( clazz == null ) {
            assertEquals( null, instance );
            return;
        }
        if ( instance == null ) {
            assertEquals( clazz.getCanonicalName(), null );
            return;
        }
        assertEquals( clazz.getCanonicalName(), instance.getClass().getName() );
    }

    public void assertSameClass(String msg, Class clazz, Object instance) {
        if ( clazz == null && instance == null ) {
            return;
        }
        if ( clazz == null ) {
            assertEquals( msg, null, instance );
            return;
        }
        if ( instance == null ) {
            assertEquals( msg, clazz.getCanonicalName(), null );
            return;
        }
        assertEquals( msg, clazz.getCanonicalName(), instance.getClass().getName() );
    }
}
