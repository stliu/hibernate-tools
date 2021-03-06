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
 * Created on 26-Nov-2004
 *
 */
package org.hibernate.cfg;

import java.util.Set;

import org.hibernate.MappingException;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

/**
 * @author max
 */
public class JDBCMetaDataConfiguration extends Configuration {
    private ReverseEngineeringStrategy revEngStrategy = new DefaultReverseEngineeringStrategy();
    @Override
    protected void secondPassCompileForeignKeys(Table table, Set done)
            throws MappingException {
        super.secondPassCompileForeignKeys( table, done );
        // TODO: doing nothing to avoid creating foreignkeys which is NOT actually in the database.
    }
    @Deprecated
    public void readFromJDBC() {
        readFromJDBC( ServiceRegistryHelper.getDefaultServiceRegistry( this ) );
    }

    public void readFromJDBC(ServiceRegistry serviceRegistry) {
        JDBCBinder binder = new JDBCBinder(
                this,
                serviceRegistry,
                buildSettings( serviceRegistry ),
                createMappings(),
                revEngStrategy
        );
        binder.readFromDatabase( null, null, buildMapping( this ) );
    }

    static private Mapping buildMapping(final Configuration cfg) {
        return new Mapping() {
            /**
             * Returns the identifier type of a mapped class
             */
            public Type getIdentifierType(String persistentClass) throws MappingException {
                PersistentClass pc = cfg.getClassMapping( persistentClass );
                if ( pc == null ) {
                    throw new MappingException( "persistent class not known: " + persistentClass );
                }
                return pc.getIdentifier().getType();
            }

            public String getIdentifierPropertyName(String persistentClass) throws MappingException {
                final PersistentClass pc = cfg.getClassMapping( persistentClass );
                if ( pc == null ) {
                    throw new MappingException( "persistent class not known: " + persistentClass );
                }
                if ( !pc.hasIdentifierProperty() ) {
                    return null;
                }
                return pc.getIdentifierProperty().getName();
            }

            public Type getReferencedPropertyType(String persistentClass, String propertyName) throws MappingException {
                final PersistentClass pc = cfg.getClassMapping( persistentClass );
                if ( pc == null ) {
                    throw new MappingException( "persistent class not known: " + persistentClass );
                }
                Property prop = pc.getProperty( propertyName );
                if ( prop == null ) {
                    throw new MappingException( "property not known: " + persistentClass + '.' + propertyName );
                }
                return prop.getType();
            }

            public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
                return null;
            }
        };
    }


    //    private boolean ignoreconfigxmlmapppings = true;
    // set to true and fk's that are part of a primary key will just be mapped as the raw value and as a readonly property. if false, it will be <many-to-one-key-property
    private boolean preferBasicCompositeIds = true;

    /**
     * If true, compositeid's will not create key-many-to-one and
     * non-updatable/non-insertable many-to-one will be created instead.
     */
    public boolean preferBasicCompositeIds() {
        return preferBasicCompositeIds;
    }

    public void setPreferBasicCompositeIds(boolean flag) {
        this.preferBasicCompositeIds = flag;
    }

//    protected void parseMappingElement(Element subelement, String name) {
//        if(!ignoreconfigxmlmapppings ) {
//            super.parseMappingElement(subelement, name);
//        }
//        else {
//            log.info("Ignoring " + name + " mapping");
//        }
//    }

    public void setReverseEngineeringStrategy(ReverseEngineeringStrategy reverseEngineeringStrategy) {
        this.revEngStrategy = reverseEngineeringStrategy;
    }

    public ReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return revEngStrategy;
    }

}
