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
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import org.junit.Test;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.pojo.ImportContextImpl;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author max
 */
public class Hbm2JavaInitializationTest extends NonReflectiveTestCase {

    @Test
    public void testFieldInitializationAndTypeNames() {
        PersistentClass classMapping = configuration().getClassMapping( "org.hibernate.tool.hbm2x.Article" );
        Cfg2JavaTool cfg2java = new Cfg2JavaTool();
        POJOClass clazz = cfg2java.getPOJOClass( classMapping );

        Property p = classMapping.getProperty( "AMap" );

        assertEquals(
                "all types should be fully qualified when no importcontext",
                "java.util.Map<java.lang.String,org.hibernate.tool.hbm2x.Article>",
                cfg2java.getJavaTypeName( p, true )
        );
        assertEquals( "Map<String,Article>", cfg2java.getJavaTypeName( p, true, clazz ) );
        assertEquals( "new HashMap<String,Article>(0)", clazz.getFieldInitialization( p, true ) );
        assertEquals( "new HashMap(0)", clazz.getFieldInitialization( p, false ) );

        p = classMapping.getProperty( "aList" );

        assertEquals(
                "lists should not have the index visible in the declaration",
                "List<Article>",
                cfg2java.getJavaTypeName( p, true, clazz )
        );
        assertEquals(
                "all types should be fully qualified when no importcontext",
                "java.util.List<org.hibernate.tool.hbm2x.Article>",
                cfg2java.getJavaTypeName( p, true )
        );

        assertEquals( "new ArrayList<Article>(0)", clazz.getFieldInitialization( p, true ) );
        assertEquals( "new ArrayList(0)", clazz.getFieldInitialization( p, false ) );

        p = classMapping.getProperty( "content" );
        assertEquals( "\"what can I say\"", clazz.getFieldInitialization( p, false ) );

        p = classMapping.getProperty( "bagarticles" );

        assertEquals( "Should be a list via property-type", "java.util.List", cfg2java.getJavaTypeName( p, false ) );
        assertEquals(
                "Should be a a generic'd list when generics=true",
                "java.util.List<org.hibernate.tool.hbm2x.Article>",
                cfg2java.getJavaTypeName( p, true )
        );
        assertEquals( "List<Article>", cfg2java.getJavaTypeName( p, true, clazz ) );
        assertEquals( "new ArrayList<Article>(0)", clazz.getFieldInitialization( p, true ) );
        assertEquals( "new ArrayList(0)", clazz.getFieldInitialization( p, false ) );

        p = classMapping.getProperty( "bagstrings" );

        assertEquals( "Bag's are just a collection", "java.util.Collection", cfg2java.getJavaTypeName( p, false ) );
        assertEquals(
                "Should be a a generic'd collection when generics=true",
                "java.util.Collection<java.lang.String>",
                cfg2java.getJavaTypeName( p, true )
        );
        assertEquals( "Collection<String>", cfg2java.getJavaTypeName( p, true, clazz ) );
        assertEquals( "new ArrayList<String>(0)", clazz.getFieldInitialization( p, true ) );
        assertEquals( "new ArrayList(0)", clazz.getFieldInitialization( p, false ) );

        p = classMapping.getProperty( "bagstrings" );
        assertEquals( "new ArrayList(0)", clazz.getFieldInitialization( p, false ) );

        p = classMapping.getProperty( "naturalSortedArticlesMap" );

        assertEquals( "java.util.SortedMap", cfg2java.getJavaTypeName( p, false ) );
        assertEquals( "SortedMap<String,Article>", cfg2java.getJavaTypeName( p, true, new ImportContextImpl( "" ) ) );
        assertEquals( "new TreeMap<String,Article>()", clazz.getFieldInitialization( p, true ) );
        assertEquals( "new TreeMap()", clazz.getFieldInitialization( p, false ) );

        p = classMapping.getProperty( "sortedArticlesMap" );

        assertEquals( "java.util.SortedMap", cfg2java.getJavaTypeName( p, false ) );
        assertEquals( "SortedMap<String,Article>", cfg2java.getJavaTypeName( p, true, new ImportContextImpl( "" ) ) );

        assertFalse( clazz.generateImports().contains( "import comparator.NoopComparator;" ) );
        assertEquals( "new TreeMap(new NoopComparator())", clazz.getFieldInitialization( p, false ) );
        assertTrue( clazz.generateImports().contains( "import comparator.NoopComparator;" ) );

        assertEquals( "new TreeMap<String,Article>(new NoopComparator())", clazz.getFieldInitialization( p, true ) );

        p = classMapping.getProperty( "sortedArticlesSet" );

        assertEquals( "java.util.SortedSet", cfg2java.getJavaTypeName( p, false ) );
        assertEquals( "SortedSet<Article>", cfg2java.getJavaTypeName( p, true, new ImportContextImpl( "" ) ) );
        assertEquals( "new TreeSet<Article>(new NoopComparator())", clazz.getFieldInitialization( p, true ) );

    }

    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/tool/hbm2x/";
    }
     @Override
    protected String[] getMappings() {
        return new String[] {
                "Author.hbm.xml",
                "Article.hbm.xml",
                "Train.hbm.xml",
                "Passenger.hbm.xml"
//				"GenericModel.hbm.xml",
//				"Customer.hbm.xml",
//				"Order.hbm.xml",
//				"LineItem.hbm.xml",
//				"Product.hbm.xml"
        };
    }

}
