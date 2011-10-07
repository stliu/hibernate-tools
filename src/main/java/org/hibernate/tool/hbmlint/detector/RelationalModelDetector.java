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

package org.hibernate.tool.hbmlint.detector;

import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbmlint.Detector;
import org.hibernate.tool.hbmlint.IssueCollector;

public abstract class RelationalModelDetector extends Detector {

    public void visit(Configuration cfg, IssueCollector collector) {
        for ( Iterator iter = cfg.getTableMappings(); iter.hasNext(); ) {
            Table table = (Table) iter.next();
            this.visit( cfg, table, collector );
        }
    }


    protected void visit(Configuration cfg, Table table, Column col, IssueCollector collector) {

    }

    protected void visitColumns(Configuration cfg, Table table, IssueCollector collector) {
        Iterator columnIter = table.getColumnIterator();
        while ( columnIter.hasNext() ) {
            Column col = (Column) columnIter.next();
            this.visit( cfg, table, col, collector );
        }
    }

    /**
     * @return true if visit should continue down through the columns
     */
    protected void visit(Configuration cfg, Table table, IssueCollector collector) {
        visitColumns( cfg, table, collector );
    }

}

