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

package org.hibernate.tool.hbm2x.pojo;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hibernate.mapping.Property;

/**
 * Helper iterator to ignore "backrefs" properties in hibernate mapping model.
 *
 * @author Max Rydahl Andersen
 */
public class SkipBackRefPropertyIterator implements Iterator {

    private Iterator delegate;

    private Property backLog;

    public SkipBackRefPropertyIterator(Iterator iterator) {
        delegate = iterator;
    }

    public boolean hasNext() {
        if ( backLog != null ) {
            return true;
        }
        else if ( delegate.hasNext() ) {
            Property nextProperty = (Property) delegate.next();
            while ( nextProperty.isBackRef() && delegate.hasNext() ) {
                nextProperty = (Property) delegate.next();
            }
            if ( !nextProperty.isBackRef() ) {
                backLog = nextProperty;
                return true;
            }
        }
        return false;
    }

    public Object next() {
        if ( backLog != null ) {
            Property p = backLog;
            backLog = null;
            return p;
        }
        Property nextProperty = (Property) delegate.next();
        while ( nextProperty.isBackRef() && delegate.hasNext() ) {
            nextProperty = (Property) delegate.next();
        }
        if ( nextProperty.isBackRef() ) {
            throw new NoSuchElementException();
        }
        return nextProperty;
    }

    public void remove() {
        throw new UnsupportedOperationException( "remove() not allowed" );
    }

}
