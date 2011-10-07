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

package persistentclasses;

import java.util.HashSet;
import java.util.Set;

/**
 * @author max
 */
public class Orders {

    Long id;

    String name;

    Set items = new HashSet();
    Set items_1 = new HashSet();


    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
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
     * @return Returns the setOfItem.
     */
    public Set getItemsForOrderId() {
        return items;
    }

    /**
     * @param items The setOfItem to set.
     */
    public void setItemsForOrderId(Set items) {
        this.items = items;
    }

    /**
     * @return Returns the setOfItem_1.
     */
    public Set getItemsForRelatedOrderId() {
        return items_1;
    }

    /**
     * @param items_1 The setOfItem_1 to set.
     */
    public void setItemsForRelatedOrderId(Set items_1) {
        this.items_1 = items_1;
    }
}
