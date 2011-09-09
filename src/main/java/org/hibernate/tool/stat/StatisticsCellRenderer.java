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

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		JLabel treeCellRendererComponent = (JLabel) super.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus );
		
		String text = treeCellRendererComponent.getText();
		String tooltip = null;
		if(value instanceof Statistics) {
			Statistics stats = (Statistics) value;
			text = "Statistics " + formatter.format( new Date(stats.getStartTime()) );
			tooltip = stats.toString();
		}
		
		if(value instanceof CategorizedStatistics ) {
			CategorizedStatistics stats = (CategorizedStatistics) value;
			text = stats.getCategoryName();
			
		}
		if(value instanceof EntityStatistics) {
			//EntityStatistics stats = (EntityStatistics) value;
			
		}
		
		if(value instanceof CollectionStatistics) {
			//CollectionStatistics stats = (CollectionStatistics) value;
			
		}
		
		if(value instanceof QueryStatistics) {
			//QueryStatistics stats = (QueryStatistics) value;
		
		}
		
		treeCellRendererComponent.setText( text );
		treeCellRendererComponent.setToolTipText( tooltip );
		return treeCellRendererComponent;
	}

	
}
