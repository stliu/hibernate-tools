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
 * Created on 2004-12-01
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.Configuration;

/**
 * @author max and david
 */
public interface Exporter {

    /**
     * @param cfg An Hibernate {@link org.hibernate.Configuration} or subclass instance that defines the hibernate meta model to be exported.
     */
    public void setConfiguration(Configuration cfg);

    public Configuration getConfiguration();

    /**
     * @param file Base directory to be used for generated files.
     */
    public void setOutputDirectory(File file);

    public File getOutputDirectory();

    /**
     * @param templatePath array of directories used sequentially to lookup templates
     */
    public void setTemplatePath(String[] templatePath);

    public String[] getTemplatePath();

    /**
     * @param properties set of properties to be used by exporter.
     */
    public void setProperties(Properties properties);

    public Properties getProperties();

    /**
     * @param collector Instance to be consulted when adding a new file.
     */
    public void setArtifactCollector(ArtifactCollector collector);

    /**
     * @return artifact collector
     */
    public ArtifactCollector getArtifactCollector();

    /**
     * Called when exporter should start generating its output
     */
    public void start();

}
