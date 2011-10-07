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
 * Created on 13-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.tool.test.TestHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author max
 */
public class AntHibernateToolTest extends BuildFileTestCase {


    private static final Log log = LogFactory.getLog( AntHibernateToolTest.class );

    private String property;

    @After
    public void tearDown() throws Exception {
        cleanup();
    }

    @Before
    public void setUp() throws Exception {
        configureProject( "src/test/resources/anttest-build.xml" );
    }

    /**
     * Maximum number of attempts to make a clean.
     */
    private final short ATTEMPT_COUNT = 20;

    private void cleanup() {
        executeTarget( "afterCfg2hbm" );
        boolean removed = false;

        short attempt = 1;
        do {
            try {
                log.debug( "Attempt " + attempt + " for calling 'remmoveDirs'" );
                executeTarget( "removeDirs" );
                removed = true;
            }
            catch ( BuildException be ) {
                try {
                    Thread.currentThread().sleep( 500 );
                }
                catch ( InterruptedException e ) {
                }
                //Unable to delete file ...\testdb\hsqldb.log
                //if isn't removed calls
                //User SA not found for all the next tests.
            }
            attempt++;

            if ( attempt > ATTEMPT_COUNT && !removed ) {
                fail( "Could not complete cleanup. Next tests behaviour is unpredictable." );
            }
        } while ( !removed );
    }

