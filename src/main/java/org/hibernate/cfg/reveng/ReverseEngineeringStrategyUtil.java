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

package org.hibernate.cfg.reveng;

import java.util.HashSet;
import java.util.Set;

final public class ReverseEngineeringStrategyUtil {

    private static Set RESERVED_KEYWORDS;

    static {
        RESERVED_KEYWORDS = new HashSet();

        RESERVED_KEYWORDS.add( "abstract" );
        RESERVED_KEYWORDS.add( "continue" );
        RESERVED_KEYWORDS.add( "for" );
        RESERVED_KEYWORDS.add( "new" );
        RESERVED_KEYWORDS.add( "switch" );
        RESERVED_KEYWORDS.add( "assert" );
        RESERVED_KEYWORDS.add( "default" );
        RESERVED_KEYWORDS.add( "goto" );
        RESERVED_KEYWORDS.add( "package" );
        RESERVED_KEYWORDS.add( "synchronized" );
        RESERVED_KEYWORDS.add( "boolean" );
        RESERVED_KEYWORDS.add( "do" );
        RESERVED_KEYWORDS.add( "if" );
        RESERVED_KEYWORDS.add( "private" );
        RESERVED_KEYWORDS.add( "this" );
        RESERVED_KEYWORDS.add( "break" );
        RESERVED_KEYWORDS.add( "double" );
        RESERVED_KEYWORDS.add( "implements" );
        RESERVED_KEYWORDS.add( "protected" );
        RESERVED_KEYWORDS.add( "throw" );
        RESERVED_KEYWORDS.add( "byte" );
        RESERVED_KEYWORDS.add( "else" );
        RESERVED_KEYWORDS.add( "import" );
        RESERVED_KEYWORDS.add( "public" );
        RESERVED_KEYWORDS.add( "throws" );
        RESERVED_KEYWORDS.add( "case" );
        RESERVED_KEYWORDS.add( "enum" );
        RESERVED_KEYWORDS.add( "instanceof" );
        RESERVED_KEYWORDS.add( "return" );
        RESERVED_KEYWORDS.add( "transient" );
        RESERVED_KEYWORDS.add( "catch" );
        RESERVED_KEYWORDS.add( "extends" );
        RESERVED_KEYWORDS.add( "int" );
        RESERVED_KEYWORDS.add( "short" );
        RESERVED_KEYWORDS.add( "try" );
        RESERVED_KEYWORDS.add( "char" );
        RESERVED_KEYWORDS.add( "final" );
        RESERVED_KEYWORDS.add( "interface" );
        RESERVED_KEYWORDS.add( "static" );
        RESERVED_KEYWORDS.add( "void" );
        RESERVED_KEYWORDS.add( "class" );
        RESERVED_KEYWORDS.add( "finally" );
        RESERVED_KEYWORDS.add( "long" );
        RESERVED_KEYWORDS.add( "strictfp" );
        RESERVED_KEYWORDS.add( "volatile" );
        RESERVED_KEYWORDS.add( "const" );
        RESERVED_KEYWORDS.add( "float" );
        RESERVED_KEYWORDS.add( "native" );
        RESERVED_KEYWORDS.add( "super" );
        RESERVED_KEYWORDS.add( "while" );
    }

    private ReverseEngineeringStrategyUtil() {

    }

    /**
     * Converts a database name (table or column) to a java name (first letter capitalised).
     * employee_name -> EmployeeName.
     *
     * Derived from middlegen's dbnameconverter.
     *
     * @param s The database name to convert.
     *
     * @return The converted database name.
     */
    public static String toUpperCamelCase(String s) {
        if ( "".equals( s ) ) {
            return s;
        }
        StringBuffer result = new StringBuffer();

        boolean capitalize = true;
        boolean lastCapital = false;
        boolean lastDecapitalized = false;
        String p = null;
        for ( int i = 0; i < s.length(); i++ ) {
            String c = s.substring( i, i + 1 );
            if ( "_".equals( c ) || " ".equals( c ) || "-".equals( c ) ) {
                capitalize = true;
                continue;
            }

            if ( c.toUpperCase().equals( c ) ) {
                if ( lastDecapitalized && !lastCapital ) {
                    capitalize = true;
                }
                lastCapital = true;
            }
            else {
                lastCapital = false;
            }

            //if(forceFirstLetter && result.length()==0) capitalize = false;

            if ( capitalize ) {
                if ( p == null || !p.equals( "_" ) ) {
                    result.append( c.toUpperCase() );
                    capitalize = false;
                    p = c;
                }
                else {
                    result.append( c.toLowerCase() );
                    capitalize = false;
                    p = c;
                }
            }
            else {
                result.append( c.toLowerCase() );
                lastDecapitalized = true;
                p = c;
            }

        }
        String r = result.toString();
        return r;
    }

    static public String simplePluralize(String singular) {
        char last = singular.charAt( singular.length() - 1 );
        switch ( last ) {
            case 'x':
            case 's':
                singular += "es";
                break;
            case 'y':
                singular = singular.substring( 0, singular.length() - 1 ) + "ies";
                break;
            default:
                singular += "s";
        }
        return singular;
    }

    static public boolean isReservedJavaKeyword(String str) {
        return RESERVED_KEYWORDS.contains( str );
    }
}