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
package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.test.PropertyChangeReport;

/**
 * Test to exposed known issues of <code>CompoundPainter</code>s.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class CompoundPainterIssues extends InteractiveTestCase {
    /**
     * Issue #??-swingx: clearCache has no detectable effect.
     * @throws IOException 
     *
     */
    public void testClearCacheDetectable() throws IOException {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        AbstractPainter<Object> painter = new ShapePainter() {
            @Override
            protected boolean shouldUseCache() {
                return isCacheable();
            }

        };
        painter.paint(g, null, 10, 10);
        // sanity
        assertFalse("clean after paint", painter.isDirty());
        assertTrue("cacheable is true by default", painter.isCacheable());
        assertFalse("has a cached image", painter.isCacheCleared());
        PropertyChangeReport report = new PropertyChangeReport();
        painter.addPropertyChangeListener(report);
        painter.clearCache();
        assertTrue("painter must have fired change event", report.hasEvents());
        
    }
}
