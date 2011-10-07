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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;

import org.hibernate.internal.util.StringHelper;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.QueryExporter;

public class QueryExporterTask extends ExporterTask {

    private String query = "";
    private String filename;
    List queries = new ArrayList();

    public QueryExporterTask(HibernateToolTask parent) {
        super( parent );
    }

    protected Exporter configureExporter(Exporter exp) {
        QueryExporter exporter = (QueryExporter) exp;
        List queryStrings = new ArrayList();
        if ( StringHelper.isNotEmpty( query ) ) {
            queryStrings.add( query );
        }
        for ( Iterator iter = queries.iterator(); iter.hasNext(); ) {
            HQL hql = (HQL) iter.next();
            if ( StringHelper.isNotEmpty( hql.query ) ) {
                queryStrings.add( hql.query );
            }
        }
        exporter.setQueries( queryStrings );
        exporter.setFilename( filename );
        super.configureExporter( exp );
        return exporter;
    }

    public void validateParameters() {
        super.validateParameters();
        if ( StringHelper.isEmpty( query ) && queries.isEmpty() ) {
            throw new BuildException( "Need to specify at least one query." );
        }

        for ( Iterator iter = queries.iterator(); iter.hasNext(); ) {
            HQL hql = (HQL) iter.next();
            if ( StringHelper.isEmpty( hql.query ) ) {
                throw new BuildException( "Query must not be empty" );
            }
        }
    }

    protected Exporter createExporter() {
        QueryExporter exporter = new QueryExporter();
        return exporter;
    }

    public void addText(String text) {
        if ( StringHelper.isNotEmpty( text ) ) {
            query += trim( text );
        }
    }

    static private String trim(String text) {
        return text.trim();
    }

    public static class HQL {
        String query = "";

        public void addText(String text) {
            if ( StringHelper.isNotEmpty( text ) ) {
                query += trim( text );
            }
        }
    }

    public HQL createHql() {
        HQL hql = new HQL();
        queries.add( hql );
        return hql;
    }

    public void setDestFile(String filename) {
        this.filename = filename;
    }

    public void execute() {
        parent.log( "Executing: [" + query + "]" );
        super.execute();
    }

    public String getName() {
        return "query (Executes queries)";
    }


}
