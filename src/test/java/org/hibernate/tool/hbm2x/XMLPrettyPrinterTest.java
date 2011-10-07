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
 * Created on 17-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.dom4j.DocumentException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 * @author max
 */
public class XMLPrettyPrinterTest {



    @Test
    public void testBasics() throws IOException, DocumentException, SAXException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLPrettyPrinter.prettyPrint(
                new ByteArrayInputStream( "<basic attrib='1'></basic>".getBytes() ),
                byteArrayOutputStream
        );

        String string = byteArrayOutputStream.toString();

        assertEquals( "<basic attrib='1'></basic>" + lineSeparator(), string );
    }

    private String lineSeparator() {
        return System.getProperty( "line.separator" );
    }

    /*  @Test public void testCloseTag() throws IOException, DocumentException, SAXException {
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLPrettyPrinter.prettyPrint(new ByteArrayInputStream("<basic></basic>".getBytes() ), byteArrayOutputStream);
        
        String string = byteArrayOutputStream.toString();
        
        assertEquals("<basic/>\r\n",string);
    }*/

    @Test
    public void testDeclarationWithoutValidation() throws IOException, DocumentException, SAXException {

        String input = "<hibernate-mapping defaultx-lazy=\"false\"/>";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLPrettyPrinter.prettyPrint( new ByteArrayInputStream( input.getBytes() ), byteArrayOutputStream );

        String string = byteArrayOutputStream.toString();

        assertEquals(
                "<hibernate-mapping defaultx-lazy=\"false\" />" + lineSeparator() + lineSeparator()
                , string
        );
    }
}
