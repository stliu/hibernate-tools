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
 * Created on 13-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 */
public class CoreTaskTest extends BuildFileTestCase {
    @After
    public void tearDown() throws Exception {
        executeTarget( "cleanup" );
    }
    @Before
    public void setUp() throws Exception {
        configureProject( "src/test/resources/coretest-build.xml" );
    }

    @Test
    public void testSchemaUpdateWarning() {
        executeTarget( "test-schemaupdatewarning" );
        Assert.assertTrue( getLog(), checkLogWithoutExceptions() );
        assertLogContaining( "Hibernate Core SchemaUpdate" );
        assertLogContaining( "tools.hibernate.org" );
    }

    /* TODO: this test is suddenly not able to get the log output from ant causing problems.
      * @Test public void testSchemaExportWarning() {
         executeTarget("test-schemaexportwarning");
         assertTrue(getLog(), checkLogWithoutExceptions());
         assertLogContaining( "Hibernate Core SchemaUpdate" );
         assertLogContaining( "tools.hibernate.org" );
     }*/


}
