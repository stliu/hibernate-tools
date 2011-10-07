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
 * Created on 13-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import java.io.File;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;

import org.hibernate.tool.hbm2x.Exporter;

/**
 * @author max
 *
 *         Is not actually a ant task, but simply just a task part of a HibernateToolTask
 */
public abstract class ExporterTask {

    // refactor out so not dependent on Ant ?
    protected HibernateToolTask parent;
    Properties properties;
    File destdir;
    private Path templatePath;
    private String templatePrefix = null;

    public ExporterTask(HibernateToolTask parent) {
        this.parent = parent;
        this.properties = new Properties();
    }


    /*final*/
    public void execute() {

        Exporter exporter = configureExporter( createExporter() );
        exporter.start();

    }

    protected abstract Exporter createExporter();

    public File getDestdir() {
        if ( destdir == null ) {
            return parent.getDestDir();
        }
        else {
            return destdir;
        }
    }

    public void setDestdir(File destdir) {
        this.destdir = destdir;
    }

    public void setTemplatePath(Path path) {
        templatePath = path;
    }

    public void setTemplatePrefix(String s) {
        templatePrefix = s;
    }

    public void validateParameters() {
        if ( getDestdir() == null ) {
            throw new BuildException( "destdir must be set, either locally or on <hibernatetool>" );
        }
    }

    public void addConfiguredPropertySet(PropertySet ps) {
        properties.putAll( ps.getProperties() );
    }

    public void addConfiguredProperty(Environment.Variable property) {
        properties.put( property.getKey(), property.getValue() );
    }

    protected Path getTemplatePath() {
        if ( templatePath == null ) {
            return parent.getTemplatePath();
        }
        else {
            return templatePath;
        }
    }


    abstract String getName();

    protected Exporter configureExporter(Exporter exporter) {
        Properties prop = new Properties();
        prop.putAll( parent.getProperties() );
        prop.putAll( properties );
        exporter.setProperties( prop );
        exporter.setConfiguration( parent.getConfiguration() );
        exporter.setOutputDirectory( getDestdir() );
        exporter.setTemplatePath( getTemplatePath().list() );
        return exporter;
    }
}
