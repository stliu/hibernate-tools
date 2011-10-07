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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * exporter for query execution.
 */
public class QueryExporter extends AbstractExporter {

    private String filename;
    private List queryStrings;

    public void doStart() {
        Session session = null;
        SessionFactory sessionFactory = null;
        Transaction transaction = null;
        try {
            sessionFactory = getConfiguration().buildSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            // TODO: this is not the most efficient loop (opening/closing file)
            for ( Iterator iter = queryStrings.iterator(); iter.hasNext(); ) {
                String query = (String) iter.next();

                List list = session.createQuery( query ).list();

                if ( getFileName() != null ) {
                    PrintWriter pw = null;
                    try {
                        File file = new File( getOutputDirectory(), getFileName() );
                        getTemplateHelper().ensureExistence( file );
                        pw = new PrintWriter( new FileWriter( file, true ) );
                        getArtifactCollector().addFile( file, "query-output" );

                        for ( Iterator iter1 = list.iterator(); iter1.hasNext(); ) {
                            Object element = iter1.next();
                            pw.println( element );
                        }

                    }
                    catch ( IOException e ) {
                        throw new ExporterException( "Could not write query output", e );
                    }
                    finally {
                        if ( pw != null ) {
                            pw.flush();
                            pw.close();
                        }
                    }
                }
            }
            transaction.commit();
        }
        catch ( HibernateException he ) {
            if ( transaction != null ) {
                transaction.rollback();
            }
            throw new ExporterException( "Error occured while trying to execute query", he );
        }
        finally {
            if ( session != null ) {
                session.close();
            }
            if ( sessionFactory != null ) {
                sessionFactory.close();
            }


        }
    }

    private String getFileName() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setQueries(List queryStrings) {
        this.queryStrings = queryStrings;
    }

}
