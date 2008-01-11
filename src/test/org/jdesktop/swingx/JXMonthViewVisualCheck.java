/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXMonthView.SelectionMode;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.event.DateSelectionListener;
import org.jdesktop.swingx.test.XTestUtils;

/**
 * Test to expose known issues with JXMonthView.
 * 
 * @author Jeanette Winzenburg
 */
public class JXMonthViewVisualCheck extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXMonthViewVisualCheck.class
            .getName());

    @SuppressWarnings("unused")
    private Calendar calendar;

    public static void main(String[] args) {
//      setSystemLF(true);
      JXMonthViewVisualCheck  test = new JXMonthViewVisualCheck();
      try {
          test.runInteractiveTests();
//        test.runInteractiveTests(".*Move.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    /**
     * #681-swingx: first row overlaps days.
     * 
     * Looks like a problem with the constructor taking a locale? 
     * Default is okay (even if German), US is okay, explicit german is wrong.
     */
    public void interactiveFirstRowOfMonthSetLocale() {
        JPanel p = new JPanel();
        // default constructor
        p.add(new JXMonthView());
        // explicit us locale
        JXMonthView us = new JXMonthView();
        us.setLocale(Locale.US);
        p.add(us);
        // explicit german locale
        JXMonthView german = new JXMonthView();
        german.setLocale(Locale.GERMAN);
        p.add(german);
        showInFrame(p, "first row overlapping - setLocale");
    }

   
    /**
     * #681-swingx: first row overlaps days.
     * 
     * Looks like a problem with the constructor taking a locale? 
     * Default is okay (even if German), US is okay, explicit german is wrong.
     */
    public void interactiveFirstRowOfMonthLocaleConstructor() {
        JPanel p = new JPanel();
        // default constructor
        p.add(new JXMonthView());
        // explicit us locale
        p.add(new JXMonthView(Locale.US));
//         explicit german locale
        p.add(new JXMonthView(Locale.GERMAN));
        showInFrame(p, "first row overlapping - constructor");
    }
    /**
     * #681-swingx: first row overlaps days.
     * Here everything looks okay.
     * 
     * @see #interactiveFirstRowOfMonthLocaleDependent()
     */
    public void interactiveFirstRowOfMonth() {
        JXMonthView monthView = new JXMonthView();
        calendar.set(2008, 1, 1);
        monthView.setSelectedDate(calendar.getTime());
        showInFrame(monthView, "first row");
    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     */
    public void interactiveUpdateOnTimeZone() {
        JPanel panel = new JPanel();

        final JComboBox zoneSelector = new JComboBox(TimeZone.getAvailableIDs());
        final JXDatePicker picker = new JXDatePicker();
        final JXMonthView monthView = new JXMonthView();
        monthView.setSelectedDate(picker.getDate());
        monthView.setTraversable(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(picker.getTimeZone().getID());

        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = TimeZone.getTimeZone(zone);
                picker.setTimeZone(tz);
                monthView.setTimeZone(tz);
              
                assertEquals(tz, monthView.getCalendar().getTimeZone());
            }
        });

        panel.add(zoneSelector);
        panel.add(picker);
        panel.add(monthView);
        JXFrame frame = showInFrame(panel, "display problems with non-default timezones");
        Action assertAction = new AbstractActionExt("assert dates") {

            public void actionPerformed(ActionEvent e) {
                Calendar cal = monthView.getCalendar();
                LOG.info("cal/firstDisplayed" + 
                        cal.getTime() +"/" + new Date(monthView.getFirstDisplayedDate()));
            }
            
        };
        addAction(frame, assertAction);
        frame.pack();
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     */
    public void interactiveUpdateOnTimeZoneJP() {
        JComponent panel = Box.createVerticalBox();

        final JComboBox zoneSelector = new JComboBox(TimeZone.getAvailableIDs());
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(monthView.getTimeZone().getID());

        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = TimeZone.getTimeZone(zone);
                monthView.setTimeZone(tz);
              
                assertEquals(tz, monthView.getCalendar().getTimeZone());
            }
        });

        panel.add(monthView);
//        JPanel bar = new JPanel();
        JLabel label = new JLabel("Select TimeZone:");
        label.setHorizontalAlignment(JLabel.CENTER);
