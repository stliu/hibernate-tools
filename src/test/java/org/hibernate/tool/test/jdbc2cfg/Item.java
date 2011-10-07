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

package org.hibernate.tool.test.jdbc2cfg;

public class Item {

    Long childId;

    Orders order;
    Orders relatedorderId;
    String name;

    /**
     * @return Returns the id.
     */
    public Long getChildId() {
        return childId;
    }

    /**
     * @param id The id to set.
     */
    public void setChildId(Long id) {
        this.childId = id;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the order.
     */
    public Orders getOrderId() {
        return order;
    }

    /**
     * @param order The order to set.
     */
    public void setOrderId(Orders order) {
        this.order = order;
    }

    /**
     * @return Returns the order.
     */
    public Orders getOrdersByOrderId() {
        return order;
    }

    /**
     * @param order The order to set.
     */
    public void setOrdersByOrderId(Orders order) {
        this.order = order;
    }

    /**
     * @return Returns the relatedorderId.
     */
    public Orders getOrdersByRelatedOrderId() {
        return relatedorderId;
    }

    /**
     * @param relatedorderId The relatedorderId to set.
     */
    public void setOrdersByRelatedOrderId(Orders relatedorderId) {
        this.relatedorderId = relatedorderId;
    }
}
