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
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbmlint.Issue;
import org.hibernate.tool.hbmlint.IssueCollector;

public class ShadowedIdentifierDetector extends EntityModelDetector {

    public String getName() {
        return "shadow-id";
    }

    public void visitProperty(Configuration configuration, PersistentClass clazz, Property property, IssueCollector collector) {
        if ( property.getName().equals( "id" ) ) {
            if ( property != property.getPersistentClass().getIdentifierProperty() ) {
                collector.reportIssue(
                        new Issue(
                                "ID_SHADOWED",
                                Issue.LOW_PRIORITY,
                                property.getPersistentClass()
                                        .getEntityName() + " has a normal property named 'id'. This can cause issues since HQL queries will always interpret 'id' as the identifier and not the concrete property"
                        )
                );
            }
        }
    }
}
