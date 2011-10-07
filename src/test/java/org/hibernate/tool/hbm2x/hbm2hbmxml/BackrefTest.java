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
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.File;
import java.util.Iterator;

import org.junit.Test;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Backref;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;

import static org.junit.Assert.assertTrue;

/**
 * @author Dmitry Geraskov
 */
public class BackrefTest extends NonReflectiveTestCase {

    private String mappingFile = "Car.hbm.xml";

    private Exporter hbmexporter;

   @Override
    protected String[] getMappings() {
        return new String[] {
                mappingFile
        };
    }
     @org.junit.Before
    public void setUp() throws Exception {

        hbmexporter = new HibernateMappingExporter( configuration(), serviceRegistry(), getOutputDir() );
        hbmexporter.start();
    }

    @Test
    public void testAllFilesExistence() {
        assertFileAndExists( new File( getOutputDir().getAbsolutePath(), getBaseForMappings() + "Car.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir().getAbsolutePath(), getBaseForMappings() + "CarPart.hbm.xml" ) );
    }

    @Test
    public void testBackrefPresent() {
        Configuration config = configuration();
        PersistentClass pc = config.getClassMapping( "org.hibernate.tool.hbm2x.hbm2hbmxml.CarPart" );
        Iterator iterator = pc.getPropertyIterator();
        boolean hasBackrefs = false;
        while ( iterator.hasNext() && !hasBackrefs ) {
            hasBackrefs = ( iterator.next() instanceof Backref );
        }
        assertTrue( "Class mapping should create Backref for this testcase", hasBackrefs );
    }

    @Test
    public void testReadable() {
        Configuration cfg = new Configuration();

        cfg.addFile( new File( getOutputDir(), getBaseForMappings() + "Car.hbm.xml" ) );
        cfg.addFile( new File( getOutputDir(), getBaseForMappings() + "CarPart.hbm.xml" ) );

        cfg.buildMappings();
    }
     @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
    }


}
