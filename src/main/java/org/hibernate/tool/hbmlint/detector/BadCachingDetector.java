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

package org.hibernate.tool.hbmlint.detector;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.visitor.EntityNameFromValueVisitor;
import org.hibernate.tool.hbmlint.Issue;
import org.hibernate.tool.hbmlint.IssueCollector;

public class BadCachingDetector extends EntityModelDetector {

    public String getName() {
        return "cache";
    }

    public void visitProperty(Configuration configuration, PersistentClass clazz, Property property, IssueCollector collector) {
        Value value = property.getValue();

        if ( value instanceof Collection ) {
            Collection col = (Collection) value;
            if ( col.getCacheConcurrencyStrategy() != null ) { // caching is enabled
                if ( !col.getElement().isSimpleValue() ) {
                    String entityName = (String) col.getElement().accept( new EntityNameFromValueVisitor() );

                    if ( entityName != null ) {
                        PersistentClass classMapping = configuration.getClassMapping( entityName );
                        if ( classMapping.getCacheConcurrencyStrategy() == null ) {
                            collector.reportIssue(
                                    new Issue(
                                            "CACHE_COLLECTION_NONCACHABLE_TARGET",
                                            Issue.HIGH_PRIORITY,
                                            "Entity '" + classMapping.getEntityName() + "' is referenced from the cache-enabled collection '" + col
                                                    .getRole() + "' without the entity being cachable"
                                    )
                            );
                        }
                    }
                }
            }
        }
    }
}
