package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JTextField;

import data.Settings;

/**
 * window for the settings
 * 
 * @author tamara
 */
public class SettingsFrame extends JFrame {

    private PodFrame owner;
    private JTextField tf_timePerBooth;
    private JTextField tf_peoplePerCar;

    public SettingsFrame(PodFrame owner) {
        super("Settings");
        this.owner = owner;
        this.setLocation(200, 100);
        this.setAlwaysOnTop(true);
        owner.setEnabled(false);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        init();
    } 

    private void init() {
        ButtonListener listener = new ButtonListener(this);
        JButton b_ok = new JButton("Save");
        JButton b_cancel = new JButton("Cancel");
        b_ok.setActionCommand("save");
        b_cancel.setActionCommand("cancel");
        b_ok.addActionListener(listener);
        b_cancel.addActionListener(listener);
        JPanel p_center = new JPanel(new GridLayout(0, 1));
        JPanel p_buttons = new JPanel();
        JPanel p_timePerBooth = new JPanel(new BorderLayout());
        JPanel p_peoplePerCar = new JPanel(new BorderLayout());
        JLabel l_timePerBooth = new JLabel("Assumed time per booth and car (in s)");
        JLabel l_peoplePerCar = new JLabel("Assumed people per car");
        tf_timePerBooth = new JTextField(4);
        tf_peoplePerCar = new JTextField(4);
        tf_timePerBooth.addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((Character.isDigit(c)
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE)))) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });
        tf_peoplePerCar.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((Character.isDigit(c)
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE)))) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });
        Settings set = owner.getSettings();
        tf_timePerBooth.setText("" + set.getDefaultTimePerBooth());
        tf_peoplePerCar.setText("" + set.getDefaultPeoplePerCar());
        p_timePerBooth.add(l_timePerBooth, BorderLayout.CENTER);
        p_timePerBooth.add(tf_timePerBooth, BorderLayout.EAST);
        p_peoplePerCar.add(l_peoplePerCar, BorderLayout.CENTER);
        p_peoplePerCar.add(tf_peoplePerCar, BorderLayout.EAST);
        p_buttons.add(b_ok);
        p_buttons.add(b_cancel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(new Dimension(335, 120));
        this.setResizable(false);
        Container content = this.getContentPane();
        content.setLayout(new BorderLayout());
        p_center.add(p_peoplePerCar);
        p_center.add(p_timePerBooth);
        content.add(p_center, BorderLayout.CENTER);
        content.add(p_buttons, BorderLayout.SOUTH);
        content.setVisible(true);
        this.setVisible(true);

    }

    private void readSettings() {
    }

    private class ButtonListener implements ActionListener {

        private SettingsFrame s;

        public ButtonListener(SettingsFrame s) {
            this.s = s;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("save")) {
                owner.setEnabled(true);
                Settings set = owner.getSettings();
                set.setDefaultPeoplePerCar(new Double(tf_peoplePerCar.getText()));
                set.setDefaultTimePerBooth(new Double(tf_timePerBooth.getText()));
                set.writeSettings();
                s.dispose();

            } else if (e.getActionCommand().equals("cancel")) {

                owner.setEnabled(true);
                s.dispose();
            }

        }
    }
}
