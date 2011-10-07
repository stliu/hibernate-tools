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

package org.hibernate.tool.hbm2x;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

/**
 * @author max
 */
public interface ConfigurationVisitor {

    boolean startPersistentClass(PersistentClass clazz) throws ExporterException;

    void endPersistentClass(PersistentClass clazz) throws ExporterException;

    boolean startComponent(Component component) throws ExporterException;

    void endComponent(Component componenet) throws ExporterException;

    void finish() throws ExporterException;

    boolean startProperty(Property prop);

    void endProperty(Property prop);

    void startEmbeddedIdentifier(Component component);

    void endEmbeddedIdentifier(Component component);

    void startIdentifierProperty(Property identifierProperty);

    void endIdentifierProperty(Property identifierProperty);

    boolean startMapping(Configuration cfg);

    void endMapping(Configuration cfg);

    boolean startGeneralConfiguration(Configuration cfg) throws ExporterException;

    void endGeneralConfiguration(Configuration cfg) throws ExporterException;

}
