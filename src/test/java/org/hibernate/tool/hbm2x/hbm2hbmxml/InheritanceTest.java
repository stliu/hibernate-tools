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

//$Id$

/*
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * this test should be fixed to have a proper model. currently a mix of subclass/joinedsubclass is in play.
 *
 * @author max
 */
public class InheritanceTest extends NonReflectiveTestCase {

    private Exporter hbmexporter;

    @Before
    public void setUp() throws Exception {

        hbmexporter = new HibernateMappingExporter( configuration(), serviceRegistry(), getOutputDir() );
        hbmexporter.start();
    }

    @Test
    public void testAllFilesExistence() {

        assertFalse( new File( getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml" ).exists() );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Human.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Alien.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Animal.hbm.xml" ) );
    }

    @Test
    public void testArtifactCollection() {

        assertEquals( 3, hbmexporter.getArtifactCollector().getFileCount( "hbm.xml" ) );

    }

    @Test
    public void testReadable() {
        Configuration cfg = new Configuration();

        cfg.addFile( new File( getOutputDir(), getBaseForMappings() + "Alien.hbm.xml" ) );
        cfg.addFile( new File( getOutputDir(), getBaseForMappings() + "Human.hbm.xml" ) );
        cfg.addFile( new File( getOutputDir(), getBaseForMappings() + "Animal.hbm.xml" ) );

        cfg.buildMappings();
    }

    @Test
    public void testComment() {
        File outputXml = new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Alien.hbm.xml" );
        assertFileAndExists( outputXml );

        SAXReader xmlReader = this.getSAXReader();

        Document document;
        try {
            document = xmlReader.read( outputXml );
            XPath xpath = DocumentHelper.createXPath( "//hibernate-mapping/joined-subclass/comment" );
            List list = xpath.selectNodes( document );
            assertEquals( "Expected to get one comment element", 1, list.size() );
        }
        catch ( DocumentException e ) {
            fail( "Can't parse file " + outputXml.getAbsolutePath() );
        }
    }

    @Test
    public void testDiscriminator() throws DocumentException {
        File outputXml = new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Animal.hbm.xml" );
        assertFileAndExists( outputXml );

        SAXReader xmlReader = this.getSAXReader();

        Document document = xmlReader.read( outputXml );
        XPath xpath = DocumentHelper.createXPath( "//hibernate-mapping/class/discriminator" );
        List list = xpath.selectNodes( document );
        assertEquals( "Expected to get one discriminator element", 1, list.size() );

        Element node = (Element) list.get( 0 );
        assertEquals( node.attribute( "type" ).getText(), "string" );

    }
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
    }
    @Override
    protected String[] getMappings() {
        return new String[] {
                "Aliens.hbm.xml"
        };
    }


}
