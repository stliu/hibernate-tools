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

import org.junit.Test;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.NonReflectiveTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author max
 */
public class Hbm2CfgTest extends NonReflectiveTestCase {

    private HibernateConfigurationExporter cfgexporter;

    @org.junit.Before
    public void setUp() throws Exception {

        cfgexporter = new HibernateConfigurationExporter( configuration(), serviceRegistry(), getOutputDir() );
        cfgexporter.start();
    }

    @Test
    public void testMagicPropertyHandling() {
        Configuration srcCfg = new Configuration();

        srcCfg.setProperty( "hibernate.basic", "aValue" );
        srcCfg.setProperty( Environment.SESSION_FACTORY_NAME, "shouldNotShowUp" );
        srcCfg.setProperty( Environment.HBM2DDL_AUTO, "false" );
        srcCfg.setProperty( "hibernate.temp.use_jdbc_metadata_defaults", "false" );

        new HibernateConfigurationExporter( srcCfg, serviceRegistry(), getOutputDir() ).start();

        File file = new File( getOutputDir(), "hibernate.cfg.xml" );
        assertNull( findFirstString( Environment.SESSION_FACTORY_NAME, file ) );
        assertNotNull( findFirstString( "hibernate.basic\">aValue<", file ) );
        assertNull( findFirstString( Environment.HBM2DDL_AUTO, file ) );
        assertNull( findFirstString( "hibernate.temp.use_jdbc_metadata_defaults", file ) );

        srcCfg = new Configuration();

        srcCfg.setProperty( Environment.HBM2DDL_AUTO, "validator" );

        new HibernateConfigurationExporter( srcCfg, serviceRegistry(), getOutputDir() ).start();

        assertNotNull( findFirstString( Environment.HBM2DDL_AUTO, file ) );


        srcCfg = new Configuration();
        srcCfg.setProperty(
                Environment.TRANSACTION_MANAGER_STRATEGY,
                "org.hibernate.console.FakeTransactionManagerLookup"
        ); // Hack for seam-gen console configurations
        HibernateConfigurationExporter exp = new HibernateConfigurationExporter(
                srcCfg,
                serviceRegistry(),
                getOutputDir()
        );
        exp.start();

        assertNull( findFirstString( Environment.TRANSACTION_MANAGER_STRATEGY, file ) );


    }

    @Test
    public void testFileExistence() {

        assertFileAndExists( new File( getOutputDir(), "hibernate.cfg.xml" ) );

    }

    @Test
    public void testArtifactCollection() {
        assertEquals( 1, cfgexporter.getArtifactCollector().getFileCount( "cfg.xml" ) );
    }

    @Test
    public void testNoVelocityLeftOvers() {

        assertEquals( null, findFirstString( "$", new File( getOutputDir(), "hibernate.cfg.xml" ) ) );

    }

    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
    @Override
    protected String[] getMappings() {
        return new String[] {
                "Customer.hbm.xml",
                "Order.hbm.xml",
                "LineItem.hbm.xml",
                "Product.hbm.xml",
                "HelloWorld.hbm.xml"
        };
    }

}
