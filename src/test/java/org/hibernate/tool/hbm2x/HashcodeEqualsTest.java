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

import org.junit.Test;

import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.test.TestHelper;

import static org.junit.Assert.assertEquals;

/**
 * @author max
 */
public class HashcodeEqualsTest extends NonReflectiveTestCase {

    private ArtifactCollector artifactCollector;

     @org.junit.Before
    public void setUp() throws Exception {

        Exporter exporter = new POJOExporter( configuration(), serviceRegistry(), getOutputDir() );
        artifactCollector = new ArtifactCollector();
        exporter.setArtifactCollector( artifactCollector );
        exporter.start();
    }

    @Test
    public void testJDK5FailureExpectedOnJDK4() {

        POJOExporter exporter = new POJOExporter( configuration(), serviceRegistry(), getOutputDir() );
        exporter.getProperties().setProperty( "jdk5", "true" );

        artifactCollector = new ArtifactCollector();
        exporter.setArtifactCollector( artifactCollector );
        exporter.start();

        testFileExistence();
        testNoVelocityLeftOvers();
        testCompilable();

    }

    @Test
    public void testFileExistence() {

        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "org/hibernate/tool/hbm2x/HashEquals.java"
                )
        );
        assertFileAndExists(
                new File(
                        getOutputDir(),
                        "org/hibernate/tool/hbm2x/Address.java"
                )
        );

        assertEquals( 2, artifactCollector.getFileCount( "java" ) );
    }

    @Test
    public void testCompilable() {

        File file = new File(getOutputDir(), "compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        TestHelper.compile(
                getOutputDir(), file, TestHelper.visitAllFiles(
                getOutputDir(), list
        )
        );

        TestHelper.deleteDir( file );
    }


    @Test
    public void testNoVelocityLeftOvers() {

        assertEquals(
                null, findFirstString(
                "$", new File(
                getOutputDir(),
                "org/hibernate/tool/hbm2x/HashEquals.java"
        )
        )
        );
        assertEquals(
                null, findFirstString(
                "$", new File(
                getOutputDir(),
                "org/hibernate/tool/hbm2x/Address.java"
        )
        )
        );

    }
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
    @Override
    protected String[] getMappings() {
        return new String[] { "HashEquals.hbm.xml" };
    }

}
