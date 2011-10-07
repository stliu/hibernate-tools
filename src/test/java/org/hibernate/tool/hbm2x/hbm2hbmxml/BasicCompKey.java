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

/*
* Created on 16/01/2005
*/
package org.hibernate.tool.hbm2x.hbm2hbmxml;

/**
 * Testing class for cfg2hbm generating hbms.
 *
 * @author David Channon (with the help of hbm2java)
 */
public class BasicCompKey implements java.io.Serializable {

    // Fields
    private java.lang.String customerId;
    private java.lang.Integer orderNumber;
    private java.lang.String productId;

    // Constructors

    /**
     * default constructor
     */
    public BasicCompKey() {
    }

    // Property accessors

    /**
     */
    public java.lang.String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(java.lang.String customerId) {
        this.customerId = customerId;
    }

    /**
     */
    public java.lang.Integer getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(java.lang.Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     */
    public java.lang.String getProductId() {
        return this.productId;
    }

    public void setProductId(java.lang.String productId) {
        this.productId = productId;
    }
}
