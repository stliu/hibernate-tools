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

package org.hibernate.tool.ide.completion;

/**
 * Class that represents an alias to some entityname in a HQL statement. e.g. "Product as p" or "Product p"
 *
 * Should not be used by external clients.
 *
 * @author leon, Max Rydahl Andersen
 */
public class EntityNameReference {

    private String alias;

    private String entityName;

    public EntityNameReference(String type, String alias) {
        this.entityName = type;
        this.alias = alias;
    }

    /**
     * @return The alias, the "p" in "Product as p"
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @return the entityname, the "Product" in "Product as b"
     */
    public String getEntityName() {
        return entityName;
    }

    public String toString() {
        return alias + ":" + entityName;
    }


}
