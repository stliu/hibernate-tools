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

import java.util.List;

import org.junit.Test;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.HbmLintExporter;
import org.hibernate.tool.hbmlint.detector.BadCachingDetector;
import org.hibernate.tool.hbmlint.detector.InstrumentationDetector;
import org.hibernate.tool.hbmlint.detector.ShadowedIdentifierDetector;

import static org.junit.Assert.assertEquals;

public class HbmLintTest extends JDBCMetaDataBinderTestCase {

    @Override
    protected String[] getMappings() {
        return new String[] {
                "hbmlint/CachingSettings.hbm.xml",
                "hbmlint/IdentifierIssues.hbm.xml",
                "hbmlint/BrokenLazy.hbm.xml"
        };
    }

    @Test
    public void testExporter() {
        new HbmLintExporter( configuration(), serviceRegistry(), getOutputDir() ).start();

    }

    @Test
    public void testValidateCache() {
        HbmLint analyzer = new HbmLint( new Detector[] { new BadCachingDetector() } );

        analyzer.analyze( configuration(), serviceRegistry() );

        List result = analyzer.getResults();

        assertEquals( 1, result.size() );

    }

    @Test
    public void testValidateIdentifier() {

        HbmLint analyzer = new HbmLint( new Detector[] { new ShadowedIdentifierDetector() } );

        analyzer.analyze( configuration(), serviceRegistry() );

        List result = analyzer.getResults();

        assertEquals( 1, result.size() );


    }

    @Test
    public void testBytecodeRestrictions() {


        HbmLint analyzer = new HbmLint( new Detector[] { new InstrumentationDetector() } );

        analyzer.analyze( configuration(), serviceRegistry() );

        List<Issue> result = analyzer.getResults();
        for(Issue i: result){
            System.out.println(i.getDescription());
        }
        assertEquals( 2, result.size() );


    }

    protected String[] getCreateSQL() {
        return new String[0];// { "create table Category (id numeric(5), parent_id numeric(5))" };
    }

    protected String[] getDropSQL() {
        return new String[0];// { "drop table Category" };
    }


}
