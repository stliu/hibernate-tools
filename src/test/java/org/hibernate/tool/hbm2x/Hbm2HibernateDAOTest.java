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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.test.TestHelper;

/**
 * @author max
 */
public class Hbm2HibernateDAOTest extends NonReflectiveTestCase {

    @org.junit.Before
    public void setUp() throws Exception {


        POJOExporter javaExporter = new POJOExporter( configuration(), serviceRegistry(), getOutputDir() );
        POJOExporter exporter = new DAOExporter( configuration(), serviceRegistry(), getOutputDir() );
        exporter.getProperties().setProperty( "ejb3", "false" );
        exporter.getProperties().setProperty( "jdk5", "true" );
        exporter.start();
        javaExporter.start();
    }

    @Test
    public void testFileExistence() {
        assertFileAndExists( new File( getOutputDir(), "org/hibernate/tool/hbm2x/ArticleHome.java" ) );
        assertFileAndExists( new File( getOutputDir(), "org/hibernate/tool/hbm2x/AuthorHome.java" ) );
    }

    @Test
    public void testCompilable() throws IOException {

        generateComparator();
        File file = new File(getOutputDir(), "compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        jars.add( "commons-logging-1.0.4.jar" );
        jars.add( "hibernate-core.jar" );
        TestHelper.compile(
                getOutputDir(),
                file,
                TestHelper.visitAllFiles( getOutputDir(), list ),
                "1.5",
                TestHelper.buildClasspath( jars )
        );


        TestHelper.deleteDir( file );
    }

    @Test
    public void testNoVelocityLeftOvers() {

        Assert.assertEquals(
                null,
                findFirstString( "$", new File( getOutputDir(), "org/hibernate/tool/hbm2x/ArticleHome.java" ) )
        );
        Assert.assertEquals(
                null,
                findFirstString( "$", new File( getOutputDir(), "org/hibernate/tool/hbm2x/AuthorHome.java" ) )
        );

    }
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
             @Override
    protected String[] getMappings() {
        return new String[] {
                "Article.hbm.xml",
                "Author.hbm.xml"
        };
    }

}
