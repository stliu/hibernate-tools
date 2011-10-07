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
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.tool.ide.formatting.JavaFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author max
 */
public class JavaFormatterTest extends BuildFileTestCase {
    @After
    public void tearDown() throws Exception {
        executeTarget( "cleanup" );
    }
          @Before
    public void setUp() throws Exception {
        configureProject( "src/test/resources/javaformattest-build.xml" );
    }

    @Test
    public void testJava() {

        executeTarget( "prepare" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        File file = new File( project.getProperty( "build.dir" ), "formatting/SimpleOne.java" );
        assertFileAndExists( file );
        long before = file.lastModified();

        waitASec();

        JavaFormatter formatter = new JavaFormatter( null );
        formatter.formatFile( file );

        assertTrue( before != file.lastModified() );

    }

    @Test
    public void testJavaJdk5() {

        executeTarget( "prepare" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        File file = new File( project.getProperty( "build.dir" ), "formatting/Simple5One.java5" );
        assertFileAndExists( file );
        long before = file.lastModified();

        JavaFormatter formatter = new JavaFormatter( new HashMap() );
        assertFalse( "formatting should fail when using zero settings", formatter.formatFile( file ) );

        assertTrue( before == file.lastModified() );

        waitASec();

        executeTarget( "prepare" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        formatter = new JavaFormatter( null );
        assertTrue( "formatting should pass when using default settings", formatter.formatFile( file ) );


        assertTrue( before < file.lastModified() );
    }

    private void waitASec() {
        try {
            Thread.sleep( 1000 );
        }
        catch ( InterruptedException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testAntxDestDir() {

        executeTarget( "prepare" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        File file = new File( project.getProperty( "build.dir" ), "formatting/SimpleOne.java" );
        assertFileAndExists( file );
        long before = file.lastModified();
        waitASec();
        executeTarget( "fileset" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
        assertTrue( before != file.lastModified() );
    }

    @Test
    public void testConfig() {

        executeTarget( "prepare" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        File jdk5file = new File( project.getProperty( "build.dir" ), "formatting/Simple5One.java5" );
        File jdkfile = new File( project.getProperty( "build.dir" ), "formatting/SimpleOne.java" );
        assertFileAndExists( jdkfile );
        long jdk5before = jdk5file.lastModified();
        long before = jdkfile.lastModified();
        waitASec();
        executeTarget( "configtest" );
        assertTrue( getLog(), checkLogWithoutExceptions() );

        assertEquals( "jdk5 should fail since config is not specifying jdk5", jdk5before, jdk5file.lastModified() );
        assertTrue( before < jdkfile.lastModified() );

        executeTarget( "noconfigtest" );
        assertTrue( getLog(), checkLogWithoutExceptions() );
        assertTrue( jdk5before < jdk5file.lastModified() );
        assertTrue( before < jdk5file.lastModified() );


    }


}
