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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.hibernate.cfg.Configuration;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Component;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2x.pojo.ComponentPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;


public class GenericExporter extends AbstractExporter {
    static abstract class ModelIterator {
        abstract void process(GenericExporter ge);
    }

    static Map<String, ModelIterator> modelIterators = new HashMap<String, ModelIterator>();

    static {
        modelIterators.put(
                "configuration", new ModelIterator() {
            void process(GenericExporter ge) {
                TemplateProducer producer = new TemplateProducer( ge.getTemplateHelper(), ge.getArtifactCollector() );
                producer.produce(
                        new HashMap(),
                        ge.getTemplateName(),
                        new File( ge.getOutputDirectory(), ge.filePattern ),
                        ge.templateName,
                        "Configuration"
                );
            }
        }
        );
        modelIterators.put(
                "entity", new ModelIterator() {
            void process(GenericExporter ge) {
                Iterator iterator = ge.getCfg2JavaTool().getPOJOIterator( ge.getConfiguration().getClassMappings() );
                Map additionalContext = new HashMap();
                while ( iterator.hasNext() ) {
                    POJOClass element = (POJOClass) iterator.next();
                    ge.exportPersistentClass( additionalContext, element );
                }
            }
        }
        );
        modelIterators.put(
                "component", new ModelIterator() {

            void process(GenericExporter ge) {
                Map components = new HashMap();

                Iterator iterator = ge.getCfg2JavaTool().getPOJOIterator( ge.getConfiguration().getClassMappings() );
                Map additionalContext = new HashMap();
                while ( iterator.hasNext() ) {
                    POJOClass element = (POJOClass) iterator.next();
                    ConfigurationNavigator.collectComponents( components, element );
                }

                iterator = components.values().iterator();
                while ( iterator.hasNext() ) {
                    Component component = (Component) iterator.next();
                    ComponentPOJOClass element = new ComponentPOJOClass( component, ge.getCfg2JavaTool() );
                    ge.exportComponent( additionalContext, element );
                }
            }
        }
        );
    }

    private String templateName;
    private String filePattern;
    private String forEach;

    public GenericExporter(Configuration cfg, ServiceRegistry serviceRegistry, File outputdir) {
        super( cfg, serviceRegistry, outputdir );
    }

    public GenericExporter() {
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }


    public void setForEach(String foreach) {
        this.forEach = foreach;
    }


    protected void doStart() {

        if ( filePattern == null ) {
            throw new ExporterException( "File pattern not set on " + this.getClass() );
        }
        if ( templateName == null ) {
            throw new ExporterException( "Template name not set on " + this.getClass() );
        }

        List exporters = new ArrayList();

        if ( StringHelper.isEmpty( forEach ) ) {
            if ( filePattern.indexOf( "{class-name}" ) >= 0 ) {
                exporters.add( modelIterators.get( "entity" ) );
                exporters.add( modelIterators.get( "component" ) );
            }
            else {
                exporters.add( modelIterators.get( "configuration" ) );
            }
        }
        else {
            StringTokenizer tokens = new StringTokenizer( forEach, "," );

            while ( tokens.hasMoreTokens() ) {
                String nextToken = tokens.nextToken();
                Object object = modelIterators.get( nextToken );
                if ( object == null ) {
                    throw new ExporterException( "for-each does not support [" + nextToken + "]" );
                }
                exporters.add( object );
            }
        }

        Iterator it = exporters.iterator();
        while ( it.hasNext() ) {
            ModelIterator mit = (ModelIterator) it.next();
            mit.process( this );
        }
    }

    protected void exportComponent(Map additionalContext, POJOClass element) {
        exportPOJO( additionalContext, element );
    }

    protected void exportPersistentClass(Map additionalContext, POJOClass element) {
        exportPOJO( additionalContext, element );
    }

    protected void exportPOJO(Map additionalContext, POJOClass element) {
        TemplateProducer producer = new TemplateProducer( getTemplateHelper(), getArtifactCollector() );
        additionalContext.put( "pojo", element );
        additionalContext.put( "clazz", element.getDecoratedObject() );
        String filename = resolveFilename( element );
        if ( filename.endsWith( ".java" ) && filename.indexOf( '$' ) >= 0 ) {
            log.warn( "Filename for " + getClassNameForFile( element ) + " contains a $. Innerclass generation is not supported." );
        }
        producer.produce(
                additionalContext,
                getTemplateName(),
                new File( getOutputDirectory(), filename ),
                templateName,
                element.toString()
        );
    }

    protected String resolveFilename(POJOClass element) {
        String filename = StringHelper.replace( filePattern, "{class-name}", getClassNameForFile( element ) );
        String packageLocation = StringHelper.replace( getPackageNameForFile( element ), ".", "/" );
        if ( StringHelper.isEmpty( packageLocation ) ) {
            packageLocation = "."; // done to ensure default package classes doesn't end up in the root of the filesystem when outputdir=""
        }
        filename = StringHelper.replace( filename, "{package-name}", packageLocation );
        return filename;
    }

    protected String getPackageNameForFile(POJOClass element) {
        return element.getPackageName();
    }

    protected String getClassNameForFile(POJOClass element) {
        return element.getDeclarationName();
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public String getFilePattern() {
        return filePattern;
    }
}
