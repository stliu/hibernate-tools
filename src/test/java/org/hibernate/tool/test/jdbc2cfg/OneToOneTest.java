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
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.test.TestHelper;
import org.hibernate.type.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author max
 */
public class OneToOneTest extends JDBCMetaDataBinderTestCase {
    private JDBCMetaDataConfiguration localCfg;

    @Before
    public void setUp() throws Exception {
        localCfg = new JDBCMetaDataConfiguration();
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        localCfg.setReverseEngineeringStrategy( c );
        localCfg.readFromJDBC(serviceRegistry());
    }
    @After
    public void tearDown() throws Exception {
        localCfg = null;
    }

    @Test
    public void testOneToOneSingleColumnBiDirectional() {

        PersistentClass person = localCfg.getClassMapping( "Person" );

        Property addressProperty = person.getProperty( "addressPerson" );
        assertNotNull( addressProperty );

        assertTrue( addressProperty.getValue() instanceof OneToOne );

        OneToOne oto = (OneToOne) addressProperty.getValue();

        assertEquals( oto.getColumnSpan(), 1 );
        assertEquals( "Person", oto.getEntityName() );
        assertEquals( "AddressPerson", oto.getReferencedEntityName() );


        assertEquals( 2, person.getPropertyClosureSpan() );
        assertEquals( "personId", person.getIdentifierProperty().getName() );
        assertFalse( oto.isConstrained() );

        PersistentClass addressPerson = localCfg.getClassMapping( "AddressPerson" );


        Property personProperty = addressPerson.getProperty( "person" );
        assertNotNull( personProperty );

        assertTrue( personProperty.getValue() instanceof OneToOne );

        oto = (OneToOne) personProperty.getValue();

        assertTrue( oto.isConstrained() );
        assertEquals( oto.getColumnSpan(), 1 );
        assertEquals( "AddressPerson", oto.getEntityName() );
        assertEquals( "Person", oto.getReferencedEntityName() );

        assertEquals( 2, addressPerson.getPropertyClosureSpan() );
        assertEquals( "addressId", addressPerson.getIdentifierProperty().getName() );

    }

    @Test
    public void testAddressWithForeignKeyGeneration() {

        PersistentClass address = localCfg.getClassMapping( "AddressPerson" );

        assertEquals( "foreign", ( (SimpleValue) address.getIdentifier() ).getIdentifierGeneratorStrategy() );
    }

    @Test
    public void testOneToOneMultiColumnBiDirectional() {

        PersistentClass person = localCfg.getClassMapping( "MultiPerson" );

        Property addressProperty = person.getProperty( "addressMultiPerson" );
        assertNotNull( addressProperty );

        assertTrue( addressProperty.getValue() instanceof OneToOne );

        OneToOne oto = (OneToOne) addressProperty.getValue();

        assertEquals( oto.getColumnSpan(), 2 );
        assertEquals( "MultiPerson", oto.getEntityName() );
        assertEquals( "AddressMultiPerson", oto.getReferencedEntityName() );
        assertFalse( oto.isConstrained() );

        assertEquals( 2, person.getPropertyClosureSpan() );
        assertEquals( "compositeid gives generic id name", "id", person.getIdentifierProperty().getName() );

        PersistentClass addressPerson = localCfg.getClassMapping( "AddressMultiPerson" );


        Property personProperty = addressPerson.getProperty( "multiPerson" );
        assertNotNull( personProperty );

        assertTrue( personProperty.getValue() instanceof OneToOne );

        oto = (OneToOne) personProperty.getValue();

        assertEquals( oto.getColumnSpan(), 2 );
        assertEquals( "AddressMultiPerson", oto.getEntityName() );
        assertEquals( "MultiPerson", oto.getReferencedEntityName() );

        assertEquals( 2, addressPerson.getPropertyClosureSpan() );
        assertEquals( "compositeid gives generic id name", "id", addressPerson.getIdentifierProperty().getName() );
        assertTrue( oto.isConstrained() );
    }

