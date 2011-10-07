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

package org.hibernate.tool.hbm2x;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.test.TestHelper;

/**
 * Be sure to test the file existence and compilation in the {@link org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test} too.
 * this will allow minimal 1.4 testing
 *
 * @author emmanuel
 */
public class Hbm2JavaEjb3ForJDK50Test extends NonReflectiveTestCase {

    @Before
    public void setUp() throws Exception {

        POJOExporter exporter = new POJOExporter( configuration(), serviceRegistry(), getOutputDir() );
        exporter.setTemplatePath( new String[0] );
        exporter.getProperties().setProperty( "ejb3", "true" );
        exporter.getProperties().setProperty( "jdk5", "true" );

        exporter.start();
    }

    @Test
    public void testFileExistence() {
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/Train.java" ) );
        assertFileAndExists(
                new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/Passenger.java" )
        );
    }


    @Test
    public void testCompile() {

        File file = new File( getOutputDir(), "ejb3compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        addAnnotationJars( jars );
        TestHelper.compile(
                getOutputDir(), file, TestHelper.visitAllFiles( getOutputDir(), list ), "1.6",
                TestHelper.buildClasspath( jars )
        );

        TestHelper.deleteDir( file );
    }

    @Test
    public void testUsageOfGeneratedItem() throws Exception {

        File file = new File( getOutputDir(), "ejb3compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        addAnnotationJars( jars );
        TestHelper.compile(
                getOutputDir(), file, TestHelper.visitAllFiles( getOutputDir(), list ), "1.6",
                TestHelper.buildClasspath( jars )
        );
        URL[] urls = new URL[] { file.toURI().toURL() };
        Thread currentThread = Thread.currentThread();
        URLClassLoader ucl = new URLClassLoader( urls, currentThread.getContextClassLoader() );
        currentThread.setContextClassLoader( ucl );

        Configuration configuration = new Configuration();
        Class train = ucl.loadClass( "org.hibernate.tool.hbm2x.Train" );
        Class passenger = ucl.loadClass( "org.hibernate.tool.hbm2x.Passenger" );
        Class transportationPk = ucl.loadClass( "org.hibernate.tool.hbm2x.TransportationPk" );
        configuration.addAnnotatedClass( train );
        configuration.addAnnotatedClass( passenger );

        configuration.setProperty( "hibernate.hbm2ddl.auto", "create-drop" );
        SessionFactory sf = configuration.buildSessionFactory( serviceRegistry() );
        Session s = sf.openSession();

        Object trainId = transportationPk.newInstance();
        transportationPk.getMethod( "setCity", new Class[] { String.class } )
                .invoke( trainId, new Object[] { "Paris" } );
        transportationPk.getMethod( "setLine", new Class[] { String.class } )
                .invoke( trainId, new Object[] { "Ligne 1" } );
        Object trainInst = train.newInstance();
        train.getMethod( "setName", new Class[] { String.class } ).invoke( trainInst, new Object[] { "train1" } );
        train.getMethod( "setTransportationId", new Class[] { transportationPk } )
                .invoke( trainInst, new Object[] { trainId } );
        Set usualPassengers = new HashSet();
        train.getMethod( "setUsualPassengers", new Class[] { Set.class } )
                .invoke( trainInst, new Object[] { usualPassengers } );
        Set currentPassengers = new HashSet();
        train.getMethod( "setCurrentPassengers", new Class[] { Set.class } )
                .invoke( trainInst, new Object[] { currentPassengers } );

        Object trainId2 = transportationPk.newInstance();
        transportationPk.getMethod( "setCity", new Class[] { String.class } )
                .invoke( trainId2, new Object[] { "Paris" } );
        transportationPk.getMethod( "setLine", new Class[] { String.class } )
                .invoke( trainId2, new Object[] { "Ligne 2" } );
        Object trainInst2 = train.newInstance();
        train.getMethod( "setName", new Class[] { String.class } ).invoke( trainInst2, new Object[] { "train2" } );
        train.getMethod( "setTransportationId", new Class[] { transportationPk } )
                .invoke( trainInst2, new Object[] { trainId2 } );
        Set usualPassengers2 = new HashSet();
        train.getMethod( "setUsualPassengers", new Class[] { Set.class } )
                .invoke( trainInst2, new Object[] { usualPassengers2 } );
        Set currentPassengers2 = new HashSet();
        train.getMethod( "setCurrentPassengers", new Class[] { Set.class } )
                .invoke( trainInst2, new Object[] { currentPassengers2 } );

        Object passengerInst = passenger.newInstance();
        passenger.getMethod( "setCurrentTrain", new Class[] { train } )
                .invoke( passengerInst, new Object[] { trainInst } );
        passenger.getMethod( "setFamilyName", new Class[] { String.class } )
                .invoke( passengerInst, new Object[] { "passenger1" } );
        Set usualTrains = new HashSet();
        passenger.getMethod( "setUsualTrains", new Class[] { Set.class } )
                .invoke( passengerInst, new Object[] { usualTrains } );

        Object passengerInst2 = passenger.newInstance();
        passenger.getMethod( "setCurrentTrain", new Class[] { train } )
                .invoke( passengerInst2, new Object[] { trainInst2 } );
        passenger.getMethod( "setFamilyName", new Class[] { String.class } )
                .invoke( passengerInst2, new Object[] { "passenger2" } );
        Set usualTrains2 = new HashSet();
        passenger.getMethod( "setUsualTrains", new Class[] { Set.class } )
                .invoke( passengerInst2, new Object[] { usualTrains2 } );
        usualTrains.add( trainInst );
        usualTrains.add( trainInst2 );
        usualTrains2.add( trainInst2 );

        currentPassengers.add( passengerInst );
        currentPassengers2.add( passengerInst2 );
        usualPassengers.add( passengerInst );
        usualPassengers2.add( passengerInst );
        usualPassengers2.add( passengerInst2 );

        s.getTransaction().begin();
        s.persist( trainInst );
        s.persist( trainInst2 );
        s.getTransaction().commit();
        s.clear();

        s.getTransaction().begin();
        trainInst2 = s.get( train, (Serializable) trainId2 );
        usualPassengers2 = (Set) train.getMethod( "getUsualPassengers", new Class[] { } )
                .invoke( trainInst2, new Object[] { } );
        Assert.assertEquals( 2, usualPassengers2.size() );
        currentPassengers2 = (Set) train.getMethod( "getCurrentPassengers", new Class[] { } )
                .invoke( trainInst2, new Object[] { } );
        Assert.assertEquals( 1, currentPassengers2.size() );
        s.getTransaction().commit();
        s.close();
        sf.close();

        currentThread.setContextClassLoader( ucl.getParent() );
        TestHelper.deleteDir( file );
    }

    @Test
    public void testNonStandardGenericGenerator() throws Exception {
        testGenerator( "Puppet" );
    }

    @Test
    public void testTableGenerator() throws Exception {
        testGenerator( "Bungalow" );
    }

    @Test
    public void testCreateAnnotationConfiguration() throws Exception {

        File file = new File( getOutputDir(), "ejb3compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        addAnnotationJars( jars );

        new ExecuteContext( getOutputDir(), file, jars ) {

            protected void execute() throws Exception {
                Configuration configuration = new Configuration();
                configuration.addAnnotatedClass( getUcl().loadClass( "org.hibernate.tool.hbm2x.Train" ) );
                configuration.addAnnotatedClass( getUcl().loadClass( "org.hibernate.tool.hbm2x.Passenger" ) );

                configuration.setProperty( "hibernate.hbm2ddl.auto", "create-drop" );
                SessionFactory sf = configuration.buildSessionFactory( serviceRegistry() );
                Session s = sf.openSession();
                Query createQuery = s.createQuery( "from java.lang.Object" );
                createQuery.list();
                s.close();
                sf.close();

            }

        }.run();
    }

    private void testGenerator(final String className) throws Exception {

        File file = new File(getOutputDir(), "ejb3compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        addAnnotationJars( jars );

        new ExecuteContext( getOutputDir(), file, jars ) {

            protected void execute() throws Exception {

                Configuration configuration = new Configuration();
                Class puppet = getUcl().loadClass( "org.hibernate.tool.hbm2x." + className );
                configuration.addAnnotatedClass( puppet );

                configuration.setProperty( "hibernate.hbm2ddl.auto", "create-drop" );
                SessionFactory sf = configuration.buildSessionFactory( serviceRegistry() );
                Session s = sf.openSession();

                Object puppetInst = puppet.newInstance();
                puppet.getMethod( "setName", new Class[] { String.class } )
                        .invoke( puppetInst, new Object[] { "Barbie" } );

                if ( className.equals( "Bungalow" ) ) { // hack to avoid not-null execption
                    puppet.getMethod( "setMascot", new Class[] { puppet } )
                            .invoke( puppetInst, new Object[] { puppetInst } );
                }

                s.getTransaction().begin();
                s.persist( puppetInst );
                s.getTransaction().commit();
                s.clear();

                s.getTransaction().begin();
                Object puppetInst2 = s.get(
                        puppet,
                        (Serializable) puppet.getMethod( "getId", new Class[] { } )
                                .invoke( puppetInst, new Object[] { } )
                );
                Assert.assertNotNull( puppetInst2 );
                Assert.assertEquals(
                        "Barbie",
                        puppet.getMethod( "getName", new Class[] { } ).invoke( puppetInst, new Object[] { } )
                );
                s.delete( puppetInst2 );
                s.getTransaction().commit();
                s.close();
                sf.close();
                new SchemaExport( serviceRegistry(), configuration ).drop( false, true );

            }

            ;

        }.run();

    }

    private void addAnnotationJars(List jars) {
        jars.add( "hibernate-jpa-2.0-api.jar" );
        jars.add( "hibernate-core.jar" );
        jars.add( "hibernate-commons-annotations.jar" );
        jars.add( "antlr.jar" );
        jars.add( "dom4j.jar" );
        jars.add( "commons-collections.jar" );
        jars.add( "classmate.jar" );
        jars.add( "jandex.jar" );
        jars.add( "javassist.jar" );
        jars.add("jboss-logging.jar");
        jars.add( "jta.jar" );

    }

    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }

    @Override
    protected String[] getMappings() {
        return new String[] {
                "Train.hbm.xml",
                "Passenger.hbm.xml",
                "Puppet.hbm.xml",
                "Bungalow.hbm.xml"
        };
    }


}
