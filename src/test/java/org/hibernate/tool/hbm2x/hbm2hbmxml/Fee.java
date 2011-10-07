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

public class Fee implements Serializable {
    public Fee anotherFee;
    public String fi;
    public String key;
    private FooComponent compon;
    private int count;

    public Fee() {
    }

    public String getFi() {
        return fi;
    }

    public void setFi(String fi) {
        this.fi = fi;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Fee getAnotherFee() {
        return anotherFee;
    }

    public void setAnotherFee(Fee anotherFee) {
        this.anotherFee = anotherFee;
    }


    public FooComponent getCompon() {
        return compon;
    }

    public void setCompon(FooComponent compon) {
        this.compon = compon;
    }

    /**
     * Returns the count.
     *
     * @return int
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the count.
     *
     * @param count The count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

}






