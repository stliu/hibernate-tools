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
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.test.TestHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Josh Moore josh.moore@gmx.de
 */
public class PropertiesTest extends NonReflectiveTestCase {

    private ArtifactCollector artifactCollector;

    @org.junit.Before
    public void setUp() throws Exception {

        artifactCollector = new ArtifactCollector();

        Exporter exporter = new POJOExporter( configuration(), serviceRegistry(), getOutputDir() );
        exporter.setArtifactCollector( artifactCollector );

        Exporter hbmexporter = new HibernateMappingExporter( configuration(), serviceRegistry(), getOutputDir() );
        hbmexporter.setArtifactCollector( artifactCollector );

        exporter.start();
        hbmexporter.start();
    }

    @Test
    public void testNoGenerationOfEmbeddedPropertiesComponent() {
        assertEquals( 2, artifactCollector.getFileCount( "java" ) );
        assertEquals( 2, artifactCollector.getFileCount( "hbm.xml" ) );
    }

    @Test
    public void testGenerationOfEmbeddedProperties() {
        File outputXml = new File( getOutputDir(), "properties/PPerson.hbm.xml" );
        assertFileAndExists( outputXml );

        SAXReader xmlReader = this.getSAXReader();

        Document document;
        try {
            document = xmlReader.read( outputXml );
            XPath xpath = DocumentHelper.createXPath( "//hibernate-mapping/class/properties" );
            List list = xpath.selectNodes( document );
            assertEquals( "Expected to get one properties element", 1, list.size() );
            Element node = (Element) list.get( 0 );
            assertEquals( node.attribute( "name" ).getText(), "emergencyContact" );

            assertNotNull( findFirstString( "name", new File( getOutputDir(), "properties/PPerson.java" ) ) );
            assertEquals(
                    "Embedded component/properties should not show up in .java", null,
                    findFirstString( "emergencyContact", new File( getOutputDir(), "properties/PPerson.java" ) )
            );
        }
        catch ( DocumentException e ) {
            fail( "Can't parse file " + outputXml.getAbsolutePath() );
        }
    }

    @Test
    public void testCompilable() {

        File file = new File(getOutputDir(), "compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        list.add(
                new File( "src/testoutputdependent/properties/PropertiesUsage.java" )
                        .getAbsolutePath()
        );
        TestHelper.compile(
                getOutputDir(), file, TestHelper.visitAllFiles(
                getOutputDir(), list
        )
        );

        TestHelper.deleteDir( file );
    }
     @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
     @Override
    protected String[] getMappings() {
        return new String[] { "Properties.hbm.xml" };
    }


}
