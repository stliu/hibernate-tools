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
 * Created on 2004-12-03
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

/**
 * @author david and max
 */
public class HibernateMappingExporter extends GenericExporter {

    protected HibernateMappingGlobalSettings globalSettings = new HibernateMappingGlobalSettings();
    @Override
    protected void setupContext() {
        super.setupContext();
        getTemplateHelper().putInContext( "hmgs", globalSettings );
    }
    public void setGlobalSettings(HibernateMappingGlobalSettings hgs) {
        this.globalSettings = hgs;
    }
    @Override
    public void doStart() {
        exportGeneralSettings();

        super.doStart();
    }

    private void exportGeneralSettings() {
        Cfg2HbmTool c2h = getCfg2HbmTool();
        Configuration cfg = getConfiguration();
        if ( c2h.isImportData( cfg ) && ( c2h.isNamedQueries( cfg ) ) && ( c2h.isNamedSQLQueries( cfg ) ) && ( c2h.isFilterDefinitions(
                cfg
        ) ) ) {
            TemplateProducer producer = new TemplateProducer( getTemplateHelper(), getArtifactCollector() );
            producer.produce(
                    new HashMap(),
                    "hbm/generalhbm.hbm.ftl",
                    new File( getOutputDirectory(), "GeneralHbmSettings.hbm.xml" ),
                    getTemplateName(),
                    "General Settings"
            );
        }
    }

    public HibernateMappingExporter(Configuration cfg, ServiceRegistry serviceRegistry, File outputdir) {
        super( cfg, serviceRegistry, outputdir );
        init();
    }

    protected void init() {
        setTemplateName( "hbm/hibernate-mapping.hbm.ftl" );
        setFilePattern( "{package-name}/{class-name}.hbm.xml" );
    }

    public HibernateMappingExporter() {
        init();
    }

    protected String getClassNameForFile(POJOClass element) {
        return StringHelper.unqualify( ( (PersistentClass) element.getDecoratedObject() ).getEntityName() );
    }

    protected String getPackageNameForFile(POJOClass element) {
        return StringHelper.qualifier( ( (PersistentClass) element.getDecoratedObject() ).getClassName() );
    }


    protected void exportComponent(Map additionalContext, POJOClass element) {
        // we don't want component's exported.
    }

    public String getName() {
        return "hbm2hbmxml";
    }
}
