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
 * Created on 07-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.sql.SQLException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.xml.DTDEntityResolver;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.test.TestHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * @author max
 */
public class GenerateFromJDBCTest extends JDBCMetaDataBinderTestCase {

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table master ( id char not null, name varchar(20), primary key (id) )",
                "create table child  ( childid char not null, masterref char, primary key (childid), foreign key (masterref) references master(id) )"
        };
    }
    @Override
    protected String[] getDropSQL() {

        return new String[] {
                "drop table child",
                "drop table master",
        };
    }
    @Override
    protected void configure(JDBCMetaDataConfiguration cfg2configure) {

        DefaultReverseEngineeringStrategy configurableNamingStrategy = new DefaultReverseEngineeringStrategy();
        configurableNamingStrategy.setSettings(
                new ReverseEngineeringSettings( configurableNamingStrategy ).setDefaultPackageName(
                        "org.reveng"
                ).setCreateCollectionForForeignKey( false )
        );
        cfg2configure.setReverseEngineeringStrategy( configurableNamingStrategy );
    }

    @Test
    public void testGenerateJava() throws SQLException, ClassNotFoundException {

        POJOExporter exporter = new POJOExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        exporter.start();

        exporter = new POJOExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        exporter.getProperties().setProperty( "ejb3", "true" );
        exporter.start();
    }

    @Test
    public void testGenerateMappings() {
        Exporter exporter = new HibernateMappingExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        exporter.start();
        assertFileAndExists( new File( getOutputDir(), "org/reveng/Child.hbm.xml" ) );
        File file = new File( getOutputDir(), "GeneralHbmSettings.hbm.xml" );
        assertTrue( file + " should not exist", !file.exists() );
        Configuration derived = new Configuration();

        derived.addFile( new File( getOutputDir(), "org/reveng/Child.hbm.xml" ) );
        derived.addFile( new File( getOutputDir(), "org/reveng/Master.hbm.xml" ) );

        derived.buildMappings();

        assertNotNull( derived.getClassMapping( "org.reveng.Child" ) );
        assertNotNull( derived.getClassMapping( "org.reveng.Master" ) );
        TestHelper.deleteDir( getOutputDir() );
    }

    @Test
    public void testGenerateCfgXml() throws DocumentException {

        Exporter exporter = new HibernateConfigurationExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );

        exporter.start();

        assertFileAndExists( new File( getOutputDir(), "hibernate.cfg.xml" ) );

        SAXReader xmlReader = this.getSAXReader();

        Document document = xmlReader.read( new File( getOutputDir(), "hibernate.cfg.xml" ) );

        // Validate the Generator and it has no arguments
        XPath xpath = DocumentHelper.createXPath( "//hibernate-configuration/session-factory/mapping" );
        Element[] elements = (Element[]) xpath.selectNodes( document ).toArray( new Element[0] );
        assertEquals( 2, elements.length );

        for ( int i = 0; i < elements.length; i++ ) {
            Element element = elements[i];
            assertNotNull( element.attributeValue( "resource" ) );
            assertNull( element.attributeValue( "class" ) );
        }
    }

    @Test
    public void testGenerateAnnotationCfgXml() throws DocumentException {

        HibernateConfigurationExporter exporter = new HibernateConfigurationExporter(
                getJDBCMetaDataConfiguration(),
                serviceRegistry(),
                getOutputDir()
        );

        exporter.getProperties().setProperty( "ejb3", "true" );

        exporter.start();


        assertFileAndExists( new File( getOutputDir(), "hibernate.cfg.xml" ) );

        SAXReader xmlReader = this.getSAXReader();

        Document document = xmlReader.read( new File( getOutputDir(), "hibernate.cfg.xml" ) );

        // Validate the Generator and it has no arguments
        XPath xpath = DocumentHelper.createXPath( "//hibernate-configuration/session-factory/mapping" );
        Element[] elements = (Element[]) xpath.selectNodes( document ).toArray( new Element[0] );
        assertEquals( 2, elements.length );

        for ( int i = 0; i < elements.length; i++ ) {
            Element element = elements[i];
            assertNull( element.attributeValue( "resource" ) );
            assertNotNull( element.attributeValue( "class" ) );
        }
    }

    private SAXReader getSAXReader() {
        SAXReader xmlReader = new SAXReader();
        xmlReader.setEntityResolver( new DTDEntityResolver() );
        xmlReader.setValidation( true );
        return xmlReader;
    }

    @Test
    public void testGenerateDoc() {

        DocExporter exporter = new DocExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );

        exporter.start();

        TestHelper.deleteDir( getOutputDir() );

    }

    @Test
    public void testPackageNames() {
        Iterator iter = getJDBCMetaDataConfiguration().getClassMappings();
        while ( iter.hasNext() ) {
            PersistentClass element = (PersistentClass) iter.next();
            assertEquals( "org.reveng", StringHelper.qualifier( element.getClassName() ) );
        }
    }
}
