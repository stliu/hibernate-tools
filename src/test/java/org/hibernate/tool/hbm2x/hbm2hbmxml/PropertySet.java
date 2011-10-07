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
import java.util.Map;

/**
 * todo: describe PropertySet
 *
 * @author Steve Ebersole
 */
public class PropertySet {
    private Long id;
    private String name;
    private PropertyValue someSpecificProperty;
    private Map generalProperties = new HashMap();

    public PropertySet() {
    }

    public PropertySet(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PropertyValue getSomeSpecificProperty() {
        return someSpecificProperty;
    }

    public void setSomeSpecificProperty(PropertyValue someSpecificProperty) {
        this.someSpecificProperty = someSpecificProperty;
    }

    public Map getGeneralProperties() {
        return generalProperties;
    }

    public void setGeneralProperties(Map generalProperties) {
        this.generalProperties = generalProperties;
    }
}
