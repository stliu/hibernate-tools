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

public interface ExporterSettings {

    public final String PREFIX_KEY = "hibernatetool.";

    /**
     * if true exporters are allowed to generate EJB3 constructs
     */
    public final String EJB3 = PREFIX_KEY + "ejb3";

    /**
     * if true then exporters are allowed to generate JDK 5 constructs
     */
    public final String JDK5 = PREFIX_KEY + "jdk5";

    /**
     * the (root) output directory for an exporter
     */
    public final String OUTPUT_DIRECTORY = PREFIX_KEY + "output_directory";

    /**
     * the (root) output directory for an exporter
     */
    public final String TEMPLATE_PATH = PREFIX_KEY + "template_path";


}
