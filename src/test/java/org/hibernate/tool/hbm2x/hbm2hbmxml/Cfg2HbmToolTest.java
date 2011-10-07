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

package org.hibernate.tool.hbm2x.hbm2hbmxml;



import org.junit.Test;
 import static org.junit.Assert.*;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;

/**
 * @author Dmitry Geraskov
 */
public class Cfg2HbmToolTest  {

    @Test
    public void testNeedsTable() {
        Cfg2HbmTool c2h = new Cfg2HbmTool();
        PersistentClass pc = new RootClass();
        assertTrue( c2h.needsTable( pc ) );
        assertTrue( c2h.needsTable( new JoinedSubclass( pc ) ) );
        assertTrue( c2h.needsTable( new UnionSubclass( pc ) ) );
        assertFalse( c2h.needsTable( new SingleTableSubclass( pc ) ) );
        assertFalse( c2h.needsTable( new Subclass( pc ) ) );

    }



}
