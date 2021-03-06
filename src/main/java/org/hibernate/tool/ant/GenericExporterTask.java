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
 * Created on 14-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import org.apache.tools.ant.BuildException;

import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;

/**
 * @author max
 */
public class GenericExporterTask extends ExporterTask {

    public GenericExporterTask(HibernateToolTask parent) {
        super( parent );
    }

    String templateName;
    String exporterClass;
    String filePattern;
    String forEach;

    /**
     * The FilePattern defines the pattern used to generate files.
     *
     * @param filePattern
     */
    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public void setForEach(String forEach) {
        this.forEach = forEach;
    }

    public void setTemplate(String templateName) {
        this.templateName = templateName;
    }

    public void setExporterClass(String exporterClass) {
        this.exporterClass = exporterClass;
    }

    protected Exporter createExporter() {
        if ( exporterClass == null ) {
            return new GenericExporter();
        }
        else {
            try {
                Class theClass = ReflectHelper.classForName( exporterClass );
                return (Exporter) theClass.newInstance();
            }
            catch ( ClassNotFoundException e ) {
                throw new BuildException( "Could not find custom exporter class: " + exporterClass, e );
            }
            catch ( InstantiationException e ) {
                throw new BuildException( "Could not create custom exporter class: " + exporterClass, e );
            }
            catch ( IllegalAccessException e ) {
                throw new BuildException( "Could not access custom exporter class: " + exporterClass, e );
            }
        }
    }

    protected Exporter configureExporter(Exporter exp) {
        super.configureExporter( exp );

        if ( exp instanceof GenericExporter ) {
            GenericExporter exporter = (GenericExporter) exp;
            if ( filePattern != null ) {
                exporter.setFilePattern( filePattern );
            }
            if ( templateName != null ) {
                exporter.setTemplateName( templateName );
            }
            if ( forEach != null ) {
                exporter.setForEach( forEach );
            }
        }

        return exp;
    }

    public String getName() {
        StringBuffer buf = new StringBuffer( "generic exporter" );
        if ( exporterClass != null ) {
            buf.append( "class: " + exporterClass );
        }
        if ( templateName != null ) {
            buf.append( "template: " + templateName );
        }
        return buf.toString();
    }
}
