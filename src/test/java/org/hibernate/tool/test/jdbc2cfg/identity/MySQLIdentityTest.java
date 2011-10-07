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

package org.hibernate.tool.test.jdbc2cfg.identity;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.dialect.MySQLMetaDataDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.testing.RequiresDialect;

/**
 * @author max
 */
@RequiresDialect(MySQLDialect.class)
public class MySQLIdentityTest extends AbstractIdentityTest {


    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop table `autoinc`",
                "drop table `noautoinc`",
        };
    }

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "CREATE TABLE `autoinc` (`id` int(11) NOT NULL auto_increment,  `data` varchar(100) default NULL,  PRIMARY KEY  (`id`))",
                "CREATE TABLE `noautoinc` (`id` int(11) NOT NULL,  `data` varchar(100) default NULL,  PRIMARY KEY  (`id`))",
        };
    }

    @Override
    protected void configure(JDBCMetaDataConfiguration configuration) {
        configuration.setProperty( "hibernatetool.metadatadialect", MySQLMetaDataDialect.class.getName() );
    }

}
