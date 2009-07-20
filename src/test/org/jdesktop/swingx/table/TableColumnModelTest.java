/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.table;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.event.TableColumnModelExtListener;
import org.jdesktop.swingx.test.ColumnModelReport;
import org.jdesktop.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Skeleton to unit test DefaultTableColumnExt.
 * 
 * Incomplete list of issues to test: 
 *   fired added after setVisible(true)
 *   behaviour when adding/removing invisible columns
 *   selection state
 * 
 * 
 * @author  Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class TableColumnModelTest extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger
            .getLogger(TableColumnModelTest.class.getName());
    protected static final int COLUMN_COUNT = 3;

    /**
     * Issue #1123-swingx: isRemovedToInvisibleEvent incorrect on second (and subsequent)
     *   hiding of first visible column.
     *   
     *   Test that there is column marker is same as event's fromIndex (beware: internals!)
     */
    @Test
    public void testIsRemovedToInvisibleMarkerOldIndex() {
        final DefaultTableColumnModelExt columnModel = 
            (DefaultTableColumnModelExt) createColumnModel(3);
        final int index = 0;
        // first okay
        columnModel.getColumnExt(0).setVisible(false);
        TableColumnExt columnB = columnModel.getColumnExt(index);
        
        // can't use report: the isRemovedToInvisible is valid during notification only
        TableColumnModelListener report = new TableColumnModelListener() {
            
            
            public void columnRemoved(TableColumnModelEvent e) {
                for (TableColumn column : ((TableColumnModelExt) e.getSource()).getColumns(true)) {
                    Object marker = ((TableColumnExt) column).getClientProperty("TableColumnModelExt.ignoreEvent");
                    if (marker != null) {
                        assertEquals("column marked with ignoreEvent", e.getFromIndex(), marker);
                    }
                }
                
            }
            public void columnAdded(TableColumnModelEvent e) {}
            public void columnMarginChanged(ChangeEvent e) {  }
            public void columnMoved(TableColumnModelEvent e) {}
            public void columnSelectionChanged(ListSelectionEvent e) {}
            
        };
        columnModel.addColumnModelListener(report);
        columnB.setVisible(false);
        // sanity testing internals: marker client property removed
        assertEquals("ignoreEvents marker must be null", null,  
                columnB.getClientProperty("TableColumnModelExt.ignoreEvent"));
    }
    /**
     * Issue #1123-swingx: isRemovedToInvisibleEvent incorrect on second (and subsequent)
     *   hiding of first visible column.
     *   
     *   Test that there is only one column marked (beware: internals!)
     */
    @Test
    public void testIsRemovedToInvisibleSingleMarker() {
        final DefaultTableColumnModelExt columnModel = 
            (DefaultTableColumnModelExt) createColumnModel(3);
        final int index = 0;
        // first okay
        columnModel.getColumnExt(0).setVisible(false);
        TableColumnExt columnB = columnModel.getColumnExt(index);
        
        // can't use report: the isRemovedToInvisible is valid during notification only
        TableColumnModelListener report = new TableColumnModelListener() {
            
            
            public void columnRemoved(TableColumnModelEvent e) {
                int markerCount = 0;
                for (TableColumn column : ((TableColumnModelExt) e.getSource()).getColumns(true)) {
                   if (((TableColumnExt) column).getClientProperty("TableColumnModelExt.ignoreEvent") != null) {
                       markerCount++;
                   }
                }
                assertEquals("exactly one column marked with ignoreEvent", 1, markerCount);
                
            }
            public void columnAdded(TableColumnModelEvent e) {}
            public void columnMarginChanged(ChangeEvent e) {  }
            public void columnMoved(TableColumnModelEvent e) {}
            public void columnSelectionChanged(ListSelectionEvent e) {}
            
        };
        columnModel.addColumnModelListener(report);
        columnB.setVisible(false);
        // sanity testing internals: marker client property removed
        assertEquals("ignoreEvents marker must be null", null,  
                columnB.getClientProperty("TableColumnModelExt.ignoreEvent"));
    }
    
    /**
     * Issue #1123-swingx: isRemovedToInvisibleEvent incorrect on second (and subsequent)
     *   hiding of first visible column
     */
    @Test
    public void testIsRemovedToInvisibleTrue1122() {
        final DefaultTableColumnModelExt columnModel = 
            (DefaultTableColumnModelExt) createColumnModel(3);
        final int index = 0;
        // first okay
        columnModel.getColumnExt(0).setVisible(false);
        TableColumnExt columnB = columnModel.getColumnExt(index);
        
        // can't use report: the isRemovedToInvisible is valid during notification only
        TableColumnModelListener report = new TableColumnModelListener() {


            public void columnRemoved(TableColumnModelEvent e) {
                int fromIndex = e.getFromIndex();
                assertEquals("old visible index of removed", index, fromIndex);
                assertEquals("moved to invisible", true, 
                        columnModel.isRemovedToInvisibleEvent(fromIndex));
                
            }
            public void columnAdded(TableColumnModelEvent e) {}
            public void columnMarginChanged(ChangeEvent e) {  }
            public void columnMoved(TableColumnModelEvent e) {}
            public void columnSelectionChanged(ListSelectionEvent e) {}
            
        };
        columnModel.addColumnModelListener(report);
        columnB.setVisible(false);
        // sanity testing internals: marker client property removed
        assertEquals("ignoreEvents marker must be null", null,  
                columnB.getClientProperty("TableColumnModelExt.ignoreEvent"));
    }



    /**
     * added api to get array of ext listeners
     */
    @Test
    public void testGetTableColumnModelExtListener() {
        // any tableColumnModelExtListener will do
        JXTable table = new JXTable();
        DefaultTableColumnModelExt columnModel = (DefaultTableColumnModelExt) createColumnModel(COLUMN_COUNT);
        columnModel.addColumnModelListener(table);
        TableColumnModelExtListener[] listeners = columnModel.getTableColumnModelExtListeners();
        assertEquals(1, listeners.length);
        assertEquals(table, listeners[0]);
        
    }
    /**
     * Issue #867-swingx: internal book-keeping must not be routed via columnModel
     *    column propertyChange <p>
     * test the change from visible to hidden.
     */
    @Test
    public void testTableColumnIgnoreNoPropertyNotificationHide() {
        TableColumnModelExt columnModel = createColumnModel(COLUMN_COUNT);
        ColumnModelReport report = new ColumnModelReport();
        columnModel.addColumnModelListener(report);
        columnModel.getColumnExt(0).setVisible(false);
        assertEquals("ignore events must not be routed", 0, report.getColumnPropertyEventCount(
                "TableColumnModelExt.ignoreEvent"));
    }

    /**
     * Issue #867-swingx: internal book-keeping must not be routed via columnModel
     *    column propertyChange <p>
     * test the change from hidden to visible.
     */
    @Test
    public void testTableColumnIgnoreNoPropertyNotificationShow() {
        TableColumnModelExt columnModel = createColumnModel(COLUMN_COUNT);
        columnModel.getColumnExt(0).setVisible(false);
        ColumnModelReport report = new ColumnModelReport();
        columnModel.addColumnModelListener(report);
        columnModel.getColumnExt(0).setVisible(true);
        assertEquals("ignore events must not be routed", 0, report.getColumnPropertyEventCount(
                "TableColumnModelExt.ignoreEvent"));
    }

    /**
     * Issue #369-swingx: properties of hidden columns are not fired. <p>
     * test the change from visible to hidden.
     */
    @Test
    public void testHideTableColumnPropertyNotification() {
        TableColumnModelExt columnModel = createColumnModel(COLUMN_COUNT);
        ColumnModelReport report = new ColumnModelReport();
        columnModel.addColumnModelListener(report);
        columnModel.getColumnExt(0).setVisible(false);
        TestUtils.assertPropertyChangeEvent(report.getPropertyChangeReport(), 
                "visible", true, false);
    }


    /**
     * Issue #846-swingx
     */
    @Test
    public void testIsAddedFromInvisibleTrue() {
        final DefaultTableColumnModelExt columnModel = (DefaultTableColumnModelExt) createColumnModel(3);
        TableColumnExt columnB = columnModel.getColumnExt(1);
        columnB.setVisible(false);
        // can't use report: the isRemovedToInvisible is valid during notification only
        TableColumnModelListener report = new TableColumnModelListener() {

            public void columnAdded(TableColumnModelEvent e) {
                int fromIndex = e.getToIndex();
                assertEquals("old column really removed", true, 
                        columnModel.isAddedFromInvisibleEvent(fromIndex));
                // note: the toIndex here is always the last
                // following are moves to position before hiding
            }
            public void columnRemoved(TableColumnModelEvent e) {}
            public void columnMarginChanged(ChangeEvent e) {  }
            public void columnMoved(TableColumnModelEvent e) {}
            public void columnSelectionChanged(ListSelectionEvent e) {}
            
        };
        columnModel.addColumnModelListener(report);
        columnB.setVisible(true);
        // sanity testing internals: marker client property removed
        assertEquals("ignoreEvents marker must be null", null,  
                columnB.getClientProperty("TableColumnModelExt.ignoreEvent"));
    }

    /**
     * Issue #846-swingx
     */
    @Test
    public void testIsRemovedToInvisibleFalse() {
        final DefaultTableColumnModelExt columnModel = (DefaultTableColumnModelExt) createColumnModel(3);
        TableColumnExt columnB = columnModel.getColumnExt(1);
        columnModel.getColumnExt(0).setVisible(false);
        // can't use report: the isRemovedToInvisible is valid during notification only
        TableColumnModelListener report = new TableColumnModelListener() {


            public void columnRemoved(TableColumnModelEvent e) {
                int fromIndex = e.getFromIndex();
                assertEquals("old visible index of removed", 0, fromIndex);
                assertEquals("old column really removed", false, 
                        columnModel.isRemovedToInvisibleEvent(fromIndex));
                
            }
            public void columnAdded(TableColumnModelEvent e) {}
            public void columnMarginChanged(ChangeEvent e) {  }
            public void columnMoved(TableColumnModelEvent e) {}
            public void columnSelectionChanged(ListSelectionEvent e) {}
            
        };
        columnModel.addColumnModelListener(report);
        columnModel.removeColumn(columnB);
        // sanity testing internals: marker client property removed
        assertEquals("ignoreEvents marker must be null", null,  
                columnB.getClientProperty("TableColumnModelExt.ignoreEvent"));
    }
    /**
     * Issue #846-swingx
     */
    @Test
    public void testIsRemovedToInvisibleTrue() {
        final DefaultTableColumnModelExt columnModel = 
            (DefaultTableColumnModelExt) createColumnModel(3);
        final int index = 2;
        TableColumnExt columnB = columnModel.getColumnExt(index);
        // can't use report: the isRemovedToInvisible is valid during notification only
        TableColumnModelListener report = new TableColumnModelListener() {


            public void columnRemoved(TableColumnModelEvent e) {
                int fromIndex = e.getFromIndex();
                assertEquals("old visible index of removed", index, fromIndex);
                assertEquals("moved to invisible", true, 
                        columnModel.isRemovedToInvisibleEvent(fromIndex));
                
            }
            public void columnAdded(TableColumnModelEvent e) {}
            public void columnMarginChanged(ChangeEvent e) {  }
            public void columnMoved(TableColumnModelEvent e) {}
            public void columnSelectionChanged(ListSelectionEvent e) {}
            
        };
        columnModel.addColumnModelListener(report);
        columnB.setVisible(false);
        // sanity testing internals: marker client property removed
        assertEquals("ignoreEvents marker must be null", null,  
                columnB.getClientProperty("TableColumnModelExt.ignoreEvent"));
    }

    
    /**
     * Issue #369-swingx: properties of hidden columns are not fired. <p>
     * test the change from hidden to visible.
     */
    @Test
    public void testShowTableColumnPropertyNotification() {
        TableColumnModelExt columnModel = createColumnModel(COLUMN_COUNT);
        Object identifier = "0";
        // sanity...
        assertNotNull(columnModel.getColumnExt(identifier));
        columnModel.getColumnExt(identifier).setVisible(false);
        ColumnModelReport report = new ColumnModelReport();
        columnModel.addColumnModelListener(report);
        columnModel.getColumnExt(identifier).setVisible(true);
        // can't use the report because of the ignore_event hack in
        // moveToVisible of DTCMExt - revisit: maybe no longer
        // needed now that the column property events are correctly
        // routed?
        TestUtils.assertPropertyChangeEvent(report.getLastColumnPropertyEvent(), 
                "visible", false, true);
    }
    

    /**
     * Issue #369-swingx: properties of hidden columns are not fired. <p>
     * test property changes while hidden 
     *
     */
    @Test
    public void testHiddenTableColumnPropertyNotification() {
        TableColumnModelExt columnModel = createColumnModel(COLUMN_COUNT);
        String identifier = "0";
        TableColumnExt columnExt = columnModel.getColumnExt(identifier);
        columnExt.setVisible(false);
        // sanity...
        assertNotNull(columnExt);
        String title = columnExt.getTitle() + "changed";
        ColumnModelReport report = new ColumnModelReport();
        columnModel.addColumnModelListener(report);
        columnExt.setIdentifier(title);
        TestUtils.assertPropertyChangeEvent(report.getPropertyChangeReport(), 
                "identifier", identifier, title);
    }
    
    /**
     * Issue #253-swingx: hiding/showing columns changes column sequence.
     * 
     * The test is modelled after the example code as 
     * http://forums.java.net/jive/thread.jspa?threadID=7344.
     *
     */
    @Test
    public void testHideShowColumns() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(10);   
        int[] columnsToHide = new int[] { 4, 7, 6, 8, };
        for (int i = 0; i < columnsToHide.length; i++) {
            model.getColumnExt(String.valueOf(columnsToHide[i])).setVisible(false);
        }
        // sanity: actually hidden
        assertEquals(model.getColumnCount(true) - columnsToHide.length, model.getColumnCount());
        for (int i = 0; i < columnsToHide.length; i++) {
            model.getColumnExt(String.valueOf(columnsToHide[i])).setVisible(true);
        }
        // sanity: all visible again
        assertEquals(10, model.getColumnCount());
        for (int i = 0; i < model.getColumnCount(); i++) {
            // the original sequence
            assertEquals(i, model.getColumn(i).getModelIndex());
        }
    
    }
    
    /**
     * test sequence of visible columns after hide/move/show.
     * 
     * Expected behaviour should be like in Thunderbird.
     *
     */
    @Test
    public void testMoveColumns() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(COLUMN_COUNT);
        TableColumnExt columnExt = model.getColumnExt(1);
        columnExt.setVisible(false);
        model.moveColumn(1, 0);
        columnExt.setVisible(true);
        assertEquals(columnExt.getModelIndex(), model.getColumnExt(2).getModelIndex());
    }
    /**
     * test the columnPropertyChangeEvent is fired as expected.
     *
     */
    @Test
    public void testColumnPropertyChangeNotification() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(COLUMN_COUNT);
        ColumnModelReport report = new ColumnModelReport();
        model.addColumnModelListener(report);
        TableColumn column = model.getColumn(0);
        column.setHeaderValue("somevalue");
        assertEquals(1, report.getColumnPropertyEventCount());
        PropertyChangeEvent event = report.getLastColumnPropertyEvent();
        assertEquals(column, event.getSource());
        assertEquals("headerValue", event.getPropertyName());
        assertEquals("somevalue", event.getNewValue());
    }
    /**
     * added TableColumnModelExtListener: test for add/remove extended listeners.
     *
     */
    @Test
    public void testAddExtListener() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(COLUMN_COUNT);
        ColumnModelReport extListener = new ColumnModelReport();
        model.addColumnModelListener(extListener);
        // JW: getListeners returns the count of exactly the given class?
