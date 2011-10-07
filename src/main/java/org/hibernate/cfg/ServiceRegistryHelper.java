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

package org.hibernate.cfg;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;

/**
 * @author Strong Liu
 */
public class ServiceRegistryHelper {
    private static final Map<Configuration, ServiceRegistry> cache = new HashMap<Configuration, ServiceRegistry>();

    public static ServiceRegistry getDefaultServiceRegistry(Configuration configuration) {
        ServiceRegistry registry = cache.get( configuration );
        if ( registry == null ) {
            Properties properties = new Properties();
            properties.putAll( configuration.getProperties() );
            Environment.verifyProperties( properties );
            ConfigurationHelper.resolvePlaceHolders( properties );
            registry =  new org.hibernate.service.ServiceRegistryBuilder(
                    properties
            ).buildServiceRegistry();
            cache.put( configuration, registry );

        }


        return registry;
    }
}
