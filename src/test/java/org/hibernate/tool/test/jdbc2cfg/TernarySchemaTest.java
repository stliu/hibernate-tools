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
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.visitor.DefaultValueVisitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests mutli schema used in collections (set)'s. See JBIDE-5628.
 *
 * Excluded from default tests for now since it currently requires HSQLDB 2.0 since previous versions of hsqldb does not support cross-schema foreign key checks.
 *
 * @author max
 */
public class TernarySchemaTest extends JDBCMetaDataBinderTestCase {

    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop table plainuserroles",
                "drop table plainrole",
                "drop table thirdschema.userroles",
                "drop table user",
                "drop table otherschema.role",
                "drop schema otherschema",
                "drop schema thirdschema"
        };
    }

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "create schema otherschema authorization sa",
                "create schema thirdschema authorization sa",

                "create table user ( id int not null, name varchar(20), primary key(id))",
                "create table otherschema.role ( id int not null, name varchar(20), primary key(id))",
                "create table thirdschema.userroles ( userid int not null, roleid int not null, primary key(userid, roleid))",
                "alter table thirdschema.userroles add constraint toroles foreign key (roleid) references otherschema.role(id)",
                "alter table thirdschema.userroles add constraint tousers foreign key (userid) references public.user(id)",

                "create table plainrole ( id int not null, name varchar(20), primary key(id))",
                "create table plainuserroles ( userid int not null, roleid int not null, primary key(userid, roleid))",
                "alter table plainuserroles add constraint plaintoroles foreign key (roleid) references plainrole(id)",
                "alter table plainuserroles add constraint plaintousers foreign key (userid) references user(id)",

        };
    }
//    @Before
//    public void setUp() throws Exception {
//        try {
//            super.setUp();
//        }
//        catch ( SQLException e ) {
//            //since it currently requires HSQLDB 2.0
//            executeDDL( getDropSQL(), true );
//            throw e;
//        }
//    }



    @Test
    public void testTernaryModel() throws SQLException {

        assertMultiSchema( getJDBCMetaDataConfiguration() );

    }

    private void assertMultiSchema(Configuration cfg) {
        assertHasNext(
                "There should be three tables!", 3, cfg
                .getTableMappings()
        );
        assertHasNext(
                "There should be four entity classes!", 4, cfg
                .getClassMappings()
        );
        Iterator<PersistentClass> iterator = cfg.getClassMappings();
        while(iterator.hasNext()){
            System.out.println(iterator.next().getClassName());
        }
        final PersistentClass role = cfg.getClassMapping( "Role" );
        PersistentClass userroles = cfg.getClassMapping( "Userroles" );
        PersistentClass user = cfg.getClassMapping( "User" );
        PersistentClass plainRole = cfg.getClassMapping( "Plainrole" );


        Property property = role.getProperty( "users" );
        assertEquals( role.getTable().getSchema(), "OTHERSCHEMA" );
        assertNotNull( property );
        property.getValue().accept(
                new DefaultValueVisitor( true ) {
                    public Object accept(Set o) {
                        assertEquals( o.getCollectionTable().getSchema(), "THIRDSCHEMA" );
                        return null;
                    }
                }
        );


        property = plainRole.getProperty( "users" );
        assertEquals( role.getTable().getSchema(), "OTHERSCHEMA" );
        assertNotNull( property );
        property.getValue().accept(
                new DefaultValueVisitor( true ) {
                    public Object accept(Set o) {
                        assertEquals( o.getCollectionTable().getSchema(), null );
                        return null;
                    }
                }
        );

    }

    @Test
    public void testGeneration() {
        HibernateMappingExporter hme = new HibernateMappingExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        hme.start();

        assertFileAndExists( new File( getOutputDir(), "Role.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "User.hbm.xml" ) );
        assertFileAndExists( new File( getOutputDir(), "Plainrole.hbm.xml" ) );

        assertEquals( 3, getOutputDir().listFiles().length );

        Configuration configuration = new Configuration()
                .addFile( new File( getOutputDir(), "Role.hbm.xml" ) )
                .addFile( new File( getOutputDir(), "User.hbm.xml" ) )
                .addFile( new File( getOutputDir(), "Plainrole.hbm.xml" ) );

        configuration.buildMappings();

        assertMultiSchema( configuration );
    }
    @Override
    protected void configure(JDBCMetaDataConfiguration configuration) {
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy() {
            public List getSchemaSelections() {
                List selections = new ArrayList();
                selections.add( new SchemaSelection( null, "PUBLIC" ) );
                selections.add( new SchemaSelection( null, "otherschema" ) );
                selections.add( new SchemaSelection( null, "thirdschema" ) );
                return selections;
            }
        };
        configuration.setReverseEngineeringStrategy( c );
    }
}
