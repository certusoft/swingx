/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class JXFindBar extends JXFindPanel {

  protected JButton findNext;
  protected JButton findPrevious;

    public JXFindBar() {
        this(null);
    }

    public JXFindBar(Searchable searchable) {
        super(searchable);
        getPatternModel().setIncremental(true);
    }

//--------------------------- action call back
    /**
     * removes itself from parent if any.
     *
     */
    public void cancel() {
       Container parent = getParent();
       if (parent != null) {
           parent.remove(this);
           if (parent instanceof JComponent) {
               ((JComponent)parent).revalidate();
           } 
       }
    }

    //-------------------- init
    
    @Override
    protected void initExecutables() {
        getActionMap().put(JXDialog.CLOSE_ACTION_COMMAND, 
                createBoundAction(JXDialog.CLOSE_ACTION_COMMAND, "cancel"));
        super.initExecutables();
    }

    @Override
    protected void bind() {
      super.bind();
      searchField.addActionListener(getAction(JXDialog.EXECUTE_ACTION_COMMAND));
      findNext.setAction(getAction(FIND_NEXT_ACTION_COMMAND));
      findPrevious.setAction(getAction(FIND_PREVIOUS_ACTION_COMMAND));
      KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
      getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, JXDialog.CLOSE_ACTION_COMMAND);
    }

    @Override
    protected void build() {
        add(searchLabel);
        add(searchField);
        add(findNext);
        add(findPrevious);
    }

   
    @Override
    protected void initComponents() {
      super.initComponents();
      findNext = new JButton();
      findPrevious = new JButton();
    }

    
}