//        assertEquals(1, model.getListeners(TableColumnModelExtListener.class).length);
//        assertEquals(2, model.getListeners(EventListener.class).length);
//        model.removeColumnModelListener(extListener);
//        assertEquals(0, model.getListeners(TableColumnModelExtListener.class).length);
//        assertEquals(0, model.getListeners(EventListener.class).length);
        assertEquals(1, model.getEventListenerList().getListenerCount(TableColumnModelExtListener.class));
        assertEquals(2, model.getEventListenerList().getListenerCount());
        model.removeColumnModelListener(extListener);
        assertEquals(0, model.getEventListenerList().getListenerCount(TableColumnModelExtListener.class));
        assertEquals(0, model.getEventListenerList().getListenerCount());
        
    }
    /**
     * Issue #??-swingx: incorrect isRemovedToInvisible after
     * removing an invisible column. 
     *
     */
    @Test
    public void testRemoveInvisibleColumn() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(COLUMN_COUNT);
        TableColumnExt tableColumnExt = ((TableColumnExt) model.getColumn(0));
        tableColumnExt.setVisible(false);
        model.removeColumn(tableColumnExt);
        assertEquals("visible column count must be reduced", COLUMN_COUNT - 1, model.getColumns(false).size());
        assertEquals("all columns count must be unchanged", COLUMN_COUNT - 1, model.getColumns(true).size());
        assertFalse("removing invisible must update event cache", model.isRemovedToInvisibleEvent(0));
    }

    
    @Test
    public void testGetColumns() {
        TableColumnModelExt model = createColumnModel(COLUMN_COUNT);
        ((TableColumnExt) model.getColumn(0)).setVisible(false);
        assertEquals("visible column count must be reduced", COLUMN_COUNT - 1, model.getColumns(false).size());
        assertEquals("all columns count must be unchanged", COLUMN_COUNT, model.getColumns(true).size());
    }
    /**
     * column count must be changed on changing 
     * column visibility.
     *
     */
    @Test
    public void testColumnCountOnSetInvisible() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        int columnCount = model.getColumnCount();
        TableColumnExt column = (TableColumnExt) model.getColumn(columnCount - 1);
        assertTrue(column.isVisible());
        column.setVisible(false);
        assertEquals("columnCount must be decremented", columnCount - 1, model.getColumnCount());
    }
    
    /**
     * Issue #156: must update internal state after setting invisible.
     * Here: the cached totalWidth. Expect similar inconsistency
     * with selection.
     *
     */
    @Test
    public void testTotalColumnWidth() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        int totalWidth = model.getTotalColumnWidth();
        TableColumnExt column = (TableColumnExt) model.getColumn(0);
        int columnWidth = column.getWidth();
        column.setVisible(false);
        assertEquals("new total width must be old minus invisible column width " + columnWidth,
                totalWidth - columnWidth, model.getTotalColumnWidth());
        
    }
    
    /** 
     * Issue #157: must fire columnRemoved after setting to invisible.
     *
     */
    @Test
    public void testRemovedFired() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        ColumnModelReport l = new ColumnModelReport();
        model.addColumnModelListener(l);
        TableColumnExt column = (TableColumnExt) model.getColumn(0);
        column.setVisible(false);
        assertTrue("must have fired columnRemoved", l.hasRemovedEvent());
    }

    /** 
     * Issue #157: must fire columnAdded after setting to invisible.
     *
     */
    @Test
    public void testAddedFired() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        ColumnModelReport l = new ColumnModelReport();
        TableColumnExt column = (TableColumnExt) model.getColumn(0);
        column.setVisible(false);
        model.addColumnModelListener(l);
        column.setVisible(true);
        assertTrue("must have fired columnRemoved", l.hasAddedEvent());
    }

    /**
     * columnAdded: event.getToIndex must be valid columnIndex.
     * 
     * 
     */
    @Test
     public void testAddInvisibleColumn()  {
         TableColumnModel model = createColumnModel(COLUMN_COUNT);
         TableColumnModelListener l = new TableColumnModelListener() {

            public void columnAdded(TableColumnModelEvent e) {
                assertTrue("toIndex must be positive", e.getToIndex() >= 0);
                ((TableColumnModel) e.getSource()).getColumn(e.getToIndex());
            }

            public void columnRemoved(TableColumnModelEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void columnMoved(TableColumnModelEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void columnMarginChanged(ChangeEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
                // TODO Auto-generated method stub
                
            }
             
         };
         model.addColumnModelListener(l);
         // add invisible column
         TableColumnExt invisibleColumn = new TableColumnExt(0);
         invisibleColumn.setVisible(false);
         model.addColumn(invisibleColumn);
         // sanity check: add visible column
         model.addColumn(createTableColumnExt(0));
    }
    /**
     * columnAt must work on visible columns.
     *
     */
    @Test
    public void testColumnAt() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        int totalWidth = model.getTotalColumnWidth();
        int lastColumn = model.getColumnIndexAtX(totalWidth - 10);
        assertEquals("lastColumn detected", model.getColumnCount() - 1, lastColumn);
        TableColumnExt column = (TableColumnExt) model.getColumn(lastColumn);
        column.setVisible(false);
        assertEquals("out of range", -1, model.getColumnIndexAtX(totalWidth - 10));
    }

//------------------  factory methods
    
    /**
     * creates and returns a TableColumnModelExt with the given number
     * of configured columns of type <code>TableColumnExt</code>.
     * 
     * @param columns the number of columns to create and add to the model
     * @return a <code>TableColumnModelExt</code> filled with columns.
     *    
     * @see createTableColumnExt
     */
    protected TableColumnModelExt createColumnModel(int columns) {
        TableColumnModelExt model = new DefaultTableColumnModelExt();
        for (int i = 0; i < columns; i++) {
            model.addColumn(createTableColumnExt(i));
      
        }
        return model;
    }

    /**
     * Creates and returns a TableColumnExt with simple standard configuration.
     * 
     * <pre><code>
     * column.getModelIndex() == modelIndex
     * column.getIdentifier() == String.valueOf(modelIndex);
     * </code></pre>
     *  
     * @param modelIndex the model column index to use for config
     * @return a <code>TableColumnExt</code> with standard configuration
     */
    protected TableColumnExt createTableColumnExt(int modelIndex) {
        TableColumnExt column = new TableColumnExt(modelIndex);
        column.setIdentifier(String.valueOf(modelIndex));
        return column;
    }
    

}
