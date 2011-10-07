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

import org.hibernate.cfg.reveng.dialect.H2MetaDataDialect;
import org.hibernate.cfg.reveng.dialect.HSQLMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MySQLMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.OracleMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.internal.util.ReflectHelper;

public class MetaDataDialectFactory {

    public MetaDataDialect createMetaDataDialect(Dialect dialect, Map<String, String> cfg) {
        String property = cfg.get( "hibernatetool.metadatadialect" );
        MetaDataDialect mdd = fromClassName( property );
        if ( mdd == null ) {
            mdd = fromDialect( dialect );
        }
        if ( mdd == null ) {
            mdd = fromDialectName( dialect.getClass().getName() );
        }
        if ( mdd == null ) {
            mdd = new JDBCMetaDataDialect();
        }
        return mdd;
    }

    private MetaDataDialect fromClassName(String property) {
        if ( property != null ) {
            try {
                //todo change to use ClassLoadingService
                return (MetaDataDialect) ReflectHelper.classForName(
                        property,
                        JDBCReaderFactory.class
                ).newInstance();
            }
            catch ( Exception e ) {
                throw new JDBCBinderException(
                        "Could not load MetaDataDialect: " + property, e
                );
            }
        }
        else {
            return null;
        }
    }

    public MetaDataDialect fromDialect(Dialect dialect) {
        if ( dialect != null ) {
            if ( dialect instanceof Oracle9Dialect ) {
                return new OracleMetaDataDialect();
            }
            else if ( dialect instanceof Oracle8iDialect ) {
                return new OracleMetaDataDialect();
            }
            else if ( dialect instanceof H2Dialect ) {
                return new H2MetaDataDialect();
            }
            else if ( dialect instanceof MySQLDialect ) {
                return new MySQLMetaDataDialect();
            }
            else if ( dialect instanceof HSQLDialect ) {
                return new HSQLMetaDataDialect();
            }
        }
        return null;
    }

    public MetaDataDialect fromDialectName(String dialect) {
        if ( dialect.toLowerCase().contains( "oracle" ) ) {
            return new OracleMetaDataDialect();
        }
        if ( dialect.toLowerCase().contains( "mysql" ) ) {
            return new MySQLMetaDataDialect();
        }
        if ( dialect.toLowerCase().contains( "h2" ) ) {
            return new H2MetaDataDialect();
        }
        if ( dialect.toLowerCase().contains( "hsql" ) ) {
            return new HSQLMetaDataDialect();
        }
        return null;
    }


}
