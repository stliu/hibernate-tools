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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnotationBuilder {

    String annotation;
    LinkedHashMap attributes = new LinkedHashMap();

    public static AnnotationBuilder createAnnotation(String annotation) {
        return new AnnotationBuilder( annotation );
    }

    protected AnnotationBuilder(String annotation) {
        this.annotation = annotation;
    }

    public AnnotationBuilder addAttribute(String name, String[] values) {
        if ( values != null && values.length > 0 ) {
            attributes.put( name, values );
        }
        return this;
    }

    public AnnotationBuilder addAttribute(String name, String value) {
        if ( value != null ) {
            addAttribute( name, new String[] { value } );
        }
        return this;
    }


    public AnnotationBuilder resetAnnotation(String annotationName) {
        this.annotation = annotationName;
        clearAttributes();
        return this;
    }

    private AnnotationBuilder clearAttributes() {
        attributes.clear();
        return this;
    }

    public String getResult() {
        StringBuffer b = new StringBuffer( "@" );
        b.append( annotation );
        if ( attributes.isEmpty() ) {
            return b.toString();
        }
        else {
            b.append( "(" );
            Iterator elements = attributes.entrySet().iterator();
            boolean addedBefore = false;
            while ( elements.hasNext() ) {
                Map.Entry element = (Map.Entry) elements.next();

                String[] s = (String[]) element.getValue();
                if ( s.length == 0 ) {
                    addedBefore = false;
                    continue;
                }
                else {
                    if ( addedBefore ) {
                        b.append( ", " );
                    }
                    String key = (String) element.getKey();
                    b.append( key ).append( "=" );
                    attributeToString( b, s );

                    addedBefore = true;
                }
            }
            b.append( ")" );
        }
        return b.toString();
    }

    private void attributeToString(StringBuffer buffer, String[] values) {
        if ( values.length > 1 ) {
            buffer.append( "{" );
        }

        for ( int i = 0; i < values.length; i++ ) {
            buffer.append( values[i] );
            if ( i < values.length - 1 ) {
                buffer.append( ", " );
            }
        }

        if ( values.length > 1 ) {
            buffer.append( "}" );
        }

    }

    public void addQuotedAttributes(String name, Iterator iterator) {
        List values = new ArrayList();
        while ( iterator.hasNext() ) {
            String element = iterator.next().toString();
            values.add( quote( element ) );
        }
        addAttribute( name, (String[]) values.toArray( new String[values.size()] ) );
    }

    public void addAttributes(String name, Iterator iterator) {
        List values = new ArrayList();
        while ( iterator.hasNext() ) {
            String element = iterator.next().toString();
            values.add( element );
        }
        addAttribute( name, (String[]) values.toArray( new String[values.size()] ) );
    }

    private String quote(String element) {
        return "\"" + element + "\"";
    }

    public AnnotationBuilder addQuotedAttribute(String name, String value) {
        if ( value != null ) {
            addAttribute( name, quote( value ) );
        }
        return this;
    }


    public String toString() {
        return getResult();
    }

    public String getAttributeAsString(String name) {
        StringBuffer buffer = new StringBuffer();
        String[] object = (String[]) attributes.get( name );
        if ( object == null ) {
            return null;
        }
        else {
            attributeToString( buffer, object );
            return buffer.toString();
        }
    }


}

