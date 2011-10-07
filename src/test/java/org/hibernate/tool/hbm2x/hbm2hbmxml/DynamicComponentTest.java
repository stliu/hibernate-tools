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
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

/**
 * @author Dmitry Geraskov
 */
public class DynamicComponentTest extends NonReflectiveTestCase {

    private String mappingFile = "Glarch.hbm.xml";

    private Exporter hbmexporter;

    @Override
    protected String[] getMappings() {
        return new String[] {
                mappingFile
        };
    }
    @Before
    public void setUp() throws Exception {

        hbmexporter = new HibernateMappingExporter( configuration(), serviceRegistry(), getOutputDir() );
        hbmexporter.start();
    }

    @Test
    public void testAllFilesExistence() {
        assertFileAndExists( new File( getOutputDir().getAbsolutePath(), getBaseForMappings() + "Fee.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath(), getBaseForMappings() + "Glarch.hbm.xml" ) );
    }

    @Test
    public void testReadable() {
        Configuration cfg = new Configuration();

        cfg.addFile( new File( getOutputDir(), getBaseForMappings() + "Fee.hbm.xml" ) );
        cfg.addFile( new File( getOutputDir(), getBaseForMappings() + "Glarch.hbm.xml" ) );

        cfg.buildMappings();
    }

    @Test
    public void testClassProxy() throws DocumentException {
        File outputXml = new File( getOutputDir(), getBaseForMappings() + "Glarch.hbm.xml" );
        assertFileAndExists( outputXml );

        SAXReader xmlReader = getSAXReader();

        Document document = xmlReader.read( outputXml );

        XPath xpath = DocumentHelper.createXPath( "//hibernate-mapping/class" );
        List list = xpath.selectNodes( document );
        assertEquals( "Expected to get one class element", 1, list.size() );
        Element node = (Element) list.get( 0 );
        assertEquals( node.attribute( "proxy" ).getText(), "org.hibernate.tool.hbm2x.hbm2hbmxml.GlarchProxy" );
    }

    @Test
    public void testDynamicComponentNode() throws DocumentException {
        File outputXml = new File( getOutputDir(), getBaseForMappings() + "Glarch.hbm.xml" );
        assertFileAndExists( outputXml );

        SAXReader xmlReader = getSAXReader();

        Document document = xmlReader.read( outputXml );

        XPath xpath = DocumentHelper.createXPath( "//hibernate-mapping/class/dynamic-component" );
        List list = xpath.selectNodes( document );
        assertEquals( "Expected to get one dynamic-component element", 1, list.size() );
        Element node = (Element) list.get( 0 );
        assertEquals( node.attribute( "name" ).getText(), "dynaBean" );

    }
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
    }

}
