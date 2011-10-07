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

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.internal.util.StringHelper;

public abstract class AbstractDatabaseCollector implements DatabaseCollector {

    private Map oneToManyCandidates;
    protected final Map<TableIdentifier, String> suggestedIdentifierStrategies;
    private MetaDataDialect metaDataDialect;

    public AbstractDatabaseCollector(MetaDataDialect metaDataDialect) {
        suggestedIdentifierStrategies = new HashMap<TableIdentifier, String>();
        this.metaDataDialect = metaDataDialect;
    }

    public void setOneToManyCandidates(Map oneToManyCandidates) {
        this.oneToManyCandidates = oneToManyCandidates;
    }

    public Map getOneToManyCandidates() {
        return oneToManyCandidates;
    }

    public String getSuggestedIdentifierStrategy(String catalog, String schema, String name) {
        TableIdentifier identifier = new TableIdentifier( catalog, schema, name );
        return suggestedIdentifierStrategies.get( identifier );
    }

    public void addSuggestedIdentifierStrategy(String catalog, String schema, String name, String idstrategy) {
        TableIdentifier identifier = new TableIdentifier( catalog, schema, name );
        suggestedIdentifierStrategies.put( identifier, idstrategy );
    }

    protected String quote(String name) {
        if ( name == null ) {
            return name;
        }
        else if ( metaDataDialect.needQuote( name ) ) {
            return StringHelper.quote( name );
        }
        else {
            return name;
        }
    }

}
