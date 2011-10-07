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

//$Id$
package org.hibernate.tool;


import org.dom4j.io.SAXReader;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.xml.DTDEntityResolver;
import org.hibernate.testing.AfterClassOnce;

public abstract class NonReflectiveTestCase extends BaseTestCase {

    private SessionFactoryImplementor sessionFactory;
    private Session session;

    protected SessionFactoryImplementor sessionFactory() {
        if ( sessionFactory == null ) {
            sessionFactory = (SessionFactoryImplementor) configuration().buildSessionFactory( serviceRegistry() );
        }
        return sessionFactory;
    }

    protected Session openSession() throws HibernateException {
        session = sessionFactory().openSession();
        return session;
    }

    protected Session openSession(Interceptor interceptor) throws HibernateException {
        session = sessionFactory().withOptions().interceptor( interceptor ).openSession();
        return session;
    }

    @AfterClassOnce
    @SuppressWarnings({ "UnusedDeclaration" })
    private void releaseSessionFactory() {
        if ( sessionFactory == null ) {
            return;
        }
        sessionFactory.close();
        sessionFactory = null;
    }

    public SAXReader getSAXReader() {
        SAXReader xmlReader = new SAXReader();
        xmlReader.setEntityResolver( new DTDEntityResolver() );
        xmlReader.setValidation( true );
        return xmlReader;
    }
}