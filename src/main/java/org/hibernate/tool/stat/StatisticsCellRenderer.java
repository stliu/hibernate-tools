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

package org.hibernate.tool.stat;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.internal.CategorizedStatistics;

public class StatisticsCellRenderer extends DefaultTreeCellRenderer {

    SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" );

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {

        JLabel treeCellRendererComponent = (JLabel) super.getTreeCellRendererComponent(
                tree,
                value,
                selected,
                expanded,
                leaf,
                row,
                hasFocus
        );

        String text = treeCellRendererComponent.getText();
        String tooltip = null;
        if ( value instanceof Statistics ) {
            Statistics stats = (Statistics) value;
            text = "Statistics " + formatter.format( new Date( stats.getStartTime() ) );
            tooltip = stats.toString();
        }

        if ( value instanceof CategorizedStatistics ) {
            CategorizedStatistics stats = (CategorizedStatistics) value;
            text = stats.getCategoryName();

        }
        if ( value instanceof EntityStatistics ) {
            //EntityStatistics stats = (EntityStatistics) value;

        }

        if ( value instanceof CollectionStatistics ) {
            //CollectionStatistics stats = (CollectionStatistics) value;

        }

        if ( value instanceof QueryStatistics ) {
            //QueryStatistics stats = (QueryStatistics) value;

        }

        treeCellRendererComponent.setText( text );
        treeCellRendererComponent.setToolTipText( tooltip );
        return treeCellRendererComponent;
    }


}
