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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.MappingException;


/**
 * Utility class for mapping between sqltypes and hibernate type names.
 *
 * @author max (based on parts from Sql2Java from Middlegen)
 */
public final class JDBCToHibernateTypeHelper {

    private JDBCToHibernateTypeHelper() {

    }

    /**
     * The Map containing the preferred conversion type values.
     */
    private static final Map PREFERRED_HIBERNATETYPE_FOR_SQLTYPE = new HashMap();

    static {
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.TINYINT,
                new String[] { "byte", Byte.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.SMALLINT,
                new String[] { "short", Short.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.INTEGER,
                new String[] { "int", Integer.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.BIGINT,
                new String[] { "long", Long.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.REAL,
                new String[] { "float", Float.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.FLOAT,
                new String[] { "double", Double.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.DOUBLE,
                new String[] { "double", Double.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.DECIMAL,
                new String[] { "big_decimal", "big_decimal" }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.NUMERIC,
                new String[] { "big_decimal", "big_decimal" }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.BIT,
                new String[] { "boolean", Boolean.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.BOOLEAN,
                new String[] { "boolean", Boolean.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.CHAR,
                new String[] { "char", Character.class.getName() }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put( Types.VARCHAR, new String[] { "string", "string" } );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.LONGVARCHAR,
                new String[] { "string", "string" }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put( Types.BINARY, new String[] { "binary", "binary" } );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put( Types.VARBINARY, new String[] { "binary", "binary" } );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.LONGVARBINARY,
                new String[] { "binary", "binary" }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put( Types.DATE, new String[] { "date", "date" } );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put( Types.TIME, new String[] { "time", "time" } );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(
                Types.TIMESTAMP,
                new String[] { "timestamp", "timestamp" }
        );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put( Types.CLOB, new String[] { "clob", "clob" } );
        PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put( Types.BLOB, new String[] { "blob", "blob" } );

        //Hibernate does not have any built-in Type for these:
        //preferredJavaTypeForSqlType.put(new Integer(Types.ARRAY), "java.sql.Array");
        //preferredJavaTypeForSqlType.put(new Integer(Types.REF), "java.sql.Ref");
        //preferredJavaTypeForSqlType.put(new Integer(Types.STRUCT), "java.lang.Object");
        //preferredJavaTypeForSqlType.put(new Integer(Types.JAVA_OBJECT), "java.lang.Object");
    }

    /* (non-Javadoc)
    * @see org.hibernate.cfg.JDBCTypeToHibernateTypesStrategy#getPreferredHibernateType(int, int, int, int)
    */
    public static String getPreferredHibernateType(int sqlType, int size, int precision, int scale, boolean nullable, boolean generatedIdentifier) {
        boolean returnNullable = nullable || generatedIdentifier;
        if ( ( sqlType == Types.DECIMAL || sqlType == Types.NUMERIC ) && scale <= 0 ) { // <=
            if ( precision == 1 ) {
                // NUMERIC(1) is a often used idiom for storing boolean thus providing it out of the box.
                return returnNullable ? Boolean.class.getName() : "boolean";
            }
            else if ( precision < 3 ) {
                return returnNullable ? Byte.class.getName() : "byte";
            }
            else if ( precision < 5 ) {
                return returnNullable ? Short.class.getName() : "short";
            }
            else if ( precision < 10 ) {
                return returnNullable ? Integer.class.getName() : "int";
            }
            else if ( precision < 19 ) {
                return returnNullable ? Long.class.getName() : "long";
            }
            else {
                return "big_decimal";
            }
        }

        if ( sqlType == Types.CHAR && size > 1 ) {
            return "string";
        }

        String[] result = (String[]) PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.get( sqlType );

        if ( result == null ) {
            return null;
        }
        else if ( returnNullable ) {
            return result[1];
        }
        else {
            return result[0];
        }
    }

    static Map jdbcTypes; // Name to value
    static Map jdbcTypeValues; // value to Name

    public static String[] getJDBCTypes() {
        checkTypes();

        return (String[]) jdbcTypes.keySet().toArray( new String[jdbcTypes.size()] );
    }

    public static int getJDBCType(String value) {
        checkTypes();

        Integer number = (Integer) jdbcTypes.get( value );

        if ( number == null ) {
            try {
                return Integer.parseInt( value );
            }
            catch ( NumberFormatException nfe ) {
                throw new MappingException( "jdbc-type: " + value + " is not a known JDBC Type nor a valid number" );
            }
        }
        else {
            return number.intValue();
        }
    }

    private static void checkTypes() {
        if ( jdbcTypes == null ) {
            jdbcTypes = new HashMap();
            Field[] fields = Types.class.getFields();
            for ( int i = 0; i < fields.length; i++ ) {
                Field field = fields[i];
                if ( Modifier.isStatic( field.getModifiers() ) ) {
                    try {
                        jdbcTypes.put( field.getName(), field.get( Types.class ) );
                    }
                    catch ( IllegalArgumentException e ) {
                        // ignore
                    }
                    catch ( IllegalAccessException e ) {
                        // ignore
                    }
                }
            }
        }
    }

    public static String getJDBCTypeName(int value) {
        if ( jdbcTypeValues == null ) {
            jdbcTypeValues = new HashMap();
            Field[] fields = Types.class.getFields();
            for ( int i = 0; i < fields.length; i++ ) {
                Field field = fields[i];
                if ( Modifier.isStatic( field.getModifiers() ) ) {
                    try {
                        jdbcTypeValues.put( field.get( Types.class ), field.getName() );
                    }
                    catch ( IllegalArgumentException e ) {
                        // ignore
                    }
                    catch ( IllegalAccessException e ) {
                        // ignore
                    }
                }
            }
        }

        String name = (String) jdbcTypeValues.get( new Integer( value ) );

        if ( name != null ) {
            return name;
        }
        else {
            return "" + value;
        }
    }

    /**
     * @param table
     * @param schema
     * @param catalog
     *
     * @throws SQLException
     */

    //		 scale and precision have numeric column
    public static boolean typeHasScaleAndPrecision(int sqlType) {
        return ( sqlType == Types.DECIMAL || sqlType == Types.NUMERIC
                || sqlType == Types.REAL || sqlType == Types.FLOAT || sqlType == Types.DOUBLE );
    }

    // length is for string column
    public static boolean typeHasLength(int sqlType) {
        return ( sqlType == Types.CHAR || sqlType == Types.DATE
                || sqlType == Types.LONGVARCHAR || sqlType == Types.TIME || sqlType == Types.TIMESTAMP
                || sqlType == Types.VARCHAR );
    }
}

