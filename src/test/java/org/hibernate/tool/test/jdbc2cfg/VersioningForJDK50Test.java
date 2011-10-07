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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


import org.junit.Test;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.test.TestHelper;


/**
 * based on VersioningTest
 * requires a default package to be easily added to classloader
 * steps:
 * 1- build mappings from jdbc (see table used in VersioningTest, some has version, some timestamp,...
 * 2- use *annotated* pojo exporter
 * 3- check if generated classes compile, add them to classloader
 * 4- make a derived configuration from annotated classes
 * 5- test the derived configuration
 *
 * @author anthony
 */
public class VersioningForJDK50Test extends VersioningTest {
    @Override
    protected void configure(JDBCMetaDataConfiguration cfgToConfigure) {
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings( new ReverseEngineeringSettings( c ) );
        cfgToConfigure.setReverseEngineeringStrategy( c );
    }

    @Test
    public void testGenerateJPA() throws Exception {
        POJOExporter exporter = new POJOExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        exporter.setTemplatePath( new String[0] );
        exporter.getProperties().setProperty( "ejb3", "true" );
        exporter.getProperties().setProperty( "jdk5", "true" );
        exporter.start();

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

        URL[] urls = new URL[] { file.toURI().toURL() };
        Thread currentThread = Thread.currentThread();
        URLClassLoader ucl = new URLClassLoader( urls, currentThread.getContextClassLoader() );
        currentThread.setContextClassLoader( ucl );


        Class withversionClazz = ucl.loadClass( "Withversion" );
        Class withrealtimestampClazz = ucl.loadClass( "Withrealtimestamp" );
        Class noversionClazz = ucl.loadClass( "Noversion" );
        Class withfaketimestampClazz = ucl.loadClass( "Withfaketimestamp" );
        Configuration derived = new Configuration();
        derived.addAnnotatedClass( withversionClazz );
        derived.addAnnotatedClass( withrealtimestampClazz );
        derived.addAnnotatedClass( noversionClazz );
        derived.addAnnotatedClass( withfaketimestampClazz );

        testVersioningInDerivedCfg( derived );

    }

}
