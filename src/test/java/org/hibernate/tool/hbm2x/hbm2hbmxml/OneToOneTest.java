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


public class OneToOneTest extends NonReflectiveTestCase {

    private Exporter hbmexporter;

    @Before
    public void setUp() throws Exception {

        hbmexporter = new HibernateMappingExporter( configuration(), serviceRegistry(), getOutputDir() );
        hbmexporter.start();
    }

    @Test
    public void testAllFilesExistence() {

        assertFalse( new File( getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml" ).exists() );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Person.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Address.hbm.xml" ) );
    }

    @Test
    public void testArtifactCollection() {

        assertEquals( 2, hbmexporter.getArtifactCollector().getFileCount( "hbm.xml" ) );

    }

    @Test
    public void testReadable() {
        Configuration cfg = new Configuration();

        cfg.addFile( new File( getOutputDir(), getBaseForMappings() + "Person.hbm.xml" ) );
        cfg.addFile( new File( getOutputDir(), getBaseForMappings() + "Address.hbm.xml" ) );

        cfg.buildMappings();


    }

    @Test
    public void testOneToOne() throws DocumentException {
        Document document = getXMLDocument( getBaseForMappings() + "Person.hbm.xml" );

        XPath xpath = DocumentHelper.createXPath( "//hibernate-mapping/class/one-to-one" );
        List list = xpath.selectNodes( document );
        assertEquals( "Expected to get one-to-one element", 1, list.size() );
        Element node = (Element) list.get( 0 );
        assertEquals( node.attribute( "name" ).getText(), "address" );
        assertEquals( node.attribute( "constrained" ).getText(), "false" );

        document = getXMLDocument( getBaseForMappings() + "Address.hbm.xml" );

        xpath = DocumentHelper.createXPath( "//hibernate-mapping/class/one-to-one" );
        list = xpath.selectNodes( document );
        assertEquals( "Expected to get one set element", 1, list.size() );
        node = (Element) list.get( 0 );
        assertEquals( node.attribute( "name" ).getText(), "person" );
        assertEquals( node.attribute( "constrained" ).getText(), "true" );
        assertEquals( node.attribute( "access" ).getText(), "field" );

    }

    private Document getXMLDocument(String location) throws DocumentException {
        File outputXml = new File( getOutputDir(), location );
        assertFileAndExists( outputXml );

        SAXReader xmlReader = this.getSAXReader();

        Document document = xmlReader.read( outputXml );
        return document;
    }
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
    }
     @Override
    protected String[] getMappings() {
        return new String[] {
                "PersonAddressOneToOnePrimaryKey.hbm.xml"
        };
    }

}
