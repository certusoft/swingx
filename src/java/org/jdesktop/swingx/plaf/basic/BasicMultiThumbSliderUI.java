/*
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
 */

package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.ThumbRenderer;
import org.jdesktop.swingx.multislider.TrackRenderer;
import org.jdesktop.swingx.plaf.MultiThumbSliderUI;

/**
 *
 * @author Joshua Marinacci
 */
public class BasicMultiThumbSliderUI extends MultiThumbSliderUI {
    
    protected JXMultiThumbSlider slider;
    
    public static ComponentUI createUI(JComponent c) {
        return new BasicMultiThumbSliderUI();
    }
    
    public void installUI(JComponent c) {
        slider = (JXMultiThumbSlider)c;
	slider.setThumbRenderer(new BasicThumbRenderer());
        slider.setTrackRenderer(new BasicTrackRenderer());        
    }
    public void uninstallUI(JComponent c) {
        slider = null;
    }

    private class BasicThumbRenderer extends JComponent implements ThumbRenderer {
        Image thumb;
        public BasicThumbRenderer() {
            try {
                thumb = ImageIO.read(this.getClass().getResourceAsStream("resources/JXMultiThumbSlider.thumb.png"));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
            setPreferredSize(new Dimension(14,14));
        }

        protected void paintComponent(Graphics g) {
            g.setColor(Color.green);
            Polygon poly = new Polygon();
            JComponent thumb = this;
            poly.addPoint(thumb.getWidth()/2,0);
            poly.addPoint(0,thumb.getHeight()/2);
            poly.addPoint(thumb.getWidth()/2,thumb.getHeight());
            poly.addPoint(thumb.getWidth(),thumb.getHeight()/2);
            g.fillPolygon(poly);
        }

        public JComponent getThumbRendererComponent(JXMultiThumbSlider slider, int index, boolean selected) {
            return this;
        }
    }

    private class BasicTrackRenderer implements TrackRenderer {

        public void paintTrack(Graphics2D g, JXMultiThumbSlider slider) {
            g.setColor(slider.getBackground());
            g.fillRect(0, 0, slider.getWidth(), slider.getHeight());
            g.setColor(Color.black);
            g.drawLine(0,slider.getHeight()/2,slider.getWidth(),slider.getHeight()/2);
            g.drawLine(0,slider.getHeight()/2+1,slider.getWidth(),slider.getHeight()/2+1);
        }
    }


    
}
