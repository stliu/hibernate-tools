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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.MappingException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author max
 */
public class ManyToManyJDK50Test extends JDBCMetaDataBinderTestCase {

    private JDBCMetaDataConfiguration localCfg;
    @Before
    public void setUp() throws Exception {
        localCfg = new JDBCMetaDataConfiguration();
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings( new ReverseEngineeringSettings( c ).setDetectManyToMany( false ) );
        localCfg.setReverseEngineeringStrategy( c );
        localCfg.readFromJDBC();
    }
    @After
    public void tearDown() throws Exception {
        localCfg = null;
    }

    @Test
    public void testNoManyToManyBiDirectional() {

        PersistentClass project = localCfg.getClassMapping( "Project" );

        assertNotNull( project.getProperty( "worksOns" ) );
        //assertNotNull(project.getProperty("employee"));
        assertEquals( 3, project.getPropertyClosureSpan() );
        assertEquals( "projectId", project.getIdentifierProperty().getName() );

        PersistentClass employee = localCfg.getClassMapping( "Employee" );

        assertNotNull( employee.getProperty( "worksOns" ) );
        assertNotNull( employee.getProperty( "employees" ) );
        assertNotNull( employee.getProperty( "employee" ) );
        //assertNotNull(employee.getProperty("projects"));
        assertEquals( 6, employee.getPropertyClosureSpan() );
        assertEquals( "id", employee.getIdentifierProperty().getName() );

        PersistentClass worksOn = localCfg.getClassMapping( "WorksOn" );

        assertNotNull( worksOn.getProperty( "project" ) );
        assertNotNull( worksOn.getProperty( "employee" ) );
        assertEquals( 2, worksOn.getPropertyClosureSpan() );
        assertEquals( "id", worksOn.getIdentifierProperty().getName() );
    }

    @Test
    public void testAutoCreation() {

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
        Property property2 = projectClass.getProperty( "employees" );
        assertNotNull( property2 );

        assertTrue( ( (Collection) property.getValue() ).isInverse() );
        assertFalse( ( (Collection) property2.getValue() ).isInverse() );

    }

    @Test
    public void testBuildMappings() {

        localCfg.buildMappings();
    }

    @Test
    public void testGenerateAndReadable() throws Exception {

        getJDBCMetaDataConfiguration().buildMappings();

        HibernateMappingExporter hme = new HibernateMappingExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        hme.start();

        getJDBCMetaDataConfiguration().buildMappings();
        POJOExporter exporter = new POJOExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        Properties p = new Properties();
        p.setProperty( "jdk5", "true" );
        p.setProperty( "ejb3", "true" );

        exporter.setProperties( p );
        exporter.start();

        File file = new File(getOutputDir(), "ejb3compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        addAnnotationJars( jars );

        new ExecuteContext( getOutputDir(), file, jars ) {

            protected void execute() throws Exception {
                Configuration configuration = new Configuration();
                configuration.addAnnotatedClass( getUcl().loadClass( "Project" ) );
                configuration.addAnnotatedClass( getUcl().loadClass( "Employee" ) );
                configuration.addAnnotatedClass( getUcl().loadClass( "WorksOnContext" ) );

                SessionFactory sf = configuration.buildSessionFactory( serviceRegistry() );
                Session s = sf.openSession();
                Query createQuery = s.createQuery( "from java.lang.Object" );
                createQuery.list();
                s.close();
                sf.close();

            }

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
                "create table PROJECT ( project_id integer not null, name varchar(50), primary key (project_id) )",
                "create table EMPLOYEE ( id integer not null, name varchar(50), manager_id integer, primary key (id), constraint employee_manager foreign key (manager_id) references EMPLOYEE)",
                "create table WORKS_ON ( project_id integer not null, employee_id integer not null, primary key (project_id, employee_id), constraint workson_employee foreign key (employee_id) references EMPLOYEE, foreign key (project_id) references PROJECT )",
                "create table WORKS_ON_CONTEXT ( project_id integer not null, employee_id integer not null, created_by integer, primary key (project_id, employee_id), constraint workson_ctx_employee foreign key (employee_id) references EMPLOYEE, foreign key (project_id) references PROJECT, foreign key (created_by) references EMPLOYEE )",
                //"alter  table PROJECT add constraint project_manager foreign key (team_lead) references EMPLOYEE"
        };
    }
    @Override
    protected String[] getDropSQL() {
        return new String[] {
                //"alter table PROJECT drop constraint project_manager",
                "drop table WORKS_ON_CONTEXT",
                "drop table WORKS_ON",
                "drop table EMPLOYEE",
                "drop table PROJECT",
        };
    }

}
