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

package org.hibernate.tool.hbmlint.detector;

import org.hibernate.MappingException;
import org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Settings;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbmlint.Issue;
import org.hibernate.tool.hbmlint.IssueCollector;

public class InstrumentationDetector extends EntityModelDetector {

    public String getName() {
        return "instrument";
    }

    private boolean cglibEnabled = false;
    private boolean javassistEnabled = true; //hibernate-core 4.0 has only support javassist for now

//    public void initialize(Configuration cfg, ServiceRegistry serviceRegistry, Settings settings) {
//        super.initialize( cfg, serviceRegistry, settings );

//        cglibEnabled = false;
//        javassistEnabled = false;

//        if ( Environment.getBytecodeProvider() instanceof BytecodeProviderImpl ) {
//            javassistEnabled = true;
//        }
//    }

    public void visit(Configuration cfg, PersistentClass clazz, IssueCollector collector) {
        Class mappedClass;


        try {
            mappedClass = clazz.getMappedClass();
        }
        catch ( MappingException me ) {
            // ignore
            return;
        }

        if ( clazz.isLazy() ) {
            try {
                mappedClass.getConstructor( new Class[0] );
            }
            catch ( SecurityException e ) {
                // ignore
            }
            catch ( NoSuchMethodException e ) {
                collector.reportIssue(
                        new Issue(
                                "LAZY_NO_DEFAULT_CONSTRUCTOR",
                                Issue.NORMAL_PRIORITY,
                                "lazy='true' set for '" + clazz.getEntityName() + "', but class has no default constructor."
                        )
                );
                return;
            }

        }
        else if ( cglibEnabled || javassistEnabled ) {
            Class[] interfaces = mappedClass.getInterfaces();
            boolean cglib = false;
            boolean javaassist = false;
            for ( int i = 0; i < interfaces.length; i++ ) {
                Class intface = interfaces[i];
                if ( intface.getName().equals( "net.sf.cglib.transform.impl.InterceptFieldEnabled" ) ) {
                    cglib = true;
                }
                else if ( intface.getName().equals( "org.hibernate.bytecode.internal.javassist.FieldHandled" ) ) {
                    javaassist = true;
                }
            }

            if ( cglibEnabled && !cglib ) {
                collector.reportIssue(
                        new Issue(
                                "LAZY_NOT_INSTRUMENTED",
                                Issue.HIGH_PRIORITY,
                                "'" + clazz.getEntityName() + "' has lazy='false', but its class '" + mappedClass.getName() + "' has not been instrumented with cglib"
                        )
                );
                return;
            }
            else if ( javassistEnabled && !javaassist ) {
                collector.reportIssue(
                        new Issue(
                                "LAZY_NOT_INSTRUMENTED",
                                Issue.HIGH_PRIORITY,
                                "'" + clazz.getEntityName() + "' has lazy='false', but its class '" + mappedClass.getName() + "' has not been instrumented with javaassist"
                        )
                );
                return;
            }
            else {
                // unknown bytecodeprovider...can't really check for that.
            }

        }
    }
}
