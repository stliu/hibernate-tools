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

package org.hibernate.tool.hbm2x.visitor;

import org.hibernate.HibernateException;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.type.CompositeCustomType;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;

public class JavaTypeFromValueVisitor extends DefaultValueVisitor {


    private boolean preferRawTypeNames = true;

    public JavaTypeFromValueVisitor() {
        super( true );
    }

    // special handling for Map's to avoid initialization of comparators that depends on the keys/values which might not be generated yet.
    public Object accept(Map o) {
        if ( o.isSorted() ) {
            return "java.util.SortedMap";
        }
        return super.accept( o );
    }

    // special handling for Set's to avoid initialization of comparators that depends on the keys/values which might not be generated yet.
    public Object accept(Set o) {
        if ( o.isSorted() ) {
            return "java.util.SortedSet";
        }
        return super.accept( o );
    }

    public Object accept(Component value) {
        // composite-element breaks without it.
        return value.getComponentClassName();
    }

    public Object accept(OneToOne o) {
        return acceptToOne( o );
    }

    public Object accept(ManyToOne o) {
        return acceptToOne( o );
    }

    private Object acceptToOne(ToOne value) {
        return value.getReferencedEntityName(); // should get the cfg and lookup the persistenclass.
    }

    public Object accept(OneToMany value) {
        return value.getAssociatedClass().getClassName();
    }

    private String toName(Class c) {

        if ( c.isArray() ) {
            Class a = c.getComponentType();

            return a.getName() + "[]";
        }
        else {
            return c.getName();
        }
    }

    protected Object handle(Value o) {
        Value value = o;
        try {
            // have to attempt calling gettype to decide if its custom type.
            Type type = value.getType();
            if ( type instanceof CustomType || type instanceof CompositeCustomType ) {
                return toName( type.getReturnedClass() );
            }
        }
        catch ( HibernateException he ) {
            // ignore
        }

        if ( preferRawTypeNames && value.isSimpleValue() ) {
            // this logic make us use the raw typename if it is something else than an Hibernate type. So, if user wrote long we will use long...if he meant to have a Long then he should use the java.lang.Long version.
            String typename = ( (SimpleValue) value ).getTypeName();
            if ( !Cfg2JavaTool.isNonPrimitiveTypeName( typename ) ) {
                String val = ( (SimpleValue) value ).getTypeName();
                if ( val != null ) {
                    return val; // val can be null when type is any
                }
            }
        }

        return toName( value.getType().getReturnedClass() );

    }


}
