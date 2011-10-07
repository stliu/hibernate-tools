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

package org.hibernate.cfg.reveng.dialect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hibernate.exception.spi.SQLExceptionConverter;


/**
 * Iterator over a resultset; intended usage only for metadata reading.
 */
public abstract class ResultSetIterator implements Iterator {

    private ResultSet rs;

    protected boolean current = false;

    protected boolean endOfRows = false;

    private SQLExceptionConverter sec;

    private Statement statement = null;

    protected ResultSetIterator(ResultSet resultset, SQLExceptionConverter sec) {
        this( null, resultset, sec );
    }

    public ResultSetIterator(Statement stmt, ResultSet resultset, SQLExceptionConverter exceptionConverter) {
        this.rs = resultset;
        this.sec = exceptionConverter;
        this.statement = stmt;
    }

    protected SQLExceptionConverter getSQLExceptionConverter() {
        return sec;
    }

    public boolean hasNext() {
        try {
            advance();
            return !endOfRows;
        }
        catch ( SQLException e ) {
            handleSQLException( e );
            return false;
        }
    }


    public Object next() {
        try {
            advance();
            if ( endOfRows ) {
                throw new NoSuchElementException();
            }
            current = false;
            return convertRow( rs );
        }
        catch ( SQLException e ) {
            handleSQLException( e );
            throw new NoSuchElementException( "excpetion occurred " + e );
        }

    }

    abstract protected Throwable handleSQLException(SQLException e);

    abstract protected Object convertRow(ResultSet rs) throws SQLException;

    public void remove() {
        throw new UnsupportedOperationException(
                "remove() not possible on ResultSet"
        );
    }

    protected void advance() throws SQLException {

        if ( !current && !endOfRows ) {
            if ( rs.next() ) {
                current = true;
                endOfRows = false;
            }
            else {
                current = false;
                endOfRows = true;
            }
        }
    }

    public void close() {
        try {
            rs.close();
            if ( statement != null ) {
                statement.close();
            }
        }
        catch ( SQLException e ) {
            handleSQLException( e );
        }
    }
}
