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
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.Serializable;
import java.util.Date;

public class FooComponent implements Serializable {

    int count;
    String name;
    Date[] importantDates;
    FooComponent subcomponent;
    Fee fee = new Fee();
    GlarchProxy glarch;

    public boolean equals(Object that) {
        FooComponent fc = (FooComponent) that;
        return count == fc.count;
    }

    public int hashCode() {
        return count;
    }

    public String toString() {
        String result = "FooComponent: " + name + "=" + count;
        result += "; dates=[";
        if ( importantDates != null ) {
            for ( int i = 0; i < importantDates.length; i++ ) {
                result += ( i == 0 ? "" : ", " ) + importantDates[i];
            }
        }
        result += "]";
        if ( subcomponent != null ) {
            result += " (" + subcomponent + ")";
        }
        return result;
    }

    public FooComponent() {
    }

    FooComponent(String name, int count, Date[] dates, FooComponent subcomponent) {
        this.name = name;
        this.count = count;
        this.importantDates = dates;
        this.subcomponent = subcomponent;
    }

    FooComponent(String name, int count, Date[] dates, FooComponent subcomponent, Fee fee) {
        this.name = name;
        this.count = count;
        this.importantDates = dates;
        this.subcomponent = subcomponent;
        this.fee = fee;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date[] getImportantDates() {
        return importantDates;
    }

    public void setImportantDates(Date[] importantDates) {
        this.importantDates = importantDates;
    }

    public FooComponent getSubcomponent() {
        return subcomponent;
    }

    public void setSubcomponent(FooComponent subcomponent) {
        this.subcomponent = subcomponent;
    }

    private String getNull() {
        return null;
    }

    private void setNull(String str) throws Exception {
        if ( str != null ) {
            throw new Exception( "null component property" );
        }
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public GlarchProxy getGlarch() {
        return glarch;
    }

    public void setGlarch(GlarchProxy glarch) {
        this.glarch = glarch;
    }

}







