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
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;


import org.junit.Test;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.type.IntegerType;
import org.hibernate.type.TimestampType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * To be extended by VersioningForJDK50Test for the JPA generation part
 *
 * @author max
 */
public class VersioningTest extends JDBCMetaDataBinderTestCase {
    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create table withVersion (first int, second int, version int, name varchar, primary key (first))",
                "create table noVersion (first int, second int, name varchar, primary key (second))",
                "create table withRealTimestamp (first int, second int, timestamp timestamp, name varchar, primary key (first))",
                "create table withFakeTimestamp (first int, second int, timestamp int, name varchar, primary key (first))",
        };
    }
    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop table withVersion",
                "drop table noVersion",
                "drop table withRealTimestamp",
                "drop table withFakeTimestamp"
        };
    }

    @Test
    public void testVersion() {

        PersistentClass cl = getJDBCMetaDataConfiguration().getClassMapping( "Withversion" );

        Property version = cl.getVersion();
        assertNotNull( version );
        assertEquals( "version", version.getName() );

        cl = getJDBCMetaDataConfiguration().getClassMapping( "Noversion" );
        assertNotNull( cl );
        version = cl.getVersion();
        assertNull( version );

    }

    @Test
    public void testGenerateMappings() {
        getJDBCMetaDataConfiguration().buildMappings();
        Exporter exporter = new HibernateMappingExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );

        exporter.start();

        Configuration derived = new Configuration();

        derived.addFile( new File( getOutputDir(), "Withversion.hbm.xml" ) );
        derived.addFile( new File( getOutputDir(), "Noversion.hbm.xml" ) );
        derived.addFile( new File( getOutputDir(), "Withrealtimestamp.hbm.xml" ) );
        derived.addFile( new File( getOutputDir(), "Withfaketimestamp.hbm.xml" ) );

        testVersioningInDerivedCfg( derived );
    }

    protected void testVersioningInDerivedCfg(Configuration derived) {
        derived.buildMappings();

        PersistentClass cl = derived.getClassMapping( "Withversion" );

        Property version = cl.getVersion();
        assertNotNull( version );
        assertEquals( "version", version.getName() );

        cl = derived.getClassMapping( "Noversion" );
        assertNotNull( cl );
        version = cl.getVersion();
        assertNull( version );

        cl = derived.getClassMapping( "Withrealtimestamp" );
        assertNotNull( cl );
        version = cl.getVersion();
        assertNotNull( version );
        assertTrue( version.getType() instanceof TimestampType );

        cl = derived.getClassMapping( "Withfaketimestamp" );
        assertNotNull( cl );
        version = cl.getVersion();
        assertNotNull( version );
        assertTrue( version.getType() instanceof IntegerType );
    }


}
