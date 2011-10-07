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

import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.config.spi.ConfigurationService;

final public class JDBCReaderFactory {


    public static JDBCReader newJDBCReader(Properties cfg, ServiceRegistry serviceRegistry, Settings settings,
                                           ReverseEngineeringStrategy revengStrategy) {
        JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
        MetaDataDialect mdd = newMetaDataDialect( jdbcServices.getDialect(), cfg );

        return newJDBCReader( serviceRegistry, settings, revengStrategy, mdd );
    }

    public static JDBCReader newJDBCReader(ServiceRegistry serviceRegistry, Settings settings,
                                           ReverseEngineeringStrategy revengStrategy) {
        JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
        ConfigurationService configurationService = serviceRegistry.getService( ConfigurationService.class );
        MetaDataDialect mdd = newMetaDataDialect( jdbcServices.getDialect(), configurationService.getSettings() );

        return newJDBCReader( serviceRegistry, settings, revengStrategy, mdd );
    }

    public static JDBCReader newJDBCReader(ServiceRegistry serviceRegistry, Settings settings, ReverseEngineeringStrategy revengStrategy, MetaDataDialect mdd) {
        JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
        return new JDBCReader(
                mdd,
                jdbcServices.getConnectionProvider(),
                jdbcServices.getSqlExceptionHelper().getSqlExceptionConverter(),
                settings.getDefaultCatalogName(),
                settings.getDefaultSchemaName(),
                revengStrategy
        );
    }

    public static MetaDataDialect newMetaDataDialect(Dialect dialect, Map cfg) {
        return new MetaDataDialectFactory().createMetaDataDialect( dialect, cfg );

    }

}
