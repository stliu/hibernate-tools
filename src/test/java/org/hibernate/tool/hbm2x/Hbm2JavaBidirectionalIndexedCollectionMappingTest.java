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

import org.junit.Before;
import org.junit.Test;

import org.hibernate.tool.NonReflectiveTestCase;

/**
 * @author max
 */
public class Hbm2JavaBidirectionalIndexedCollectionMappingTest extends NonReflectiveTestCase {

     @Before
    public void setUp() throws Exception {

        Exporter exporter = new POJOExporter( configuration(), serviceRegistry(), getOutputDir() );
        exporter.start();
    }


    @Test
    public void testReflection() throws Exception {

    }
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
             @Override
    protected String[] getMappings() {
        return new String[] {
                "GenericModel.hbm.xml",
        };
    }


}
