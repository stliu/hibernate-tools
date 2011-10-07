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
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.hibernate.tool.ide.completion;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author leon
 */
public class CompletionHelperTest {

    @Test
    public void testGetCanonicalPath() {
        List qts = new ArrayList();
        qts.add( new EntityNameReference( "Article", "art" ) );
        qts.add( new EntityNameReference( "art.descriptions", "descr" ) );
        qts.add( new EntityNameReference( "descr.name", "n" ) );
        assertEquals(
                "Invalid path",
                "Article/descriptions/name/locale",
                CompletionHelper.getCanonicalPath( qts, "n.locale" )
        );
        assertEquals( "Invalid path", "Article/descriptions", CompletionHelper.getCanonicalPath( qts, "descr" ) );
        //
        qts.clear();
        qts.add( new EntityNameReference( "com.company.Clazz", "clz" ) );
        qts.add( new EntityNameReference( "clz.attr", "a" ) );
        assertEquals( "Invalid path", "com.company.Clazz/attr", CompletionHelper.getCanonicalPath( qts, "a" ) );
        //
        qts.clear();
        qts.add( new EntityNameReference( "Agga", "a" ) );
        assertEquals( "Invalid path", "Agga", CompletionHelper.getCanonicalPath( qts, "a" ) );
    }

    @Test
    public void testStackOverflowInGetCanonicalPath() {
        List qts = new ArrayList();
        qts.add( new EntityNameReference( "Article", "art" ) );
        qts.add( new EntityNameReference( "art.stores", "store" ) );
        qts.add( new EntityNameReference( "store.articles", "art" ) );
        // This should not result in a stack overflow
        CompletionHelper.getCanonicalPath( qts, "art" );
    }


}
