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
 * Created on 25-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import org.apache.tools.ant.BuildException;

import org.hibernate.cfg.Configuration;
import org.hibernate.internal.util.ReflectHelper;

/**
 * Class that uses reflection to load AnnotatioConfiguration.
 * Done to avoid jdk 1.5 compile dependency in tools.
 *
 * @author max
 */
public class AnnotationConfigurationTask extends ConfigurationTask {

    public AnnotationConfigurationTask() {
        setDescription( "Hibernate Annotation/EJB3 Configuration" );
    }

    protected Configuration createConfiguration() {
        try {
            Class clazz = ReflectHelper.classForName(
                    "org.hibernate.cfg.AnnotationConfiguration",
                    AnnotationConfigurationTask.class
            );
            return (Configuration) clazz.newInstance();
        }
        catch ( Throwable t ) {
            throw new BuildException(
                    "Problems in creating a AnnotationConfiguration. Have you remembered to add it to the classpath ?",
                    t
            );
        }
    }

    protected void validateParameters() throws BuildException {
        super.validateParameters();
        if ( getConfigurationFile() == null ) {
            log( "No hibernate.cfg.xml configuration provided. Annotated classes/packages is only configurable via hibernate.cfg.xml" );
        }
    }

}
