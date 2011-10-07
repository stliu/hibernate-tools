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

import java.util.Iterator;

import org.junit.Test;

import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.dialect.CachedMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * @author max
 */
public class CachedMetaDataTest extends JDBCMetaDataBinderTestCase {

    public class MockedMetaDataDialect implements MetaDataDialect {

        MetaDataDialect delegate;
        private boolean failOnDelegateAccess;

        public MockedMetaDataDialect(MetaDataDialect realMetaData) {
            delegate = realMetaData;
        }

        public void close() {
            delegate.close();
        }

        public void close(Iterator iterator) {
            delegate.close( iterator );
        }

        public void configure(ReverseEngineeringRuntimeInfo info) {
            delegate.configure( info );
        }

        public Iterator getColumns(String catalog, String schema, String table, String column) {
            if ( failOnDelegateAccess ) {
                throw new IllegalStateException( "delegate not accessible" );
            }
            else {
                return delegate.getColumns( catalog, schema, table, column );
            }
        }

        public Iterator getExportedKeys(String catalog, String schema, String table) {
            if ( failOnDelegateAccess ) {
                throw new IllegalStateException( "delegate not accessible" );
            }
            else {
                return delegate.getExportedKeys( catalog, schema, table );
            }
        }

        public Iterator getIndexInfo(String catalog, String schema, String table) {
            if ( failOnDelegateAccess ) {
                throw new IllegalStateException( "delegate not accessible" );
            }
            else {
                return delegate.getIndexInfo( catalog, schema, table );
            }
        }

        public Iterator getPrimaryKeys(String catalog, String schema, String name) {
            if ( failOnDelegateAccess ) {
                throw new IllegalStateException( "delegate not accessible" );
            }
            else {
                return delegate.getPrimaryKeys( catalog, schema, name );
            }
        }

        public Iterator getTables(String catalog, String schema, String table) {
            if ( failOnDelegateAccess ) {
                throw new IllegalStateException( "delegate not accessible" );
            }
            else {
                return delegate.getTables( catalog, schema, table );
            }
        }

        public boolean needQuote(String name) {
            return delegate.needQuote( name );
        }

        public void setDelegate(Object object) {
            this.delegate = null;
        }

        public void setFailOnDelegateAccess(boolean b) {
            failOnDelegateAccess = b;
        }

        public Iterator getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String name) {
            if ( failOnDelegateAccess ) {
                throw new IllegalStateException( "delegate not accessible" );
            }
            else {
                return delegate.getSuggestedPrimaryKeyStrategyName( catalog, schema, name );
            }
        }

    }

    @Test
    public void testCachedDialect() {

        MetaDataDialect realMetaData = JDBCReaderFactory.newMetaDataDialect(
                getDialect(),
                configuration().getProperties()
        );

        MockedMetaDataDialect mock = new MockedMetaDataDialect( realMetaData );
        CachedMetaDataDialect dialect = new CachedMetaDataDialect( mock );

        JDBCReader reader = JDBCReaderFactory.newJDBCReader(
                serviceRegistry(),
                settings(),
                new DefaultReverseEngineeringStrategy(),
                dialect
        );

        DatabaseCollector dc = new DefaultDatabaseCollector( reader.getMetaDataDialect() );
        reader.readDatabaseSchema( dc, null, null );

        validate( dc );

        mock.setFailOnDelegateAccess( true );

        reader = JDBCReaderFactory.newJDBCReader(
                serviceRegistry(),
                settings(),
                new DefaultReverseEngineeringStrategy(),
                dialect
        );

        dc = new DefaultDatabaseCollector( reader.getMetaDataDialect() );
        reader.readDatabaseSchema( dc, null, null );

        validate( dc );


    }

    private void validate(DatabaseCollector dc) {
        Iterator iterator = dc.iterateTables();
        Table firstChild = (Table) iterator.next();
        Table secondChild = (Table) iterator.next();
        assertNotNull( firstChild );
        assertNotNull( secondChild );
        if ( "CHILD".equals( firstChild.getName() ) ) {
            assertEquals( "MASTER", secondChild.getName() );
            assertHasNext(
                    "should have recorded one foreignkey to child table",
                    1,
                    firstChild.getForeignKeyIterator()
            );
        }
        else if ( "MASTER".equals( firstChild.getName() ) ) {
            assertEquals( "CHILD", secondChild.getName() );
            assertHasNext(
                    "should have recorded one foreignkey to child table",
                    1,
                    secondChild.getForeignKeyIterator()
            );
        }
        else {
            fail( "can't find expected table name" );
        }
        assertFalse( iterator.hasNext() );


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
