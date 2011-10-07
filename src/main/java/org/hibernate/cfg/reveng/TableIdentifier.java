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

package org.hibernate.cfg.reveng;

import org.hibernate.mapping.Table;

/**
 * Identifier used for the full name of tables.
 *
 * @author max
 */
public class TableIdentifier {

    private final String catalog;
    private final String schema;
    private final String name;

    public TableIdentifier(String name) {
        this( null, null, name );
    }

    public TableIdentifier(String catalog, String schema, String name) {
        this.catalog = ( catalog == null ? null : catalog.intern() );
        this.schema = ( schema == null ? null : schema.intern() );
        this.name = ( name == null ? null : name.intern() );
    }

    public String getCatalog() {
        return catalog;
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    public boolean equals(Object obj) {
        return obj instanceof TableIdentifier && equals( (TableIdentifier) obj );
    }

    public boolean equals(TableIdentifier otherIdentifier) {
        if ( otherIdentifier == null ) {
            return false;
        }
        if ( this == otherIdentifier ) {
            return true;
        }

        if ( equals( this.name, otherIdentifier.name ) ) {
            if ( equals( this.schema, otherIdentifier.schema ) ) {
                return equals( this.catalog, otherIdentifier.catalog );
            }
        }

        return false;
    }

    public int hashCode() {
        int result = 13;
        result = 37 * result + ( name == null ? 0 : name.hashCode() );
        result = 37 * result + ( schema == null ? 0 : schema.hashCode() );
        result = 37 * result + ( catalog == null ? 0 : catalog.hashCode() );

        return result;
    }

    private boolean equals(String left, String right) {
        if ( left == right ) {
            return true;
        }
        if ( left == null || right == null ) {
            return false;
        }
        return left.equals( right );
    }

    public static TableIdentifier create(Table table) {
        return new TableIdentifier( table.getCatalog(), table.getSchema(), table.getName() );
    }

    public String toString() {
        StringBuffer buf = new StringBuffer()
                .append( "TableIdentifier" )
                .append( '(' );
        if ( getCatalog() != null ) {
            buf.append( getCatalog() + "." );
        }
        if ( getSchema() != null ) {
            buf.append( getSchema() + "." );
        }
        buf.append( getName() ).append( ')' );
        return buf.toString();
    }

}
