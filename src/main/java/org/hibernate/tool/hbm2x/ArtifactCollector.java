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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.internal.util.collections.CollectionHelper;

/**
 * Callback class that all exporters are given to allow
 * better feedback and processing of the output afterwards.
 *
 * @author Max Rydahl Andersen
 */
public class ArtifactCollector {

    final protected Map<String,List<File>> files = new HashMap<String,List<File>>();

    /**
     * Called to inform that a file has been created by the exporter.
     */
    public void addFile(File file, String type) {
        List<File> existing = files.get( type );
        if ( existing == null ) {
            existing = new ArrayList<File>();
            files.put( type, existing );
        }
        existing.add( file );
    }

    public int getFileCount(String type) {
        List existing = files.get( type );

        return ( existing == null ) ? 0 : existing.size();
    }

    public File[] getFiles(String type) {
        List existing = files.get( type );

        if ( existing == null ) {
            return new File[0];
        }
        else {
            return (File[]) existing.toArray( new File[existing.size()] );
        }
    }

    public Set getFileTypes() {
        return files.keySet();
    }

    public void formatFiles() {
        formatXml( "xml" );
        formatXml( "hbm.xml" );
        formatXml( "cfg.xml" );
    }

    private void formatXml(String type) throws ExporterException {
        List<File> list =  files.get( type );
        if ( CollectionHelper.isNotEmpty( list ) ) {
            for(File xmlFile: list){
               try {
                    XMLPrettyPrinter.prettyPrintFile( XMLPrettyPrinter.getDefaultTidy(), xmlFile, xmlFile, true );
                }
                catch ( IOException e ) {
                    throw new ExporterException( "Could not format XML file: " + xmlFile, e );
                }
            }
        }
    }
}
