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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.Version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author max
 */
public class GenericExporterTest extends NonReflectiveTestCase {
    @Before
    public void setUp() throws Exception {
        prepareTest();
    }
    @After
    public void tearDown(){
        cleanupOutputDir();
    }


    @Test
    public void testSingleFileGeneration() {

        GenericExporter ge = new GenericExporter();
        ge.setConfiguration( configuration() );
        ge.setOutputDirectory( getOutputDir() );
        ge.setTemplateName( "generictemplates/pojo/generic-test.ftl" );
        ge.setFilePattern( "generictest.txt" );
        ge.start();

        assertFileAndExists( new File( getOutputDir(), "artifacts.txt" ) );

        assertFileAndExists( new File( getOutputDir(), "templates.txt" ) );

        assertEquals(
                null, findFirstString(
                "$", new File(
                getOutputDir(),
                "artifacts.txt"
        )
        )
        );
        String version = findFirstString(
                "artifacts", new File(
                getOutputDir(),
                "artifacts.txt"
        )
        );
        assertEquals(
                "File for artifacts in " + Version.getDefault().getVersion(), version
        );

    }

    /*@Test public void testFreeMarkerSyntaxFailureExpected() {

         GenericExporter ge = new GenericExporter();
         ge.setConfiguration(configuration());
         ge.setOutputDirectory(getOutputDir());
         ge.setTemplateName("generictemplates/freemarker.ftl");
         ge.setFilePattern("{class-name}.ftltest");
         ge.start();

     }*/
    @Test
    public void testClassFileGeneration() {

        GenericExporter ge = new GenericExporter();
        ge.setConfiguration( configuration() );
        ge.setOutputDirectory( getOutputDir() );
        ge.setTemplateName( "generictemplates/pojo/generic-class.ftl" );
        ge.setFilePattern( "generic{class-name}.txt" );
        ge.start();

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "genericAuthor.txt"
                )
        );

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "genericArticle.txt"
                )
        );
    }

    @Test
    public void testPackageFileGeneration() {

        GenericExporter ge = new GenericExporter();
        ge.setConfiguration( configuration() );
        ge.setOutputDirectory( getOutputDir() );
        ge.setTemplateName( "generictemplates/pojo/generic-class.ftl" );
        ge.setFilePattern( "{package-name}/generic{class-name}.txt" );
        ge.start();

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "org/hibernate/tool/hbm2x/genericAuthor.txt"
                )
        );

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "org/hibernate/tool/hbm2x/genericArticle.txt"
                )
        );

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "org/hibernate/tool/hbm2x/genericArticle.txt"
                )
        );

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "genericUniversalAddress.txt"
                )
        );

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "genericHelloUniverse.txt"
                )
        );
    }

    @Test
    public void testForEachGeneration() {

        GenericExporter ge = new GenericExporter();
        ge.setConfiguration( configuration() );
        ge.setOutputDirectory( getOutputDir() );
        ge.setTemplateName( "generictemplates/pojo/generic-class.ftl" );
        ge.setFilePattern( "{package-name}/generic{class-name}.txt" );
        ge.setForEach( "entity" );
        ge.start();

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "org/hibernate/tool/hbm2x/genericAuthor.txt"
                )
        );

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "org/hibernate/tool/hbm2x/genericArticle.txt"
                )
        );

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "org/hibernate/tool/hbm2x/genericArticle.txt"
                )
        );

        assertFalse(
                "component file should not exist",
                new File( getOutputDir(), "genericUniversalAddress.txt" ).exists()
        );


        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "genericHelloUniverse.txt"
                )
        );


        try {
            ge.setForEach( "does, not, exist" );
            ge.start();
            fail();
        }
        catch ( Exception e ) {
            //e.printStackTrace();
            //expected
        }
    }

    @Test
    public void testForEachWithExceptionGeneration() {

        GenericExporter ge = new GenericExporter();
        ge.setConfiguration( configuration() );
        ge.setOutputDirectory( getOutputDir() );
        ge.setTemplateName( "generictemplates/generic-exception.ftl" );
        ge.setFilePattern( "{package-name}/generic{class-name}.txt" );

        try {
            ge.setForEach( "entity" );
            ge.start();
            fail();
        }
        catch ( ExporterException e ) {
            assertTrue( e.getMessage().startsWith( "Error while processing Entity:" ) );
        }


        try {
            ge.setForEach( "component" );
            ge.start();
            fail();
        }
        catch ( ExporterException e ) {
            assertTrue( e.getMessage().startsWith( "Error while processing Component: UniversalAddress" ) );
        }

        try {
            ge.setForEach( "configuration" );
            ge.start();
            fail();
        }
        catch ( ExporterException e ) {
            assertTrue( e.getMessage().startsWith( "Error while processing Configuration" ) );
        }


    }

    @Test
    public void testPropertySet() throws FileNotFoundException, IOException {
        GenericExporter ge = new GenericExporter();
        ge.setConfiguration( configuration() );
        ge.setOutputDirectory( getOutputDir() );
        Properties p = new Properties();
        p.setProperty( "proptest", "A value" );
        p.setProperty( "refproperty", "proptest=${proptest}" );
        p.setProperty( "hibernatetool.booleanProperty", "true" );
        p.setProperty( "hibernatetool.myTool.toolclass", "org.hibernate.tool.hbm2x.Cfg2JavaTool" );

        ge.setProperties( p );
        ge.setTemplateName( "generictemplates/pojo/generic-class.ftl" );
        ge.setFilePattern( "{package-name}/generic{class-name}.txt" );
        ge.start();

        Properties generated = new Properties();
        generated.load(
                new FileInputStream(
                        new File(
                                getOutputDir(),
                                "org/hibernate/tool/hbm2x/genericArticle.txt"
                        )
                )
        );

        assertEquals( generated.getProperty( "booleanProperty" ), "true" );
        assertEquals( generated.getProperty( "hibernatetool.booleanProperty" ), "true" );
        assertNull( generated.getProperty( "booleanWasTrue" ) );
        assertEquals( generated.getProperty( "myTool.value" ), "value" );
        assertEquals( generated.getProperty( "refproperty" ), "proptest=A value" );

    }

    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }

    @Override
    protected String[] getMappings() {
        return new String[] { "Author.hbm.xml", "Article.hbm.xml", "HelloWorld.hbm.xml" };
    }
}
