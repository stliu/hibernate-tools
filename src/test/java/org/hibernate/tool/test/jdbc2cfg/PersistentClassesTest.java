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
 * Created on 27-Nov-2004
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.SQLException;


import org.junit.Test;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * @author max
 */
public class PersistentClassesTest extends JDBCMetaDataBinderTestCase {

    private static final String PACKAGE_NAME = "org.hibernate.tool.test.jdbc2cfg";

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table orders ( id numeric(10,0) not null, name varchar(20), primary key (id) )",
                "create table item  ( child_id numeric(10,0) not null, name varchar(50), order_id numeric(10,0), related_order_id numeric(10,0), primary key (child_id), foreign key (order_id) references orders(id), foreign key (related_order_id) references orders(id) )"
                // todo - link where pk is fk to something

        };
    }
    @Override
    protected void configure(JDBCMetaDataConfiguration cfgToConfigure) {
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings( new ReverseEngineeringSettings( c ).setDefaultPackageName( PACKAGE_NAME ) );
        cfgToConfigure.setReverseEngineeringStrategy( c );
    }
     @Override
    protected String[] getDropSQL() {

        return new String[] {
                "drop table item",
                "drop table orders",
        };
    }

    @Test
    public void testCreatePersistentClasses() {
        getJDBCMetaDataConfiguration().buildMappings();
        PersistentClass classMapping = getJDBCMetaDataConfiguration().getClassMapping( toClassName( "orders" ) );
        assertNotNull( "class not found", classMapping );
        KeyValue identifier = classMapping.getIdentifier();
        assertNotNull( identifier );
    }

    @Test
    public void testCreateManyToOne() {
        getJDBCMetaDataConfiguration().buildMappings();
        PersistentClass classMapping = getJDBCMetaDataConfiguration().getClassMapping( toClassName( "item" ) );

        assertNotNull( classMapping );

        KeyValue identifier = classMapping.getIdentifier();

        assertNotNull( identifier );

        assertEquals( 3, classMapping.getPropertyClosureSpan() );

        Property property = classMapping.getProperty( "ordersByRelatedOrderId" );
        assertNotNull( property );

        property = classMapping.getProperty( "ordersByOrderId" );
        assertNotNull( property );


    }

    @Test
    public void testCreateOneToMany() {
        getJDBCMetaDataConfiguration().buildMappings();

        PersistentClass orders = getJDBCMetaDataConfiguration().getClassMapping( toClassName( "orders" ) );

        Property itemset = orders.getProperty( "itemsForRelatedOrderId" );

        Collection col = (Collection) itemset.getValue();

        OneToMany otm = (OneToMany) col.getElement();
        assertEquals( otm.getReferencedEntityName(), toClassName( "item" ) );
        assertEquals( otm.getAssociatedClass().getClassName(), toClassName( "item" ) );
        assertEquals( otm.getTable().getName(), identifier( "orders" ) );

        assertNotNull( itemset );

        assertTrue( itemset.getValue() instanceof Set );

    }

    @Test
    public void testBinding() throws HibernateException, SQLException {

        SessionFactory sf = getJDBCMetaDataConfiguration().buildSessionFactory( serviceRegistry() );


        Session session = sf.openSession();
        Transaction t = session.beginTransaction();

        Orders order = new Orders();
        order.setId( new Long( 1 ) );
        order.setName( "Mickey" );

        session.save( order );

        Item item = addItem( order, 42, "item 42" );
        session.save( item );
        session.save( addItem( order, 43, "x" ) );
        session.save( addItem( order, 44, "y" ) );
        session.save( addItem( order, 45, "z" ) );
        session.save( addItem( order, 46, "w" ) );

        t.commit();
        session.close();

        session = sf.openSession();
        t = session.beginTransaction();

        Item loadeditem = (Item) session.get( toClassName( "item" ), new Long( 42 ) );

        assertEquals( item.getName(), loadeditem.getName() );
        assertEquals( item.getChildId(), loadeditem.getChildId() );
        assertEquals( item.getOrderId().getId(), loadeditem.getOrderId().getId() );

        assertTrue( loadeditem.getOrderId().getItemsForOrderId().contains( loadeditem ) );
        assertTrue( item.getOrderId().getItemsForOrderId().contains( item ) );

        assertEquals( 5, item.getOrderId().getItemsForOrderId().size() );
        assertEquals( 5, loadeditem.getOrderId().getItemsForOrderId().size() );

        t.commit();
        session.close();

        session = sf.openSession();
        t = session.beginTransaction();

        order = (Orders) session.load( Orders.class, new Long( 1 ) );
        assertFalse( Hibernate.isInitialized( order ) );
        assertFalse( Hibernate.isInitialized( order.getItemsForOrderId() ) );

        order = (Orders) session.createQuery( "from " + PACKAGE_NAME + ".Orders" ).uniqueResult();

        assertFalse( Hibernate.isInitialized( order.getItemsForOrderId() ) );
        t.commit();
        session.close();
        sf.close();
    }

    private Item addItem(Orders m, int itemid, String name) {
        Item item = new Item();
        item.setChildId( new Long( itemid ) );
        item.setOrderId( m );
        item.setName( name );
        m.getItemsForOrderId().add( item );
        return item;
    }


}
