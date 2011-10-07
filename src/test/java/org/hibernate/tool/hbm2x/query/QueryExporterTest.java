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

package org.hibernate.tool.hbm2x.query;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.QueryExporter;

public class QueryExporterTest extends NonReflectiveTestCase {

    @Override
    protected String[] getMappings() {
        return new String[] { "hbm2x/query/UserGroup.hbm.xml" };
    }

    String FILE = "queryresult.txt";

    @Override
    protected void configure(Configuration configuration) {
        configuration.setProperty( Environment.HBM2DDL_AUTO, "update" );
    }

    @Before
    public void setUp() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        User user = new User( "max", "jboss" );
        s.persist( user );
        user = new User( "gavin", "jboss" );
        s.persist( user );
        s.getTransaction().commit();
        s.close();

        QueryExporter exporter = new QueryExporter();
        exporter.setConfiguration( configuration() );
        exporter.setOutputDirectory( getOutputDir() );
        exporter.setFilename( FILE );
        List queries = new ArrayList();
        queries.add( "from java.lang.Object" );
        exporter.setQueries( queries );
        exporter.start();
    }

    @After
    public void tearDown() throws Exception {
        SchemaExport export = new SchemaExport( serviceRegistry(), configuration() );
        export.drop( false, true );

    }


    @Test
    public void testQueryExporter() {
        assertFileAndExists( new File( getOutputDir(), FILE ) );
    }

}
