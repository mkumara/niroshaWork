package edu.unt.cerl.replan.view.mainframe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;


public class TabButton extends JPanel implements ActionListener {

    private JTabbedPane pane;
    private ScenarioTabbedPane owner;
    private JLabel titleLabel;
    private int tabIndex;

    public TabButton(final JTabbedPane pane, final int index, ScenarioTabbedPane owner) {
        this.pane = pane;
        this.owner = owner;
        setOpaque(false);
        titleLabel = new JLabel(pane.getTitleAt(index));
        add(titleLabel);
        Icon closeIcon = new CloseIcon(false);
        Icon closeIconR = new CloseIcon(true);
        JButton btClose = new JButton(closeIcon);
        btClose.setContentAreaFilled(false);
        btClose.setRolloverEnabled(true);
        btClose.setRolloverIcon(closeIconR);
        btClose.setUI(new BasicButtonUI());
        btClose.setPreferredSize(new Dimension(closeIcon.getIconWidth(), closeIcon.getIconHeight()));
        add(btClose);
        btClose.addActionListener(this);
        pane.setTabComponentAt(index, this);
        this.tabIndex = index;
    }
    
    
    public int getCurrentIndexOfTab(){
        return this.tabIndex;
    }

    public void updateTitle(String newTitle){
        this.titleLabel.setText(newTitle);
    }

    public void actionPerformed(final ActionEvent e) {
        int r = JOptionPane.showConfirmDialog(this,"Are you sure you want to close this tab?","Close tab?", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            int i = pane.indexOfTabComponent(this);
            System.out.println("Component " + i + "selected; title = " + owner.getTitleAt(i));

            owner.cleanUpTab(i);
            if (i != -1) {
                pane.remove(i);
            }
        }

    }

    private class CloseIcon implements Icon {

        private boolean rollover;

        public CloseIcon(boolean rollover) {
            this.rollover = rollover;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            Stroke stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2));
            g.setColor(Color.BLACK);
            if (rollover) {
                g.setColor(Color.RED);
            }
            g.drawLine(6, 6, getIconWidth() - 7, getIconHeight() - 7);
            g.drawLine(getIconWidth() - 7, 6, 6, getIconHeight() - 7);
            g2.setStroke(stroke);
        }

        public int getIconWidth() {
            return 17;
        }

        public int getIconHeight() {
            return 17;
        }
    }
}
