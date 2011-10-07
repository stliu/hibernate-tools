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

package org.hibernate.tool.test.jdbc2cfg;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KeyPropertyCompositeIdTest extends CompositeIdTest {
    @Override
    protected void configure(JDBCMetaDataConfiguration configuration) {
        configuration.setPreferBasicCompositeIds( false );
    }

    @Override
    protected void checkKeyProperties(Property id, Property extraId) {
        assertEquals( toPropertyName( "customer" ), id.getName() );
        assertEquals( toPropertyName( "ordernumber" ), extraId.getName() );

        assertTrue( id.getValue() instanceof ManyToOne );
        assertFalse( extraId.getValue() instanceof ManyToOne );
    }
    @Override
    protected String getCustomerOrderQuery() {
        return "select li.id.customerorder.id from Lineitem as li";
    }

}
