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

import java.util.Iterator;

import org.junit.Test;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author max
 */
@RequiresDialect(Oracle8iDialect.class)
public class OracleCompositeIdOrderTest extends JDBCMetaDataBinderTestCase {
     @Override
    protected void configure(JDBCMetaDataConfiguration configuration) {
        configuration.setPreferBasicCompositeIds( false );
    }

    @Override
    protected String[] getCreateSQL() {

        return new String[] {
                "CREATE TABLE REQUEST" +
                        "(" +
                        "  REQUEST_KEY             NUMBER(11)            NOT NULL," +
                        "  TIMEFRAME_KEY           NUMBER(11)" +
                        ")",
                "CREATE UNIQUE INDEX PK_REQUEST ON REQUEST" +
                        "(REQUEST_KEY)",
                "ALTER TABLE REQUEST ADD (" +
                        "  CONSTRAINT PK_REQUEST PRIMARY KEY (REQUEST_KEY))",
                "CREATE TABLE SCHEDULE" +
                        "(" +
                        "  SCHEDULE_KEY           NUMBER(11)             NOT NULL," +
                        "  TITLE                  VARCHAR2(255)     NOT NULL" +
                        ")",
                "CREATE UNIQUE INDEX PK_SCHEDULE ON SCHEDULE" +
                        "(SCHEDULE_KEY)",
                "ALTER TABLE SCHEDULE ADD (" +
                        "  CONSTRAINT PK_SCHEDULE PRIMARY KEY (SCHEDULE_KEY))",
                "CREATE TABLE COURSE" +
                        "(" +
                        "  SCHEDULE_KEY                 NUMBER(11)       NOT NULL," +
                        "  REQUEST_KEY                  NUMBER(11)       NOT NULL," +
                        "  TIMEFRAME_KEY                NUMBER(11)" +
                        ")",
                "CREATE UNIQUE INDEX PK_COURSE ON COURSE" +
                        "(SCHEDULE_KEY, REQUEST_KEY)",
                "ALTER TABLE COURSE ADD (" +
                        "  CONSTRAINT PK_COURSE PRIMARY KEY (SCHEDULE_KEY, REQUEST_KEY))",
                "ALTER TABLE COURSE ADD (" +
                        "  CONSTRAINT FK_COURSE__REQUEST FOREIGN KEY (REQUEST_KEY) " +
                        "    REFERENCES REQUEST (REQUEST_KEY)" +
                        "    ON DELETE CASCADE)",
                "ALTER TABLE COURSE ADD (" +
                        "  CONSTRAINT FK_COURSE__SCHEDULE FOREIGN KEY (SCHEDULE_KEY) " +
                        "    REFERENCES SCHEDULE (SCHEDULE_KEY)" +
                        "    ON DELETE CASCADE)",
                "CREATE TABLE COURSE_TOPIC" +
                        "(" +
                        "  SCHEDULE_KEY        NUMBER(11)                NOT NULL," +
                        "  REQUEST_KEY         NUMBER(11)                NOT NULL," +
                        "  TOPIC_KEY           NUMBER(11)" +
                        ")",
                "ALTER TABLE COURSE_TOPIC ADD (" +
                        "  CONSTRAINT FK_COURSE_TOPIC__COURSE FOREIGN KEY (SCHEDULE_KEY, REQUEST_KEY) " +
                        "    REFERENCES COURSE (SCHEDULE_KEY,REQUEST_KEY)" +
                        "    ON DELETE CASCADE)",

                "ALTER TABLE COURSE_TOPIC ADD (" +
                        "  CONSTRAINT PK_COURSE_TOPIC PRIMARY KEY (TOPIC_KEY))",

        };
    }
    protected String[] getGenDataSQL() {
        return new String[] {
                "insert into PRODUCT (productId, extraId, description, price, numberAvailable) values('PC', '0', 'My PC', 100.0, 23)",
                "insert into PRODUCT (productId, extraId, description, price, numberAvailable) values('MS', '1', 'My Mouse', 101.0, 23)",
                "insert into CUSTOMER (customerId, name, address) values('MAX', 'Max Rydahl Andersen', 'Neuchatel')",
                "insert into CUSTOMERORDER (customerId, orderNumber, orderDate) values ('MAX', 1, '11-11-2005')",
                "insert into LINEITEM (customerIdref, orderNumber, productId, extraProdId, quantity) values ('MAX', 1, 'PC', '0', 10)",
                "insert into LINEITEM (customerIdref, orderNumber, productId, extraProdId, quantity) values ('MAX', 1, 'MS', '1', 12)",
        };
    }
    @Override
    protected String[] getDropSQL() {
        return new String[] {
                "drop TABLE SCHEDULE cascade constraints",
                "drop TABLE REQUEST cascade constraints",
                "drop TABLE COURSE cascade constraints",
                "drop TABLE COURSE_TOPIC cascade constraints"
        };
    }


    @Test
    public void testMultiColumnForeignKeys() {
        Table table = getTable( identifier( "Course" ) );
        assertNotNull( table );
        ForeignKey foreignKey = getForeignKey( table, identifier( "FK_COURSE__SCHEDULE" ) );
        assertNotNull( foreignKey );

        assertEquals( toClassName( identifier( "Schedule" ) ), foreignKey.getReferencedEntityName() );
        assertEquals( identifier( "Course" ), foreignKey.getTable().getName() );

        assertEquals( 1, foreignKey.getColumnSpan() );
        assertEquals( foreignKey.getColumn( 0 ).getName(), "SCHEDULE_KEY" );

        Table tab = getTable( identifier( "COURSE" ) );
        assertEquals( tab.getPrimaryKey().getColumn( 0 ).getName(), "SCHEDULE_KEY" );
        assertEquals( tab.getPrimaryKey().getColumn( 1 ).getName(), "REQUEST_KEY" );

        getJDBCMetaDataConfiguration().buildMappings();

        PersistentClass course = getJDBCMetaDataConfiguration().getClassMapping( toClassName( identifier( "Course" ) ) );

        assertEquals( 2, course.getIdentifier().getColumnSpan() );
        Iterator columnIterator = course.getIdentifier().getColumnIterator();
        assertEquals( ( (Column) ( columnIterator.next() ) ).getName(), "SCHEDULE_KEY" );
        assertEquals( ( (Column) ( columnIterator.next() ) ).getName(), "REQUEST_KEY" );

        course = getJDBCMetaDataConfiguration().getClassMapping( toClassName( identifier( "COURSE_TOPIC" ) ) );

        Property property = course.getProperty( toPropertyName( identifier( "course" ) ) );
        columnIterator = property.getValue().getColumnIterator();
        assertEquals( ( (Column) ( columnIterator.next() ) ).getName(), "SCHEDULE_KEY" );
        assertEquals( ( (Column) ( columnIterator.next() ) ).getName(), "REQUEST_KEY" );
    }
}
     

