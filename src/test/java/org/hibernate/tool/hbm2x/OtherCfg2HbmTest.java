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
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.NonReflectiveTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author max
 */
public class OtherCfg2HbmTest extends NonReflectiveTestCase {

    @org.junit.Before
    public void setUp() throws Exception {

        Exporter hbmexporter = new HibernateMappingExporter( configuration(), serviceRegistry(), getOutputDir() );

        hbmexporter.start();
    }

    @Test
    public void testFileExistence() {

        assertFileAndExists( new File( getOutputDir(), "org/hibernate/tool/hbm2x/Customer.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "org/hibernate/tool/hbm2x/LineItem.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "org/hibernate/tool/hbm2x/Order.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "org/hibernate/tool/hbm2x/Product.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "HelloWorld.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "HelloUniverse.hbm.xml" ) );
    }

    @Test
    public void testReadable() {
        Configuration cfg = new Configuration();

        cfg.addFile( new File( getOutputDir(), "org/hibernate/tool/hbm2x/Customer.hbm.xml" ) );
        cfg.addFile( new File( getOutputDir(), "org/hibernate/tool/hbm2x/LineItem.hbm.xml" ) );
        cfg.addFile( new File( getOutputDir(), "org/hibernate/tool/hbm2x/Order.hbm.xml" ) );
        cfg.addFile( new File( getOutputDir(), "org/hibernate/tool/hbm2x/Product.hbm.xml" ) );

        cfg.buildMappings();

    }

    @Test
    public void testNoVelocityLeftOvers() {

        assertEquals(
                null,
                findFirstString( "$", new File( getOutputDir(), "org/hibernate/tool/hbm2x/Customer.hbm.xml" ) )
        );
        assertEquals(
                null,
                findFirstString( "$", new File( getOutputDir(), "org/hibernate/tool/hbm2x/LineItem.hbm.xml" ) )
        );
        assertEquals(
                null,
                findFirstString( "$", new File( getOutputDir(), "org/hibernate/tool/hbm2x/Order.hbm.xml" ) )
        );
        assertEquals(
                null,
                findFirstString( "$", new File( getOutputDir(), "org/hibernate/tool/hbm2x/Product.hbm.xml" ) )
        );

    }

    @Test
    public void testVersioning() throws DocumentException {

        SAXReader xmlReader = this.getSAXReader();

        Document document = xmlReader.read( new File( getOutputDir(), "org/hibernate/tool/hbm2x/Product.hbm.xml" ) );

        XPath xpath = DocumentHelper.createXPath( "//hibernate-mapping/class/version" );
        List list = xpath.selectNodes( document );
        assertEquals( "Expected to get one version element", 1, list.size() );
    }
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
    @Override
    protected String[] getMappings() {
        return new String[] {
                "Customer.hbm.xml",
                "Order.hbm.xml",
                "LineItem.hbm.xml",
                "Product.hbm.xml",
                "HelloWorld.hbm.xml"
        };
    }

}
