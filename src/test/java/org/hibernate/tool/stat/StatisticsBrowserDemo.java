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

package org.hibernate.tool.stat;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import org.junit.After;
import org.junit.Test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.tool.NonReflectiveTestCase;

public class StatisticsBrowserDemo /*extends NonReflectiveTestCase */{

//   //todo @Test
//    public void testBrowser() throws Exception {
//        sessionFactory().getStatistics().setStatisticsEnabled( true );
//
//        new StatisticsBrowser().showStatistics( sessionFactory().getStatistics(), false );
//
//
//        Session s = openSession();
//        Transaction tx = s.beginTransaction();
//
//        for ( int i = 0; i < 100; i++ ) {
//            Group group = new Group( "Hibernate" + i );
//            group.addUser( new User( "gavin" + i, "figo123" ) );
//            group.addUser( new User( "cbauer" + i, "figo123" ) );
//            group.addUser( new User( "steve" + i, "figo123" ) );
//            group.addUser( new User( "max" + i, "figo123" ) );
//            group.addUser( new User( "anthony" + i, "figo123" ) );
//
//            s.saveOrUpdate( group );
//            if ( i % 20 == 0 ) {
//                s.flush();
//            }
//        }
//        s.flush();
//        s.clear();
//        s.createQuery( "from java.lang.Object" ).list();
//        tx.commit();
//        s.close();
//
//
//        //Uncomment if you want to look on StatisticsBrowser
//        //Thread.sleep( 100000 );
//
//    }
//     @After
//    public void tearDown() throws Exception {
//        Statement statement = null;
//        Connection con = null;
//        Settings settings = null;
//        final ConnectionProvider connectionProvider = serviceRegistry().getService( JdbcServices.class )
//                .getConnectionProvider();
//        try {
//            settings = configuration().buildSettings( serviceRegistry() );
//
//            con = connectionProvider.getConnection();
//            statement = con.createStatement();
//            statement.execute( "drop table Session_attributes" );
//            statement.execute( "drop table Users" );
//            statement.execute( "drop table Groups" );
//            con.commit();
//        }
//        finally {
//            if ( statement != null ) {
//                statement.close();
//            }
//            connectionProvider.closeConnection( con );
//        }
//
//    }
//     @Override
//    protected String getBaseForMappings() {
//        return "org/hibernate/tool/stat/";
//    }
//    @Override
//    protected void addMappings(String[] files, Configuration cfg) {
//        Properties prop = new Properties();
//        prop.put(
//                AvailableSettings.CACHE_REGION_FACTORY,
//                "org.hibernate.cache.ehcache.EhCacheRegionFactory"
//        );
//        cfg.addProperties( prop );
//        super.addMappings( files, cfg );
//    }
//    @Override
//    protected String[] getMappings() {
//        return new String[] { "UserGroup.hbm.xml" };
//    }

}
