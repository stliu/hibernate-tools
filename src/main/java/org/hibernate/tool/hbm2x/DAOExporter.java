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

import java.io.File;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

public class DAOExporter extends POJOExporter {

    private static final String DAO_DAOHOME_FTL = "dao/daohome.ftl";

    private String sessionFactoryName = "SessionFactory";

    public DAOExporter() {
    }

    public DAOExporter(Configuration cfg, ServiceRegistry serviceRegistry, File outputdir) {
        super( cfg, serviceRegistry, outputdir );
    }

    protected void init() {
        super.init();
        setTemplateName( DAO_DAOHOME_FTL );
        setFilePattern( "{package-name}/{class-name}Home.java" );
    }

    protected void exportComponent(Map additionalContext, POJOClass element) {
        // noop - we dont want components
    }

    public String getSessionFactoryName() {
        return sessionFactoryName;
    }

    public void setSessionFactoryName(String sessionFactoryName) {
        this.sessionFactoryName = sessionFactoryName;
    }

    protected void setupContext() {
        getProperties().put( "sessionFactoryName", getSessionFactoryName() );
        super.setupContext();
    }

    public String getName() {
        return "hbm2dao";
    }


}
