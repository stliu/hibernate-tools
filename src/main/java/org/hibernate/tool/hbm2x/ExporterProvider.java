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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.internal.util.ReflectHelper;

public class ExporterProvider {

    final String exporterClassName;
    final String exporterName;
    final Map supportedProperties;

    public ExporterProvider(String exporterName, String exporterClassName, Map supportedProperties) {
        this.exporterClassName = exporterClassName;
        this.exporterName = exporterName;
        this.supportedProperties = supportedProperties;
    }

    public String getExporterName() {
        return exporterName;
    }

    public Set getSupportedProperties() {
        return supportedProperties.keySet();
    }

    public List validateProperties(Properties properties) {
        return Collections.EMPTY_LIST;
    }

    public Exporter createProvider() {
        try {
            return (Exporter) ReflectHelper.classForName( exporterClassName, this.getClass() ).newInstance();
        }
        catch ( Exception e ) {
            throw new ExporterException( "Could not create exporter: " + exporterClassName, e );
        }
    }

}
