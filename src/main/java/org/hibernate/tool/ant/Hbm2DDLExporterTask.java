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

import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;

/**
 * @author max
 */
public class Hbm2DDLExporterTask extends ExporterTask {

    boolean exportToDatabase = true;
    boolean scriptToConsole = true;
    boolean schemaUpdate = false;
    String delimiter = ";";
    boolean drop = false;
    boolean create = true;
    boolean format = false;

    String outputFileName = null;
    private boolean haltOnError = false;

    public Hbm2DDLExporterTask(HibernateToolTask parent) {
        super( parent );
    }

    public String getName() {
        return "hbm2ddl (Generates database schema)";
    }

    protected Exporter configureExporter(Exporter exp) {
        Hbm2DDLExporter exporter = (Hbm2DDLExporter) exp;
        super.configureExporter( exp );
        exporter.setExport( exportToDatabase );
        exporter.setConsole( scriptToConsole );
        exporter.setUpdate( schemaUpdate );
        exporter.setDelimiter( delimiter );
        exporter.setDrop( drop );
        exporter.setCreate( create );
        exporter.setFormat( format );
        exporter.setOutputFileName( outputFileName );
        exporter.setHaltonerror( haltOnError );
        return exporter;
    }

    protected Exporter createExporter() {
        Hbm2DDLExporter exporter = new Hbm2DDLExporter(
                parent.getConfiguration(),
                parent.getServiceRegistry(),
                parent.getDestDir()
        );
        return exporter;
    }


    public void setExport(boolean export) {
        exportToDatabase = export;
    }

    /**
     * Run SchemaUpdate instead of SchemaExport
     */
    public void setUpdate(boolean update) {
        this.schemaUpdate = update;
    }

    /**
     * Output sql to console ? (default true)
     */
    public void setConsole(boolean console) {
        this.scriptToConsole = console;
    }

    /**
     * Format the generated sql
     */
    public void setFormat(boolean format) {
        this.format = format;
    }

    /**
     * File out put name (default: empty)
     */
    public void setOutputFileName(String fileName) {
        outputFileName = fileName;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setHaltonerror(boolean haltOnError) {
        this.haltOnError = haltOnError;
    }
}
