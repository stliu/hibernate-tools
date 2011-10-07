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
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;



import org.junit.Test;

import org.hibernate.tool.JDBCMetaDataBinderTestCase;



/**
 * @author max
 */
public class NoPrimaryKeyTest extends JDBCMetaDataBinderTestCase {
    @Override
	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table G0 ( AN_ID VARCHAR(20), CONSTRAINT \"C0\" PRIMARY KEY (\"AN_ID\") )", 
				"create table G1 ( AN_ID VARCHAR(20), CONSTRAINT \"C1\" FOREIGN KEY (\"AN_ID\") REFERENCES \"G0\")"
		};
	}
    @Override
	protected String[] getDropSQL() {
		
		return new String[]  {
				"drop table G1",
				"drop table G0"								
		};
	}
	
	@Test
    public void testMe() throws Exception {
		getJDBCMetaDataConfiguration().buildMappings();
	}
	

}
