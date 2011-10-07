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

public class SchemaSelection {

    String matchCatalog;
    String matchSchema;
    String matchTable;

    public SchemaSelection(String catalog, String schema, String table) {
        matchCatalog = catalog;
        matchSchema = schema;
        matchTable = table;
    }

    public SchemaSelection(String catalog, String schema) {
        this( catalog, schema, null );
    }

    public SchemaSelection() {
    }


    public String getMatchCatalog() {
        return matchCatalog;
    }

    public void setMatchCatalog(String catalogPattern) {
        this.matchCatalog = catalogPattern;
    }

    public String getMatchSchema() {
        return matchSchema;
    }

    public void setMatchSchema(String schemaPattern) {
        this.matchSchema = schemaPattern;
    }

    public String getMatchTable() {
        return matchTable;
    }

    public void setMatchTable(String tablePattern) {
        this.matchTable = tablePattern;
    }

}
