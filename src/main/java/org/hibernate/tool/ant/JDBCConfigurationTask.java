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
 * Created on 15-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import java.io.File;
import java.lang.reflect.Constructor;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.internal.util.ReflectHelper;


/**
 * @author max
 * @author <a href='mailto:the_mindstorm@evolva.ro'>Alexandru Popescu</a>
 */
public class JDBCConfigurationTask extends ConfigurationTask {
    //not expfosed here.
    private boolean preferBasicCompositeIds = true;

    private String reverseEngineeringStrategyClass;
    private String packageName;
    private Path revengFiles;

    private boolean detectOneToOne = true;
    private boolean detectManyToMany = true;
    private boolean detectOptimisticLock = true;

    public JDBCConfigurationTask() {
        setDescription( "JDBC Configuration (for reverse engineering)" );
    }

    protected Configuration createConfiguration() {
        return new JDBCMetaDataConfiguration();
    }

    /* (non-Javadoc)
      * @see org.hibernate.tool.hbm2x.ant.ConfigurationTask#doConfiguration(org.hibernate.cfg.Configuration)
      */
    protected void doConfiguration(Configuration configuration) {
        JDBCMetaDataConfiguration jmdc = (JDBCMetaDataConfiguration) configuration;
        super.doConfiguration( jmdc );

        jmdc.setPreferBasicCompositeIds( preferBasicCompositeIds );

        DefaultReverseEngineeringStrategy defaultStrategy = new DefaultReverseEngineeringStrategy();

        ReverseEngineeringStrategy strategy = defaultStrategy;

        if ( revengFiles != null ) {
            OverrideRepository or = new OverrideRepository();

            String[] fileNames = revengFiles.list();
            for ( int i = 0; i < fileNames.length; i++ ) {
                or.addFile( new File( fileNames[i] ) );
            }
            strategy = or.getReverseEngineeringStrategy( defaultStrategy );
        }

        if ( reverseEngineeringStrategyClass != null ) {
            strategy = loadreverseEngineeringStrategy( reverseEngineeringStrategyClass, strategy );
        }

        ReverseEngineeringSettings qqsettings =
                new ReverseEngineeringSettings( strategy ).setDefaultPackageName( packageName )
                        .setDetectManyToMany( detectManyToMany )
                        .setDetectOneToOne( detectOneToOne )
                        .setDetectOptimisticLock( detectOptimisticLock );

        defaultStrategy.setSettings( qqsettings );
        strategy.setSettings( qqsettings );

        jmdc.setReverseEngineeringStrategy( strategy );

        jmdc.readFromJDBC();
    }


    public void setPackageName(String pkgName) {
        packageName = pkgName;
    }

    public void setReverseStrategy(String fqn) {
        reverseEngineeringStrategyClass = fqn;
    }

    public void setRevEngFile(Path p) {
        revengFiles = p;
    }

    public void setPreferBasicCompositeIds(boolean b) {
        preferBasicCompositeIds = b;
    }

    public void setDetectOneToOne(boolean b) {
        detectOneToOne = b;
    }

    public void setDetectManyToMany(boolean b) {
        detectManyToMany = b;
    }

    public void setDetectOptimisticLock(boolean b) {
        detectOptimisticLock = b;
    }

    private ReverseEngineeringStrategy loadreverseEngineeringStrategy(final String className, ReverseEngineeringStrategy delegate)
            throws BuildException {
        try {
            Class clazz = ReflectHelper.classForName( className );
            Constructor constructor = clazz.getConstructor( new Class[] { ReverseEngineeringStrategy.class } );
            return (ReverseEngineeringStrategy) constructor.newInstance( new Object[] { delegate } );
        }
        catch ( NoSuchMethodException e ) {
            try {
                getProject().log(
                        "Could not find public " + className + "(ReverseEngineeringStrategy delegate) constructor on ReverseEngineeringStrategy. Trying no-arg version.",
                        Project.MSG_VERBOSE
                );
                Class clazz = ReflectHelper.classForName( className );
                ReverseEngineeringStrategy rev = (ReverseEngineeringStrategy) clazz.newInstance();
                getProject().log(
                        "Using non-delegating strategy, thus packagename and revengfile will be ignored.",
                        Project.MSG_INFO
                );
                return rev;
            }
            catch ( Exception eq ) {
                throw new BuildException(
                        "Could not create or find " + className + " with default no-arg constructor",
                        eq
                );
            }
        }
        catch ( Exception e ) {
            throw new BuildException(
                    "Could not create or find " + className + " with one argument delegate constructor",
                    e
            );
        }
    }
}
