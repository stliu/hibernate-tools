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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.internal.util.StringHelper;

public class ImportContextImpl implements ImportContext {

    Set imports = new TreeSet();
    Set staticImports = new TreeSet();
    Map simpleNames = new HashMap();

    String basePackage = "";

    // TODO: share this somehow, redundant from Cfg2JavaTool
    private static final Map PRIMITIVES = new HashMap();

    static {
        PRIMITIVES.put( "char", "Character" );

        PRIMITIVES.put( "byte", "Byte" );
        PRIMITIVES.put( "short", "Short" );
        PRIMITIVES.put( "int", "Integer" );
        PRIMITIVES.put( "long", "Long" );

        PRIMITIVES.put( "boolean", "Boolean" );

        PRIMITIVES.put( "float", "Float" );
        PRIMITIVES.put( "double", "Double" );

    }

    public ImportContextImpl(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * Add fqcn to the import list. Returns fqcn as needed in source code.
     * Attempts to handle fqcn with array and generics references.
     *
     * e.g.
     * java.util.Collection<org.marvel.Hulk> imports java.util.Collection and returns Collection
     * org.marvel.Hulk[] imports org.marvel.Hulk and returns Hulk
     *
     * @param fqcn
     *
     * @return import string
     */
    public String importType(String fqcn) {
        String result = fqcn;

        String additionalTypePart = null;
        if ( fqcn.indexOf( '<' ) >= 0 ) {
            additionalTypePart = result.substring( fqcn.indexOf( '<' ) );
            result = result.substring( 0, fqcn.indexOf( '<' ) );
            fqcn = result;
        }
        else if ( fqcn.indexOf( '[' ) >= 0 ) {
            additionalTypePart = result.substring( fqcn.indexOf( '[' ) );
            result = result.substring( 0, fqcn.indexOf( '[' ) );
            fqcn = result;
        }

        String pureFqcn = fqcn.replace( '$', '.' );

        boolean canBeSimple = true;


        String simpleName = StringHelper.unqualify( fqcn );
        if ( simpleNames.containsKey( simpleName ) ) {
            String existingFqcn = (String) simpleNames.get( simpleName );
            if ( existingFqcn.equals( pureFqcn ) ) {
                canBeSimple = true;
            }
            else {
                canBeSimple = false;
            }
        }
        else {
            canBeSimple = true;
            simpleNames.put( simpleName, pureFqcn );
            imports.add( pureFqcn );
        }


        if ( inSamePackage( fqcn ) || ( imports.contains( pureFqcn ) && canBeSimple ) ) {
            result = StringHelper.unqualify( result ); // dequalify
        }
        else if ( inJavaLang( fqcn ) ) {
            result = result.substring( "java.lang.".length() );
        }

        if ( additionalTypePart != null ) {
            result = result + additionalTypePart;
        }

        result = result.replace( '$', '.' );
        return result;
    }

    public String staticImport(String fqcn, String member) {
        String local = fqcn + "." + member;
        imports.add( local );
        staticImports.add( local );

        if ( member.equals( "*" ) ) {
            return "";
        }
        else {
            return member;
        }
    }

    private boolean inDefaultPackage(String className) {
        return className.indexOf( "." ) < 0;
    }

    private boolean isPrimitive(String className) {
        return PRIMITIVES.containsKey( className );
    }

    private boolean inSamePackage(String className) {
        String other = StringHelper.qualifier( className );
        return other == basePackage
                || ( other != null && other.equals( basePackage ) );
    }

    private boolean inJavaLang(String className) {
        return "java.lang".equals( StringHelper.qualifier( className ) );
    }

    public String generateImports() {
        StringBuffer buf = new StringBuffer();

        for ( Iterator imps = imports.iterator(); imps.hasNext(); ) {
            String next = (String) imps.next();
            if ( isPrimitive( next ) || inDefaultPackage( next ) || inJavaLang( next ) || inSamePackage( next ) ) {
                // dont add automatically "imported" stuff
            }
            else {
                if ( staticImports.contains( next ) ) {
                    buf.append( "import static " + next + ";\r\n" );
                }
                else {
                    buf.append( "import " + next + ";\r\n" );
                }
            }
        }

        if ( buf.indexOf( "$" ) >= 0 ) {
            return buf.toString();
        }
        return buf.toString();
    }
}
