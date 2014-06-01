package edu.unt.cerl.replanexecution;


import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 *
 * @author tamara
 */
public class ProgressWindow extends JFrame {

    private Replan task;
    private JFrame progressFrame;
    private JProgressBar progressBar;
    private Timer timer;
    private JTextArea taskOutput;

    public ProgressWindow(Replan task) {
        this.task = task;
        init();
    }

    private void init() {
        progressBar = new JProgressBar(0, task.getLengthOfTask());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);

        progressFrame = new JFrame();
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(progressBar, BorderLayout.NORTH);
        content.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        progressFrame.setContentPane(content);

        timer = new Timer(1000, new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                progressBar.setValue(task.getCurrent());
                if (task.msgChanged()) {
                    taskOutput.append(task.getMessage() + "\n");
                }
                taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
                if (task.done()) {
                    Toolkit.getDefaultToolkit().beep();
                    timer.stop();
                    progressBar.setValue(progressBar.getMinimum());
                    progressFrame.dispose();
                }
            }
        });
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        progressFrame.pack();
        progressFrame.setVisible(true);
        timer.start();
    }
}