//        panel.add(label);
        panel.add(zoneSelector);
        JXFrame frame = wrapInFrame(panel, "TimeZone");
        frame.pack();
        frame.setVisible(true);
    }
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     */
    public void interactiveTimeZoneClearDateState() {
        JPanel panel = new JPanel();

        final JComboBox zoneSelector = new JComboBox(TimeZone.getAvailableIDs());
        final JXDatePicker picker = new JXDatePicker();
        final JXMonthView monthView = new JXMonthView();
        monthView.setSelectedDate(picker.getDate());
        monthView.setLowerBound(XTestUtils.getStartOfToday(-10));
        monthView.setUpperBound(XTestUtils.getStartOfToday(10));
        monthView.setUnselectableDates(XTestUtils.getStartOfToday(2));
        monthView.setFlaggedDates(new long[] {XTestUtils.getStartOfToday(4).getTime()});
        monthView.setTraversable(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(picker.getTimeZone().getID());

        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = TimeZone.getTimeZone(zone);
                picker.setTimeZone(tz);
                monthView.setTimeZone(tz);
              
                assertEquals(tz, monthView.getCalendar().getTimeZone());
            }
        });

        panel.add(zoneSelector);
        panel.add(picker);
        panel.add(monthView);
        JXFrame frame = showInFrame(panel, "clear internal date-related state");
        Action assertAction = new AbstractActionExt("assert dates") {

            public void actionPerformed(ActionEvent e) {
                Calendar cal = monthView.getCalendar();
                LOG.info("cal/firstDisplayed" + 
                        cal.getTime() +"/" + new Date(monthView.getFirstDisplayedDate()));
            }
            
        };
        addAction(frame, assertAction);
        frame.pack();
    }
    
    /**
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * 
     */
    public void interactiveLastDisplayed() {
        final JXMonthView month = new JXMonthView();
        month.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        month.setTraversable(true);
        Action action = new AbstractActionExt("check lastDisplayed") {

            public void actionPerformed(ActionEvent e) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(month.getLastDisplayedDate());
                Date viewLast = cal.getTime();
                cal.setTimeInMillis(month.getUI().getLastDisplayedDate());
                Date uiLast = cal.getTime();
                LOG.info("last(view/ui): " + viewLast + "/" + uiLast);
                
            }
            
        };
        JXFrame frame = wrapInFrame(month, "default - for debugging only");
        addAction(frame, action);
        frame.setVisible(true);
    }


    /**
     * Issue #637-swingx: make JXMonthView Locale-aware.
     * 
     * Applied the patch as provided by pes17.
     * 
     */
    public void interactiveLocale() {
        JXMonthView monthView = new JXMonthView(Locale.GERMAN);
        JXMonthView other = new JXMonthView(Locale.FRANCE);
        JComponent comp = new JPanel();
        comp.add(monthView);
        comp.add(other);
        showInFrame(comp, "Localized monthView");
    }

    /**
     * Issue #563-swingx: arrow keys active even if not focused.
     * focus the button and use the arrow keys: selection moves.
     * Reason was that the WHEN_IN_FOCUSED_WINDOW key bindings
     * were always installed. 
     * 
     * Fixed by dynamically bind/unbind component input map bindings
     * based on the JXMonthView's componentInputMapEnabled property.
     *
     */
    public void interactiveMistargetedKeyStrokes() {
        JXMonthView month = new JXMonthView();
        JComponent panel = new JPanel();
        panel.add(new JButton("something to focus"));
        panel.add(month);
        showInFrame(panel, "default - for debugging only");
    }
    
    /**
     * Issue #563-swingx: arrow keys active even if not focused.
     * focus the button and use the arrow keys: selection moves.
     *
     * Fixed by dynamically bind/unbind component input map bindings
     * based on the JXMonthView's componentInputMapEnabled property.
     */
    public void interactiveMistargetedKeyStrokesPicker() {
        JXMonthView month = new JXMonthView();
        JComponent panel = new JPanel();
        JXDatePicker button = new JXDatePicker();
        panel.add(button);
        panel.add(month);
        showInFrame(panel, "default - for debugging only");
    }
    
    /**
     * Informally testing adjusting property on mouse events.
     * 
     * Hmm .. not formally testable without mocks/ui unit tests?
     *
     */
    public void interactiveAdjustingOnMouse() {
        final JXMonthView month = new JXMonthView();
        // we rely on being notified after the ui delegate ... brittle.
        MouseAdapter m = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                LOG.info("pressed - expect true " + month.getSelectionModel().isAdjusting());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                LOG.info("released - expect false" + month.getSelectionModel().isAdjusting());
            }
            
        };
        month.addMouseListener(m);
        showInFrame(month, "Mouse and adjusting - state on pressed/released");
    }

    /**
     * Issue #555-swingx: multiple selection with keyboard not working
     * Happens for standalone, okay for monthview in popup.
     * 
     * Fixed as a side-effect of cleanup of input map bindings. 
     * 
     */
    public void interactiveMultipleSelectionWithKeyboard() {
        JXMonthView interval = new JXMonthView();
        interval.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        JXMonthView multiple = new JXMonthView();
        multiple.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        // for comparison: single interval in popup is working
        JXDatePicker picker = new JXDatePicker();
        JXMonthView intervalForPicker = new JXMonthView();
        intervalForPicker.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        picker.setMonthView(intervalForPicker);
        
        JComponent comp = new JPanel();
        comp.add(interval);
        comp.add(multiple);
        comp.add(picker);
        showInFrame(comp, "select interval with keyboard");
        
    }
    /**
     * Issue #??-swingx: esc/enter does not always fire actionEvent.
     * 
     * Fixed: committing/canceling user gestures always fire.
     * 
     * Open: mouse-gestures?
     *
     */
    public void interactiveMonthViewEvents() {
        JXMonthView monthView = new JXMonthView();
        JXMonthView interval = new JXMonthView();
        interval.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        JXMonthView multiple = new JXMonthView();
        multiple.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName() + 
                        "\n" + e);
            }
            
        };
        monthView.addActionListener(l);
        interval.addActionListener(l);
        multiple.addActionListener(l);
        DateSelectionListener d = new DateSelectionListener() {

            public void valueChanged(DateSelectionEvent ev) {
                LOG.info("got selection from: " + ev.getSource().getClass().getName() + 
                        "\n" + ev);
            }
            
        };
        monthView.getSelectionModel().addDateSelectionListener(d);
        interval.getSelectionModel().addDateSelectionListener(d);
        multiple.getSelectionModel().addDateSelectionListener(d);
        
        JXDatePicker picker = new JXDatePicker();
        JXMonthView intervalForPicker = new JXMonthView();
        intervalForPicker.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        // JW: this picker comes up with today - should have taken the
        // empty selection (which it does the unit test)
        picker.setMonthView(intervalForPicker);
        
        JComponent comp = new JPanel();
        comp.add(monthView);
        comp.add(interval);
        comp.add(multiple);
        comp.add(picker);
        JXFrame frame = showInFrame(comp, "events from monthView");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");

    }
    
//----------------------
    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }

    
    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
