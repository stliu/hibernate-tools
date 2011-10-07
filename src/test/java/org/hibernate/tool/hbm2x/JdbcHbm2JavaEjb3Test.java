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

import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.test.TestHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author max
 */
public class JdbcHbm2JavaEjb3Test extends JDBCMetaDataBinderTestCase {

    @Before
    public void setUp() throws Exception {
        POJOExporter exporter = new POJOExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        exporter.setTemplatePath( new String[0] );
        exporter.getProperties().setProperty( "ejb3", "true" );
        exporter.getProperties().setProperty( "jdk5", "true" );

        exporter.start();
    }

    @Test
    public void testFileExistence() {
        assertFileAndExists( new File( getOutputDir().getAbsolutePath() + "/Master.java" ) );
    }

    @Test
    public void testUniqueConstraints() {
        assertEquals( null, findFirstString( "uniqueConstraints", new File( getOutputDir(), "Master.java" ) ) );
        assertNotNull( findFirstString( "uniqueConstraints", new File( getOutputDir(), "Uniquemaster.java" ) ) );
    }

    @Test
    public void testCompile() {

        File file = new File(getOutputDir(), "ejb3compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        jars.add( "hibernate-jpa-2.0-api.jar" );
        TestHelper.compile(
                getOutputDir(),
                file,
                TestHelper.visitAllFiles( getOutputDir(), list ),
                "1.5",
                TestHelper.buildClasspath( jars )
        );

        TestHelper.deleteDir( file );
    }

    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table master ( id char not null, name varchar(20), othername varchar(20), primary key (id) )",
                "create table uniquemaster ( id char not null, name varchar(20), othername varchar(20), primary key (id), constraint o1 unique (name), constraint o2 unique (othername) )",
        };
    }
    @Override
    protected String[] getDropSQL() {

        return new String[] {
                "drop table master",
                "drop table uniquemaster"
        };
    }
}
