/*
 * EnumPropertyEditor.java
 *
 * Created on August 18, 2006, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.editors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.jdesktop.swingx.painter.RectanglePainter;

/**
 *
 * @author joshy
 */
public class EnumPropertyEditor<E extends Enum<E>> extends PropertyEditorSupport {
    private Class<E> en;
    private EnumSet<E> set;
    /** Creates a new instance of EnumPropertyEditor */
    public EnumPropertyEditor(Class<E> en) {
        this.en = en;
        set = EnumSet.allOf(en);
    }
    
    public String[] getTags() {
        List<String> strs = new ArrayList<String>();
        for(E e : set) {
            strs.add(e.toString());
        }
        return strs.toArray(new String[0]);
    }
    
    public String getAsText() {
        return getValue().toString();
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
//        u.p("setting as text: " + text);
        Enum<E> e = Enum.valueOf(en, text);
        setValue(e);
    }
}
