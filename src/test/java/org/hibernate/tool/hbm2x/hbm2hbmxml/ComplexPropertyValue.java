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

package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * todo: describe ${NAME}
 *
 * @author Steve Ebersole
 */
public class ComplexPropertyValue implements PropertyValue {
    private Long id;
    private Map subProperties = new HashMap();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map getSubProperties() {
        return subProperties;
    }

    public void setSubProperties(Map subProperties) {
        this.subProperties = subProperties;
    }

    public String asString() {
        return "complex[" + keyString() + "]";
    }

    private String keyString() {
        StringBuffer buff = new StringBuffer();
        Iterator itr = subProperties.keySet().iterator();
        while ( itr.hasNext() ) {
            buff.append( itr.next() );
            if ( itr.hasNext() ) {
                buff.append( ", " );
            }
        }
        return buff.toString();
    }
}