    @Test
    public void testHbm2DDLLogic() {
        cleanupOutputDir();
        File baseDir = new File( project.getProperty( "build.dir" ), "topdown" );
        File onlyCreate = new File( baseDir, "onlycreate.sql" );
        File onlyDrop = new File( baseDir, "onlydrop.sql" );
        File dropAndCreate = new File( baseDir, "dropandcreate.sql" );
        File update = new File( baseDir, "update.sql" );

        assertFalse( onlyCreate.exists() );
        assertFalse( onlyDrop.exists() );
        assertFalse( dropAndCreate.exists() );
        assertFalse( update.exists() );

        // allow to test creation of script file + delimiter
        // + non execution (test will fail if executed because of crappy delimiter)
        executeTarget( "testScriptCreation" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        assertTrue( onlyCreate.exists() );
        assertTrue( onlyDrop.exists() );
        assertTrue( dropAndCreate.exists() );
        assertTrue( update.exists() );

        assertNotNull( TestHelper.findFirstString( "drop", dropAndCreate ) );
        assertNotNull( TestHelper.findFirstString( "create", dropAndCreate ) );
        assertNotNull( TestHelper.findFirstString( "---", dropAndCreate ) );

        assertEquals( null, TestHelper.findFirstString( "create", onlyDrop ) );
        assertNotNull( TestHelper.findFirstString( "drop", onlyDrop ) );

        assertEquals( null, TestHelper.findFirstString( "drop", onlyCreate ) );
        assertNotNull( TestHelper.findFirstString( "create", onlyCreate ) );
        assertNotNull( TestHelper.findFirstString( "---", onlyCreate ) );

        assertNotNull( TestHelper.findFirstString( "create", update ) );
        assertNotNull( TestHelper.findFirstString( "---", update ) );

        onlyCreate.delete();
        onlyDrop.delete();
        dropAndCreate.delete();
        update.delete();
    }

    @Test
    public void testHbm2DDLUpdateExecution() {
        cleanupOutputDir();
        File baseDir = new File( project.getProperty( "build.dir" ), "topdown" );
        File update1 = new File( baseDir, "update1.sql" );
        File update2 = new File( baseDir, "update2.sql" );
        File onlydrop = new File( baseDir, "onlydrop.sql" );

        assertFalse( update1.exists() );
        assertFalse( update2.exists() );
        assertFalse( onlydrop.exists() );


        executeTarget( "testantcfgUpdateExecuted" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        assertTrue( update1.exists() );
        assertTrue( update2.exists() );

        assertNotNull( TestHelper.findFirstString( "create", update1 ) );
        // if first update is executed, the second should be empty
        assertEquals( 0, update2.length() );

        update1.delete();
        update2.delete();
        onlydrop.delete();
    }

    @Test
    public void testHbm2DDLExportExecution() {
        cleanupOutputDir();
        File baseDir = new File( project.getProperty( "build.dir" ), "topdown" );
        File export = new File( baseDir, "export.sql" );
        File update = new File( baseDir, "update.sql" );
        File onlydrop = new File( baseDir, "onlydrop.sql" );

        assertFalse( export.exists() );
        assertFalse( update.exists() );
        assertFalse( onlydrop.exists() );


        executeTarget( "testantcfgExportExecuted" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        assertTrue( export.exists() );
        assertTrue( update.exists() );

        assertNotNull( TestHelper.findFirstString( "create", export ) );
        // if export is executed, update should be empty
        assertEquals( 0, update.length() );

        export.delete();
        update.delete();
        onlydrop.delete();
    }

    @Test
    public void testJDBCConfiguration() {
        executeTarget( "testantjdbccfg" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testAnnotationConfigurationFailureExpected() {
        executeTarget( "testantannotationcfg" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testEJB3ConfigurationFailureExpected() {
        executeTarget( "testantejb3cfg" );
        File baseDir = new File( project.getProperty( "build.dir" ) );
        File ejb3 = new File( baseDir, "ejb3.sql" );

        assertTrue( ejb3.exists() );
        assertEquals( null, TestHelper.findFirstString( "drop", ejb3 ) );

        assertTrue( getLog().indexOf( "<ejb3configuration> is deprecated" ) > 0 );
    }

    @Test
    public void testJPABogusPUnit() {
        try {
            executeTarget( "jpa-boguspunit" );
            fail( "Bogus unit accepted" );
        }
        catch ( BuildException be ) {
            assertTrue( getLog(), getLog().contains( "Persistence unit not found: 'shouldnotbethere'" ) );
        }
    }

    @Test
    public void testJPAPUnit() {
        executeTarget( "jpa-punit" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testJPAPropertyOveridesPUnit() {
        try {
            executeTarget( "jpa-overrides" );
            fail( "property overrides not accepted" );
        }
        catch ( BuildException be ) {
            // should happen
            assertTrue( be.getMessage(), be.getMessage().indexOf( "FAKEDialect" ) > 0 );
        }
    }

    @Test
    public void testHbm2JavaConfiguration() {
        executeTarget( "testanthbm2java" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testHbm2JavaEJB3Configuration() {
        executeTarget( "testantejb3hbm2java" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testCfg2HbmNoError() {
        executeTarget( "testantcfg2hbm1" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testCfg2HbmWithCustomReverseNamingStrategy() {
        executeTarget( "testantcfg2hbm2" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testCfg2HbmWithInvalidReverseNamingStrategy() {
        expectSpecificBuildException(
                "testantcfg2hbm3",
                "namingStrategy attribute should not be loaded",
                "Could not create or find invalid.classname with one argument delegate constructor"
        );
    }

    @Test
    public void testCfg2HbmWithPackageName() {
        executeTarget( "testantcfg2hbm4" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testCfg2HbmWithPackageNameAndReverseNamingStrategy() {
        executeTarget( "testantcfg2hbm5" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testJDBCConfigWithRevEngXml() {
        executeTarget( "testantjdbccfgoverrides" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testProperties() {
        executeTarget( "testproperties" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
    }

    @Test
    public void testGenericExport() {
        executeTarget( "testgeneric" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        property = project.getProperty( "build.dir" );
        assertTrue( new File( property, "generic" ).exists() );
        assertTrue( new File( property, "generic/org/hibernate/tool/hbm2x/ant/TopDown.quote" ).exists() );
    }

    @Test
    public void testNoConnInfoExport() {
        executeTarget( "noconinfoexport" );
        File baseDir = new File( project.getProperty( "build.dir" ), "noconinfo" );
        File onlyCreate = new File( baseDir, "noconinfo.sql" );

        assertTrue( onlyCreate.toString() + " should exist", onlyCreate.exists() );

        assertTrue( onlyCreate.length() > 0 );

        assertNotNull( TestHelper.findFirstString( "create", onlyCreate ) );


    }

    @Test
    public void testNoExporters() {
        try {
            executeTarget( "testnoexporters" );
            fail( "should have failed with no exporters!" );
        }
        catch ( BuildException be ) {
            // should happen!
            assertTrue( be.getMessage().indexOf( "No exporters specified" ) >= 0 );
        }

    }

    @Test
    public void testException() {
        try {
            executeTarget( "testexceptions" );
            fail( "should have failed with an exception!" );
        }
        catch ( BuildException be ) {
            // should happen!
        }
    }

    @Test
    public void testQuery() {
        executeTarget( "testquery" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        File baseDir = new File( project.getProperty( "build.dir" ), "querytest" );

        assertTrue( new File( baseDir, "queryresult.txt" ).exists() );

    }

    @Test
    public void testHbmLint() {
        executeTarget( "testhbmlint" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        File baseDir = new File( project.getProperty( "build.dir" ), "linttest" );

        assertTrue( new File( baseDir, "hbmlint-result.txt" ).exists() );

    }

    @Test
    public void testNoConfig() {
        try {
            executeTarget( "noconfig-shoulderror" );
        }
        catch ( BuildException e ) {
            assertTrue( e.getMessage().indexOf( "No configuration specified" ) >= 0 );
        }

    }

}