    @Test
    @Ignore
    public void testNoCreation() {

        assertNull( "No middle class should be generated.", getJDBCMetaDataConfiguration().getClassMapping( "WorksOn" ) );

        assertNotNull(
                "Should create worksontext since one of the foreign keys is not part of pk",
                getJDBCMetaDataConfiguration().getClassMapping( "WorksOnContext" )
        );

        PersistentClass projectClass = getJDBCMetaDataConfiguration().getClassMapping( "Project" );
        assertNotNull( projectClass );

        PersistentClass employeeClass = getJDBCMetaDataConfiguration().getClassMapping( "Employee" );
        assertNotNull( employeeClass );

        assertPropertyNotExist( projectClass, "worksOns" );
        assertPropertyNotExist( employeeClass, "worksOns" );

        Property property = employeeClass.getProperty( "projects" );
        assertNotNull( property );
        assertNotNull( projectClass.getProperty( "employees" ) );

    }

    @Test
    public void testBuildMappings() {

        localCfg.buildMappings();

    }

    @Test
    public void testGenerateMappingAndReadable() throws MalformedURLException {

        getJDBCMetaDataConfiguration().buildMappings();

        HibernateMappingExporter hme = new HibernateMappingExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        hme.start();

        assertFileAndExists( new File( getOutputDir(), "Person.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "AddressPerson.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "AddressMultiPerson.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "MultiPerson.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "Middle.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "Left.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "Right.hbm.xml" ) );

        assertEquals( 7, getOutputDir().listFiles().length );

        POJOExporter exporter = new POJOExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        exporter.setTemplatePath( new String[0] );
        exporter.getProperties().setProperty( "ejb3", "false" );
        exporter.getProperties().setProperty( "jdk5", "false" );
        exporter.start();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        //addAnnotationJars(jars);
        TestHelper.compile(
                getOutputDir(), getOutputDir(), TestHelper.visitAllFiles( getOutputDir(), list ), "1.6",
                TestHelper.buildClasspath( jars )
        );

        URL[] urls = new URL[] { getOutputDir().toURI().toURL() };
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        URLClassLoader ucl = new URLClassLoader( urls, oldLoader );
        try {
            Thread.currentThread().setContextClassLoader( ucl );

            Configuration configuration = new Configuration()
                    .addFile( new File( getOutputDir(), "Person.hbm.xml" ) )
                    .addFile( new File( getOutputDir(), "AddressPerson.hbm.xml" ) )
                    .addFile( new File( getOutputDir(), "AddressMultiPerson.hbm.xml" ) )
                    .addFile( new File( getOutputDir(), "MultiPerson.hbm.xml" ) )
                    .addFile( new File( getOutputDir(), "Middle.hbm.xml" ) )
                    .addFile( new File( getOutputDir(), "Left.hbm.xml" ) )
                    .addFile( new File( getOutputDir(), "Right.hbm.xml" ) );

            configuration.buildMappings();

            new SchemaValidator(serviceRegistry(), configuration ).validate();
        }
        finally {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
    }

    @Test
    public void testGenerateAnnotatedClassesAndReadable() throws Exception{

        Configuration c = getJDBCMetaDataConfiguration();
        Iterator<PersistentClass> iter =  c.getClassMappings();
        while(iter.hasNext()){
            PersistentClass p = iter.next();
            System.out.println(p.getClassName());
            Iterator i =p.getPropertyIterator();
            while(i.hasNext()){
                Property property = (Property)i.next();
                Type type = property.getValue().getType();
                System.out.println("\t"+type.getName()+"--"+property.getName());
            }
        }

        POJOExporter exporter = new POJOExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        exporter.setTemplatePath( new String[0] );
        exporter.getProperties().setProperty( "ejb3", "true" );
        exporter.getProperties().setProperty( "jdk5", "true" );
        exporter.start();

        assertFileAndExists( new File( getOutputDir(), "Person.java" ) );
        assertFileAndExists( new File( getOutputDir(), "AddressPerson.java" ) );
        assertFileAndExists( new File( getOutputDir(), "MultiPersonId.java" ) );
        assertFileAndExists( new File( getOutputDir(), "AddressMultiPerson.java" ) );
        assertFileAndExists( new File( getOutputDir(), "AddressMultiPersonId.java" ) );
        assertFileAndExists( new File( getOutputDir(), "MultiPerson.java" ) );

        assertEquals( 9, getOutputDir().listFiles().length );
        ArrayList list = new ArrayList();
        List jars = new ArrayList();


        jars.add( "hibernate-core.jar" );
        jars.add( "hibernate-jpa-2.0-api.jar" );

        TestHelper.compile(
                getOutputDir(), getOutputDir(), TestHelper.visitAllFiles( getOutputDir(), list ), "1.6",
                TestHelper.buildClasspath( jars )
        );
        URL[] urls = new URL[] { getOutputDir().toURI().toURL() };
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        URLClassLoader ucl = new URLClassLoader( urls, oldLoader );
        Class personClass = ucl.loadClass( "Person" );
        Class multiPersonClass = ucl.loadClass( "MultiPerson" );
        Class addressMultiPerson = ucl.loadClass( "AddressMultiPerson" );
        Class addressMultiPersonId = ucl.loadClass( "AddressMultiPersonId" );
        Class addressPerson = ucl.loadClass( "AddressPerson" );
        Class multiPersonIdClass = ucl.loadClass( "MultiPersonId" );
        Class middleClass = ucl.loadClass( "Middle" );
        Class rightClass = ucl.loadClass( "Left" );
        Class leftClass = ucl.loadClass( "Right" );
        try {
            Thread.currentThread().setContextClassLoader( ucl );
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass( personClass )
                    .addAnnotatedClass( multiPersonClass )
                    .addAnnotatedClass( addressMultiPerson )
                    .addAnnotatedClass( addressMultiPersonId )
                    .addAnnotatedClass( addressPerson )
                    .addAnnotatedClass( multiPersonIdClass )
                    .addAnnotatedClass( middleClass )
                    .addAnnotatedClass( rightClass )
                    .addAnnotatedClass( leftClass );

            configuration.buildMappings();
            new SchemaValidator( configuration ).validate();
        }
        finally {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }

    }

    private void assertPropertyNotExist(PersistentClass projectClass, String prop) {
        try {
            projectClass.getProperty( prop );
            fail( "property " + prop + " should not exist on " + projectClass );
        }
        catch ( MappingException e ) {
            // expected
        }
    }
    @Override
    protected String[] getCreateSQL() {
        return new String[] {
                // middle left and right are used to test a false association table isn't detected.
                "create table LEFT ( id integer not null, primary key (id) )",
                "create table RIGHT ( id integer not null, primary key (id) )",
                "create table MIDDLE ( left_id integer not null, right_id integer not null, primary key (left_id), constraint FK_MIDDLE_LEFT foreign key (left_id) references LEFT, constraint FK_MIDDLE_RIGHT foreign key (right_id) references RIGHT)",
                "create table PERSON ( person_id integer not null, name varchar(50), primary key (person_id) )",
                "create table ADDRESS_PERSON ( address_id integer not null, name varchar(50), primary key (address_id), constraint address_person foreign key (address_id) references PERSON)",
                "create table MULTI_PERSON ( person_id integer not null, person_compid integer not null, name varchar(50), primary key (person_id, person_compid) )",
                "create table ADDRESS_MULTI_PERSON ( address_id integer not null, address_compid integer not null, name varchar(50), primary key (address_id, address_compid), constraint address_multi_person foreign key (address_id, address_compid) references MULTI_PERSON)",

        };
    }
    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop table MIDDLE",
                "drop table LEFT",
                "drop table RIGHT",
                "drop table ADDRESS_PERSON",
                "drop table PERSON",
                "drop table ADDRESS_MULTI_PERSON",
                "drop table MULTI_PERSON",

        };
    }

}
