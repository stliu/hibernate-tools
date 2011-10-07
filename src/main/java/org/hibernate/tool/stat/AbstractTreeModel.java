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

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Abstract class implementing the support for TreeModelEvents
 * and TreeModelListeners. The code is partly snipped from the
 * implementation of DefaultTreeModel.
 *
 * @version $Revision: 1.1.2.1 $
 */

public abstract class AbstractTreeModel implements TreeModel {

    private EventListenerList listenerList = new EventListenerList();

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     *
     * @param l the listener to add
     *
     * @see #removeTreeModelListener
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add( TreeModelListener.class, l );
    }

    /**
     * Removes a listener previously added with <B>addTreeModelListener()</B>.
     *
     * @param l the listener to remove
     *
     * @see #addTreeModelListener
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove( TreeModelListener.class, l );
    }

    /**
     * Messaged when the user has altered the value for the item identified
     * by <I>path</I> to <I>newValue</I>.  If <I>newValue</I> signifies
     * a truly new value the model should post a treeNodesChanged
     * event.
     *
     * @param path path to the node that the user has altered.
     * @param newValue the new value from the TreeCellEditor.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new RuntimeException(
                "AbstractTreeModel.valueForPathChanged: you MUST override this when using a TreeCellEditor!"
        );
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
            if ( listeners[i] == TreeModelListener.class ) {
                // Lazily create the event:
                if ( e == null ) {
                    e = new TreeModelEvent(
                            source, path,
                            childIndices, children
                    );
                }
                ( (TreeModelListener) listeners[i + 1] ).treeNodesChanged( e );
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
                                         int[] childIndices,
                                         Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
            if ( listeners[i] == TreeModelListener.class ) {
                // Lazily create the event:
                if ( e == null ) {
                    e = new TreeModelEvent(
                            source, path,
                            childIndices, children
                    );
                }
                ( (TreeModelListener) listeners[i + 1] ).treeNodesInserted( e );
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
            if ( listeners[i] == TreeModelListener.class ) {
                // Lazily create the event:
                if ( e == null ) {
                    e = new TreeModelEvent(
                            source, path,
                            childIndices, children
                    );
                }
                ( (TreeModelListener) listeners[i + 1] ).treeNodesRemoved( e );
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeStructureChanged(Object source, Object[] path,
                                            int[] childIndices,
                                            Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
            if ( listeners[i] == TreeModelListener.class ) {
                // Lazily create the event:
                if ( e == null ) {
                    e = new TreeModelEvent(
                            source, path,
                            childIndices, children
                    );
                }
                ( (TreeModelListener) listeners[i + 1] ).treeStructureChanged( e );
            }
        }
    }

}
