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

public class SQLTypeMapping implements Comparable {

    //static public final int UNKNOWN_TYPE = Integer.MAX_VALUE;
    public static final int UNKNOWN_LENGTH = Integer.MAX_VALUE;
    public static final int UNKNOWN_PRECISION = Integer.MAX_VALUE;
    public static final int UNKNOWN_SCALE = Integer.MAX_VALUE;
    public static final Boolean UNKNOWN_NULLABLE = null;

    private final int jdbcType;
    private int length = UNKNOWN_LENGTH;
    private int precision = UNKNOWN_PRECISION;
    private int scale = UNKNOWN_SCALE;
    private Boolean nullable;

    private String hibernateType;

    public SQLTypeMapping(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    /*public void setJDBCType(int jdbcType) {
         this.jdbcType = jdbcType;
     }*/

    public SQLTypeMapping(int sqlType, int length, int precision, int scale, Boolean nullable) {
        this.jdbcType = sqlType;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
        this.nullable = nullable;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setHibernateType(String hibernateType) {
        this.hibernateType = hibernateType;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public int getJDBCType() {
        return jdbcType;
    }

    public String getHibernateType() {
        return hibernateType;
    }

    public int getLength() {
        return length;
    }

    public String toString() {
        return getJDBCType() + " l:" + getLength() + " p:" + getPrecision() + " s:" + getScale() + " n:" + getNullable() + " ht:" + getHibernateType();
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean match(int matchjdbctype, int matchlength, int matchprecision, int matchscale, boolean matchnullable) {
        if ( matchjdbctype == this.jdbcType ) {// this always need to be exact
            if ( matchlength == this.length || this.length == UNKNOWN_LENGTH ) {
                if ( matchprecision == this.precision || this.precision == UNKNOWN_PRECISION ) {
                    if ( matchscale == this.scale || this.scale == UNKNOWN_SCALE ) {
                        if ( this.nullable == UNKNOWN_NULLABLE || nullable.equals( Boolean.valueOf( matchnullable ) ) ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public int compareTo(Object o) {
        if ( o == null ) {
            return 1;
        }

        SQLTypeMapping other = (SQLTypeMapping) o;

        if ( this.jdbcType == other.jdbcType ) {
            if ( this.length == other.length ) {
                if ( this.precision == other.precision ) {
                    if ( this.scale == other.scale ) {
                        return compare( this.nullable, other.nullable );
                    }
                    else {
                        return compare( this.scale, other.scale );
                    }
                }
                else {
                    return compare( this.precision, other.precision );
                }
            }
            else {
                return compare( this.length, other.length );
            }
        }
        else {
            return compare( this.jdbcType, other.jdbcType );
        }
    }

    private int compare(int value, int other) {
        if ( value == other ) {
            return 0;
        }
        if ( value > other ) {
            return 1;
        }
        if ( value < other ) {
            return -1;
        }
        throw new IllegalStateException();
    }

    // complete ordering of the tri-state: false, true, UNKNOWN_NULLABLE
    private int compare(Boolean value, Boolean other) {
        if ( value == other ) {
            return 0;
        }
        if ( value == UNKNOWN_NULLABLE ) {
            return 1;
        }
        if ( other == UNKNOWN_NULLABLE ) {
            return -1;
        }
        if ( value.equals( other ) ) {
            return 0;
        }
        if ( value.equals( Boolean.TRUE ) ) {
            return 1;
        }
        else {
            return -1;
        }
    }

    public boolean equals(Object obj) {
        return compareTo( obj ) == 0;
    }

    public int hashCode() {
        return ( jdbcType + length + precision + scale + ( nullable == UNKNOWN_NULLABLE ? 1 : nullable.hashCode() ) ) % 17;
    }


}
