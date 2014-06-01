package edu.unt.cerl.applicationframework.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.IdentityHashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class DockPanel extends JPanel implements ActionListener,
        MouseMotionListener, MouseListener {

    private JPanel dockPanel;
    private final IdentityHashMap<Object, Long> pressMap =
            new IdentityHashMap<Object, Long>();
    private Point mousePos = null;
    private static Font[] FONTS = new Font[120];
    private final Timer repaintTimer = new Timer(20, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            dockPanel.revalidate();
        }
    });

    public DockPanel(List<String> names, List<ActionListener> listeners) {
        this.initDock(names, listeners);
    }

    @Override
    protected void paintComponent(Graphics g) {
        ((Graphics2D) g).setPaint(new GradientPaint(0, getHeight() / 2,
                Color.WHITE, 0, getHeight(), new Color(240, 238, 235)));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void initDock(List<String> names, List<ActionListener> listeners) {

        MigLayout migLayout = new MigLayout("align center bottom, insets 30");
        this.setLayout(migLayout);
        dockPanel = this;

        // This callback methods will be called for every layout cycle and let you make correction before and after the calculations.
        migLayout.addLayoutCallback(new LayoutCallback() {

            // This is the size change part
            @Override
            public BoundSize[] getSize(ComponentWrapper comp) {
                if (comp.getComponent() instanceof JButton) {
                    Component c = (Component) comp.getComponent();
                    Point p = mousePos != null ? SwingUtilities.convertPoint(
                            dockPanel, mousePos, c) : new Point(-1000, -1000);

                    float fact = (float) Math.sqrt(Math.pow(Math.abs(p.x - c.
                            getWidth() / 2f), 2) + Math.pow(Math.abs(p.y - c.
                            getHeight() / 2f), 2));
                    fact = Math.max(2 - (fact / 200), 1);

                    return new BoundSize[]{new BoundSize(
                                new UnitValue(70 * fact), ""), new BoundSize(new UnitValue(70
                                * fact), "")};
                }
                return null;
            }

            // This is the jumping part
            @Override
            public void correctBounds(ComponentWrapper c) {
                Long pressedNanos = pressMap.get(c.getComponent());
                if (pressedNanos != null) {
                    long duration = System.nanoTime() - pressedNanos;
                    double maxHeight = 100.0 - (duration / 100000000.0);
                    int deltaY = (int) Math.round(Math.abs(Math.sin((duration)
                            / 300000000.0) * maxHeight));
                    c.setBounds(c.getX(), c.getY() - deltaY, c.getWidth(), c.
                            getHeight());
                    if (maxHeight < 0.5) {
                        pressMap.remove(c.getComponent());
                        if (pressMap.isEmpty()) {
                            repaintTimer.stop();
                        }
                    }
                }
            }
        });
        while(!names.isEmpty()){
            dockPanel.add(createButton(names.remove(0),listeners.remove(0)), "aligny 0.8al");
        }
        dockPanel.addMouseMotionListener(this);
        dockPanel.addMouseListener(this);
    }

    private JButton createButton(String name, ActionListener listener) {
        JButton button = new JButton(name);

        button.setForeground(new Color(100, 100, 100));
        button.setFocusPainted(false);
        button.addMouseMotionListener(this);
        button.addActionListener(this);
        button.addActionListener(listener);
        button.setMargin(new Insets(0, 0, 0, 0));

        return button;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.getSource() instanceof JButton) {
            mousePos = SwingUtilities.convertPoint((Component) e.getSource(), e.
                    getPoint(), dockPanel);
        } else {
            mousePos = e.getPoint();
        }
        this.revalidate();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mousePos = null;
        this.revalidate();
    }

    public void actionPerformed(ActionEvent e) {
        pressMap.put(e.getSource(), System.nanoTime());
        repaintTimer.start();
    }
}
