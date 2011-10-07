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

import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbmlint.HbmLint;

public class HbmLintExporter extends GenericExporter {

    private static final String TEXT_REPORT_FTL = "lint/text-report.ftl";

    public HbmLintExporter() {
    }

    public HbmLintExporter(Configuration cfg, ServiceRegistry serviceRegistry, File outputDir) {
        super( cfg, serviceRegistry, outputDir );

    }

    public void start() {
        // TODO: make non-hardwired
        setFilePattern( "hbmlint-result.txt" );
        setTemplateName( TEXT_REPORT_FTL );
        super.start();
    }

    protected void setupContext() {
        HbmLint hbmlint = HbmLint.createInstance();
        hbmlint.analyze( getConfiguration(), getServiceRegistry() );
        getProperties().put( "lintissues", hbmlint.getResults() );
        super.setupContext();
    }

    public String getName() {
        return "hbmlint";
    }


}