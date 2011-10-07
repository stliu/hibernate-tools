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

import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.POJOExporter;

/**
 * @author max
 */
public class Hbm2JavaExporterTask extends ExporterTask {

    boolean ejb3 = false;

    boolean jdk5 = false;

    public Hbm2JavaExporterTask(HibernateToolTask parent) {
        super( parent );
    }

    public void setEjb3(boolean b) {
        ejb3 = b;
    }

    public void setJdk5(boolean b) {
        jdk5 = b;
    }

    protected Exporter configureExporter(Exporter exp) {
        POJOExporter exporter = (POJOExporter) exp;
        super.configureExporter( exp );
        exporter.getProperties().setProperty( "ejb3", "" + ejb3 );
        exporter.getProperties().setProperty( "jdk5", "" + jdk5 );
        return exporter;
    }

    protected Exporter createExporter() {
        return new POJOExporter();
    }

    public String getName() {
        return "hbm2java (Generates a set of .java files)";
    }
}
