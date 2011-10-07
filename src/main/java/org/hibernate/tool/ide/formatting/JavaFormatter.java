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

package org.hibernate.tool.ide.formatting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import org.hibernate.tool.hbm2x.ExporterException;


public class JavaFormatter {

    private CodeFormatter codeFormatter;

    /**
     * @param args
     */
    public static void main(String[] args) {
        //DefaultCodeFormatter.USE_NEW_FORMATTER = true;
        HashMap hashMap = new HashMap();
        hashMap.put( JavaCore.COMPILER_SOURCE, "1.6" );
        hashMap.put( JavaCore.COMPILER_COMPLIANCE, "1.6" );
        hashMap.put( JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, "1.6" );
        final CodeFormatter codeFormatter = ToolFactory.createCodeFormatter( hashMap );

        //new JavaFormatter().formatFile(new File("c:/temp/SomeJava.java"), codeFormatter);

    }

    public JavaFormatter(Map settings) {
        if ( settings == null ) {
            // if no settings run with jdk 5 as default
            settings = new HashMap();
            settings.put( JavaCore.COMPILER_SOURCE, "1.6" );
            settings.put( JavaCore.COMPILER_COMPLIANCE, "1.6" );
            settings.put( JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, "1.6" );
        }

        this.codeFormatter = ToolFactory.createCodeFormatter( settings );
    }

    /**
     * Throws exception if not possible to read or write the file.
     * Returns true if formatting went ok; returns false if the formatting could not finish because of errors in the input.
     *
     * @param file
     * @param codeFormatter
     *
     * @return
     */
    public boolean formatFile(File file) throws ExporterException {
        IDocument doc = new Document();
        try {
            String contents = new String(
                    org.eclipse
                            .jdt
                            .internal
                            .compiler
                            .util
                            .Util
                            .getFileCharContent( file, null )
            );
            doc.set( contents );
            TextEdit edit = codeFormatter.format(
                    CodeFormatter.K_COMPILATION_UNIT,
                    contents,
                    0,
                    contents.length(),
                    0,
                    null
            );
            if ( edit != null ) {
                edit.apply( doc );
            }
            else {
                return false; // most likely syntax errror
            }

            // write the file
            final BufferedWriter out = new BufferedWriter( new FileWriter( file ) );
            try {
                out.write( doc.get() );
                out.flush();
            }
            finally {
                try {
                    out.close();
                }
                catch ( IOException e ) {
                    /* ignore */
                }
            }
            return true;
        }
        catch ( IOException e ) {
            throw new ExporterException( "Could not format " + file, e );
        }
        catch ( BadLocationException e ) {
            throw new ExporterException( "Could not format " + file, e );
        }
    }

}
