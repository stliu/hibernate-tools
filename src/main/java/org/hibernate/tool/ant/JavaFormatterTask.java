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

package org.hibernate.tool.ant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import org.hibernate.tool.hbm2x.ExporterException;
import org.hibernate.tool.ide.formatting.JavaFormatter;

public class JavaFormatterTask extends Task {

    private List fileSets = new ArrayList();
    private boolean failOnError;
    private File configurationFile;

    public void addConfiguredFileSet(FileSet fileSet) {
        fileSets.add( fileSet );
    }

    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    private Properties readConfig(File cfgfile) throws IOException {
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream( new FileInputStream( cfgfile ) );
            final Properties settings = new Properties();
            settings.load( stream );
            return settings;
        }
        catch ( IOException e ) {
            throw e;
        }
        finally {
            if ( stream != null ) {
                try {
                    stream.close();
                }
                catch ( IOException e ) {

                }
            }
        }
    }


    public void execute() throws BuildException {

        Map settings = null;
        if ( configurationFile != null ) {
            try {
                settings = readConfig( configurationFile );
            }
            catch ( IOException e ) {
                throw new BuildException( "Could not read configurationfile " + configurationFile, e );
            }
        }

        File[] files = getFiles();

        int success = 0;
        int failed = 0;

        if ( files.length > 0 ) {

            JavaFormatter formatter = new JavaFormatter( settings );
            for ( int i = 0; i < files.length; i++ ) {
                File file = files[i];
                try {
                    boolean ok = formatter.formatFile( file );
                    if ( !ok ) {
                        failed++;
                        getProject().log( this, "Formatting failed - skipping " + file, Project.MSG_WARN );
                    }
                    else {
                        success++;
                        getProject().log( this, "Formatted " + file, Project.MSG_VERBOSE );
                    }
                }
                catch ( ExporterException ee ) {
                    failed++;
                    if ( failOnError ) {
                        throw new BuildException( "Java formatting failed on " + file, ee );
                    }
                    else {
                        getProject().log(
                                this,
                                "Java formatting failed on " + file + ", " + ee.getLocalizedMessage(),
                                Project.MSG_ERR
                        );
                    }
                }
            }
        }

        getProject().log(
                this,
                "Java formatting of " + files.length + " files completed. Skipped " + failed + " file(s).",
                Project.MSG_INFO
        );

    }

    private File[] getFiles() {

        List files = new LinkedList();
        for ( Iterator i = fileSets.iterator(); i.hasNext(); ) {

            FileSet fs = (FileSet) i.next();
            DirectoryScanner ds = fs.getDirectoryScanner( getProject() );

            String[] dsFiles = ds.getIncludedFiles();
            for ( int j = 0; j < dsFiles.length; j++ ) {
                File f = new File( dsFiles[j] );
                if ( !f.isFile() ) {
                    f = new File( ds.getBasedir(), dsFiles[j] );
                }

                files.add( f );
            }
        }

        return (File[]) files.toArray( new File[files.size()] );
    }

}
