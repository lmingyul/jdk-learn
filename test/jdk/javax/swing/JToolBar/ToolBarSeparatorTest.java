/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 4346610
 * @key headful
 * @summary Verifies if Adding JSeparator to JToolBar "pushes" buttons added
 *          after separator to edge
 * @run main ToolBarSeparatorTest
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import javax.imageio.ImageIO;

public class ToolBarSeparatorTest {

    private static JFrame frame;
    private static JToolBar toolBar;
    private static JButton btn;
    private static volatile Point pt;
    private static volatile Dimension size;
    private static volatile int btnWidth;

    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();
        robot.setAutoDelay(100);
        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = new JFrame("Troy's ToolBarTest");
                toolBar = new JToolBar();
                toolBar.setMargin(new Insets(0,0,0,0));
                btn = new JButton("button 1");
                toolBar.add(btn);
                toolBar.add(new JButton("button 2"));
                toolBar.add(new JSeparator(SwingConstants.VERTICAL));
                toolBar.add(new JButton("button 3"));
                toolBar.setBackground(Color.red);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(toolBar, BorderLayout.NORTH);
                frame.getContentPane().add(new JPanel(), BorderLayout.CENTER);
                frame.setSize(400, 100);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
            robot.waitForIdle();
            robot.delay(1000);
            SwingUtilities.invokeAndWait(() -> {
                pt = toolBar.getLocationOnScreen();
                size = toolBar.getSize();
                btnWidth = btn.getWidth();
            });
            boolean passed = true;
            System.out.println("point " + pt + " size " + size +
                                " btn width " + btn.getWidth());

            // Capture button width area after 2 buttons which shouldn't be red
            BufferedImage img = robot.createScreenCapture(
                new Rectangle(pt.x + btnWidth*2 + 20, pt.y, btnWidth, size.height));

            int y = img.getHeight() / 2;
            for (int x = 10; x < img.getWidth(); x += 10) {
                System.out.println("x " + x + " y " + y +
                                   " color: " + new Color(img.getRGB(x, y)));
                Color c = new Color(img.getRGB(x, y));
                if (c.equals(Color.RED)) {
                    passed = false;
                    break;
                }
            }
            if (!passed) {
                ImageIO.write(img, "png", new java.io.File("image.png"));
                throw new RuntimeException("Separator takes more space");
            }
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                if (frame != null) {
                    frame.dispose();
                }
            });
        }
    }
}
