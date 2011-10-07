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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author max
 */
public class ManyToManyTest extends JDBCMetaDataBinderTestCase {


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
        assertNotNull( projectClass.getProperty( "employees" ) );

    }

    @Test
    public void testFalsePositive() {

        assertNotNull( "Middle class should be generated.", getJDBCMetaDataConfiguration().getClassMapping( "NonMiddle" ) );


    }

    @Test
    public void testBuildMappings() {

        localCfg.buildMappings();
    }

    @Test
    public void testGenerateAndReadable() {

        getJDBCMetaDataConfiguration().buildMappings();

        HibernateMappingExporter hme = new HibernateMappingExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        hme.start();

        assertFileAndExists( new File( getOutputDir(), "Employee.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "Project.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "WorksOnContext.hbm.xml" ) );

        assertFileAndExists( new File( getOutputDir(), "Right.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "Left.hbm.xml" ) );
        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "NonMiddle.hbm.xml"
                )
        ); //Must be there since it has a fkey that is not part of the pk

        assertFalse( new File( getOutputDir(), "WorksOn.hbm.xml" ).exists() );

        assertEquals( 6, getOutputDir().listFiles().length );

        Configuration configuration = new Configuration()
                .addFile( new File( getOutputDir(), "Employee.hbm.xml" ) )
                .addFile( new File( getOutputDir(), "Project.hbm.xml" ) )
                .addFile( new File( getOutputDir(), "WorksOnContext.hbm.xml" ) );

        configuration.buildMappings();

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
                "create table EMPLOYEE ( id integer not null, name varchar(50), manager_id integer, primary key (id), constraint employee_manager foreign key (manager_id) references EMPLOYEE(id))",
                "create table WORKS_ON ( project_id integer not null, employee_id integer not null, primary key (project_id, employee_id), constraint workson_employee foreign key (employee_id) references EMPLOYEE(id), foreign key (project_id) references PROJECT(project_id) )",
                "create table WORKS_ON_CONTEXT ( project_id integer not null, employee_id integer not null, created_by integer, primary key (project_id, employee_id), constraint workson_ctx_employee foreign key (employee_id) references EMPLOYEE, foreign key (project_id) references PROJECT(project_id), foreign key (created_by) references EMPLOYEE(id) )",
                //"alter  table PROJECT add constraint project_manager foreign key (team_lead) references EMPLOYEE"
                // nonmiddle left and right are used to test a false association table isn't detected.
                "create table LEFT ( id integer not null, primary key (id) )",
                "create table RIGHT ( id integer not null, primary key (id) )",
                "create table NON_MIDDLE ( left_id integer not null, right_id integer not null, primary key (left_id), constraint FK_MIDDLE_LEFT foreign key (left_id) references LEFT(id), constraint FK_MIDDLE_RIGHT foreign key (right_id) references RIGHT(id))",
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
                "drop table NON_MIDDLE",
                "drop table LEFT",
                "drop table RIGHT",
        };
    }

}
