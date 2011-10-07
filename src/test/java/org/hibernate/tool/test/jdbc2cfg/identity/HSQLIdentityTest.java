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
import org.hibernate.cfg.reveng.dialect.HSQLMetaDataDialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.testing.RequiresDialect;

/**
 * @author Dmitry Geraskov
 */
@RequiresDialect(HSQLDialect.class)
public class HSQLIdentityTest extends AbstractIdentityTest {

    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "DROP TABLE AUTOINC IF EXISTS",
                "DROP TABLE NOAUTOINC IF EXISTS",
        };
    }

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "CREATE TABLE AUTOINC (I identity, C CHAR(20), D CHAR(20))",
                "CREATE TABLE NOAUTOINC (I int, C CHAR(20), D CHAR(20))",
        };
    }


    @Override
    protected void configure(JDBCMetaDataConfiguration configuration) {
        configuration.setProperty( "hibernatetool.metadatadialect", HSQLMetaDataDialect.class.getName() );
    }


}
