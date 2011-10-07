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

import org.junit.Before;
import org.junit.Test;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.pojo.AnnotationBuilder;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass.IteratorTransformer;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.test.TestHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author max
 */
public class Hbm2JavaEjb3Test extends NonReflectiveTestCase {

   @Before
    public void setUp() throws Exception {

        generateComparator();

        POJOExporter exporter = new POJOExporter( configuration(), serviceRegistry(), getOutputDir() );
        exporter.setTemplatePath( new String[0] );
        exporter.getProperties().setProperty( "ejb3", "true" );
        exporter.getProperties().setProperty( "jdk5", "true" );

        exporter.start();
    }



    @Test
    public void testFileExistence() {
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/Author.java" ) );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/Article.java" ) );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/Train.java" ) );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/Passenger.java" ) );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/TransportationPk.java" ) );
//		assertFileAndExists( new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/GenericObject.java") );
//		assertFileAndExists( new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/GenericValue.java") );
    }

    @Test
    public void testBasicComponent() {
        assertEquals(
                "@Embeddable", findFirstString(
                "@Embeddable", new File(
                getOutputDir(),
                "org/hibernate/tool/hbm2x/TransportationPk.java"
        )
        )
        );

        //	assertEquals( null, findFirstString( "@Column", new File( getOutputDir(),
        //"org/hibernate/tool/hbm2x/TransportationPK.java" ) ) );

    }

    @Test
    public void testCompile() {

        File file = new File(getOutputDir(), "ejb3compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        jars.add( "hibernate-jpa-2.0-api.jar" );
        jars.add( "hibernate-core.jar" );
        TestHelper.compile(
                getOutputDir(),
                file,
                TestHelper.visitAllFiles( getOutputDir(), list ),
                "1.6",
                TestHelper.buildClasspath( jars )
        );

        TestHelper.deleteDir( file );
    }

    @Test
    public void testEqualsHashCode() {
        PersistentClass classMapping = configuration().getClassMapping( "org.hibernate.tool.hbm2x.Passenger" );
        POJOClass clazz = new Cfg2JavaTool().getPOJOClass( classMapping );

        assertFalse( clazz.needsEqualsHashCode() );

        classMapping = configuration().getClassMapping( "org.hibernate.tool.hbm2x.Article" );
        clazz = new Cfg2JavaTool().getPOJOClass( classMapping );

        assertTrue( clazz.needsEqualsHashCode() );

    }


    @Test
    public void testAnnotationColumnDefaults() {
        PersistentClass classMapping = configuration().getClassMapping( "org.hibernate.tool.hbm2x.Article" );
        Cfg2JavaTool cfg2java = new Cfg2JavaTool();
        POJOClass clazz = cfg2java.getPOJOClass( classMapping );

        Property p = classMapping.getProperty( "content" );

        String string = clazz.generateAnnColumnAnnotation( p );

        assertNotNull( string );
        assertEquals( -1, string.indexOf( "unique=" ) );
        assertTrue( string.indexOf( "nullable=" ) >= 0 );
        assertEquals( -1, string.indexOf( "insertable=" ) );
        assertEquals( -1, string.indexOf( "updatable=" ) );
        assertTrue( string.indexOf( "length=10000" ) > 0 );

        p = classMapping.getProperty( "name" );
        string = clazz.generateAnnColumnAnnotation( p );

        assertNotNull( string );
        assertEquals( -1, string.indexOf( "unique=" ) );
        assertTrue( string.indexOf( "nullable=" ) >= 0 );
        assertEquals( -1, string.indexOf( "insertable=" ) );
        assertTrue( string.indexOf( "updatable=false" ) > 0 );
        assertTrue( string.indexOf( "length=100" ) > 0 );


        classMapping = configuration().getClassMapping( "org.hibernate.tool.hbm2x.Train" );
        clazz = cfg2java.getPOJOClass( classMapping );

        p = classMapping.getProperty( "name" );
        string = clazz.generateAnnColumnAnnotation( p );
        assertNotNull( string );
        assertTrue( string.indexOf( "unique=true" ) > 0 );
        assertTrue( string.indexOf( "nullable=" ) >= 0 );
        assertEquals( -1, string.indexOf( "insertable=" ) );
        assertEquals( -1, string.indexOf( "updatable=" ) );
        assertEquals( -1, string.indexOf( "length=" ) );

    }

    @Test
    public void testEmptyCascade() {
        PersistentClass classMapping = configuration().getClassMapping( "org.hibernate.tool.hbm2x.Article" );

        Cfg2JavaTool cfg2java = new Cfg2JavaTool();
        EntityPOJOClass clazz = (EntityPOJOClass) cfg2java.getPOJOClass( classMapping );
        Property property = classMapping.getProperty( "author" );

        assertEquals( 0, clazz.getCascadeTypes( property ).length );

        assertEquals(
                null,
                findFirstString( "cascade={}", new File( getOutputDir(), "org/hibernate/tool/hbm2x/Article.java" ) )
        );
    }

    @Test
    public void testAnnotationBuilder() {

        AnnotationBuilder builder = AnnotationBuilder.createAnnotation( "SingleCleared" ).resetAnnotation( "Single" );

        assertEquals( "@Single", builder.getResult() );

        builder = AnnotationBuilder.createAnnotation( "javax.persistence.OneToMany" )
                .addAttribute( "willbecleared", (String) null )
                .resetAnnotation( "javax.persistence.OneToMany" )
                .addAttribute( "cascade", new String[] { "val1", "val2" } )
                .addAttribute( "fetch", "singleValue" );

        assertEquals( "@javax.persistence.OneToMany(cascade={val1, val2}, fetch=singleValue)", builder.getResult() );

        builder = AnnotationBuilder.createAnnotation( "javax.persistence.OneToMany" );
        builder.addAttribute( "cascade", (String[]) null );
        builder.addAttribute( "fetch", (String) null );

        assertEquals( "@javax.persistence.OneToMany", builder.getResult() );

        builder = AnnotationBuilder.createAnnotation( "abc" );
        ArrayList list = new ArrayList();
        list.add( new Integer( 42 ) );
        list.add( new String( "xxx" ) );
        builder.addQuotedAttributes( "it", list.iterator() );

        assertEquals( "@abc(it={\"42\", \"xxx\"})", builder.getResult() );

        List columns = new ArrayList();
        columns.add( "first" );
        columns.add( "second" );

        AnnotationBuilder constraint = AnnotationBuilder.createAnnotation( "UniqueConstraint" );
        constraint.addQuotedAttributes(
                "columnNames", new IteratorTransformer( columns.iterator() ) {
            public Object transform(Object object) {
                return object.toString();
            }
        }
        );
        constraint.addAttribute( "single", "value" );

        String attribute = constraint.getAttributeAsString( "columnNames" );
        assertEquals( "{\"first\", \"second\"}", attribute );

        assertEquals( "value", constraint.getAttributeAsString( "single" ) );

    }

   @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
    @Override
    protected String[] getMappings() {
        return new String[] {
                "Author.hbm.xml",
                "Article.hbm.xml",
                "Train.hbm.xml",
                "Passenger.hbm.xml"
//				"GenericModel.hbm.xml",
//				"Customer.hbm.xml",
//				"Order.hbm.xml",
//				"LineItem.hbm.xml",
//				"Product.hbm.xml"
        };
    }

}
