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
 * Created on 07-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x.visitor;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.UnionSubclass;

/**
 * @author max
 */
public class HBMTagForPersistentClassVisitor implements PersistentClassVisitor {

    public static final PersistentClassVisitor INSTANCE = new HBMTagForPersistentClassVisitor();

    protected HBMTagForPersistentClassVisitor() {

    }

    public Object accept(RootClass class1) {
        return "class";
    }

    public Object accept(UnionSubclass subclass) {
        return "union-subclass";
    }

    public Object accept(SingleTableSubclass subclass) {
        return "subclass";
    }

    public Object accept(JoinedSubclass subclass) {
        return "joined-subclass";
    }

    public Object accept(Subclass subclass) {
        return "subclass";
    }


}
