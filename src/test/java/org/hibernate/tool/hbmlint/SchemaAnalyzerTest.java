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

package org.hibernate.tool.hbmlint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.HbmLintExporter;
import org.hibernate.tool.hbmlint.detector.SchemaByMetaDataDetector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SchemaAnalyzerTest extends JDBCMetaDataBinderTestCase {
    @Override
    protected String[] getMappings() {
        return new String[] { "hbmlint/SchemaIssues.hbm.xml" };
    }

    static class MockCollector implements IssueCollector {
        List<Issue> problems = new ArrayList<Issue>();

        @Override
        public void reportIssue(Issue issue) {
            problems.add( issue );
        }
    }

    @Test
    public void testSchemaAnalyzer() {
        SchemaByMetaDataDetector analyzer = new SchemaByMetaDataDetector();
        analyzer.initialize( configuration(), serviceRegistry(), settings() );

        Iterator tableMappings = configuration().getTableMappings();

        while ( tableMappings.hasNext() ) {
            Table table = (Table) tableMappings.next();
            MockCollector mc = new MockCollector();
            if ( table.getName().equalsIgnoreCase( "missingtable" ) ) {
                analyzer.visit( getJDBCMetaDataConfiguration(), table, mc );
                assertEquals( 1, mc.problems.size() );
                Issue ap = mc.problems.get( 0 );
                assertTrue( ap.getDescription().indexOf( "Missing table" ) >= 0 );
            }
            else if ( table.getName().equalsIgnoreCase( "category" ) ) {
                analyzer.visit( getJDBCMetaDataConfiguration(), table, mc );
                assertEquals( 1, mc.problems.size() );
                Issue ap = mc.problems.get( 0 );
                assertTrue( ap.getDescription().indexOf( "missing column: name" ) >= 0 );
            }
            else if ( table.getName().equalsIgnoreCase( "badtype" ) ) {
                analyzer.visit( getJDBCMetaDataConfiguration(), table, mc );
                assertEquals( 1, mc.problems.size());
                Issue ap = mc.problems.get( 0 );
                System.out.println(ap.getDescription());
                assertTrue( ap.getDescription().indexOf( "wrong column type for name" ) >= 0 );
            }
            else {
                fail( "Unknown table " + table );
            }
        }

        MockCollector mc = new MockCollector();
        analyzer.visitGenerators( configuration(), mc );
        assertEquals( 1, mc.problems.size() );
        Issue issue = mc.problems.get( 0 );
        assertTrue( issue.getDescription().indexOf( "hibernate_unique_key" ) >= 0 );
    }


    @Test
    public void testExporter() {
        new HbmLintExporter( configuration(), serviceRegistry(), getOutputDir() ).start();

    }

    @Override
    protected String[] getCreateSQL() {
        return new String[] {
                "create table Category (id int, parent_id numeric(5))",
                "create table BadType (id int, name varchar(100))",
                "create sequence should_be_there start with 1",
                "create table hilo_table (id int)"
        };
    }

    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop table Category",
                "drop table BadType",
                "drop sequence should_be_there",
                "drop table hilo_table"
        };
    }
}
