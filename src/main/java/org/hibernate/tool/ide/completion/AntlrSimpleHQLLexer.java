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

package org.hibernate.tool.ide.completion;

import java.io.CharArrayReader;

import antlr.Token;
import antlr.TokenStreamException;

import org.hibernate.hql.internal.antlr.HqlBaseLexer;

/**
 * A lexer implemented on top of the Antlr grammer implemented in core.
 *
 * @author Max Rydahl Andersen
 */
public class AntlrSimpleHQLLexer implements SimpleHQLLexer {

    private HqlBaseLexer lexer;
    private Token token;

    public AntlrSimpleHQLLexer(char[] cs, int length) {
        lexer = new HqlBaseLexer( new CharArrayReader( cs, 0, length ) ) {
            public void newline() {
                //super.newline();
            }

            public int getColumn() {
                return super.getColumn() - 1;
            }
        };
        lexer.setTabSize( 1 );
    }

    public int getTokenLength() {
        if ( token.getText() == null ) {
            return 0;
        }
        return token.getText().length();
    }

    public int getTokenOffset() {
        return token.getColumn() - 1;
    }

    public int nextTokenId() {
        try {
            token = lexer.nextToken();
            if ( token == null ) {
                System.out.println( token );
            }
        }
        catch ( TokenStreamException e ) {
            throw new SimpleLexerException( e );
        }
        return token.getType();
    }

}
