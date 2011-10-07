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

/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.hibernate.tool.ide.completion;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;

/**
 * @author leon
 */
public class Model {

    private Model() {
    }

    public static Configuration buildConfiguration() {
        Configuration cfg = new Configuration();
        cfg.setProperty( Environment.DIALECT, HSQLDialect.class.getName() );
        cfg.addInputStream(
                Model.class.getResourceAsStream( "Product.hbm.xml" )
        ).
                addInputStream( Model.class.getResourceAsStream( "Store.hbm.xml" ) ).
                addInputStream( Model.class.getResourceAsStream( "ProductOwnerAddress.hbm.xml" ) ).
                addInputStream( Model.class.getResourceAsStream( "City.hbm.xml" ) ).
                addInputStream( Model.class.getResourceAsStream( "StoreCity.hbm.xml" ) );
        cfg.buildMappings();
        return cfg;
    }


}
