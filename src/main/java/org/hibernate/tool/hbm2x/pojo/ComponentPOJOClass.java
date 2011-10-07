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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;

public class ComponentPOJOClass extends BasicPOJOClass {

    private Component clazz;

    public ComponentPOJOClass(Component component, Cfg2JavaTool cfg) {
        super( component, cfg );
        this.clazz = component;
        init();
    }

    protected String getMappedClassName() {
        return clazz.getComponentClassName();
    }

    public String getExtends() {
        String extendz = "";

        if ( isInterface() ) {
            if ( clazz.getMetaAttribute( EXTENDS ) != null ) {
                if ( !"".equals( extendz ) ) {
                    extendz += ",";
                }
                extendz += getMetaAsString( EXTENDS, "," );
            }
        }
        else if ( clazz.getMetaAttribute( EXTENDS ) != null ) {
            extendz = getMetaAsString( EXTENDS, "," );
        }

        return "".equals( extendz ) ? null : extendz;
    }

    public String getImplements() {
        List interfaces = new ArrayList();

        //	implement proxy, but NOT if the proxy is the class it self!
        if ( !isInterface() ) {
            if ( clazz.getMetaAttribute( IMPLEMENTS ) != null ) {
                interfaces.addAll( clazz.getMetaAttribute( IMPLEMENTS ).getValues() );
            }
            interfaces.add( Serializable.class.getName() ); // TODO: is this "nice" ? shouldn't it be a user choice ?
        }
        else {
            // interfaces can't implement suff
        }


        if ( interfaces.size() > 0 ) {
            StringBuffer sbuf = new StringBuffer();
            for ( Iterator iter = interfaces.iterator(); iter.hasNext(); ) {
                //sbuf.append(JavaTool.shortenType(iter.next().toString(), pc.getImports() ) );
                sbuf.append( iter.next() );
                if ( iter.hasNext() ) {
                    sbuf.append( "," );
                }
            }
            return sbuf.toString();
        }
        else {
            return null;
        }
    }

    public Iterator getAllPropertiesIterator() {
        return clazz.getPropertyIterator();
    }

    public boolean isComponent() {
        return true;
    }

    public boolean hasIdentifierProperty() {
        return false;
    }

    public boolean needsAnnTableUniqueConstraints() {
        return false;
    }

    public String generateBasicAnnotation(Property property) {
        return "";
    }

    public String generateAnnIdGenerator() {
        return "";
    }

    public String generateAnnTableUniqueConstraint() {
        return "";
    }

    public Object getDecoratedObject() {
        return clazz;
    }

    public boolean isSubclass() {
        return false;
    }

    public List getPropertiesForFullConstructor() {
        List res = new ArrayList();

        Iterator iter = getAllPropertiesIterator();
        while ( iter.hasNext() ) {
            res.add( iter.next() );
        }
        return res;
    }

    public List getPropertyClosureForFullConstructor() {
        return getPropertiesForFullConstructor();
    }

    public List getPropertyClosureForSuperclassFullConstructor() {
        return CollectionHelper.EMPTY_LIST;
    }

    public List getPropertiesForMinimalConstructor() {
        List res = new ArrayList();
        Iterator iter = getAllPropertiesIterator();
        while ( iter.hasNext() ) {
            Property prop = (Property) iter.next();
            if ( isRequiredInConstructor( prop ) ) {
                res.add( prop );
            }
        }
        return res;
    }

    public List getPropertyClosureForMinimalConstructor() {
        return getPropertiesForMinimalConstructor();
    }

    public List getPropertyClosureForSuperclassMinimalConstructor() {
        return CollectionHelper.EMPTY_LIST;
    }

    /*
      * @see org.hibernate.tool.hbm2x.pojo.POJOClass#getSuperClass()
      */
    public POJOClass getSuperClass() {
        return null;
    }

    public String toString() {
        return "Component: " + ( clazz == null ? "<none>" : clazz.getComponentClassName() );
    }

    public Property getIdentifierProperty() {
        return null;
    }

    public boolean hasVersionProperty() {
        return false;
    }

    /*
      * @see org.hibernate.tool.hbm2x.pojo.POJOClass#getVersionProperty()
      */
    public Property getVersionProperty() {
        return null;
    }
}
