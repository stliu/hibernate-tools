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
 * Created on 13-Jan-2005
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author max
 */
public class CompositeIdJPATest extends JDBCMetaDataBinderTestCase {
    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "CREATE TABLE Person ( id identity, name varchar(100) NOT NULL, address varchar(255), city varchar(20) default NULL, PRIMARY KEY  (id))"
                ,
                "create table modelyear ( make varchar(20), model varchar(30), year int, name varchar(30), primary key (make, model, year))",
                "CREATE TABLE Vehicle ( " +
                        "    		state varchar(2) NOT NULL, " +
                        "    		registration varchar(8) NOT NULL, " +
                        "    	    v_make varchar(20) , " +
                        "    		v_model varchar(30) , " +
                        "    		v_year int , " +
                        "    			  owner int , " +
                        "    	    PRIMARY KEY  (registration), " +
                        //"    		KEY  (make,model,year), " +
                        //"    			  KEY  (owner), " +
                        "    			  constraint vehicle_owner FOREIGN KEY (owner) REFERENCES person (id), " +
                        "    			  constraint vehicle_modelyear FOREIGN KEY (v_make, v_model, v_year) REFERENCES modelyear (make, model, year) " +
                        "    			)",
        };
    }
    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop table Vehicle",
                "drop table modelyear",
                "drop table Person",


        };
    }

    @Test
    public void testMultiColumnForeignKeys() {

        Table vehicleTable = getTable( identifier( "Vehicle" ) );

        Iterator foreignKeyIterator = vehicleTable.getForeignKeyIterator();
        assertHasNext( 2, foreignKeyIterator );

        ForeignKey foreignKey = getForeignKey( vehicleTable, identifier( "vehicle_owner" ) );
        assertEquals( foreignKey.getColumnSpan(), 1 );

        foreignKey = getForeignKey( vehicleTable, identifier( "vehicle_modelyear" ) );
        assertEquals( foreignKey.getColumnSpan(), 3 );

        PersistentClass vehicle = getJDBCMetaDataConfiguration().getClassMapping( "Vehicle" );
        EntityPOJOClass vechiclePojo = new EntityPOJOClass( vehicle, new Cfg2JavaTool() );
        assertNotNull( vechiclePojo.getDecoratedObject() );

        Property property = vehicle.getProperty( "modelyear" );
        assertNotNull( property );
        String generateJoinColumnsAnnotation = vechiclePojo.generateJoinColumnsAnnotation( property, getJDBCMetaDataConfiguration() );
        assertTrue( generateJoinColumnsAnnotation.indexOf( "referencedColumnName=\"MAKE\"" ) > 0 );


    }

    @Test
    public void testJPAGeneration() throws Exception {
        POJOExporter exporter = new POJOExporter( getJDBCMetaDataConfiguration(), serviceRegistry(), getOutputDir() );
        Properties p = new Properties();
        p.setProperty( "jdk5", "true" );
        p.setProperty( "ejb3", "true" );

        exporter.setProperties( p );
        exporter.start();

        File file = new File(getOutputDir(), "ejb3compilable" );
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
        addAnnotationJars( jars );

        new ExecuteContext( getOutputDir(), file, jars ) {

            protected void execute() throws Exception {
                Configuration configuration = new Configuration();
                configuration.addAnnotatedClass( getUcl().loadClass( "Vehicle" ) );
                configuration.addAnnotatedClass( getUcl().loadClass( "Person" ) );
                configuration.addAnnotatedClass( getUcl().loadClass( "Modelyear" ) );

                SessionFactory sf = configuration.buildSessionFactory( serviceRegistry() );
                Session s = sf.openSession();
                Query createQuery = s.createQuery( "from java.lang.Object" );
                createQuery.list();
                s.close();
                sf.close();

            }

        }.run();
    }

    private void addAnnotationJars(List jars) {
        jars.add( "antlr.jar" );
        jars.add( "hibernate-core.jar" );
        jars.add( "hibernate-commons-annotations.jar" );
        jars.add( "hibernate-jpa-2.0-api.jar" );
        jars.add( "commons-logging-1.0.4.jar" );

    }

}
     

