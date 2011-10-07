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
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * @author max
 */
public class POJOExporter extends GenericExporter {

    private static final String POJO_JAVA_CLASS_FTL = "pojo/Pojo.ftl";

    public POJOExporter(Configuration cfg, ServiceRegistry serviceRegistry, File outputDir) {
        super( cfg, serviceRegistry, outputDir );
        init();
    }

    protected void init() {
        setTemplateName( POJO_JAVA_CLASS_FTL );
        setFilePattern( "{package-name}/{class-name}.java" );
    }

    public POJOExporter() {
        init();
    }

    public String getName() {
        return "hbm2java";
    }

    protected void setupContext() {
        //TODO: this safe guard should be in the root templates instead for each variable they depend on.
        if ( !getProperties().containsKey( "ejb3" ) ) {
            getProperties().put( "ejb3", "false" );
        }
        if ( !getProperties().containsKey( "jdk5" ) ) {
            getProperties().put( "jdk5", "false" );
        }
        super.setupContext();
    }
}
