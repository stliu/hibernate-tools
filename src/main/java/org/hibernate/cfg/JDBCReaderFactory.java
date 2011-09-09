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
