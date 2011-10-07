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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.test.TestHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author max
 */
public class Hbm2JavaConstructorTest extends NonReflectiveTestCase {

    private ArtifactCollector artifactCollector;

    @Before
    public void setUp() throws Exception {

        Exporter exporter = new POJOExporter( configuration(), serviceRegistry(), getOutputDir() );
        artifactCollector = new ArtifactCollector();
        exporter.setArtifactCollector( artifactCollector );
        exporter.start();
    }

    @Test
    public void testCompilable() {

        File file = new File(getOutputDir(), "compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        list.add(
                new File( "src/testoutputdependent/ConstructorUsage.java" )
                        .getAbsolutePath()
        );
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
                "Company.java"
        )
        )
        );
        assertEquals(
                null, findFirstString(
                "$", new File(
                getOutputDir(),
                "BigCompany.java"
        )
        )
        );
        assertEquals(
                null, findFirstString(
                "$", new File(
                getOutputDir(),
                "EntityAddress.java"
        )
        )
        );

    }

    @Test
    public void testEntityConstructorLogic() {

        Cfg2JavaTool c2j = new Cfg2JavaTool();

        POJOClass company = c2j.getPOJOClass( configuration().getClassMapping( "Company" ) );

        List all = company.getPropertyClosureForFullConstructor();
        assertNoDuplicates( all );
        assertEquals( 3, all.size() );

        List superCons = company.getPropertyClosureForSuperclassFullConstructor();
        assertEquals( "company is a base class, should not have superclass cons", 0, superCons.size() );

        List subCons = company.getPropertiesForFullConstructor();
        assertNoDuplicates( subCons );
        assertEquals( 3, subCons.size() );

        assertNoOverlap( superCons, subCons );

        POJOClass bigCompany = c2j.getPOJOClass( configuration().getClassMapping( "BigCompany" ) );

        List bigsuperCons = bigCompany.getPropertyClosureForSuperclassFullConstructor();
        assertNoDuplicates( bigsuperCons );
        //assertEquals(3, bigsuperCons.size());

        List bigsubCons = bigCompany.getPropertiesForFullConstructor();

        assertEquals( 1, bigsubCons.size() );

        assertNoOverlap( bigsuperCons, bigsubCons );

        List bigall = bigCompany.getPropertyClosureForFullConstructor();
        assertNoDuplicates( bigall );
        assertEquals( 4, bigall.size() );

        PersistentClass classMapping = configuration().getClassMapping( "Person" );
        POJOClass person = c2j.getPOJOClass( classMapping );
        List propertiesForMinimalConstructor = person.getPropertiesForMinimalConstructor();
        assertEquals( 2, propertiesForMinimalConstructor.size() );
        assertFalse( propertiesForMinimalConstructor.contains( classMapping.getIdentifierProperty() ) );
        List propertiesForFullConstructor = person.getPropertiesForFullConstructor();
        assertEquals( 2, propertiesForFullConstructor.size() );
        assertFalse( propertiesForFullConstructor.contains( classMapping.getIdentifierProperty() ) );

    }

    @Test
    public void testSingleFieldLogic() {


    }


    @Test
    public void testMinimal() {
        POJOClass bp = new EntityPOJOClass( configuration().getClassMapping( "BrandProduct" ), new Cfg2JavaTool() );

        List propertiesForMinimalConstructor = bp.getPropertiesForMinimalConstructor();

        assertEquals( 1, propertiesForMinimalConstructor.size() );

        List propertiesForFullConstructor = bp.getPropertiesForFullConstructor();

        assertEquals( 2, propertiesForFullConstructor.size() );
    }

    private void assertNoDuplicates(List bigall) {
        Set set = new HashSet();
        set.addAll( bigall );

        assertEquals( "list had duplicates!", set.size(), bigall.size() );

    }

    private void assertNoOverlap(List first, List second) {
        Set set = new HashSet();
        set.addAll( first );
        set.addAll( second );

        assertEquals( set.size(), first.size() + second.size() );
    }
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
             @Override
    protected String[] getMappings() {
        return new String[] { "Constructors.hbm.xml" };
    }


}
