package view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class PodAnalysisApplet extends JApplet {
	
	private int numberOfPods = 5;
	Container content = this.getContentPane();

	public void init() {
		
		
		content.setLayout(new BorderLayout());

		JScrollPane podScroller = new JScrollPane();
		podScroller.setAutoscrolls(true);
		JPanel podPanel = new JPanel(new GridLayout(0,1));
		podScroller.getViewport().add(podPanel);
		
		content.add(podScroller, BorderLayout.CENTER);
		content.add(new JButton("Numerical Analysis"), BorderLayout.NORTH);

		
		for(int i = 0; i < numberOfPods; i++){
		//	podPanel.add(new PodPanel("POD" + i),BorderLayout.CENTER);
		}
	    podScroller.setVisible(true);
		podPanel.setVisible(true);
		content.setVisible(true);
	}
	
	public void paint(Graphics g) {
//		int width = getSize().width;
//		int height = getSize().height;

	}
	
	

}
