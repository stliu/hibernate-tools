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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

/**
 * Creates domain model abstract base classes from .hbm files
 *
 * @author Alex Kalinovsky
 */
public class DAONewExporter extends GenericExporter {

    // Store file pattern because it's declared private in GenericTemplateExporter
    protected String filePattern;

    public DAONewExporter(Configuration cfg, ServiceRegistry serviceRegistry, File outputdir) {
        super( cfg, serviceRegistry, outputdir );
    }

    public DAONewExporter() {
    }

    protected void setupContext() {
        if ( !getProperties().containsKey( "ejb3" ) ) {
            getProperties().put( "ejb3", "false" );
        }
        if ( !getProperties().containsKey( "jdk5" ) ) {
            getProperties().put( "jdk5", "false" );
        }

        initFilePattern();
        setTemplateName( getProperty( "hibernatetool.template_name" ) );

        super.setupContext();
    }

    private void initFilePattern() {
        filePattern = getProperty( "hibernatetool.file_pattern" );
        if ( filePattern == null ) {
            throw new IllegalStateException( "Expected parameter file_pattern is not found" );
        }
        filePattern = replaceParameters( filePattern, getProperties() );
        setFilePattern( filePattern );
        log.debug( "File pattern set to " + filePattern );
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// USEFUL CODE STARTS HERE //////////////////////////////////////

    /**
     * Helper method to lookup a property
     */
    public String getProperty(String key) {
        return (String) getProperties().get( key );
    }

    /**
     * Override to control file overwriting via isOverrite() method
     */
    public void doStart() {
        boolean doExport = true;
        if ( filePattern != null && filePattern.indexOf( "{class-name}" ) == -1 ) {
            File file = new File( getOutputDirectory(), filePattern );
            if ( file.exists() && !isOverwrite() ) {
                log.warn( "Skipping the generation of file " + file + " because target already exists" );
                doExport = false;
            }
        }
        if ( doExport ) {
            super.doStart();
        }
    }

    /**
     * Override to avoid overwriting the existing files
     * In the final version this should be moved to GenericExporter
     */
    protected void exportPOJO(Map additionalContext, POJOClass element) {
        String filename = resolveFilename( element );
        File file = new File( getOutputDirectory(), filename );
        if ( file.exists() && !isOverwrite() ) {
            log.warn( "Skipping the generation of file " + file + " because target already exists" );
        }
        else {
            super.exportPOJO( additionalContext, element );
        }
    }

    /**
     * Checks if the file overwriting is true (default) or false
     *
     * @return
     */
    public boolean isOverwrite() {
        return "true".equalsIgnoreCase( (String) getProperties().get( "hibernatetool.overwrite" ) );
    }

    /**
     * Helper method that replaces all parameters in a given pattern
     *
     * @param pattern String with parameters surrounded with braces, for instance "Today is {day} day of {month}"
     * @param paramValues map with key-value pairs for parameter values
     *
     * @return string where parameters are replaced with their values
     */
    public String replaceParameters(String pattern, Map paramValues) {
        Matcher matcher = Pattern.compile( "\\{(.*?)\\}" ).matcher( pattern );
        String output = pattern;
        while ( matcher.find() ) {
            String param = matcher.group( 1 );
            String value = (String) paramValues.get( param );
            if ( value != null ) {
                value = value.replace( '.', '/' );
                output = output.replaceAll( "\\{" + param + "\\}", value );
            }
        }
        return output;
    }

}
